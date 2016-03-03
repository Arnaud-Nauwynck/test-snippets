package fr.an.dynadapter.tstdynobj;

public class TstDynTypeUtils {

    public static final TstDynType fooType = new TstDynType("foo", null, null);
    public static final TstDynType foo2Type = new TstDynType("foo2", fooType, null);

    public static final TstDynInterfaceId CST_IDynBar = new TstDynInterfaceId("IDynBar"); 

}
