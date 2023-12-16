package fr.an.tests.classgraphduplicate;

import java.util.Collection;
import java.util.Iterator;

public class CommonPrefix {

    public static String findCommonPrefix(Collection<String> strs) {
        if (strs == null || strs.isEmpty()) {
            return "";
        }
        Iterator<String> iter = strs.iterator();
        String currPrefix = iter.next();
        for (; iter.hasNext(); ) {
            String str = iter.next();
            if (! str.startsWith(currPrefix)) {
                int len = Math.min(currPrefix.length(), str.length());
                for(int i = 0; i < len; i++) {
                    if (str.charAt(i) != currPrefix.charAt(i)) {
                        currPrefix = currPrefix.substring(0, i);
                        if (i == 0) {
                            return "";
                        }
                        break;
                    }
                }
            }
        }
        return currPrefix;
    }

}
