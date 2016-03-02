package fr.an.dynadapter;

public interface IDynDataTypeHierarchy<DT> {

    public DT dataTypeOf(Object object);
    
    public DT[] computeSuperTypesOrder(DT type);
    
    
    public void flushLookup();
    
}
