package fr.an.dynadapter.lang;

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

public class DefaultLangDynDataTypeHierarchy implements IDynDataTypeHierarchy<Class<?>> {

        /**
         * Cache of class lookup order (Class -> Class[]). This avoids having to compute often, and
         * provides clients with quick lookup for instanceOf checks based on type name.
         * Thread safety note: The map is synchronized using a synchronized
         * map wrapper class.  The arrays within the map are immutable.
         */
        private Map<Class<?>,Class<?>[]> classSearchOrderLookup;

        // ------------------------------------------------------------------------

        public DefaultLangDynDataTypeHierarchy() {
        }

        // ------------------------------------------------------------------------


        public void flushLookup() {
            classSearchOrderLookup = null;
        }
        
        public Class<?> dataTypeOf(Object object) {
            return object.getClass();
        }

//        public boolean isInstance(Object adaptable, Class<?> dataType) {
//            return dataType.isInstance(adaptable);
//        }

        /**
         * Returns the super-type search order starting with <code>adaptable</code>. 
         * The search order is defined in this class' comment.
         */
        public Class<?>[] computeSuperTypesOrder(Class<?> adaptable) {
            Class<?>[] classes = null;
            //cache reference to lookup to protect against concurrent flush
            Map<Class<?>,Class<?>[]> lookup = classSearchOrderLookup;
            if (lookup == null)
                classSearchOrderLookup = lookup = Collections.synchronizedMap(new HashMap<>());
            else
                classes = lookup.get(adaptable);
            // compute class order only if it hasn't been cached before
            if (classes == null) {
                classes = doComputeClassOrder(adaptable);
                lookup.put(adaptable, classes);
            }
            return classes;
        }

        /**
         * Computes the super-type search order starting with <code>adaptable</code>. 
         * The search order is defined in this class' comment.
         */
        private Class<?>[] doComputeClassOrder(Class<?> adaptable) {
            List<Class<?>> classes = new ArrayList<>();
            Class<?> clazz = adaptable;
            Set<Class<?>> seen = new HashSet<>(4);
            //first traverse class hierarchy
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }
            //now traverse interface hierarchy for each class
            Class<?>[] classHierarchy = (Class[]) classes.toArray(new Class[classes.size()]);
            for (int i = 0; i < classHierarchy.length; i++)
                computeInterfaceOrder(classHierarchy[i].getInterfaces(), classes, seen);
            return (Class[]) classes.toArray(new Class[classes.size()]);
        }

        private void computeInterfaceOrder(Class<?>[] interfaces, Collection<Class<?>> classes, Set<Class<?>> seen) {
            List<Class<?>> newInterfaces = new ArrayList<>(interfaces.length);
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> interfac = interfaces[i];
                if (seen.add(interfac)) {
                    //note we cannot recurse here without changing the resulting interface order
                    classes.add(interfac);
                    newInterfaces.add(interfac);
                }
            }
            for (Iterator<Class<?>> it = newInterfaces.iterator(); it.hasNext();)
                computeInterfaceOrder(it.next().getInterfaces(), classes, seen);
        }

        
    }