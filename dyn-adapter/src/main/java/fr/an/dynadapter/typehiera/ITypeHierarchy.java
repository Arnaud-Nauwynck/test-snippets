package fr.an.dynadapter.typehiera;

public interface ITypeHierarchy<DT> {

    public DT dataTypeOf(Object object);
    
    public DT[] computeSuperTypesOrder(DT type);
    
    
    public void flushLookup();
    
}
