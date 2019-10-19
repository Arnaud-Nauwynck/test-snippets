package fr.an.tests.mvnextensionslifecycle;

public class DisplayStackUtils
{

    public static void message(String className, String meth) {
        System.out.println( "##### " + className + "." + meth 
                            + "\n\t stack=" + ExUtils.currentStackTraceShortPath()
                            );
    }
}
