package fr.an.test.aspectjfile;

import lombok.Value;
import lombok.val;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BaseAspect {


    private static PrintStream output;

    @Value
    protected static class MessageAtKey {
        final String message;
        final String at;
    }
    protected static class MessageCounter {
        int count;
    }
    protected static Map<MessageAtKey,MessageCounter> countByMessageAt = new HashMap<>();

    static {
        System.out.println("#### BaseAspect.<cinit>");
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File("file-aspect.log"))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        output.println("#### init");
    }


    protected static void logAspect(String pointcut, String message) {
        String at = ExUtils.currentStackTraceShortPath();
        MessageAtKey key = new MessageAtKey(message, at);
        val counter = countByMessageAt.computeIfAbsent(key, k -> new MessageCounter());
        int count = counter.count++;
        if (count == 1) {
            String logText = "### Aspect " + pointcut + ": " + message + "\n"
                    + "    at " + at;
            output.println(logText);
            System.out.println(logText);
        }
    }
}
