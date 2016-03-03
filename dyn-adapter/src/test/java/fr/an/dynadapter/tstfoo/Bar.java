package fr.an.dynadapter.tstfoo;

public class Bar implements IBar {

    protected String barValue;
    
    public void setBarValue(String barValue) {
        this.barValue = barValue;
    }

    @Override
    public String getBarValue() {
        return barValue;
    }
    
}
