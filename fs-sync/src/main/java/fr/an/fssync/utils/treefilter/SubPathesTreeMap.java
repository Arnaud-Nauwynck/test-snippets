package fr.an.fssync.utils.treefilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.an.fssync.model.FsPath;

/**
 * Tree structure for storing elements per FsPath, for sub-trees
 *
 */
public class SubPathesTreeMap<T> {

    private final Node<T> root = new Node<T>();
    
    private static class Node<T> {
        private Map<String,Node<T>> childMap;
        private List<T> elements;
        
        public Node<T> getOrCreateChild(String name) {
            Node<T> res = childMap.get(name);
            if (res == null) {
                res = new Node<T>();
                childMap.put(name, res);
            }
            return res;
        }
        
        public Node<T> getChild(String name) {
            return childMap.get(name);
        }
        
        public void add(T p) {
            if (elements == null) {
                elements = new ArrayList<>();
            }
            elements.add(p);
        }
        
        public void remove(T p) {
            if (elements == null) {
                return;
            }
            elements.remove(p);
        }

    }

    // ------------------------------------------------------------------------

    public SubPathesTreeMap() {
    }

    // ------------------------------------------------------------------------

    public void put(FsPath path, T element) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getOrCreateChild(pathElts[i]);
        }
        curr.add(element);
    }

    public void remove(FsPath path, T element) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getChild(pathElts[i]);
            if (curr == null) {
                return;
            }
        }
        curr.remove(element);
    }

    public void collectElementsUpToPath(Collection<T> res, FsPath path) {
        String[] pathElts = path.elements;
        final int len = pathElts.length;
        Node<T> curr = root;
        for(int i = 0; i < len; i++) {
            curr = curr.getChild(pathElts[i]);
            if (curr == null) {
                return;
            }
            if (curr.elements != null) {
                res.addAll(curr.elements);
            }
        }
    }

}
