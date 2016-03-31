package org.rtthread.wizard.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rtthread.wizard.RDTPlugin;
import org.rtthread.wizard.RDTWizardPluginMessage;

/**
 * PreferencePage
 * 
 * @author RDT Team
 * @date 2011-9-13
 */
@SuppressWarnings("restriction")
public class RTTPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String RTT_ROOT = "RTTRoot";
	private static final String GCC_PATH = "GCCPath";
	// private static final String ARMCC_PATH = "ARMCCPath";
	// private static final String IAR_PATH = "IARPath";

	private DirectoryFieldEditor rttDirectoryEditor;
	private DirectoryFieldEditor gccDirectoryEditor;
	// private DirectoryFieldEditor armccDirectoryEditor;
	// private DirectoryFieldEditor iarDirectoryEditor;

	public RTTPreferencePage() {
		super(GRID);

		setPreferenceStore(RDTPlugin.getDefault().getPreferenceStore());
		setDescription("Set RT-Thread RTOS preference");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	// Override
	protected void createFieldEditors() {
		this.rttDirectoryEditor = new DirectoryFieldEditor(RTT_ROOT, RDTWizardPluginMessage.RTPreferencePage_RTThread, getFieldEditorParent());
		addField(rttDirectoryEditor);

		/* compiler path */
		this.gccDirectoryEditor = new DirectoryFieldEditor(GCC_PATH, "GNU GCC directory", getFieldEditorParent());
		addField(gccDirectoryEditor);
		// this.armccDirectoryEditor = new DirectoryFieldEditor(ARMCC_PATH, "ARM compiler directory", getFieldEditorParent());
		// addField(armccDirectoryEditor);
		// this.iarDirectoryEditor = new DirectoryFieldEditor(IAR_PATH, "IAR compiler directory", getFieldEditorParent());
		// addField(iarDirectoryEditor);
	}

	private String getRTTRootPath() {
		return rttDirectoryEditor.getStringValue();
	}

	private String getGCCPath() {
		return gccDirectoryEditor.getStringValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	// Override
	public void init(IWorkbench workbench) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	// Override
	public boolean performOk() {
		RDTPlugin.getDefault().getPreferenceStore().setValue(RDTPlugin.PLUGIN_ID + RDTPlugin.RTT_CC_ID, "gcc");
		RDTPlugin.getDefault().getPreferenceStore().setValue(RDTPlugin.PLUGIN_ID + RDTPlugin.RTT_ROOT_ID, getRTTRootPath());
		RDTPlugin.getDefault().getPreferenceStore().setValue(RDTPlugin.PLUGIN_ID + RDTPlugin.RTT_EXEC_PATH_ID, getGCCPath());
		return super.performOk();
	}
}
