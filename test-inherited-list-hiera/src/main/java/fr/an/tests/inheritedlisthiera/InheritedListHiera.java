package fr.an.tests.inheritedlisthiera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a Prefix Tree, for inheriting list of values per sub-trees
 * 
 * example:
 * <code>
 * InheritedListHiera<> hiera = new InheritedListHiera<T>();
 * hiera.addToSubTree("/a", value_a);
 * hiera.addToSubTree("/a/b", value_a_b);
 * hiera.addToSubTree("/a/b/c1", value_a_b_c1);
 * hiera.addToSubTree("/a/b/c2", value_a_b_c2);
 * </code>
 * 
 * <PRE>
 * /
 * +- a        .. [value_a]   => inherited: [value_a]
 *    +- b     .. [value_b]   =>            [value_a, value_b]
 *       +- c1 .. [value_c1]                [value_a, value_b, value_c1]
 *          ..
 *       +- c2 .. [value_c2]                [value_a, value_b, value_c2]
 *          ..
 * 
 * </PRE>
 * 
 * Performance = The structure is a Tree, containing all intermediate node for configured path 
 * if the max depth of the tree is N, and a path to resolve is of size P 
 * then the cost of collectInheritedValues() is O(min(N,P))
 * 
 * Analogy with logback Logger hierarchy:
 * having appenders attached to logger (sparse tree configured from logback.xml conf)
 * then for a given package name, you can get the inherited list of appenders
 * 
 * @param <T>
 */
public class InheritedListHiera<T> {

    private final Node<T> rootNode = new Node<>(null, "/");
    
    protected static class Node<T> {
	protected final String pathName;
	protected final String debugFullPath;
	protected final Map<String,Node<T>> childNodes = new HashMap<>();
	protected final List<T> attachedValues = new ArrayList<>();
	
	public Node(Node<T> parent, String pathName) {
	    this.pathName = pathName;
	    this.debugFullPath = (parent != null)? parent.debugFullPath + "/" + pathName : pathName;
	}
	
    }
    
    // ------------------------------------------------------------------------
    
    public InheritedListHiera() {
    }

    // ------------------------------------------------------------------------

    /** helper method for addToSubTree(String[] baseTreePath, T value) */
    public void addToSubTree(String baseTreePath, T value) {
	addToSubTree(baseTreePath.split("/"), value);
    }

    /** helper method for inheritedValuesForPath(String[] baseTreePath, T value) */
    public List<T> inheritedValuesForPath(String path) {
	return inheritedValuesForPath(path.split("/"));
    }
    
    public void addToSubTree(String[] baseTreePath, T value) {
	// recursive navigate or create on the fly sub nodes
	Node<T> currNode = rootNode;
	for(String pathName : baseTreePath) {
	    Node<T> childNode = currNode.childNodes.get(pathName);
	    if (childNode == null) {
		childNode = new Node<>(currNode, pathName);
		currNode.childNodes.put(pathName, childNode);
	    }
	    currNode = childNode;
	}
	// then append value to bottom node
	currNode.attachedValues.add(value);
    }

    public List<T> inheritedValuesForPath(String[] path) {
	List<T> res = new ArrayList<>();
	collectInheritedValues(res, path);
	return res;
    }

    public void collectInheritedValues(Collection<T> res, String[] path) {
	Node<T> currNode = rootNode;
	for(String pathName : path) {
	    Node<T> childNode = currNode.childNodes.get(pathName);
	    if (childNode != null) {
		res.addAll(childNode.attachedValues);
		currNode = childNode;
	    } else {
		// finish navigating to node with explicit values
		break;
	    }
	}
    }
    
}
