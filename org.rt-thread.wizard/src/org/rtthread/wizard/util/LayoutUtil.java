package org.rtthread.wizard.util;

import org.eclipse.swt.layout.GridLayout;

/**
 * Layout Utilities
 * 
 * @author RDT Team
 * 
 */
public class LayoutUtil {
	public static GridLayout createCompactGridLayout(int r_ColumnCount) {
		GridLayout t_GridLayout = new GridLayout(r_ColumnCount, false);
		t_GridLayout.marginHeight = 0;
		t_GridLayout.marginWidth = 0;
		t_GridLayout.horizontalSpacing = 0;
		t_GridLayout.verticalSpacing = 0;

		return t_GridLayout;
	}
}