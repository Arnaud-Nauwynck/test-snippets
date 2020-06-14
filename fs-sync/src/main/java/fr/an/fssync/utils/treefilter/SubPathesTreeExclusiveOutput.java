package fr.an.fssync.utils.treefilter;

import java.util.Map;

import fr.an.fssync.model.FsPath;
import lombok.AllArgsConstructor;

/**
 * Tree structure for storing exclusive "output" per FsPath sub-tree
 *
 */
public class SubPathesTreeExclusiveOutput<T> {

    private final Node<T> root = new Node<T>(FsPath.ROOT);
    
    private static class Node<T> {
        private final FsPath path;
        private Map<String,Node<T>> childMap;
        private T output;
        
        public Node(FsPath path) {
            this.path = path;
        }

        public Node<T> getOrCreateChild(String name) {
            Node<T> res = childMap.get(name);
            if (res == null) {
                res = new Node<T>(path.child(name));
                childMap.put(name, res);
            }
            return res;
        }
        
        public Node<T> getChild(String name) {
            return childMap.get(name);
        }
        
        public void addExclusiveOutput(T p) {
            if (output != null || childMap != null) {
                throw new IllegalArgumentException();
            }
            this.output = p;
        }
        
        public void removeExclusiveOutput(T p) {
            if (this.output != p) {
                throw new IllegalArgumentException();
            }
            this.output = null;
        }

    }

    // ------------------------------------------------------------------------

    public SubPathesTreeExclusiveOutput() {
    }

    // ------------------------------------------------------------------------

    public void putExclusiveOutput(FsPath path, T exclusiveOutput) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getOrCreateChild(pathElts[i]);
            if (curr.output != null) {
                throw new IllegalArgumentException();
            }
        }
        if (curr.childMap != null) {
            throw new IllegalArgumentException();
        }
        curr.addExclusiveOutput(exclusiveOutput);
    }

    public void removeExclusiveOutput(FsPath path, T exclusiveOutput) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getChild(pathElts[i]);
            if (curr == null) {
                return;
            }
        }
        curr.removeExclusiveOutput(exclusiveOutput);
        // TODO cleanup ..
    }

    @AllArgsConstructor
    protected static class FsPathOutput<T>  {
        public final FsPath path;
        public T exclusiveOutput;
    }
    
    public FsPathOutput<T> findExlusiveOutputUpToPath(FsPath path) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getChild(pathElts[i]);
            if (curr == null) {
                return null;
            }
            if (curr.output != null) {
                return new FsPathOutput<>(curr.path, curr.output);
            }
        }
        return null;
    }

}
