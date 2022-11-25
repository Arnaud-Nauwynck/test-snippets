
@echo OFF

@echo .. call c:/apps/setenv-jdk8.cmd
call c:/apps/setenv-jdk8.cmd

@echo .. call c:/apps/hadoop/setenv-hadoop3-nohdfs.cmd
call c:/apps/hadoop/setenv-hadoop3-nohdfs.cmd

@echo .. call c:/apps/hadoop/setenv-spark3.cmd
call c:/apps/hadoop/setenv-spark3.cmd


@echo ..JAVA_HOME: %JAVA_HOME%


@echo .. HADOOP_HOME: %HADOOP_HOME%
@echo .. HADOOP_CONF_DIR: %HADOOP_CONF_DIR%
@echo .. HADOOP_OPS: %HADOOP_OPS%

@echo ..SPARK_HOME: %SPARK_HOME%


