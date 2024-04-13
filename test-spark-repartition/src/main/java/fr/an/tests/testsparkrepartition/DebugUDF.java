package fr.an.tests.testsparkrepartition;

import lombok.Getter;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

public class DebugUDF {

    public static final String NAME_udfDebugIdentityInt = "udfDebugIdentityInt";

    @Getter
    private static int udfDebugIdentityIntCallCount = 0;


    public static void registerUDFs(UDFRegistration udfRegistration) {
        udfRegistration.register(NAME_udfDebugIdentityInt, UDF_debugIdentityInt, DataTypes.IntegerType);
    }

    public static final UDF1<Integer, Integer> UDF_debugIdentityInt = (Integer x) -> {
        udfDebugIdentityIntCallCount++;
        return x;
    };

}
