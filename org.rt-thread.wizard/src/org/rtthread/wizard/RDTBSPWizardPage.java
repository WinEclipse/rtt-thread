package org.rtthread.wizard;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.FileSystemSelectionArea;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * BSP Wizard
 * 
 * @author Ming.He
 * @date 2011-9-2
 */
public class RDTBSPWizardPage extends WizardPage {

	private static final int SIZING_TEXT_FIELD_WIDTH = 250;

	private static final String SAVED_LOCATION_ATTR = "BSP";

	private Text pathText;

	private Text targetText;

	private Button browseButton;

	private Button clearButton;

	private Tree tree;

	private FileSystemSelectionArea fileSystemSelectionArea;

	private String rtPath;
	
	/**
	 * @param pageName
	 * @param rtPath 
	 */
	protected RDTBSPWizardPage(String pageName, String rtPath) {
		super(pageName);
		this.rtPath = rtPath;
	}
	
	public String getPath(){
		return pathText.getText();
	}
	
	public String getTarget(){
		return targetText.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	// Override
	public void createControl(Composite parent) {
		createGroup(parent);
	}

	/**
	 * @param parent
	 */
	private void createGroup(Composite parent) {

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(1, true));

		Group group1 = new Group(c, SWT.NONE);

		group1.setLayout(new GridLayout());
		group1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_BOTH));
		group1.setText("Browser");

		Composite browserComp = new Composite(group1, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		browserComp.setLayout(layout);

		createBrowserEntryArea(browserComp);

		Composite treeCom = new Composite(group1, SWT.NONE);
		treeCom.setLayout(new GridLayout());
		treeCom.setLayoutData(new GridData(GridData.FILL_BOTH));

		tree = new Tree(treeCom, SWT.SINGLE | SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_BOTH));
		tree.addSelectionListener(new SelectionAdapter() {
			// Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] tis = tree.getSelection();
				if (tis == null || tis.length == 0)
					return;
				targetText.setText(tis[0].getText());
				setPageComplete(validatePage());
			}
		});

		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(final Event event) {
				final TreeItem root = (TreeItem) event.item;
				TreeItem[] items = root.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData() != null)
						return;
					items[i].dispose();
				}
				File file = (File) root.getData();
				File[] files = file.listFiles();
				if (files == null)
					return;
				
				Arrays.sort(files, new Comparator<File>() {
				    public int compare( File a, File b ) {
				    	return a.getName().compareTo( b.getName() );
				    }
				} );

				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith(".")) continue;
					
					if (files[i].isDirectory()) {
						TreeItem item = new TreeItem(root, 0);
						item.setText(files[i].getName());
						item.setData(files[i]);
						item.setImage(RDTPluginImage.IMG_OBJS_SEARCHFOLDER);
						if(files[i].isDirectory()){
							new TreeItem(item, 0);
						}
					}
				}
			}
		});

		Group group2 = new Group(c, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		group2.setLayout(layout);
		group2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group2.setText("Active Target");

		targetText = new Text(group2, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		data.horizontalSpan = 2;
		targetText.setLayoutData(data);
		targetText.setEnabled(false);

		clearButton = new Button(group2, SWT.PUSH);
		clearButton.setText("Clear");

		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				targetText.setText("");
				setPageComplete(validatePage());
			}
		});
		setPageComplete(validatePage());
		setControl(c);
	}
	
	// Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			initData();
		}
	}
	
	private void initData(){
		String path = "";
		if (null != rtPath && !"".equals(rtPath.trim())) {
			path = rtPath;
		} else {
			path = getDialogSettings().get("RT_THREAD");
		}
		if (null != path && !"".equals(path)) {
			pathText.setText(path + "/bsp");
			updateTreeItems(path);
			TreeItem item = searchTreeItem(tree.getItems(), "bsp");
			if (null != item)
				tree.select(item);
		}
	}
	
	private TreeItem searchTreeItem(TreeItem[] items, String name){
		for(int i = 0; i < items.length; i++){
			if(name.equals(items[i].getText())){
				return items[i];
			}
			if(0 < items[i].getItems().length){
				searchTreeItem(items[i].getItems(), name);
			}
		}
		return null;
	}

	private void createBrowserEntryArea(Composite composite) {
		Label targetLabel = new Label(composite, SWT.NONE);
		targetLabel.setText("RT-Thread Root Folder:");

		pathText = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		data.horizontalSpan = 2;
		pathText.setLayoutData(data);
		pathText.setEnabled(false);

		// browse button
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Change Root");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});

		// createFileSystemSelection(composite);

		composite.layout();
	}
	
	private boolean validatePage(){
		
		if(!"".equals(getTarget()))
			return true;
		
		return false;
	}

	/**
	 * Create the file system selection area.
	 * 
	 * @param composite
	 */
	private void createFileSystemSelection(Composite composite) {

		// Always use the default if that is all there is.
		if (FileSystemSupportRegistry.getInstance().hasOneFileSystem()) {
			return;
		}

		new Label(composite, SWT.NONE);

		fileSystemSelectionArea = new FileSystemSelectionArea();
		fileSystemSelectionArea.createContents(composite);
	}

	/**
	 * Open an appropriate directory browser
	 */
	private void handleLocationBrowseButtonPressed() {

		String selectedDirectory = null;
		String dirName = getPathFromLocationField();

		if (!dirName.equals(IDEResourceInfoUtils.EMPTY_STRING)) {
			IFileInfo info;
			info = IDEResourceInfoUtils.getFileInfo(dirName);

			if (info == null || !(info.exists()))
				dirName = IDEResourceInfoUtils.EMPTY_STRING;
		} else {
			String value = getDialogSettings().get(SAVED_LOCATION_ATTR);
			if (value != null) {
				dirName = value;
			}
		}

		FileSystemConfiguration config = getSelectedConfiguration();
		if (config == null || config.equals(FileSystemSupportRegistry.getInstance().getDefaultConfiguration())) {
			DirectoryDialog dialog = new DirectoryDialog(pathText.getShell(), SWT.SHEET);
			dialog.setMessage(IDEWorkbenchMessages.ProjectLocationSelectionDialog_directoryLabel);

			dialog.setFilterPath(dirName);

			selectedDirectory = dialog.open();

		} else {
			URI uri = getSelectedConfiguration().getContributor().browseFileSystem(dirName, browseButton.getShell());
			if (uri != null)
				selectedDirectory = uri.toString();
		}

		if (selectedDirectory != null) {
			updateLocationField(selectedDirectory);
			updateTreeItems(selectedDirectory);
			getDialogSettings().put(SAVED_LOCATION_ATTR, selectedDirectory);
		}
	}

	/**
	 * @param selectedDirectory
	 */
	private void updateTreeItems(String selectedDirectory) {
		if(null == selectedDirectory || "".equals(selectedDirectory.trim()))
			return;
		File file = new File(selectedDirectory);
		File[] files = file.listFiles();
		tree.removeAll();
		
		Arrays.sort(files, new Comparator<File>() {
		    public int compare( File a, File b ) {
		    	return a.getName().compareTo( b.getName() );
		    }
		} );

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith(".")) continue;
			
			if (files[i].isDirectory()) {
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText(files[i].getName());
				item.setData(files[i]);
				item.setImage(RDTPluginImage.IMG_OBJS_SEARCHFOLDER);
				if(files[i].isDirectory()){
					new TreeItem(item, SWT.NONE);
				}
			}
		}
	}

	/**
	 * Update the location field based on the selected path.
	 * 
	 * @param selectedPath
	 */
	private void updateLocationField(String selectedPath) {
		pathText.setText(TextProcessor.process(selectedPath));
	}

	/**
	 * Return the selected contributor
	 * 
	 * @return FileSystemConfiguration or <code>null</code> if it cannot be
	 *         determined.
	 */
	private FileSystemConfiguration getSelectedConfiguration() {
		if (fileSystemSelectionArea == null) {
			return FileSystemSupportRegistry.getInstance().getDefaultConfiguration();
		}

		return fileSystemSelectionArea.getSelectedConfiguration();
	}

	/**
	 * Return the path on the location field.
	 * 
	 * @return String
	 */
	private String getPathFromLocationField() {
		URI fieldURI;
		try {
			fieldURI = new URI(pathText.getText());
		} catch (URISyntaxException e) {
			return pathText.getText();
		}
		return fieldURI.getPath();
	}

}
