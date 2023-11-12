package org.example;

import java.net.URL;

public class Main {

    public static void main(String[] args) {
        ClassLoader cl = Main.class.getClassLoader();
        checkClassLoaderResource(cl, "javax/servlet/Servlet.class");
    }

    private static void checkClassLoaderResource(ClassLoader cl, String resource) {
        URL servletClassResource = cl.getResource(resource);
        System.out.println("classloader resource for: " + resource + " => " + servletClassResource);
    }
}