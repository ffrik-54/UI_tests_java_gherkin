package com.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Report {



	public static void logFailed(String msg) {
		Logger.getGlobal().log(Level.SEVERE, msg);
	}

	public static void logInfo(String msg) {
		Logger.getGlobal().log(Level.INFO, msg);
	}

	public static void logError(String msg) {
		Logger.getGlobal().log(Level.WARNING, msg);
	}
}
