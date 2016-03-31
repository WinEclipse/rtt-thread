package org.rtthread.wizard;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.ui.newui.UIMessages;

/**
 * Configuration Holder
 * 
 * @author RDT Team
 * @date 2011-9-5
 */
public class CfgHolder {
	
	private static final String DELIMITER = "_with_";  //$NON-NLS-1$
	private static final String LBR = " (v ";  //$NON-NLS-1$
	private static final String RBR = ")";  //$NON-NLS-1$

	private String name;
	private IConfiguration cfg;
	private IToolChain tc;

	/**
	 * @param toolChain
	 * @param iConfiguration
	 */
	public CfgHolder(IToolChain _tc, IConfiguration _cfg) {
		tc = _tc;
		cfg = _cfg;
		if (cfg == null) {
			if (tc == null || tc.getParent() == null)
				name = UIMessages.getString("StdProjectTypeHandler.2"); //$NON-NLS-1$
			else
				name = tc.getParent().getName();
		} else
			name = cfg.getName();
	}

	public boolean isSystem() {
		if (cfg == null)
			return false;
		return (cfg.isSystemObject());
	}

	public boolean isSupported() {
		if (cfg == null)
			return true;
		return (cfg.isSupported());
	}

	/**
	 * Creates array of holders on a basis of configurations array.
	 * 
	 * @param cfgs
	 * @return
	 */
	public static CfgHolder[] cfgs2items(IConfiguration[] cfgs) {
		CfgHolder[] its = new CfgHolder[cfgs.length];
		for (int i = 0; i < cfgs.length; i++) {
			its[i] = new CfgHolder(cfgs[i].getToolChain(), cfgs[i]);
		}
		return its;
	}
	
	/**
	 * Checks whether names are unique
	 * 
	 * @param its
	 * @return
	 */
	
    public static boolean hasDoubles(CfgHolder[] its) {
   		for (int i=0; i<its.length; i++) {
       		String s = its[i].name;
       		for (int j=0; j<its.length; j++) {
       			if (i == j) continue;
       			if (s.equals(its[j].name)) 
       				return true;
       		}
       	}
   		return false;
    }
    
    public IConfiguration getTcCfg() {
    	if (tc != null)
    		return tc.getParent();
    	return null;
    }

    /**
     * Returns corresponding project type
     * obtained either from configuration
     * (if any) or from toolchain. 
     * 
     * @return projectType
     */
    
    public IProjectType getProjectType() {
    	if (cfg != null)
    		return cfg.getProjectType();
    	if (tc != null && tc.getParent() != null)
    		return tc.getParent().getProjectType();
    	return null;
    }
	
	/**
     * Makes configuration's names unique.
     * Adds either version number or toolchain name. 
     * If it does not help, simply adds index.
     * 
     * @param its - list of items.
     * @return the same list with unique names. 
     */
    
    public static CfgHolder[] unique(CfgHolder[] its) {
    	// if names are not unique, add version name
    	if (hasDoubles(its)) {
       		for (int k=0; k<its.length; k++) {
       			if (its[k].tc != null) {
       				String ver = ManagedBuildManager.getVersionFromIdAndVersion(its[k].tc.getId());
       				if(ver != null)
       					its[k].name = its[k].name + LBR + ver + RBR;
       			}
       		}
		}
    	// if names are still not unique, add toolchain name
    	if (hasDoubles(its)) {
       		for (int k=0; k<its.length; k++) {
       			String s = its[k].name;
       			int x = s.indexOf(LBR);
       			if (x >= 0) 
       				s = s.substring(0, x); 
       			IToolChain tc = its[k].tc;
       			if (tc == null && its[k].cfg != null) 
       				tc = its[k].cfg.getToolChain();
       			if (tc != null)
       				its[k].name = s + DELIMITER + tc.getUniqueRealName();
       		}
		}
    	// if names are still not unique, add index
    	if (hasDoubles(its)) {
       		for (int k=0; k<its.length; k++) {
       			its[k].name = its[k].name + k;
       		}
		}
    	return its;
    }
    
    /**
     * Reorders selected configurations in "physical" order.
     * Although toolchains are displayed in alphabetical 
     * order in Wizard, it's required to create corresponding
     * configurations in the same order as they are listed
     * in xml file, inside of single project type.   
     * 
     * @param its - items in initial order.
     * @return - items in "physical" order.
     */
    
    public static CfgHolder[] reorder(CfgHolder[] its) {
    	ArrayList<CfgHolder> ls = new ArrayList<CfgHolder>(its.length);
    	boolean found = true;
    	while (found) {
			found = false;
    		for (int i=0; i<its.length; i++) {
    			if (its[i] == null) 
    				continue;
    			found = true;
    			IProjectType pt = its[i].getProjectType();
    			if (pt == null) {
					ls.add(its[i]);
					its[i] = null;
    				continue;
    			}
    			IConfiguration[] cfs = pt.getConfigurations();
    			for (int j=0; j<cfs.length; j++) {
    				for (int k=0; k<its.length; k++) {
    					if (its[k] == null) 
    						continue;
    					if (cfs[j].equals(its[k].getTcCfg())) {
    						ls.add(its[k]);
    						its[k] = null;
    					}
    				}
    			}
    		}
    	}
    	return ls.toArray(new CfgHolder[ls.size()]); 
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cfg
	 */
	public IConfiguration getConfiguration() {
		return cfg;
	}

	/**
	 * @param cfg the cfg to set
	 */
	public void setCfg(IConfiguration cfg) {
		this.cfg = cfg;
	}

	/**
	 * @return the tc
	 */
	public IToolChain getTc() {
		return tc;
	}

	/**
	 * @param tc the tc to set
	 */
	public void setTc(IToolChain tc) {
		this.tc = tc;
	}

}
