package fr.an.tests.testejml.util;

import java.text.DecimalFormat;

import org.ejml.data.DenseMatrix64F;

public class DenseMatrix64FUtils {

    public static String matOrVectToString(DenseMatrix64F mat, DecimalFormat df) {
        if (mat.getNumRows() == 1 || mat.getNumCols() == 1) {
            return vectToString(mat, df);
        } else {
            return matToString(mat, df);
        }
    }
    
    public static String matToString(DenseMatrix64F mat, DecimalFormat df) {
        StringBuilder sb = new StringBuilder();
        if (df == null) df = new DecimalFormat("000000.000");
        for( int y = 0; y < mat.getNumRows(); y++ ) {
            for( int x = 0; x < mat.getNumCols(); x++ ) {
                sb.append(df.format(mat.get(y,x)));
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    public static String vectToString(DenseMatrix64F vect, DecimalFormat df) {
        StringBuilder sb = new StringBuilder();
        if (df == null) df = new DecimalFormat("000000.000");
        if (vect.getNumCols() == 1) {
            for( int y = 0; y < vect.getNumRows(); y++ ) {
                sb.append(df.format(vect.get(y, 0)));
                sb.append(' ');
            }
        } else {
            for( int x = 0; x < vect.getNumCols(); x++ ) {
                sb.append(df.format(vect.get(0, x)));
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    
    
}
