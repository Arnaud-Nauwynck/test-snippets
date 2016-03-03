package fr.an.dynadapter.tstdynobj;

import java.util.HashMap;
import java.util.Map;

public class TstDynObject {

    public final TstDynType type;

    private Map<String,Object> fields = new HashMap<String,Object>();
    
    public TstDynObject(TstDynType type) {
        this.type = type;
    }

    public Object getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, Object value) {
        this.fields.put(name, value);
    }
        
}
