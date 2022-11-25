
REM call ..\setenv-test-spark.cmd

set CLASS=org.apache.spark.deploy.master.Master
set SPARK_MASTER_HOST=localhost
set SPARK_MASTER_PORT=7077
set SPARK_MASTER_WEBUI_PORT=8080


@echo .. %SPARK_HOME%\bin\spark-class2.cmd %CLASS% --host %SPARK_MASTER_HOST% --port %SPARK_MASTER_PORT% --webui-port %SPARK_MASTER_WEBUI_PORT%
%SPARK_HOME%\bin\spark-class2.cmd %CLASS% --host %SPARK_MASTER_HOST% --port %SPARK_MASTER_PORT% --webui-port %SPARK_MASTER_WEBUI_PORT%
 	

 	