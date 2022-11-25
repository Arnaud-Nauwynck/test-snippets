
CLASS="org.apache.spark.deploy.master.Master"
SPARK_MASTER_HOST=$( hostname )
SPARK_MASTER_PORT=7077
SPARK_MASTER_WEBUI_PORT=8080


"${SPARK_HOME}/bin/spark-class" $CLASS \
 	--host $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT

 	