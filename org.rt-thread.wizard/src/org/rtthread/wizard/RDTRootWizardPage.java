package org.rtthread.wizard;

import java.net.URI;
import java.net.URISyntaxException;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.FileSystemSelectionArea;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * RT-Thread Root Directory Wizard
 * 
 * @author RDT Team
 * 2011-9-26
 */
public class RDTRootWizardPage extends WizardPage {
	
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;
	
	private static final String SAVED_RTTHREAD = "RT_THREAD";
	
	private FileSystemSelectionArea fileSystemSelectionArea;
	
	private Text pathText;

	private Button browseButton;
	
	private String rttPath;

	protected RDTRootWizardPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	//Override
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(1, true));
		createBrowserEntryArea(c);
		setPageComplete(validatePage());
		setControl(c);
	}
	
	private boolean validatePage() {

		if (!"".equals(getRTTPath()))
			return true;
		else
			return false;
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

		composite.layout();
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
			String value = getDialogSettings().get(SAVED_RTTHREAD);
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
			if (uri != null) {
				selectedDirectory = uri.toString();
			}

		}

		if (selectedDirectory != null) {
			updateLocationField(selectedDirectory);
			getDialogSettings().put(SAVED_RTTHREAD, selectedDirectory);
		}
		setPageComplete(validatePage());
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
	
	/**
	 * Update the location field based on the selected path.
	 * 
	 * @param selectedPath
	 */
	private void updateLocationField(String selectedPath) {
		pathText.setText(TextProcessor.process(selectedPath));
	}

	public void setRTTPath(String rttPath) {
		this.rttPath = rttPath;
	}

	public String getRTTPath() {
		return pathText.getText();
	}

}
