package fr.an.util.collections;




/**
 * M: maximum degree/number of key-value pairs in a node
 * HT:height of the B-tree
 * N: number of key-value pairs in the tree
 * @author shirleyyoung
 *
 * @param <Key>
 * @param <Value>
 */
@SuppressWarnings("rawtypes")
public class BTree3 <Key extends Comparable<Key>, Value>{
    private int M;
    private Node root;
    private int HT;
    private int N;
    
    /******************************************************
     * The structure to store the key-value pairs
     * @author shirleyyoung
     *
     ******************************************************/
    private class Entry <Key extends Comparable<Key>, Value> implements Comparable<Entry>{
        private Comparable key;
        private Object value;
        public Entry(Comparable key, Object value){
            this.key = key;
            this.value = value;
        }
        
        public int compareTo(Entry e) {
            return key.compareTo(e.key);
        }
        public String toString(){
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }
    
    
    /***************************************************************
     * tree node class
     * Each node is a list of Entry objects
     * Entry list is used for all key - value pairs stored in the node
     * index: the current number of key-value pairs,
     * as well as the index where next key-value pair will be added
     * since it's 0 index, the node contains m + 1 key - value pairs
     * entryList: the entryList
     * children: children list of the node, each node may have at most
     * M + 1 children
     * @author shirleyyoung
     *
     ***************************************************************/
    private class Node <Key extends Comparable<Key>, Value>{
        private int index;
        private Entry[] entryList;
        private Node[] children;
        private Node(){
            index = 0;
            entryList = new Entry[M];
            children = new Node[M + 1];
        }
        /***********************************************************
         * search closest
         * if key presents in node, return the index of the key
         * otherwise return the index of the root of the subtree
         * where the desired key is supposed to present
         * @param key
         * @return
         ***********************************************************/
        int searchEntry(Comparable key){
            int toReturn = 0;
            for (int i = 0; i < index; i++){
                if (i == index - 1 || key.compareTo(entryList[i].key) <= 0){
                    toReturn = i;
                    break;
                }
            }
            return toReturn;
        }
        
        Entry removeEntry(int indexToRemove){
            //System.out.println("index to remove: " + indexToRemove);
            Entry toRemove = entryList[indexToRemove];
            for (int i = indexToRemove; i < index - 1; i++){
                entryList[i] = entryList[i + 1];
                children[i + 1] = children[i + 2];
            }
             
            index--;
            return toRemove;    
        }

        
        /**
         * add entry to the node, will only be called when 
         * index < M/2
         * @param e
         * @param indexToAdd
         */
        void add(Entry e, int indexToAdd){
            for (int i = index; i > indexToAdd; i--)
                entryList[i] = entryList[i - 1];
            entryList[indexToAdd] = e;
            index++;
        }
        
        public String toString(){
            String s = "";
            for (int i = 0; i < index; i++){
                s += entryList[i].toString() + ", ";
                
            }
            s = s.substring(0, s.length() - 2);
            s += String.format("\nindex: %d\n", index);
            return s;
        }
    }

    /*******************************************************
     * constructor
     * @param M
    ********************************************************/
    public BTree3(){
        M = 4;
        HT = 0;
        N = 0;
        root = new Node();
    }
    public BTree3(int M){
        this();
        this.M = M;
        root = new Node();
    }
    
    /********************************************************
     * search for a value given the key
     * @param key
     * @return
     *******************************************************/
    public Object get(Comparable key){
        Entry found = search(root, key, HT);
        if (found == null){
            System.out.println("No such key!");
            return null;
        }
        else return found.value;
    }
    private Entry search(Node x, Comparable key, int ht){
        if (x == null)
            return null;
        Entry[] entryList = x.entryList;
        if(ht == 0){//leaf node
            for(int j = 0; j < x.index; j++){
                if(key.equals(entryList[j].key))
                    return entryList[j];
            }
        } else{
            for(int j = 0; j < x.index; j++){
                if (key.equals(entryList[j].key))
                    return entryList[j];
                else if(j == 0 && key.compareTo(entryList[j].key) < 0)
                    return search(x.children[j], key, ht - 1);
                else if(j == x.index - 1|| key.compareTo(entryList[j + 1].key) < 0)
                    return search(x.children[j + 1], key, ht - 1);
            }
        }
        return null;
    }
    
    
    /*******************************************************
     * Insertion
     * @param key
     * @param value
     *******************************************************/
    public void insert(Comparable key, Object value){
        Entry exists = search(root, key, HT);
        if(exists != null){
            exists.value = value;
            System.out.println("Key already exists, modify the value to the input value.");
            return;
        }
        Node newRoot = insert(root, key, value, HT);
        N++;
        if(newRoot == null) return;
        root = newRoot;
        HT++;
    }
    private Node insert(Node curr, Comparable key, Object value, int ht){
        int indexToInsert;
        if(ht == 0){//leaf nodes level
            //find the correct place to insert the value            
            for(indexToInsert = 0; indexToInsert < curr.index; indexToInsert++){
                if(key.compareTo(curr.entryList[indexToInsert].key) < 0) break;
            }
            //shift the larger nodes right
            curr.add(new Entry(key, value), indexToInsert);
            
            //the key value pair is added to the existing node, no new node is inserted, return null
            if(curr.index < M) return null;
            return split(curr);
        }else {
            for(indexToInsert = 0; indexToInsert < curr.index; indexToInsert++){
                if (indexToInsert == 0 && key.compareTo(curr.entryList[indexToInsert].key) < 0) break;          
                else if((indexToInsert == curr.index - 1) || key.compareTo(curr.entryList[indexToInsert + 1].key) < 0){
                    //children[j].next, j++
                    indexToInsert += 1;
                    break;
                }
            }
            Node u = insert(curr.children[indexToInsert], key, value, ht - 1);
            if(u == null) return null;
            return combine(curr, u);
        }
    }
    /*********************************************************
     * split the node if it reaches maximum degree
     * choose middle node as the new root 
     * e.g., if M = 4 or 5, choose entryList[2] as new root
     * @param curr
     * @return
     *********************************************************/
    private Node split(Node curr){
        Node newRoot = new Node();
        newRoot.entryList[0] = curr.entryList[M/2];
        newRoot.index++;
        Node right = new Node();
        int j = 0, i = M/2 + 1;
        while(j < M && i < M){
            right.entryList[j] = curr.entryList[i];
            right.children[j++] = curr.children[i++];
        }
        right.children[j] = curr.children[i];
        right.index = j;
        curr.index = M/2;
        newRoot.children[0] = curr;
        newRoot.children[1] = right;
        return newRoot;
    }
    /****************************************************
     * combine two nodes when total elements 
     * are less than M
     * split the node if the entry list of the 
     * combined node reaches M
     * @param left
     * @param right
     * @return
     ****************************************************/
    private Node combine(Node left, Node right){
        int j = 0;
        int total = left.index + right.index;
        while (left.index < total){
            left.entryList[left.index] = right.entryList[j];
            left.children[left.index++] = right.children[j++];
        }
        left.children[left.index] = right.children[j];
        right.children = null;
        right = null;
        if (left.index < M) {return null;}
        else return split(left);
    }
    
    /****************************************************
     * Deletion
     * @param key
     * @return
     ****************************************************/
    public Object delete(Comparable key){
        Entry toDelete = search(root, key, HT);
        if(toDelete == null) {
            System.out.println("No such key!");
            return null;
        }
        N--; 
        return delete(root, key, HT).value;
    }
    private Entry delete(Node n, Comparable key, int ht){
        int index = n.searchEntry(key);
        if(ht == 0){ // if leaf nodes
            if(n.entryList[index].key != key)
                return null;
            else
                return n.removeEntry(index);
        }else {
            if (key.equals(n.entryList[index].key)){ //if key is in internal node n
                //if left children has a size larger than M/2
                //replace entry that has the key to be deleted in n with the largest 
                //entry in left children, then recursively delete children entry
                //System.out.println("found index: " + index);
                Entry toDelete = n.entryList[index];
                int h = ht - 1;
                if(n.children[index].index >= M/2){ 
                    Node child = n.children[index];
                    while (h != 0){
                        child = child.children[child.index];
                        h--;
                    }
                    n.entryList[index] = child.entryList[child.index - 1];
                    delete(n.children[index], child.entryList[child.index - 1].key, ht - 1);
                //else if right children has a size larger than M/2
                //replace entry has the key to be deleted in n with the smallest
                //entry in right children, then recursively delete children entry
                } else if (n.children[index + 1].index >= M/2){
                    Node child = n.children[index + 1];
                    while(h != 0){
                        child = child.children[0];
                        h--;
                    }
                    n.entryList[index] = child.entryList[0];
                    delete(child, child.entryList[0].key, h);
                //case 3 will take care of the case when node n has too few entries to be moved
                } else {
                    n = moveEntryDown(n.children[index], n, index, n.children[index + 1]);
                    if (ht == HT && root.index == 0){
                        root = n;
                        HT--;
                        ht--;
                    }
                    delete(n, key, ht);
                }
                return toDelete;
            } else { // if key is not in internal node n
                //find the root of the subtree where the desired key is 
                //supposed to present
                //System.out.println("index: " + index);
                //System.out.println("children[index] has index: " + n.children[index].index);
                //Node child = n.children[index];
                //System.out.println("Largest children key: " + child.entryList[child.index - 1].key);
                if(index == (n.index - 1) && key.compareTo(n.entryList[n.index - 1].key) > 0){
                    //child = n.children[index + 1];
                    index += 1;
                }
                //if key is in the children
                boolean chk = key == n.children[index].entryList[n.children[index].searchEntry(key)].key;
                if (n.children[index].index < M/2 && chk){
                    //case 3a, left sibling has size at least M/2
                    //remove the last entry from the left sibling, put into n[index - 1]
                    //add n[index - 1] to the n.children[index]
                    if(index > 0 && n.children[index - 1].index >= M/2){
                        Node helper = n.children[index - 1];
                        n.children[index].add(n.entryList[index - 1], 0);
                        n.entryList[index - 1] = helper.entryList[helper.index - 1];
                        helper.removeEntry(helper.index - 1);
                    //case 3a, right sibling has size at least M/2
                    } else if (index < n.index && n.children[index + 1].index >= M/2){
                        Node helper = n.children[index + 1];
                        n.children[index].add(n.entryList[index], n.children[index].index);
                        n.entryList[index] = helper.entryList[0];
                        helper.removeEntry(0);
                    //case 3b, merge two nodes
                    } else {
                        Node mergeChild = n.children[index];
                        if(index == n.index)
                            n = moveEntryDown(n.children[index - 1], n, index - 1, mergeChild);
                        else n = moveEntryDown(mergeChild, n, index, n.children[index + 1]);
                        if (ht == HT && root.index == 0){
                            root = n;
                            HT--;
                            ht--;
                        }
                        return delete(n, key, ht);
                    }
                }
                if (index == n.index - 1 && key.compareTo(n.entryList[index].key) > 0)
                    return delete(n.children[index + 1], key, ht - 1);
                else return delete(n.children[index], key, ht - 1);
            }
        }
    }
    /**
     * combine left and right children and move the entry (parent[index]) down
     * to serve as median node
     * only be called when both left and right has size < M/2
     * and parent has size >= M/2
     * @param left
     * @param parent
     * @param index
     * @param right
     */
    private Node moveEntryDown(Node left, Node parent, int index, Node right){
        left.add(parent.entryList[index], left.index);
        parent.removeEntry(index);
        combine(left, right);
        if (parent.index == 0) 
            parent = left;
        else parent.children[index] = left;
        return parent;
    }
    
    public String toString(){
        return toString(root, HT) + "\n";
    }
    private String toString(Node n, int ht){
        String s = "";
        if(ht == 0){
            for(int i = 0; i < n.index; i++)
                s += i + ": " + n.entryList[i].toString() +  "\n";
        } else{
            for(int i = 0; i < n.index; i++){
                s += i + ": " + n.entryList[i].toString() + "\nChildren " + i + ": \n";
                s += toString(n.children[i], ht - 1) + "\n";
            }
            s += toString(n.children[n.index], ht - 1);
        }
        return s;
    }
    
    
    public int size(){
        return N;
    }
    public int height(){
        return HT + 1;
    }
}
