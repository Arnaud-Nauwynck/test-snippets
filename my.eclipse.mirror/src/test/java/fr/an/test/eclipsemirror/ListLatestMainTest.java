package fr.an.test.eclipsemirror;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ListLatestMainTest {

    @Test
    public void testRun() throws Exception {
        ListLatestMain sut = new ListLatestMain();
        sut.setDir(new File("/home/arnaud/eclipsemirror/nodeclipse/plugins"));
        
        List<File> ls = sut.run();
        PrintStream scriptOut = new PrintStream(new FileOutputStream("target/tests/script.txt"));
        
        Map<String,List<File>> group2ls = new HashMap<String,List<File>>(); 
        for(File f : ls) {
            String fileName = f.getName();
            if (fileName.contains(".source_")) {
                continue;
            }
            String group = nameToGroup3(fileName);
            
            if (group.equals("org.eclipse.m2e") 
                    || group.equals("org.eclipse.mylyn")
                    || group.equals("org.eclipse.egit")
                    || group.equals("org.eclipse.jgit")
                    || group.equals("com.google.gerrit")
                    ) {
                continue;
            }
            
            scriptOut.print("plugins/" + fileName + " ");
            
            if (group.contains("json") || group.contains("gson")
                    || group.contains("script")
                    || group.contains("node")
                    || group.contains("tern")
                    || group.contains("js")
                    || group.contains("angular")
                    || group.contains("chrom")
                    || group.contains("rhino")
                    || group.contains("nash")
                    || group.contains("web")
                    ) {
                group = "web";
            }
            
//            System.out.println(group + "\t\t" + fileName);
            List<File> tmpls = group2ls.get(group);
            if (tmpls == null) {
                tmpls = new ArrayList<File>();
                group2ls.put(group, tmpls);
            }
            tmpls.add(f);
        }
        
        for(Map.Entry<String,List<File>> g2lsEntry : group2ls.entrySet()) {
            System.out.println(g2lsEntry.getKey() + ":");
            for(File f : g2lsEntry.getValue()) {
                System.out.println(f.getName());
            }
            
            System.out.println("\n");
        }
        
        scriptOut.close();
    }

    private String nameToGroup3(String fileName) {
        int indexUnderscore = fileName.lastIndexOf('_');
        String baseName = fileName.substring(0, indexUnderscore != -1? indexUnderscore : fileName.length()); 
        int indexDot = baseName.indexOf('.');
        if (indexDot != -1) {
            indexDot = baseName.indexOf('.', indexDot+1);
        }
        if (indexDot != -1) {
            indexDot = baseName.indexOf('.', indexDot+1);
        }
        String group = (indexDot != -1)? baseName.substring(0, indexDot) : baseName;
        return group;
    }
}
