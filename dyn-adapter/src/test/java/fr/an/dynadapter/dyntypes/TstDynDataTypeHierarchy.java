package fr.an.dynadapter.dyntypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.an.dynadapter.IDynDataTypeHierarchy;

public class TstDynDataTypeHierarchy implements IDynDataTypeHierarchy<TstDynType> {

    private Map<TstDynType,TstDynType[]> classSearchOrderLookup;
    
    // ------------------------------------------------------------------------

    public TstDynDataTypeHierarchy() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void flushLookup() {
        classSearchOrderLookup = null;
    }
    
    @Override
    public TstDynType dataTypeOf(Object object) {
        TstDynType res = ((TstDynObject) object).type;
        return res;
    }

    @Override
    public TstDynType[] computeSuperTypesOrder(TstDynType type) {
        TstDynType[] classes = null;
        //cache reference to lookup to protect against concurrent flush
        Map<TstDynType,TstDynType[]> lookup = classSearchOrderLookup;
        if (lookup == null)
            classSearchOrderLookup = lookup = Collections.synchronizedMap(new HashMap<>());
        else
            classes = lookup.get(type);
        // compute class order only if it hasn't been cached before
        if (classes == null) {
            classes = doComputeClassOrder(type);
            lookup.put(type, classes);
        }
        return classes;
    }

    /**
     * Computes the super-type search order starting with <code>adaptable</code>. 
     * The search order is defined in this class' comment.
     */
    private TstDynType[] doComputeClassOrder(TstDynType adaptable) {
        List<TstDynType> classes = new ArrayList<>();
        TstDynType clazz = adaptable;
        Set<TstDynType> seen = new HashSet<>(4);
        //first traverse class hierarchy
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperType();
        }
        //now traverse interface hierarchy for each class
        TstDynType[] classHierarchy = (TstDynType[]) classes.toArray(new TstDynType[classes.size()]);
        for (int i = 0; i < classHierarchy.length; i++)
            computeInterfaceOrder(classHierarchy[i].getInterfaces(), classes, seen);
        return (TstDynType[]) classes.toArray(new TstDynType[classes.size()]);
    }

    private void computeInterfaceOrder(TstDynType[] interfaces, Collection<TstDynType> classes, Set<TstDynType> seen) {
        List<TstDynType> newInterfaces = new ArrayList<>(interfaces.length);
        for (int i = 0; i < interfaces.length; i++) {
            TstDynType interfac = interfaces[i];
            if (seen.add(interfac)) {
                //note we cannot recurse here without changing the resulting interface order
                classes.add(interfac);
                newInterfaces.add(interfac);
            }
        }
        for (Iterator<TstDynType> it = newInterfaces.iterator(); it.hasNext();)
            computeInterfaceOrder(it.next().getInterfaces(), classes, seen);
    }
}
