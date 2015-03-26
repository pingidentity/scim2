/*
 * Copyright 2011-2015 UnboundID Corp.
 * All Rights Reserved.
 */

package com.unboundid.scim2.utils;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.unboundid.scim2.utils.StaticUtils.*;



/**
 * This class provides a means of enabling and configuring debugging in the
 * SCIM SDK.
 * <BR><BR>
 * Access to debug information can be enabled through applications that use the
 * SDK by calling the {@link Debug#setEnabled} methods, or it can also be
 * enabled without any code changes through the use of system properties.  In
 * particular, the {@link Debug#PROPERTY_DEBUG_ENABLED},
 * {@link Debug#PROPERTY_DEBUG_LEVEL}, and {@link Debug#PROPERTY_DEBUG_TYPE}
 * properties may be used to control debugging without the need to alter any
 * code within the application that uses the SDK.
 * <BR><BR>
 * The SCIM SDK debugging subsystem uses the Java logging framework available
 * through the {@code java.util.logging} package with a logger name of
 * "{@code com.unboundid.scim2.sdk}".  The {@link Debug#getLogger} method may
 * be used to access the logger instance used by the SCIM SDK.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process that may be used to enable
 * debugging within the SCIM SDK and write information about all messages with
 * a {@code WARNING} level or higher to a file named "/tmp/test.log":
 * <PRE>
 *   Debug.setEnabled(true);
 *   Logger logger = Debug.getLogger();
 *
 *   FileHandler fileHandler = new FileHandler("/tmp/test.log");
 *   fileHandler.setLevel(Level.WARNING);
 *   logger.addHandler(fileHandler);
 * </PRE>
 */
public final class Debug
       implements Serializable
{
  /**
   * The name of the system property that will be used to enable debugging in
   * the UnboundID SCIM SDK for Java.  The fully-qualified name for this
   * property is "{@code com.unboundid.scim2.sdk.debug.enabled}".  If it is set,
   * then it should have a value of either "true" or "false".
   */
  public static final String PROPERTY_DEBUG_ENABLED =
       "com.unboundid.scim2.sdk.debug.enabled";



  /**
   * The name of the system property that may be used to indicate whether stack
   * trace information for the thread calling the debug method should be
   * included in debug log messages.  The fully-qualified name for this property
   * is "{@code com.unboundid.scim2.sdk.debug.includeStackTrace}".  If it is
   * set, then it should have a value of either "true" or "false".
   */
  public static final String PROPERTY_INCLUDE_STACK_TRACE =
       "com.unboundid.scim2.sdk.debug.includeStackTrace";



  /**
   * The name of the system property that will be used to set the initial level
   * for the debug logger.  The fully-qualified name for this property is
   * "{@code com.unboundid.scim2.sdk.debug.level}".  If it is set, then it
   * should be one of the strings "{@code SEVERE}", "{@code WARNING}",
   * "{@code INFO}", "{@code CONFIG}", "{@code FINE}", "{@code FINER}", or
   * "{@code FINEST}".
   */
  public static final String PROPERTY_DEBUG_LEVEL =
       "com.unboundid.scim2.sdk.debug.level";



  /**
   * The name of the system property that will be used to indicate that
   * debugging should be enabled for specific types of messages.  The
   * fully-qualified name for this property is
   * "{@code com.unboundid.scim2.sdk.debug.type}". If it is set, then it should
   * be a comma-delimited list of the names of the desired debug types.  See the
   * {@link DebugType} enum for the available debug types.
   */
  public static final String PROPERTY_DEBUG_TYPE =
       "com.unboundid.scim2.sdk.debug.type";



  /**
   * The name that will be used for the Java logger that will actually handle
   * the debug messages if debugging is enabled.
   */
  public static final String LOGGER_NAME = "com.unboundid.scim2.sdk";



  /**
   * The logger that will be used to handle the debug messages if debugging is
   * enabled.
   */
  private static final Logger logger = Logger.getLogger(LOGGER_NAME);



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 1481879450652966238L;



  // Indicates whether any debugging is currently enabled for the SDK.
  private static boolean debugEnabled;

  // Indicates whether to capture a thread stack trace whenever a debug message
  // is logged.
  private static boolean includeStackTrace;

  // The set of debug types for which debugging is enabled.
  private static EnumSet<DebugType> debugTypes;



  static
  {
    initialize(System.getProperties());
  }



  /**
   * Prevent this class from being instantiated.
   */
  private Debug()
  {
    // No implementation is required.
  }



  /**
   * Initializes this debugger with the default settings.  Debugging will be
   * disabled, the set of debug types will include all types, and the debug
   * level will be "ALL".
   */
  public static void initialize()
  {
    includeStackTrace = false;
    debugEnabled      = false;
    debugTypes        = EnumSet.allOf(DebugType.class);

    logger.setLevel(Level.ALL);
  }



  /**
   * Initializes this debugger with settings from the provided set of
   * properties.  Any debug setting that isn't configured in the provided
   * properties will be initialized with its default value.
   *
   * @param  properties  The set of properties to use to initialize this
   *                     debugger.
   */
  public static void initialize(final Properties properties)
  {
    // First, apply the default values for the properties.
    initialize();
    if ((properties == null) || properties.isEmpty())
    {
      // No properties were provided, so we don't need to do anything.
      return;
    }

    final String enabledProp = properties.getProperty(PROPERTY_DEBUG_ENABLED);
    if ((enabledProp != null) && (enabledProp.length() > 0))
    {
      if (enabledProp.equalsIgnoreCase("true"))
      {
        debugEnabled = true;
      }
      else if (enabledProp.equalsIgnoreCase("false"))
      {
        debugEnabled = false;
      }
      else
      {
        throw new IllegalArgumentException("Invalid value '" + enabledProp +
                                           "' for property " +
                                           PROPERTY_DEBUG_ENABLED +
                                           ".  The value must be either " +
                                           "'true' or 'false'.");
      }
    }

    final String stackProp =
         properties.getProperty(PROPERTY_INCLUDE_STACK_TRACE);
    if ((stackProp != null) && (stackProp.length() > 0))
    {
      if (stackProp.equalsIgnoreCase("true"))
      {
        includeStackTrace = true;
      }
      else if (stackProp.equalsIgnoreCase("false"))
      {
        includeStackTrace = false;
      }
      else
      {
        throw new IllegalArgumentException("Invalid value '" + stackProp +
                                           "' for property " +
                                           PROPERTY_INCLUDE_STACK_TRACE +
                                           ".  The value must be either " +
                                           "'true' or 'false'.");
      }
    }

    final String typesProp = properties.getProperty(PROPERTY_DEBUG_TYPE);
    if ((typesProp != null) && (typesProp.length() > 0))
    {
      debugTypes = EnumSet.noneOf(DebugType.class);
      final StringTokenizer t = new StringTokenizer(typesProp, ", ");
      while (t.hasMoreTokens())
      {
        final String debugTypeName = t.nextToken();
        final DebugType debugType = DebugType.forName(debugTypeName);
        if (debugType == null)
        {
          // Throw a runtime exception to indicate that the debug type is
          // invalid.
          throw new IllegalArgumentException("Invalid value '" + debugTypeName +
                      "' for property " + PROPERTY_DEBUG_TYPE +
                      ".  Allowed values include:  " +
                      DebugType.getTypeNameList() + '.');
        }
        else
        {
          debugTypes.add(debugType);
        }
      }
    }

    final String levelProp = properties.getProperty(PROPERTY_DEBUG_LEVEL);
    if ((levelProp != null) && (levelProp.length() > 0))
    {
      logger.setLevel(Level.parse(levelProp));
    }
  }



  /**
   * Retrieves the logger that will be used to write the debug messages.
   *
   * @return  The logger that will be used to write the debug messages.
   */
  public static Logger getLogger()
  {
    return logger;
  }



  /**
   * Indicates whether any form of debugging is enabled.
   *
   * @return  {@code true} if debugging is enabled, or {@code false} if not.
   */
  public static boolean debugEnabled()
  {
    return debugEnabled;
  }



  /**
   * Indicates whether debugging is enabled for messages of the specified debug
   * type.
   *
   * @param  debugType  The debug type for which to make the determination.
   *
   * @return  {@code true} if debugging is enabled for messages of the specified
   *          debug type, or {@code false} if not.
   */
  public static boolean debugEnabled(final DebugType debugType)
  {
    return (debugEnabled && debugTypes.contains(debugType));
  }



  /**
   * Specifies whether debugging should be enabled.  If it should be, then it
   * will be enabled for all debug types.
   *
   * @param  enabled  Specifies whether debugging should be enabled.
   */
  public static void setEnabled(final boolean enabled)
  {
    debugTypes   = EnumSet.allOf(DebugType.class);
    debugEnabled = enabled;
  }



  /**
   * Specifies whether debugging should be enabled.  If it should be, then it
   * will be enabled for all debug types in the provided set.
   *
   * @param  enabled  Specifies whether debugging should be enabled.
   * @param  types    The set of debug types that should be enabled.  It may be
   *                  {@code null} or empty to indicate that it should be for
   *                  all debug types.
   */
  public static void setEnabled(final boolean enabled,
                                final Set<DebugType> types)
  {
    if ((types == null) || types.isEmpty())
    {
      debugTypes = EnumSet.allOf(DebugType.class);
    }
    else
    {
      debugTypes = EnumSet.copyOf(types);
    }

    debugEnabled = enabled;
  }



  /**
   * Indicates whether log messages should include a stack trace of the thread
   * that invoked the debug method.
   *
   * @return  {@code true} if log messages should include a stack trace of the
   *          thread that invoked the debug method, or {@code false} if not.
   */
  public static boolean includeStackTrace()
  {
    return includeStackTrace;
  }



  /**
   * Specifies whether log messages should include a stack trace of the thread
   * that invoked the debug method.
   *
   * @param  includeStackTrace  Indicates whether log messages should include a
   *                            stack trace of the thread that invoked the debug
   *                            method.
   */
  public static void setIncludeStackTrace(final boolean includeStackTrace)
  {
    Debug.includeStackTrace = includeStackTrace;
  }



  /**
   * Retrieves the set of debug types that will be used if debugging is enabled.
   *
   * @return  The set of debug types that will be used if debugging is enabled.
   */
  public static EnumSet<DebugType> getDebugTypes()
  {
    return debugTypes;
  }



  /**
   * Writes debug information about the provided exception, if appropriate.  If
   * it is to be logged, then it will be sent to the underlying logger using the
   * {@code WARNING} level.
   *
   * @param  t  The exception for which debug information should be written.
   */
  public static void debugException(final Throwable t)
  {
    if (debugEnabled && debugTypes.contains(DebugType.EXCEPTION))
    {
      debugException(Level.WARNING, t);
    }
  }



  /**
   * Writes debug information about the provided exception, if appropriate.
   *
   * @param  l  The log level that should be used for the debug information.
   * @param  t  The exception for which debug information should be written.
   */
  public static void debugException(final Level l, final Throwable t)
  {
    if (debugEnabled && debugTypes.contains(DebugType.EXCEPTION))
    {
      final StringBuilder buffer = new StringBuilder();
      addCommonHeader(buffer, l);
      buffer.append("caughtException=\"");
      getStackTrace(t, buffer);
      buffer.append('"');

      logger.log(l, buffer.toString(), t);
    }
  }



  /**
   * Writes debug information about a coding error detected in the use of the
   * SCIM SDK.  If it is to be logged, then it will be sent to the underlying
   * logger using the {@code SEVERE} level.
   *
   * @param  t  The {@code Throwable} object that was created and will be thrown
   *            as a result of the coding error.
   */
  public static void debugCodingError(final Throwable t)
  {
    if (debugEnabled && debugTypes.contains(DebugType.CODING_ERROR))
    {
      final StringBuilder buffer = new StringBuilder();
      addCommonHeader(buffer, Level.SEVERE);
      buffer.append("codingError=\"");
      getStackTrace(t, buffer);
      buffer.append('"');

      logger.log(Level.SEVERE, buffer.toString());
    }
  }



  /**
   * Writes a generic debug message, if appropriate.
   *
   * @param  l  The log level that should be used for the debug information.
   * @param  t  The debug type to use to determine whether to write the message.
   * @param  m  The message to be written.
   */
  public static void debug(final Level l, final DebugType t, final String m)
  {
    if (debugEnabled && debugTypes.contains(t))
    {
      final StringBuilder buffer = new StringBuilder();
      addCommonHeader(buffer, l);
      buffer.append("message=\"");
      buffer.append(m);
      buffer.append('"');

      logger.log(l, buffer.toString());
    }
  }



  /**
   * Writes a generic debug message, if appropriate.
   *
   * @param  l  The log level that should be used for the debug information.
   * @param  t  The debug type to use to determine whether to write the message.
   * @param  m  The message to be written.
   * @param  e  An exception to include with the log message.
   */
  public static void debug(final Level l, final DebugType t, final String m,
                           final Throwable e)
  {
    if (debugEnabled && debugTypes.contains(t))
    {
      final StringBuilder buffer = new StringBuilder();
      addCommonHeader(buffer, l);
      buffer.append("message=\"");
      buffer.append(m);
      buffer.append('"');
      buffer.append(" exception=\"");
      getStackTrace(e, buffer);
      buffer.append('"');

      logger.log(l, buffer.toString(), e);
    }
  }



  /**
   * Writes common header information to the provided buffer.  It will include
   * the thread ID, name, and caller stack trace (optional), and it will be
   * followed by a trailing space.
   *
   * @param  buffer  The buffer to which the information should be appended.
   * @param  level   The log level for the message that will be written.
   */
  private static void addCommonHeader(final StringBuilder buffer,
                                      final Level level)
  {
    buffer.append("level=\"");
    buffer.append(level.getName());
    buffer.append("\" threadID=");
    buffer.append(Thread.currentThread().getId());
    buffer.append(" threadName=\"");
    buffer.append(Thread.currentThread().getName());

    if (includeStackTrace)
    {
      buffer.append("\" calledFrom=\"");

      boolean appended   = false;
      boolean foundDebug = false;
      for (final StackTraceElement e : Thread.currentThread().getStackTrace())
      {
        final String className = e.getClassName();
        if (className.equals(Debug.class.getName()))
        {
          foundDebug = true;
        }
        else if (foundDebug)
        {
          if (appended)
          {
            buffer.append(" / ");
          }
          appended = true;

          buffer.append(e.getMethodName());
          buffer.append('(');
          buffer.append(e.getFileName());

          final int lineNumber = e.getLineNumber();
          if (lineNumber > 0)
          {
            buffer.append(':');
            buffer.append(lineNumber);
          }
          else if (e.isNativeMethod())
          {
            buffer.append(":native");
          }

          buffer.append(')');
        }
      }
    }

    buffer.append("\" revision=");
    buffer.append(Version.REVISION_NUMBER);
    buffer.append(' ');
  }
}
