package fr.an.tests.log4j2;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class AppMain {

    public static void main(String[] args) {
//        System.setProperty("log4j2.debug", "true");

        // override for finding not in CLASSPATH
        System.setProperty("log4j2.configurationFile", "log4j2.xml");

        org.apache.logging.log4j.Logger log4j2 = org.apache.logging.log4j.LogManager.getLogger(AppMain.class);
        System.out.println("******** done Log4j2 getLogger");
        log4j2.trace("test log4j2 TRACE");
        log4j2.debug("test log4j2 DEBUG");
        log4j2.info("test log4j2 INFO");
        log4j2.warn("test log4j2 WARN");

        System.out.println("******** testing Slf4j ...");

        org.slf4j.Logger slf4jLog = org.slf4j.LoggerFactory.getLogger(AppMain.class);
        System.out.println("******** done Slf4j getLogger");
        slf4jLog.trace("test slf4j TRACE");
        slf4jLog.debug("test slf4j DEBUG");
        slf4jLog.info("test slf4j INFO");
        slf4jLog.warn("test slf4j WARN");

        diagnosticSlf4j();

        System.out.println("***** Finished");
    }

    // cf in org/slf4j/LoggerFactory.java
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    private static void diagnosticSlf4j() {
        ClassLoader cl = AppMain.class.getClassLoader();
        try {
            Enumeration<URL> resources = cl.getResources(STATIC_LOGGER_BINDER_PATH);
            while(resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println("Classloader resource for " + STATIC_LOGGER_BINDER_PATH + " : " + resource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
