package fr.an.fssync.utils.treefilter;

import java.util.function.Predicate;

import fr.an.fssync.model.FsPath;

/**
 * Predicate for FsPath
 */
public abstract class FsPathPredicate implements Predicate<FsPath> {

    @Override
    public abstract boolean test(FsPath t);
    
    /**
     *
     */
    public static class SubPathOfPredicate extends FsPathPredicate {
        private final FsPath parentPath;

        public SubPathOfPredicate(FsPath parentPath) {
            this.parentPath = parentPath;
        }

        @Override
        public boolean test(FsPath t) {
            String[] testElts = t.elements;
            String[] parentElts = parentPath.elements;
            int parentLength = parentElts.length;
            if (parentLength > testElts.length) {
                return false;
            }
            for(int i = 0; i < parentLength; i++) {
                if (! testElts[i].equals(parentElts[i])) {
                    return false;
                }
            }
            return true;
        }
        
    }

    /**
     *
     */
    public static class LastNameFsPathPredicate extends FsPathPredicate {
        private final Predicate<String> namePredicate;

        public LastNameFsPathPredicate(Predicate<String> namePredicate) {
            this.namePredicate = namePredicate;
        }

        @Override
        public boolean test(FsPath t) {
            int eltsLen = t.elements.length;
            if (eltsLen == 0) {
                return false;
            }
            String lastName = t.elements[eltsLen-1];
            return namePredicate.test(lastName);
        }
    }

}
