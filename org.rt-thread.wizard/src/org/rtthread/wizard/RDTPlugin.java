package org.rtthread.wizard;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RDTPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rt-thread.wizard"; //$NON-NLS-1$
	public static final String RTT_ROOT_ID = "RTT_ROOT";
	public static final String RTT_CC_ID = "RTT_CC";
	public static final String RTT_EXEC_PATH_ID = "RTT_EXEC_PATH";

	// The shared instance
	private static RDTPlugin plugin;
	
	/**
	 * The constructor
	 */
	public RDTPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	//Override
	protected void saveDialogSettings() {
		super.saveDialogSettings();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RDTPlugin getDefault() {
		return plugin;
	}
}
