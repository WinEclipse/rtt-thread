package org.rtthread.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * RDT Pluging Message
 * 
 * @author RDT Team
 * @date 2011-8-28
 */
public class RDTWizardPluginMessage extends NLS {

	private static final String BUNDLE_NAME = "org.rtthread.wizard.message";
	
	public static String RDTMainWizardPage_CanExecuteFile;
	public static String RDTMainWizardPage_AppModule;
	public static String RDTMainWizardPage_ExtensionsModule;
	
	public static String RDTMainWizardPage_CanExecuteFile_GCCBuilderToolChains;
	public static String RDTMainWizardPage_CanExecuteFile_ARMCCBuilderToolChains;
	public static String RDTMainWizardPage_CanExecuteFile_IARBuilderToolChains;

	
	public static String RTPreferencePage_RTThread;
	
	static{
		initializeMessages(BUNDLE_NAME, RDTWizardPluginMessage.class);
	}
	
}
