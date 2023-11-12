

# Running with springboot3, no <dependenciesManagement>, force version slf4j-api, <exclusion> org.apache.logging.log4j:log4j-slf4j2-impl

```
.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/
:: Spring Boot ::                (v3.1.2)

2023-11-12T17:42:18.126+01:00  INFO 11632 --- [           main] o.example.Springboot3SparkNoDepMgtMain   : Starting Springboot3SparkNoDepMgtMain using Java 20.0.1 with PID 11632 (C:\arn\devPerso\test-snippets\test-springboot-spark\springboot3-spark-no-depmgt\target\classes started by arnaud in C:\arn\devPerso\test-snippets\test-springboot-spark\springboot3-spark-no-depmgt)
2023-11-12T17:42:18.131+01:00  INFO 11632 --- [           main] o.example.Springboot3SparkNoDepMgtMain   : No active profile set, falling back to 1 default profile: "default"
2023-11-12T17:42:18.988+01:00  INFO 11632 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2023-11-12T17:42:18.996+01:00  INFO 11632 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-11-12T17:42:18.996+01:00  INFO 11632 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.11]
2023-11-12T17:42:19.084+01:00  INFO 11632 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-11-12T17:42:19.084+01:00  INFO 11632 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 900 ms
SparkSession getOrCreate
2023-11-12T17:42:19.486+01:00  INFO 11632 --- [           main] org.apache.spark.SparkContext            : Running Spark version 3.5.0
2023-11-12T17:42:19.486+01:00  INFO 11632 --- [           main] org.apache.spark.SparkContext            : OS info Windows 11, 10.0, amd64
2023-11-12T17:42:19.486+01:00  INFO 11632 --- [           main] org.apache.spark.SparkContext            : Java version 20.0.1
2023-11-12T17:42:19.651+01:00  INFO 11632 --- [           main] o.apache.spark.resource.ResourceUtils    : ==============================================================
2023-11-12T17:42:19.651+01:00  INFO 11632 --- [           main] o.apache.spark.resource.ResourceUtils    : No custom resources configured for spark.driver.
2023-11-12T17:42:19.652+01:00  INFO 11632 --- [           main] o.apache.spark.resource.ResourceUtils    : ==============================================================
2023-11-12T17:42:19.652+01:00  INFO 11632 --- [           main] org.apache.spark.SparkContext            : Submitted application: test-springboot-spark
2023-11-12T17:42:19.670+01:00  INFO 11632 --- [           main] o.apache.spark.resource.ResourceProfile  : Default ResourceProfile created, executor resources: Map(cores -> name: cores, amount: 1, script: , vendor: , memory -> name: memory, amount: 1024, script: , vendor: , offHeap -> name: offHeap, amount: 0, script: , vendor: ), task resources: Map(cpus -> name: cpus, amount: 1.0)
2023-11-12T17:42:19.673+01:00  INFO 11632 --- [           main] o.apache.spark.resource.ResourceProfile  : Limiting resource is cpu
2023-11-12T17:42:19.674+01:00  INFO 11632 --- [           main] o.a.s.resource.ResourceProfileManager    : Added ResourceProfile id: 0
2023-11-12T17:42:19.729+01:00  INFO 11632 --- [           main] org.apache.spark.SecurityManager         : Changing view acls to: arnaud
2023-11-12T17:42:19.729+01:00  INFO 11632 --- [           main] org.apache.spark.SecurityManager         : Changing modify acls to: arnaud
2023-11-12T17:42:19.729+01:00  INFO 11632 --- [           main] org.apache.spark.SecurityManager         : Changing view acls groups to:
2023-11-12T17:42:19.730+01:00  INFO 11632 --- [           main] org.apache.spark.SecurityManager         : Changing modify acls groups to:
2023-11-12T17:42:19.730+01:00  INFO 11632 --- [           main] org.apache.spark.SecurityManager         : SecurityManager: authentication disabled; ui acls disabled; users with view permissions: arnaud; groups with view permissions: EMPTY; users with modify permissions: arnaud; groups with modify permissions: EMPTY
2023-11-12T17:42:20.035+01:00  INFO 11632 --- [           main] org.apache.spark.util.Utils              : Successfully started service 'sparkDriver' on port 53023.
2023-11-12T17:42:20.058+01:00  INFO 11632 --- [           main] org.apache.spark.SparkEnv                : Registering MapOutputTracker
2023-11-12T17:42:20.087+01:00  INFO 11632 --- [           main] org.apache.spark.SparkEnv                : Registering BlockManagerMaster
2023-11-12T17:42:20.106+01:00  INFO 11632 --- [           main] o.a.s.s.BlockManagerMasterEndpoint       : Using org.apache.spark.storage.DefaultTopologyMapper for getting topology information
2023-11-12T17:42:20.106+01:00  INFO 11632 --- [           main] o.a.s.s.BlockManagerMasterEndpoint       : BlockManagerMasterEndpoint up
2023-11-12T17:42:20.109+01:00  INFO 11632 --- [           main] org.apache.spark.SparkEnv                : Registering BlockManagerMasterHeartbeat
2023-11-12T17:42:20.129+01:00  INFO 11632 --- [           main] o.apache.spark.storage.DiskBlockManager  : Created local directory at C:\Users\arnaud\AppData\Local\Temp\blockmgr-46db2cd2-60ec-4eb1-a9df-164849e4f17b
2023-11-12T17:42:20.154+01:00  INFO 11632 --- [           main] o.a.spark.storage.memory.MemoryStore     : MemoryStore started with capacity 1663.2 MiB
2023-11-12T17:42:20.166+01:00  INFO 11632 --- [           main] org.apache.spark.SparkEnv                : Registering OutputCommitCoordinator
2023-11-12T17:42:20.201+01:00  INFO 11632 --- [           main] org.sparkproject.jetty.util.log          : Logging initialized @3239ms to org.sparkproject.jetty.util.log.Slf4jLog
2023-11-12T17:42:20.270+01:00  INFO 11632 --- [           main] org.apache.spark.ui.JettyUtils           : Start Jetty 0.0.0.0:4040 for SparkUI
2023-11-12T17:42:20.282+01:00  INFO 11632 --- [           main] org.sparkproject.jetty.server.Server     : jetty-9.4.52.v20230823; built: 2023-08-23T19:29:37.669Z; git: abdcda73818a1a2c705da276edb0bf6581e7997e; jvm 20.0.1+9
2023-11-12T17:42:20.303+01:00  INFO 11632 --- [           main] org.sparkproject.jetty.server.Server     : Started @3341ms
2023-11-12T17:42:20.340+01:00  INFO 11632 --- [           main] o.s.jetty.server.AbstractConnector       : Started ServerConnector@7657d90b{HTTP/1.1, (http/1.1)}{0.0.0.0:4040}
2023-11-12T17:42:20.340+01:00  INFO 11632 --- [           main] org.apache.spark.util.Utils              : Successfully started service 'SparkUI' on port 4040.
2023-11-12T17:42:20.359+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@13e1e816{/,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.429+01:00  INFO 11632 --- [           main] org.apache.spark.executor.Executor       : Starting executor ID driver on host DesktopArnaud
2023-11-12T17:42:20.430+01:00  INFO 11632 --- [           main] org.apache.spark.executor.Executor       : OS info Windows 11, 10.0, amd64
2023-11-12T17:42:20.430+01:00  INFO 11632 --- [           main] org.apache.spark.executor.Executor       : Java version 20.0.1
2023-11-12T17:42:20.436+01:00  INFO 11632 --- [           main] org.apache.spark.executor.Executor       : Starting executor with user classpath (userClassPathFirst = false): ''
2023-11-12T17:42:20.437+01:00  INFO 11632 --- [           main] org.apache.spark.executor.Executor       : Created or updated repl class loader org.apache.spark.util.MutableURLClassLoader@7d1cb59f for default.
2023-11-12T17:42:20.471+01:00  INFO 11632 --- [           main] org.apache.spark.util.Utils              : Successfully started service 'org.apache.spark.network.netty.NettyBlockTransferService' on port 53024.
2023-11-12T17:42:20.471+01:00  INFO 11632 --- [           main] o.a.s.n.netty.NettyBlockTransferService  : Server created on DesktopArnaud:53024
2023-11-12T17:42:20.472+01:00  INFO 11632 --- [           main] org.apache.spark.storage.BlockManager    : Using org.apache.spark.storage.RandomBlockReplicationPolicy for block replication policy
2023-11-12T17:42:20.480+01:00  INFO 11632 --- [           main] o.a.spark.storage.BlockManagerMaster     : Registering BlockManager BlockManagerId(driver, DesktopArnaud, 53024, None)
2023-11-12T17:42:20.484+01:00  INFO 11632 --- [ckManagerMaster] o.a.s.s.BlockManagerMasterEndpoint       : Registering block manager DesktopArnaud:53024 with 1663.2 MiB RAM, BlockManagerId(driver, DesktopArnaud, 53024, None)
2023-11-12T17:42:20.487+01:00  INFO 11632 --- [           main] o.a.spark.storage.BlockManagerMaster     : Registered BlockManager BlockManagerId(driver, DesktopArnaud, 53024, None)
2023-11-12T17:42:20.488+01:00  INFO 11632 --- [           main] org.apache.spark.storage.BlockManager    : Initialized BlockManager: BlockManagerId(driver, DesktopArnaud, 53024, None)
2023-11-12T17:42:20.528+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Stopped o.s.j.s.ServletContextHandler@13e1e816{/,null,STOPPED,@Spark}
2023-11-12T17:42:20.529+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@1e0294a7{/jobs,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.530+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@15cee630{/jobs/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.531+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@246de37e{/jobs/job,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.533+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@7b79ff1c{/jobs/job/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.533+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@194037f9{/stages,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.534+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@17003497{/stages/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.535+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@376498da{/stages/stage,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.536+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@2f9addd4{/stages/stage/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.537+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@ff2266c{/stages/pool,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.538+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@83ebdc5{/stages/pool/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.538+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@14dbfdb1{/storage,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.539+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@5fbae40{/storage/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.541+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@67f591c2{/storage/rdd,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.542+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@709d6de5{/storage/rdd/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.542+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@51f34185{/environment,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.543+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@e49437{/environment/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.550+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@4c50cd7d{/executors,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.551+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@1a4930cf{/executors/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.552+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@75a226ea{/executors/threadDump,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.553+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@5a1a20ae{/executors/threadDump/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.554+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@186481d4{/executors/heapHistogram,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.555+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@2ffa91dc{/executors/heapHistogram/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.562+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@448086ab{/static,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.563+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@1ff463bb{/,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.565+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@7769d9b6{/api,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.566+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@6e794f53{/jobs/job/kill,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.567+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@69ba72da{/stages/stage/kill,null,AVAILABLE,@Spark}
2023-11-12T17:42:20.571+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@9bf63d2{/metrics/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:21.026+01:00  INFO 11632 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-11-12T17:42:21.034+01:00  INFO 11632 --- [           main] o.example.Springboot3SparkNoDepMgtMain   : Started Springboot3SparkNoDepMgtMain in 3.31 seconds (process running for 4.073)
spark.sql ...
2023-11-12T17:42:21.100+01:00  WARN 11632 --- [           main] o.apache.spark.sql.internal.SharedState  : URL.setURLStreamHandlerFactory failed to set FsUrlStreamHandlerFactory
2023-11-12T17:42:21.101+01:00  INFO 11632 --- [           main] o.apache.spark.sql.internal.SharedState  : Setting hive.metastore.warehouse.dir ('null') to the value of spark.sql.warehouse.dir.
2023-11-12T17:42:21.109+01:00  INFO 11632 --- [           main] o.apache.spark.sql.internal.SharedState  : Warehouse path is 'file:/C:/arn/devPerso/test-snippets/test-springboot-spark/springboot3-spark-no-depmgt/spark-warehouse'.
2023-11-12T17:42:21.122+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@1b3bb287{/SQL,null,AVAILABLE,@Spark}
2023-11-12T17:42:21.123+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@625f5712{/SQL/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:21.124+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@6a38e3d1{/SQL/execution,null,AVAILABLE,@Spark}
2023-11-12T17:42:21.125+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@bdda8a7{/SQL/execution/json,null,AVAILABLE,@Spark}
2023-11-12T17:42:21.126+01:00  INFO 11632 --- [           main] o.s.jetty.server.handler.ContextHandler  : Started o.s.j.s.ServletContextHandler@1df9f7c6{/static/sql,null,AVAILABLE,@Spark}
2023-11-12T17:42:23.140+01:00  INFO 11632 --- [           main] o.a.s.s.c.e.codegen.CodeGenerator        : Code generated in 168.902 ms
2023-11-12T17:42:23.225+01:00  INFO 11632 --- [           main] org.apache.spark.SparkContext            : Starting job: show at Springboot3SparkNoDepMgtMain.java:45
2023-11-12T17:42:23.239+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Got job 0 (show at Springboot3SparkNoDepMgtMain.java:45) with 1 output partitions
2023-11-12T17:42:23.239+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Final stage: ResultStage 0 (show at Springboot3SparkNoDepMgtMain.java:45)
2023-11-12T17:42:23.239+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Parents of final stage: List()
2023-11-12T17:42:23.239+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Missing parents: List()
2023-11-12T17:42:23.241+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Submitting ResultStage 0 (MapPartitionsRDD[2] at show at Springboot3SparkNoDepMgtMain.java:45), which has no missing parents
2023-11-12T17:42:23.282+01:00  INFO 11632 --- [uler-event-loop] o.a.spark.storage.memory.MemoryStore     : Block broadcast_0 stored as values in memory (estimated size 7.7 KiB, free 1663.2 MiB)
2023-11-12T17:42:23.332+01:00  INFO 11632 --- [uler-event-loop] o.a.spark.storage.memory.MemoryStore     : Block broadcast_0_piece0 stored as bytes in memory (estimated size 3.9 KiB, free 1663.2 MiB)
2023-11-12T17:42:23.334+01:00  INFO 11632 --- [ckManagerMaster] o.apache.spark.storage.BlockManagerInfo  : Added broadcast_0_piece0 in memory on DesktopArnaud:53024 (size: 3.9 KiB, free: 1663.2 MiB)
2023-11-12T17:42:23.337+01:00  INFO 11632 --- [uler-event-loop] org.apache.spark.SparkContext            : Created broadcast 0 from broadcast at DAGScheduler.scala:1580
2023-11-12T17:42:23.350+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Submitting 1 missing tasks from ResultStage 0 (MapPartitionsRDD[2] at show at Springboot3SparkNoDepMgtMain.java:45) (first 15 tasks are for partitions Vector(0))
2023-11-12T17:42:23.352+01:00  INFO 11632 --- [uler-event-loop] o.a.spark.scheduler.TaskSchedulerImpl    : Adding task set 0.0 with 1 tasks resource profile 0
2023-11-12T17:42:23.414+01:00  INFO 11632 --- [er-event-loop-6] o.apache.spark.scheduler.TaskSetManager  : Starting task 0.0 in stage 0.0 (TID 0) (DesktopArnaud, executor driver, partition 0, PROCESS_LOCAL, 7967 bytes)
2023-11-12T17:42:23.433+01:00  INFO 11632 --- [age 0.0 (TID 0)] org.apache.spark.executor.Executor       : Running task 0.0 in stage 0.0 (TID 0)
2023-11-12T17:42:23.507+01:00  INFO 11632 --- [age 0.0 (TID 0)] o.a.s.s.c.e.codegen.CodeGenerator        : Code generated in 7.2939 ms
2023-11-12T17:42:23.525+01:00  INFO 11632 --- [age 0.0 (TID 0)] org.apache.spark.executor.Executor       : Finished task 0.0 in stage 0.0 (TID 0). 1560 bytes result sent to driver
2023-11-12T17:42:23.532+01:00  INFO 11632 --- [result-getter-0] o.apache.spark.scheduler.TaskSetManager  : Finished task 0.0 in stage 0.0 (TID 0) in 136 ms on DesktopArnaud (executor driver) (1/1)
2023-11-12T17:42:23.534+01:00  INFO 11632 --- [result-getter-0] o.a.spark.scheduler.TaskSchedulerImpl    : Removed TaskSet 0.0, whose tasks have all completed, from pool
2023-11-12T17:42:23.538+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : ResultStage 0 (show at Springboot3SparkNoDepMgtMain.java:45) finished in 0,287 s
2023-11-12T17:42:23.540+01:00  INFO 11632 --- [uler-event-loop] o.apache.spark.scheduler.DAGScheduler    : Job 0 is finished. Cancelling potential speculative or zombie tasks for this job
2023-11-12T17:42:23.540+01:00  INFO 11632 --- [uler-event-loop] o.a.spark.scheduler.TaskSchedulerImpl    : Killing all running tasks in stage 0: Stage finished
2023-11-12T17:42:23.542+01:00  INFO 11632 --- [           main] o.apache.spark.scheduler.DAGScheduler    : Job 0 finished: show at Springboot3SparkNoDepMgtMain.java:45, took 0,316509 s
2023-11-12T17:42:23.577+01:00  INFO 11632 --- [ckManagerMaster] o.apache.spark.storage.BlockManagerInfo  : Removed broadcast_0_piece0 on DesktopArnaud:53024 in memory (size: 3.9 KiB, free: 1663.2 MiB)
2023-11-12T17:42:24.380+01:00  INFO 11632 --- [           main] o.a.s.s.c.e.codegen.CodeGenerator        : Code generated in 13.0032 ms
+---+
|  1|
+---+
|  1|
+---+
```


