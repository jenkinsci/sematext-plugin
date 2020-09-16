package com.sematext.jenkins.plugins.utils;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public final class LogUtils {
  public static void severe(Logger logger, Throwable e, String message) {
    if (message == null) {
      message = e != null ? "Unexpected error occurred" : "";
    }
    if (!message.isEmpty()) {
      logger.severe(message);
    }
    if (e != null) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      logger.finer(message + ": " + sw.toString());
    }
  }
}
