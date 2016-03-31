package org.rtthread.wizard.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Log Utilities 
 * 
 * @author RDT Team
 */
public class LogUtil {

	public static void logError(Throwable t) {
		logError(null, t.getMessage(), t);
	}

	public static void logError(String bundleID, Throwable t) {
		logError(bundleID, t.getMessage(), t);
	}

	public static void logError(String bundleID, String message, Throwable t) {
		log(bundleID, message, t, Status.ERROR, Status.OK);
	}

	public static void logWarning(String message) {
		log(null, message, null, Status.WARNING, Status.OK);
	}
	
	public static void logInfo(String message) {
		log(null, message, null, Status.INFO, Status.OK);
	}

	public static void logError(String bundleID, String message) {
		logError(bundleID, message, null);
	}

	public static void log(String bundleID, String message, Throwable t, int serverity, int code) {
		if (bundleID == null) {
			bundleID = "org.rt-thread.wizard";
		}
		Platform.getLog(Platform.getBundle(bundleID)).log(new Status(serverity, bundleID, code, message, t));
	}
}
