
REM call ..\setenv-test-spark.cmd

set CLASS=org.apache.spark.deploy.worker.Worker

set MASTER=spark://localhost:7077

set WORKER_ARGS=

REM set WEBUI_PORT=8081
REM set WORKER_ARGS=%WORKER_ARGS% --webui-port %WEBUI_PORT% 

REM set SPARK_WORKER_PORT
REM set WORKER_ARGS=%WORKER_ARGS% --port %SPARK_WORKER_PORT%


@echo .. %SPARK_HOME%/bin/spark-class.cmd %CLASS% %WORKER_ARGS%  %MASTER% %*
%SPARK_HOME%/bin/spark-class.cmd %CLASS% %WORKER_ARGS%  %MASTER% %*

