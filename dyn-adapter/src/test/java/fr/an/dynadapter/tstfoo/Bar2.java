package fr.an.dynadapter.tstfoo;

public class Bar2 extends Bar implements IBar2 {

    protected String bar2Value;
    
    public void setBar2Value(String p) {
        this.bar2Value = p;
    }

    @Override
    public String getBar2Value() {
        return bar2Value;
    }
    
}
