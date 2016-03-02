package fr.an.dynadapter;

import java.util.Set;

public interface IDynAdapterManager<DT,IId> {

    public /*<T> T*/ Object getAdapter(Object adaptable, IId interfaceId);

    public boolean hasAdapter(Object adaptable, IId interfaceId);

    public Set<IId> computeAdapterTypes(DT adaptableDataType);

}