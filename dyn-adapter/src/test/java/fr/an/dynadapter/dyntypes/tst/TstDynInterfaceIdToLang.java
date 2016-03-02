package fr.an.dynadapter.dyntypes.tst;

import fr.an.dynadapter.IDynInterfaceToLang;

public class TstDynInterfaceIdToLang implements IDynInterfaceToLang<TstDynInterfaceId> {
    
    public boolean isInstance(Object obj, TstDynInterfaceId interfaceId) {
        return false;
    }

}