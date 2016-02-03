package fr.an.test.eclipsemirror;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListLatestMain {

    private File dir;
    
    // ------------------------------------------------------------------------

    
    public static void main(String[] args) {
        ListLatestMain app = new ListLatestMain();
        app.parseArgs(args);
        app.run();
    }

    // ------------------------------------------------------------------------
    
    public void parseArgs(String[] args) {
        this.dir = new File(".");
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public List<File> run() {
        Map<File,Version> fileInfo = new LinkedHashMap<File,Version>();
        File[] files = dir.listFiles();
        for(File file : files) {
            String name = file.getName();
            if (name.startsWith(".")) {
                continue;
            }
            int indexUnderscore = name.lastIndexOf('_');
            if (indexUnderscore == -1) {
                continue;
            }
            // String baseName = name.substring(0, indexUnderscore);
            String version = name.substring(indexUnderscore, name.length());
            String[] versionElts = version.split(".-");
            Version v = new Version(versionElts);
            Version prev = fileInfo.get(file);
            if (prev != null) {
                if (prev.compareTo(v) < 0) {
                    prev = null;
                }
            }
            fileInfo.put(file, v);
        }
        
        return new ArrayList<File>(fileInfo.keySet()); 
    }
    
    
    public static class Version implements Comparable<Version> {
        String[] elts;
        Integer[] eltNums;
        
        public Version(String[] elts) {
            this.elts = elts;
            for(int i = 0; i < elts.length; i++) {
                try {
                    eltNums[i] = Integer.parseInt(elts[i]);
                } catch(Exception ex) {
                }
            }
        }

        public int compareTo(Version o) {
            int res = 0;
            int maxLen = Math.min(elts.length, o.elts.length);
            for(int i = 0; i < maxLen; i++) {
                if (eltNums[i] != null && o.eltNums[i] != null) {
                    res = eltNums[i].compareTo(o.eltNums[i]);
                    if (res != 0) {
                        return res;
                    }
                }
                if (elts[i] != null && o.elts[i] != null) {
                    res = elts[i].compareTo(o.elts[i]);
                    if (res != 0) {
                        return res;
                    }
                }
            }
            res = Integer.compare(elts.length, o.elts.length);
            return res;
        }

        
    }
}
