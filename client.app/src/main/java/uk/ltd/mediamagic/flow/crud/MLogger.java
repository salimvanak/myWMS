package uk.ltd.mediamagic.flow.crud;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MLogger {

	public static void setLevel(Level level) {
		Logger.getGlobal().setLevel(level);
		Logger.getLogger("").setLevel(level);
	}
	
	public static Logger log(Object o) {
		return log(o.getClass());
	}
	
	public static Logger log(Class<?> cls) {
		return Logger.getLogger(cls.getPackage().getName());
	}
	
  /**
   *  Creates an unformatted string for outputing data 
   * from caught exceptions.  The output will contain 
   * the message and the stack trace
   * @param e the exception to format.
   * @return an unformatted string containg the exception data.
   */
  public static String formatException(Exception e) {
    //String m = "" + e.getClass().getName() + " " + e.getMessage() + "\n";
    //for (StackTraceElement ste : e.getStackTrace()) { m += "    " + ste.toString() + "\n"; }
    //return m;
  	return format(e);
  }

  /**
   * Creates an unformatted string for outputting data 
   * from caught exceptions.  The output will contain 
   * the message and the stack trace
   * @param e the exception to format.
   * @return an unformatted string containing the exception data.
   */
  public static String format(Throwable e) {
  	if (e == null) {
  		return "Cannot build stack trace for null Throwable";
  	}
  	else {
  		StringBuilder m = new StringBuilder("").append(e.getClass().getName()).append(" ").append(e.getMessage()).append("\n");
  		m.append(format(e.getStackTrace()));
      if (e.getCause() != null) m.append(format(e.getCause()));
      return m.toString();
  	}
  }

	/**
   * Creates an formatted string for outputting data 
   * from a stack trace.
   * @param e the stack trace.
   * @return an unformatted string containing the exception data.
   */
  public static String format(StackTraceElement[] e) {
    StringBuilder m = new StringBuilder();
    for (StackTraceElement ste : e) { m.append("    at ").append(ste.toString()).append("\n"); }
    return m.toString();
  }

  public static String formatStachTrace() {
  	return format(Thread.currentThread().getStackTrace());
  }

}
