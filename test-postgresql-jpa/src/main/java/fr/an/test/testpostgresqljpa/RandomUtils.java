package fr.an.test.testpostgresqljpa;

import java.util.Random;

public class RandomUtils {
    
    public static int[] randomIntArray(int size, int max) {
        Random rand = new Random(0);
        int[] ids = new int[size];
        for(int i = 0; i < size; i++) {
            ids[i] = rand.nextInt(max);
        }
        return ids;
    }

    
}
