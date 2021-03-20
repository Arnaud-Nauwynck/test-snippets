# Test Byteman


## Introduction

This snippet project show a sample usage of Byteman jvm instrumetnation, via file script.btm


see https://github.com/bytemanproject/byteman

https://github.com/bytemanproject/byteman/tree/main/sample/scripts


Show instrumentation for METHOD, AFTER INVOKE, AT EXIT, AT LINE, AT WRITE, ... with simple BIND and logs 

 

## How to Test

### Step 1
 run the instrumented application test-snippets/jvm-thread-tool/jvm-thread-tool-server
with additionnal jvm args:

```
	-javaagent:byteman-4.0.14.jar=script:script.btm
```

### Step 2
 call some http methods

```
	$ curl http://localhost:8080/flame-graph
```


### Step 3
 see instrumented logs on server app
 
```
	### RULE ThreadToolRestController.getFlameGraph: getFlameGraph this:fr.an.jvm.thread.tool.server.ThreadToolRestController@2e16923c
	### RULE FlameGraphUtil: getDefaultFlameGraph
	### RULE ThreadToolStatefullService.getFlameGraph AFTER INVOKE getDefaultFlameGraph res:fr.an.jvm.thread.tool.server.FlameGraphNodeDTO@133f33a1
	### RULE ThreadToolRestController.getFlameGraph AFTER INVOKE  res:fr.an.jvm.thread.tool.server.FlameGraphNodeDTO@133f33a1
	### RULE ThreadToolRestController.getFlameGraph AT LINE: getFlameGraph
	### RULE ThreadToolRestController.getFlameGraph WRITE lastFlameGraph: getFlameGraph lastFlameGraph:
	### RULE ThreadToolRestController.getFlameGraph READ lastFlameGraph: getFlameGraph lastFlameGraph:
	### RULE ThreadToolRestController.getFlameGraph AT EXIT: getFlameGraph res:fr.an.jvm.thread.tool.server.FlameGraphNodeDTO@133f33a1
	### RULE ThreadToolRestController.getFlameGraph AT EXIT 2: getFlameGraph res:fr.an.jvm.thread.tool.server.FlameGraphNodeDTO@133f33a1
```
