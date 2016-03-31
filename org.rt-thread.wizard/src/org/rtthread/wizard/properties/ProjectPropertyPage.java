package org.rtthread.wizard.properties;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.resources.VariableDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.FileSystemSelectionArea;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;
import org.rtthread.wizard.RDTPlugin;
import org.rtthread.wizard.RDTProjectWizard;

/**
 * ProjectProperty Page
 * 
 * @author RDT Team
 * @date 2011-9-14
 */
@SuppressWarnings("restriction")
public class ProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private IProject project;
	private Text pathText;

	private Button browseButton;
	
	private String oldPath = "";

	private FileSystemSelectionArea fileSystemSelectionArea;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	// Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 500;
		gridData.heightHint = 320;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout());
		Object object = getElement().getAdapter(IProject.class);
		if ((object instanceof IProject)) {
			this.project = (IProject) object;
			Label targetLabel = new Label(composite, SWT.NONE);
			targetLabel.setText("RT-Thread Root Folder:");

			pathText = new Text(composite, SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.widthHint = 250;
			data.horizontalSpan = 2;
			pathText.setLayoutData(data);
			pathText.setEnabled(false);

			// browse button
			browseButton = new Button(composite, SWT.PUSH);
			browseButton.setText("Change Root Directory");
			browseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					handleLocationBrowseButtonPressed();
				}
			});
			ProjectDescription desc = ((Project) this.project).internalGetDescription();
			if (desc.getVariables() != null)
			{
				HashMap<String, VariableDescription> vds = desc.getVariables();
				VariableDescription vd = (VariableDescription) desc.getVariables().get(RDTPlugin.RTT_ROOT_ID);
				if (null != vd) {
					try {
						oldPath = new File(new URI(vd.getValue())).getAbsolutePath();
						pathText.setText(oldPath);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Label label = new Label(composite, SWT.NONE);
			label.setText("Error");
		}
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	//Override
	public boolean performOk() {
		boolean set_link = false;
		
		/* update variable */
		try {
			RDTProjectWizard.setEnvironmentVariables(project);
		} catch (CoreException e1) {
		}

		if(pathText.getText().equals(oldPath)){
			return true;
		}

		if (set_link)
		{
			ProjectDescription desc = ((Project) this.project).internalGetDescription();
			if (desc.getVariables() != null)
			{
				VariableDescription vd = (VariableDescription) desc.getVariables().get(RDTPlugin.RTT_ROOT_ID);
				if (null != vd) {
					File file = new File(pathText.getText());
					vd.setValue(file.toURI().toString());
					desc.setVariableDescription(RDTPlugin.RTT_ROOT_ID, vd);
					try {
						((Project) this.project).setDescription(desc, IResource.FORCE, null);
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return super.performOk();
	}

	private void handleLocationBrowseButtonPressed() {

		String selectedDirectory = null;
		String dirName = getPathFromLocationField();

		if (!dirName.equals(IDEResourceInfoUtils.EMPTY_STRING)) {
			IFileInfo info;
			info = IDEResourceInfoUtils.getFileInfo(dirName);

			if (info == null || !(info.exists()))
				dirName = IDEResourceInfoUtils.EMPTY_STRING;
		} else {
			dirName = "";
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
		}
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

}
