package fr.an.sample.impl;

import lombok.val;
import org.apache.spark.api.java.function.MapFunction;

import java.io.*;

public class ObjectSerializationUtils {


    public static Object deserializeObjectFromBytes(byte[] bytes) {
        // de-serializing from bytes
        Object readObj;
        ObjectInputStream oin = null;
        try {
            try {
                oin = new ObjectInputStream(new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                throw new RuntimeException("should not occur", e);
            }
            try {
                readObj = oin.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("should not occur", e);
            }
        } finally {
            closeSafely(oin);
        }
        return readObj;
    }

    public static byte[] serializeObjectToBytes(MapFunction<Integer, Integer> mapFuncObj) {
        val buffer = new ByteArrayOutputStream();
        ObjectOutputStream oout = null;
        try {
            try {
                oout = new ObjectOutputStream(buffer);
            } catch (IOException e) {
                throw new RuntimeException("should not occur", e);
            }
            // serializing
            try {
                oout.writeObject(mapFuncObj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            closeSafely(oout);
        }
        byte[] bytes = buffer.toByteArray();
        return bytes;
    }

    public static void closeSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {
                System.out.println("should not occur: closeable.close() ex:" + ex.getMessage() + " .. ignore, no rethrow");
                // ignore, no retrow
            }
        }
    }
}
