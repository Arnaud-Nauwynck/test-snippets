
# Test log4j2 + Slf4j

## Base Test 

```text
        System.setProperty("log4j2.configurationFile", "log4j2.xml");
```

```text
C:\apps\jdk\jdk-17.0.1.12\bin\java.exe -javaagent:C:\apps\intellij\curr\lib\idea_rt.jar=50189:C:\apps\intellij\curr\bin -Dfile.encoding=UTF-8 -classpath C:\arn\devPerso\test-snippets\test-slf4j-log4j2\target\classes;C:\Users\arnaud\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-api\2.20.0\log4j-api-2.20.0.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-core\2.20.0\log4j-core-2.20.0.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-slf4j-impl\2.20.0\log4j-slf4j-impl-2.20.0.jar fr.an.tests.log4j2.AppMain
******** done Log4j2 getLogger
21:25:53.107 [main] INFO  fr.an.tests.log4j2.AppMain - test log4j2 INFO
21:25:53.109 [main] WARN  fr.an.tests.log4j2.AppMain - test log4j2 WARN
******** testing Slf4j ...
******** done Slf4j getLogger
21:25:53.121 [main] INFO  fr.an.tests.log4j2.AppMain - test slf4j INFO
21:25:53.122 [main] WARN  fr.an.tests.log4j2.AppMain - test slf4j WARN
***** Finished

Process finished with exit code 0
```



## Test using '-Dlog4j2.debug=true'

```text
        System.setProperty("log4j2.debug", "true");
        System.setProperty("log4j2.configurationFile", "log4j2.xml");
```


```text
C:\apps\jdk\jdk-17.0.1.12\bin\java.exe -javaagent:C:\apps\intellij\curr\lib\idea_rt.jar=50162:C:\apps\intellij\curr\bin -Dfile.encoding=UTF-8 -classpath C:\arn\devPerso\test-snippets\test-slf4j-log4j2\target\classes;C:\Users\arnaud\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-api\2.20.0\log4j-api-2.20.0.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-core\2.20.0\log4j-core-2.20.0.jar;C:\Users\arnaud\.m2\repository\org\apache\logging\log4j\log4j-slf4j-impl\2.20.0\log4j-slf4j-impl-2.20.0.jar fr.an.tests.log4j2.AppMain
DEBUG StatusLogger Using ShutdownCallbackRegistry class org.apache.logging.log4j.core.util.DefaultShutdownCallbackRegistry
DEBUG StatusLogger Took 0,108853 seconds to load 225 plugins from jdk.internal.loader.ClassLoaders$AppClassLoader@1d44bcfa
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger AsyncLogger.ThreadNameStrategy=UNCACHED (user specified null, default is UNCACHED)
TRACE StatusLogger Using default SystemClock for timestamps.
DEBUG StatusLogger org.apache.logging.log4j.core.util.SystemClock supports precise timestamps.
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger PluginManager 'Converter' found 45 plugins
DEBUG StatusLogger Starting OutputStreamManager SYSTEM_OUT.false.false-1
DEBUG StatusLogger Starting LoggerContext[name=1d44bcfa, org.apache.logging.log4j.core.LoggerContext@67e2d983]...
DEBUG StatusLogger Reconfiguration started for context[name=1d44bcfa] at URI null (org.apache.logging.log4j.core.LoggerContext@67e2d983) with optional ClassLoader: null
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger PluginManager 'ConfigurationFactory' found 4 plugins
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger Missing dependencies for Yaml support, ConfigurationFactory org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory is inactive
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger Missing dependencies for Json support, ConfigurationFactory org.apache.logging.log4j.core.config.json.JsonConfigurationFactory is inactive
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger Using configurationFactory org.apache.logging.log4j.core.config.ConfigurationFactory$Factory@1f1c7bf6
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger Closing FileInputStream java.io.FileInputStream@1d548a08
DEBUG StatusLogger Apache Log4j Core 2.20 initializing configuration XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml]
DEBUG StatusLogger PluginManager 'Core' found 130 plugins
DEBUG StatusLogger PluginManager 'Level' found 0 plugins
DEBUG StatusLogger PluginManager 'Lookup' found 16 plugins
DEBUG StatusLogger Building Plugin[name=layout, class=org.apache.logging.log4j.core.layout.PatternLayout].
TRACE StatusLogger TypeConverterRegistry initializing.
DEBUG StatusLogger PluginManager 'TypeConverter' found 26 plugins
DEBUG StatusLogger PatternLayout$Builder(pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n", PatternSelector=null, Configuration(C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml), Replace=null, charset="null", alwaysWriteExceptions="null", disableAnsi="null", noConsoleNoAnsi="null", header="null", footer="null")
DEBUG StatusLogger PluginManager 'Converter' found 45 plugins
DEBUG StatusLogger Building Plugin[name=appender, class=org.apache.logging.log4j.core.appender.ConsoleAppender].
DEBUG StatusLogger ConsoleAppender$Builder(target="SYSTEM_OUT", follow="null", direct="null", bufferedIo="null", bufferSize="null", immediateFlush="null", ignoreExceptions="null", PatternLayout(%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n), name="Console", Configuration(C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml), Filter=null, ={})
DEBUG StatusLogger Starting OutputStreamManager SYSTEM_OUT.false.false
DEBUG StatusLogger Building Plugin[name=appenders, class=org.apache.logging.log4j.core.config.AppendersPlugin].
DEBUG StatusLogger createAppenders(={Console})
DEBUG StatusLogger Building Plugin[name=AppenderRef, class=org.apache.logging.log4j.core.config.AppenderRef].
DEBUG StatusLogger createAppenderRef(ref="Console", level="null", Filter=null)
DEBUG StatusLogger Building Plugin[name=root, class=org.apache.logging.log4j.core.config.LoggerConfig$RootLogger].
DEBUG StatusLogger LoggerConfig$RootLogger$Builder(additivity="null", level="INFO", levelAndRefs="null", includeLocation="null", ={Console}, ={}, Configuration(C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml), Filter=null)
DEBUG StatusLogger Building Plugin[name=loggers, class=org.apache.logging.log4j.core.config.LoggersPlugin].
DEBUG StatusLogger createLoggers(={root})
DEBUG StatusLogger Configuration XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml] initialized
DEBUG StatusLogger Starting configuration XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml]
DEBUG StatusLogger Started configuration XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml] OK.
TRACE StatusLogger Stopping org.apache.logging.log4j.core.config.DefaultConfiguration@5579bb86...
TRACE StatusLogger DefaultConfiguration notified 1 ReliabilityStrategies that config will be stopped.
TRACE StatusLogger DefaultConfiguration stopping root LoggerConfig.
TRACE StatusLogger DefaultConfiguration notifying ReliabilityStrategies that appenders will be stopped.
TRACE StatusLogger DefaultConfiguration stopping remaining Appenders.
DEBUG StatusLogger Shutting down OutputStreamManager SYSTEM_OUT.false.false-1
DEBUG StatusLogger OutputStream closed
DEBUG StatusLogger Shut down OutputStreamManager SYSTEM_OUT.false.false-1, all resources released: true
DEBUG StatusLogger Appender DefaultConsole-1 stopped with status true
TRACE StatusLogger DefaultConfiguration stopped 1 remaining Appenders.
TRACE StatusLogger DefaultConfiguration cleaning Appenders from 1 LoggerConfigs.
DEBUG StatusLogger Stopped org.apache.logging.log4j.core.config.DefaultConfiguration@5579bb86 OK
TRACE StatusLogger Reregistering MBeans after reconfigure. Selector=org.apache.logging.log4j.core.selector.ClassLoaderContextSelector@4cc8eb05
TRACE StatusLogger Reregistering context (1/1): '1d44bcfa' org.apache.logging.log4j.core.LoggerContext@67e2d983
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=StatusLogger'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=ContextSelector'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=Loggers,name=*'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=Appenders,name=*'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=AsyncAppenders,name=*'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=AsyncLoggerRingBuffer'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=Loggers,name=*,subtype=RingBuffer'
DEBUG StatusLogger Registering MBean org.apache.logging.log4j2:type=1d44bcfa
DEBUG StatusLogger Registering MBean org.apache.logging.log4j2:type=1d44bcfa,component=StatusLogger
DEBUG StatusLogger Registering MBean org.apache.logging.log4j2:type=1d44bcfa,component=ContextSelector
DEBUG StatusLogger Registering MBean org.apache.logging.log4j2:type=1d44bcfa,component=Loggers,name=
DEBUG StatusLogger Registering MBean org.apache.logging.log4j2:type=1d44bcfa,component=Appenders,name=Console
TRACE StatusLogger Using default SystemClock for timestamps.
DEBUG StatusLogger org.apache.logging.log4j.core.util.SystemClock supports precise timestamps.
TRACE StatusLogger Using DummyNanoClock for nanosecond timestamps.
DEBUG StatusLogger Reconfiguration complete for context[name=1d44bcfa] at URI C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml (org.apache.logging.log4j.core.LoggerContext@67e2d983) with optional ClassLoader: null
DEBUG StatusLogger Shutdown hook enabled. Registering a new one.
DEBUG StatusLogger LoggerContext[name=1d44bcfa, org.apache.logging.log4j.core.LoggerContext@67e2d983] started OK.
******** done Log4j2 getLogger
21:23:02.395 [main] INFO  fr.an.tests.log4j2.AppMain - test log4j2 INFO
21:23:02.397 [main] WARN  fr.an.tests.log4j2.AppMain - test log4j2 WARN
******** testing Slf4j ...
******** done Slf4j getLogger
21:23:02.407 [main] INFO  fr.an.tests.log4j2.AppMain - test slf4j INFO
21:23:02.407 [main] WARN  fr.an.tests.log4j2.AppMain - test slf4j WARN
***** Finished
TRACE StatusLogger Log4jLoggerFactory.getContext() found anchor class fr.an.tests.log4j2.AppMain
DEBUG StatusLogger Stopping LoggerContext[name=1d44bcfa, org.apache.logging.log4j.core.LoggerContext@67e2d983]
DEBUG StatusLogger Stopping LoggerContext[name=1d44bcfa, org.apache.logging.log4j.core.LoggerContext@67e2d983]...
TRACE StatusLogger Unregistering 1 MBeans: [org.apache.logging.log4j2:type=1d44bcfa]
TRACE StatusLogger Unregistering 1 MBeans: [org.apache.logging.log4j2:type=1d44bcfa,component=StatusLogger]
TRACE StatusLogger Unregistering 1 MBeans: [org.apache.logging.log4j2:type=1d44bcfa,component=ContextSelector]
TRACE StatusLogger Unregistering 1 MBeans: [org.apache.logging.log4j2:type=1d44bcfa,component=Loggers,name=]
TRACE StatusLogger Unregistering 1 MBeans: [org.apache.logging.log4j2:type=1d44bcfa,component=Appenders,name=Console]
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=AsyncAppenders,name=*'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=AsyncLoggerRingBuffer'
TRACE StatusLogger Unregistering but no MBeans found matching 'org.apache.logging.log4j2:type=1d44bcfa,component=Loggers,name=*,subtype=RingBuffer'
TRACE StatusLogger Stopping XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml]...
TRACE StatusLogger XmlConfiguration notified 2 ReliabilityStrategies that config will be stopped.
TRACE StatusLogger XmlConfiguration stopping 1 LoggerConfigs.
TRACE StatusLogger XmlConfiguration stopping root LoggerConfig.
TRACE StatusLogger XmlConfiguration notifying ReliabilityStrategies that appenders will be stopped.
TRACE StatusLogger XmlConfiguration stopping remaining Appenders.
DEBUG StatusLogger Shutting down OutputStreamManager SYSTEM_OUT.false.false
DEBUG StatusLogger OutputStream closed
DEBUG StatusLogger Shut down OutputStreamManager SYSTEM_OUT.false.false, all resources released: true
DEBUG StatusLogger Appender Console stopped with status true
TRACE StatusLogger XmlConfiguration stopped 1 remaining Appenders.
TRACE StatusLogger XmlConfiguration cleaning Appenders from 2 LoggerConfigs.
DEBUG StatusLogger Stopped XmlConfiguration[location=C:\arn\devPerso\test-snippets\test-slf4j-log4j2\log4j2.xml] OK
DEBUG StatusLogger Stopped LoggerContext[name=1d44bcfa, org.apache.logging.log4j.core.LoggerContext@67e2d983] with status true

Process finished with exit code 0
```


## Test when forgetting dependency "org.apache.logging.log4j:log4j-slf4j-impl"

```text
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

## Diagnostic Test for slf4j StaticLoggerBinder

```text
Classloader resource for org/slf4j/impl/StaticLoggerBinder.class : jar:file:/C:/Users/arnaud/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.20.0/log4j-slf4j-impl-2.20.0.jar!/org/slf4j/impl/StaticLoggerBinder.class

org.slf4j.LoggerFactory.getLogger(..)
    =>
    ILoggerFactory iLoggerFactory = getILoggerFactory();
    iLoggerFactory.getLogger(name);
        
org.slf4j.LoggerFactory.getILoggerFactory
    => first init, then check state SUCCESSFUL_INITIALIZATION .. 
    => StaticLoggerBinder.getSingleton().getLoggerFactory();

ILoggerFactory.getLogger()
    => org.apache.logging.slf4j.Log4jLoggerFactory.getContext
    ConcurrentMap<String, L> loggers = getLoggersInContext(context);
    loggers.get ... else putIfAbsent new Log4jLogger(markerFactory, validateContext(context).getLogger(key), name)

org.apache.logging.log4j.core.LoggerContext.getLogger(java.lang.String)
    => log4j-core ... Logger logger = loggerRegistry.getLogger(name, messageFactory);
        
```
