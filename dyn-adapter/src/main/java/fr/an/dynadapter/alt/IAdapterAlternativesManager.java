package fr.an.dynadapter.alt;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface IAdapterAlternativesManager<DT> {

    /** 
     * @return first found alternative adapter ... 
     * could
     */
    default public <T> T getAdapter(Object adaptable, ItfId<T> interfaceId) {
        return getAdapter(adaptable, interfaceId, x -> true);
    }

    default public <T> boolean hasAdapter(Object adaptable, ItfId<T> interfaceId) {
        return hasAdapter(adaptable, interfaceId, x -> true);
    }

    // specific alternative adapter lookup
    default public <T> T getAdapter(Object adaptable, ItfId<T> interfaceId, String alternativeId) {
        return getAdapter(adaptable, interfaceId, x -> x.equals(alternativeId));
    }

    default public <T> boolean hasAdapter(Object adaptable, ItfId<T> interfaceId, String alternativeId)  {
        return hasAdapter(adaptable, interfaceId, x -> x.equals(alternativeId));
    }


    public <T> T getAdapter(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate);

    public <T> boolean hasAdapter(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate);

    default public <T> Map<String,T> getAdapters(Object adaptable, ItfId<T> interfaceId) {
        return getAdapters(adaptable, interfaceId, x -> true);
    }

    public <T> Map<String,T> getAdapters(Object adaptable, ItfId<T> interfaceId, Predicate<String> alternativePredicate);

    public Set<String> getAdapterAlternatives(DT adaptableDataType, ItfId<?> interfaceId);

    public Set<ItfId<?>> computeAdapterTypes(DT adaptableDataType, Predicate<String> alternativePredicate);

    
    // ------------------------------------------------------------------------
    
    default public <T> T getAdapter(Object adaptable, ItfAlternativeId<T> interfaceAlternativeId) {
        return getAdapter(adaptable, interfaceAlternativeId.itfId, interfaceAlternativeId.alternativeName);
    }

    default public <T> boolean hasAdapter(Object adaptable, ItfAlternativeId<T> interfaceAlternativeId) {
        return hasAdapter(adaptable, interfaceAlternativeId.itfId, interfaceAlternativeId.alternativeName);
    }

    
    default public <T> T getAdapter(Object adaptable, Class<T> interfaceIdClass) {
        return (T) getAdapter(adaptable, new ItfId<>(interfaceIdClass, ""));
    }

    default public <T> boolean hasAdapter(Object adaptable, Class<T> interfaceIdClass) {
        return hasAdapter(adaptable, new ItfId<>(interfaceIdClass, ""));
    }

    default public Set<ItfId<?>> computeAdapterTypes(DT adaptableDataType, String alternative) {
        return computeAdapterTypes(adaptableDataType, x -> x.equals(alternative));
    }

    default public Set<ItfId<?>> computeAdapterTypes(DT adaptableDataType) {
        return computeAdapterTypes(adaptableDataType, x -> true);
    }

    

}