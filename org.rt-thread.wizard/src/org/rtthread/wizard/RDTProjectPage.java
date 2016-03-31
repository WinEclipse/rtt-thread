package org.rtthread.wizard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.ui.newui.PageLayout;
import org.eclipse.cdt.ui.wizards.CWizardHandler;
import org.eclipse.cdt.ui.wizards.IWizardItemsListListener;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Main Wizard
 * 
 * @author RDT Team
 * @date 2011-8-28
 */
public class RDTProjectPage extends WizardNewProjectCreationPage implements IWizardItemsListListener {

	// widgets
	private Tree tree;
	private Composite right;
	private Label right_label;
	private Table table;
	private static final String SAVED_RTTHREAD = "RT_THREAD";
	
	public CWizardHandler h_selected = null;

	/**
	 * @param pageName
	 */
	public RDTProjectPage(String pageName) {
		super(pageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org
	 * .eclipse.swt.widgets.Composite)
	 */
	// Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		createDynamicGroup((Composite) getControl());
		initDate();
		handleSelection(updateData());
		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
	}

	private TreeItem updateData() {
		TreeItem[] selection = tree.getSelection();
		TreeItem selectedItem = selection.length > 0 ? selection[0] : null;
		String savedLabel = selectedItem != null ? selectedItem.getText() : null;
		String savedParentLabel = getParentText(selectedItem);
		if (tree.getItemCount() > 0) {
			TreeItem target = null;
			// try to search item which was selected before
			if (savedLabel != null) {
				target = findItem(tree, savedLabel, savedParentLabel);
			}
			if (target == null) {
				target = tree.getItem(0);
				if (target.getItemCount() != 0)
					target = target.getItem(0);
			}
			tree.setSelection(target);
			return target;
		}
		return null;
	}

	private void initDate() {
		List<String> items = new ArrayList<String>();
		items.add(RDTWizardPluginMessage.RDTMainWizardPage_CanExecuteFile);
		// items.add(RDTPluginMessage.RDTMainWizardPage_AppModule);
		// items.add(RDTPluginMessage.RDTMainWizardPage_ExtensionsModule);
		addItemsTotree(tree, items);

	}

	private void createDynamicGroup(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(2, true));

		Label l1 = new Label(c, SWT.NONE);
		l1.setText("Project type:"); //$NON-NLS-1$
		l1.setFont(parent.getFont());
		l1.setLayoutData(new GridData(GridData.BEGINNING));

		right_label = new Label(c, SWT.NONE);
		right_label.setFont(parent.getFont());
		right_label.setLayoutData(new GridData(GridData.BEGINNING));
		right_label.setText("Toolchains:");

		tree = new Tree(c, SWT.SINGLE | SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.addSelectionListener(new SelectionAdapter() {
			//Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] tis = tree.getSelection();
				if (tis == null || tis.length == 0)
					return;
				handleSelection(updateData());
				setPageComplete(validatePage());
			}
		});
		tree.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			//Override
			public void getName(AccessibleEvent e) {
				for (int i = 0; i < tree.getItemCount(); i++) {
					if (tree.getItem(i).getText().compareTo(e.result) == 0)
						return;
				}
				e.result = "Project type:"; //$NON-NLS-1$
			}
		});
		right = new Composite(c, SWT.NONE);
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		right.setLayout(new PageLayout());

	}

	public URI getProjectLocation() {
		return useDefaults() ? null : getLocationURI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#validatePage()
	 */
	// Override
	protected boolean validatePage() {
		setMessage(null);
		if(null == table){
			return false;
		}
		if(1 != table.getSelectionCount()){
			setErrorMessage("Toolchains cannnot be null!");
			return false;
		}
		if (!super.validatePage())
			return false;
		return super.validatePage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.ui.wizards.IWizardItemsListListener#toolChainListChanged
	 * (int)
	 */
	// Override
	public void toolChainListChanged(int count) {
		setPageComplete(validatePage());
		getWizard().getContainer().updateButtons();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.ui.wizards.IWizardItemsListListener#isCurrent()
	 */
	// Override
	public boolean isCurrent() {
		return isCurrentPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.ui.wizards.IWizardItemsListListener#filterItems(java.
	 * util.List)
	 */
	// Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List filterItems(List items) {
		return items;
	}

	private static String getParentText(TreeItem item) {
		if (item == null || item.getParentItem() == null)
			return ""; //$NON-NLS-1$
		return item.getParentItem().getText();
	}

	private static TreeItem findItem(Tree tree, String label, String parentLabel) {
		for (TreeItem item : tree.getItems()) {
			TreeItem foundItem = findTreeItem(item, label, parentLabel);
			if (foundItem != null)
				return foundItem;
		}
		return null;
	}

	private static TreeItem findTreeItem(TreeItem item, String label, String parentLabel) {
		if (item.getText().equals(label) && getParentText(item).equals(parentLabel))
			return item;

		for (TreeItem child : item.getItems()) {
			TreeItem foundItem = findTreeItem(child, label, parentLabel);
			if (foundItem != null)
				return foundItem;
		}
		return null;
	}

	private static void addItemsTotree(Tree tree, List<String> items) {
		for (String str : items) {
			TreeItem ti = new TreeItem(tree, SWT.NONE);
			ti.setText(TextProcessor.process(str));
			ti.setImage(RDTPluginImage.IMG_OBJS_SEARCHFOLDER);
		}
	}

	/**
	 * Called when user selects corresponding item in wizard tree
	 * 
	 * @parame pane - parent for handler-specific data
	 */
	public void handleSelection(TreeItem target) {
		if (table == null) {
			table = new Table(right, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText("---"); //$NON-NLS-1$
			table.select(0);
		}
		table.setVisible(true);
		right.layout();
		updateTable(target);
	}

	/**
	 * @param target
	 */
	private void updateTable(TreeItem target) {
		String str = target.getText();
		List<String> input = new ArrayList<String>();
		if (RDTWizardPluginMessage.RDTMainWizardPage_CanExecuteFile.equals(str.trim())) {
			input.add(RDTWizardPluginMessage.RDTMainWizardPage_CanExecuteFile_GCCBuilderToolChains);
			// input.add(RDTPluginMessage.RDTMainWizardPage_CanExecuteFile_ARMCCBuilderToolChains);
			// input.add(RDTPluginMessage.RDTMainWizardPage_CanExecuteFile_IARBuilderToolChains);
		} else if (RDTWizardPluginMessage.RDTMainWizardPage_AppModule.equals(str.trim())) {

		} else if (RDTWizardPluginMessage.RDTMainWizardPage_ExtensionsModule.equals(str.trim())) {

		}
		if (input.isEmpty())
			handleUnSelection();
		else {
			table.removeAll();
			for (String item : input) {
				TableItem ti = new TableItem(table, SWT.NONE);
				ti.setText(item);
			}
			table.select(0);
		}

	}

	/**
	 * Called when user leaves corresponding item in wizard tree
	 */
	public void handleUnSelection() {
		if (table != null) {
			table.setVisible(false);
		}
		right.layout();
	}

	//Override
//	public IWizardPage getNextPage() {
//		String rtPath = RDTPlugin.getDefault().getPreferenceStore().getString(RDTPlugin.PLUGIN_ID + "rtroot");
//		String rtroot = getDialogSettings().get(SAVED_RTTHREAD);
//		if((null == rtPath || "".equals(rtPath)) && (null == rtroot || "".equals(rtroot)))
//			return ((RTProjectWizard)getWizard()).getRTpage();
//		return ((RTProjectWizard)getWizard()).getTargetPage();
//	}
//	
}
