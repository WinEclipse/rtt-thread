package org.rtthread.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Image
 * @author RDT Team
 * @date 2011-8-28
 */
public class RDTPluginImage {
	
	public static final Image IMG_OBJS_SEARCHFOLDER;
	public static final Image IMG_OBJS_SEARCHPROJECT;
	public static final Image IMG_OBJS_VARIABLE;
	
	static{
		String iconPath = "icons/";
		String prefixObj = iconPath + "obj16/";
		IMG_OBJS_SEARCHFOLDER = createImageDescriptor(prefixObj + "fldr_obj.gif").createImage();
		IMG_OBJS_SEARCHPROJECT = createImageDescriptor(prefixObj + "cprojects.gif").createImage();
		IMG_OBJS_VARIABLE = createImageDescriptor(prefixObj + "variable_obj.gif").createImage();
	}

	private static ImageDescriptor createImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(RDTPlugin.PLUGIN_ID, path);
	}
	
}
