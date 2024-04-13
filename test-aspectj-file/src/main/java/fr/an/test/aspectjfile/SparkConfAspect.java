package fr.an.test.aspectjfile;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SparkConfAspect extends BaseAspect {

    @Before("execution(org.apache.spark.SparkConf org.apache.spark.SparkConf.set(String,String)) && args(key,value)")
    public void sparkConf_set(String key, String value) {
        logAspect("before", "SparkConf.set(" + key + ", " + value + ")");
    }

//    @Before("execution(org.apache.spark.SparkConf org.apache.spark.SparkConf.set(String,String,scala.Boolean)) && args(key,value,silent)")
//    public void private_sparkConf_set(String key, String value, Object silent) {
//        logAspect("before", "private SparkConf.set(" + key + ", " + value + ", " + silent + ")");
//    }

}
