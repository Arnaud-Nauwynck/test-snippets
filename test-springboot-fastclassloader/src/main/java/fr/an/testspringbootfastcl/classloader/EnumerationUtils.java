package fr.an.testspringbootfastcl.classloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public final class EnumerationUtils {

	private EnumerationUtils() {}
	
	/**
	 * utility converter: Enumeration<T> -> List<T>
	 */
    public static <T> List<T> toList(Enumeration<T> iter) {
        List<T> res = new ArrayList<>();
        while(iter.hasMoreElements()) {
            res.add(iter.nextElement());
        }
        return res;
    }

	/**
	 * utility converter: Iterator<T> -> Enumeration<T>
	 */
	public static <T> Enumeration<T> toEnumeration(Iterator<T> iter) {
		return new IterEnumeration<>(iter);
	}
	
	/**
	 * utility converter: Collection<T> -> Enumeration<T>
	 */
	public static <T> Enumeration<T> toEnumeration(Collection<T> ls) {
		return toEnumeration(ls.iterator());
	}

    /**
     * similar to commons.collection IteratorEnumeration, but with template
     */
    public static class IterEnumeration<T> implements Enumeration<T> {

        private Iterator<T> iterator;
            
        public IterEnumeration(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public T nextElement() {
            return iterator.next();
        }

    }
    
}
