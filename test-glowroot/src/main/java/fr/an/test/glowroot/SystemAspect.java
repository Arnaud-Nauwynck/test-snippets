package fr.an.test.glowroot;

import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class SystemAspect extends AbstractFilePluginAspect {

    /**
     * Instrument calls to <code>java.lang.System.getenv()</code>
     */
    @Pointcut(className = "java.lang.System", methodName = "getenv", methodParameterTypes = {} )
    public static class SystemGetEnvAdvice {

        @OnBefore
        public static void onBefore() {
            logCall("System.getenv()");
        }
    }

    /**
     * Instrument calls to <code>java.lang.System.getenv(envVariable)</code>
     */
    @Pointcut(className = "java.lang.System", methodName = "getenv", methodParameterTypes = { "String" } )
    public static class SystemGetEnvVariableAdvice {

        @OnBefore
        public static void onBefore(@BindParameter() String envVariable) {
            logCall("System.getenv(" + envVariable + ")");
        }
    }

    /**
     * Instrument calls to <code>java.lang.System.getProperties()</code>
     */
    @Pointcut(className = "java.lang.System", methodName = "getProperties", methodParameterTypes = {} )
    public static class SystemGetPropertiesAdvice {

        @OnBefore
        public static void onBefore() {
            logCall("System.getProperties()");
        }
    }

    /**
     * Instrument calls to <code>java.lang.System.getProperty(propertyName)</code>
     */
    @Pointcut(className = "java.lang.System", methodName = "getProperty", methodParameterTypes = { "String" } )
    public static class SystemGetPropertyAdvice {

        @OnBefore
        public static void onBefore(@BindParameter() String propertyName) {
            logCall("System.getProperty(" + propertyName + ")");
        }
    }

}
