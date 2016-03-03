package fr.an.dynadapter.tstdynobj;

import fr.an.dynadapter.simple.IDynInterfaceToLang;

public class TstDynInterfaceIdToLang implements IDynInterfaceToLang<TstDynInterfaceId> {
    
    public boolean isInstance(Object obj, TstDynInterfaceId interfaceId) {
        return false;
    }

}