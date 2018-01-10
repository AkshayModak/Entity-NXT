package architecture.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import one.DefaultObjects;


public class DebugWrapper {
	
	private static Logger logger = LogManager.getRootLogger();
	
	private static void log(String logMessage, Level level, String className) {
		if (DefaultObjects.isNotEmpty(className)) {
			logger = LogManager.getLogger(className);
		}
		logger.log(level, logMessage);
	}
	

	public static void logError(Object errMessage, String className) {
		log(""+errMessage, Level.ERROR, className);
	}
	
	public static void logInfo(String infoMessage, String className) {
		log(infoMessage, Level.INFO, className);	
	}
	
	public static void logDebug(String debugMessage, String className) {
		log(debugMessage, Level.DEBUG, className);
	}
}
