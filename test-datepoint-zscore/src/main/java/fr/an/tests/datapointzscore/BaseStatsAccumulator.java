package fr.an.tests.datapointzscore;

public class BaseStats {
    public final int count;
    public final double sumValues;
    public final double sumSquares;

    //---------------------------------------------------------------------------------------------

    public BaseStats(int count, double sumValues, double sumSquares) {
        this.count = count;
        this.sumValues = sumValues;
        this.sumSquares = sumSquares;
    }

    //---------------------------------------------------------------------------------------------

}
