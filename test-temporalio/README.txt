Test for Temporalio
----


MyWorkflow1Test.java
=> 
```
10:32:45.558 [main] INFO  i.t.s.WorkflowServiceStubsImpl - Created GRPC client for channel: ManagedChannelOrphanWrapper{delegate=ManagedChannelImpl{logId=1, target=directaddress:///996841ba-e779-4d77-910e-a561cb3838ad}}
10:32:45.581 [main] INFO  i.t.s.WorkflowServiceStubsImpl - Created GRPC client for channel: ManagedChannelOrphanWrapper{delegate=ManagedChannelImpl{logId=5, target=directaddress:///7eb8984a-f4ed-4b2d-b735-22bdd4823dfb}}
10:32:46.078 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=2, pollThreadNamePrefix='Workflow Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.081 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=1, pollThreadNamePrefix='Local Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.083 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=5, pollThreadNamePrefix='Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.086 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=5, pollThreadNamePrefix='Host Local Workflow Poller'}, identity=babcd1f5-e596-4e54-9f0f-df1f4e6fe5e4}
10:32:46.616 [main] INFO  io.temporal.worker.WorkerFactory - shutdown
10:32:46.616 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.618 [Host Local Workflow Poller: 5] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.618 [Host Local Workflow Poller: 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.618 [Host Local Workflow Poller: 4] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.618 [Host Local Workflow Poller: 3] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.618 [Host Local Workflow Poller: 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.619 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.620 [Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 3] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 4] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 5] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.620 [Local Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.620 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.623 [Workflow Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.623 [Workflow Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.624 [main] INFO  io.temporal.worker.WorkerFactory - shutdownNow
10:32:46.624 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Host Local Workflow Poller
10:32:46.626 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"
10:32:46.626 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Local Activity Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"
10:32:46.626 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Workflow Poller taskQueue="WorkflowTest-testMockedGetGreeting-98ffae43-5c5b-4dbe-b48e-76bd4c5dfb1f", namespace="UnitTest"
10:32:46.626 [main] INFO  io.temporal.worker.WorkerFactory - awaitTermination begin
10:32:46.626 [main] INFO  io.temporal.worker.WorkerFactory - awaitTermination done
10:32:46.626 [main] INFO  i.t.s.WorkflowServiceStubsImpl - shutdown
10:32:46.628 [main] INFO  i.t.s.WorkflowServiceStubsImpl - shutdownNow
10:32:46.636 [main] INFO  i.t.s.WorkflowServiceStubsImpl - Created GRPC client for channel: ManagedChannelOrphanWrapper{delegate=ManagedChannelImpl{logId=9, target=directaddress:///e38da776-26e5-4f7f-b7ff-c9b7ddd0d67f}}
10:32:46.639 [main] INFO  i.t.s.WorkflowServiceStubsImpl - Created GRPC client for channel: ManagedChannelOrphanWrapper{delegate=ManagedChannelImpl{logId=13, target=directaddress:///3e2a3344-d060-416c-a33f-11c27cc70b90}}
10:32:46.641 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=2, pollThreadNamePrefix='Workflow Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.641 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=1, pollThreadNamePrefix='Local Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.641 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=5, pollThreadNamePrefix='Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"'}, identity=17364@DESKTOP-2EGCC8R}
10:32:46.642 [main] INFO  io.temporal.internal.worker.Poller - start(): Poller{options=PollerOptions{maximumPollRateIntervalMilliseconds=1000, maximumPollRatePerSecond=0.0, pollBackoffCoefficient=2.0, pollBackoffInitialInterval=PT0.1S, pollBackoffMaximumInterval=PT1M, pollThreadCount=5, pollThreadNamePrefix='Host Local Workflow Poller'}, identity=ad9d137f-8e5f-4ecf-8dc5-b25267434b77}
10:32:46.652 [main] INFO  io.temporal.worker.WorkerFactory - shutdown
10:32:46.652 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.652 [Host Local Workflow Poller: 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.652 [Host Local Workflow Poller: 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.652 [Host Local Workflow Poller: 5] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.652 [Host Local Workflow Poller: 3] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [Host Local Workflow Poller: 4] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.653 [Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 5] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 4] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 3] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.653 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.653 [Local Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.654 [main] INFO  io.temporal.internal.worker.Poller - shutdown
10:32:46.654 [Workflow Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 1] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.654 [Workflow Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest": 2] INFO  io.temporal.internal.worker.Poller - poll loop is terminated
10:32:46.654 [main] INFO  io.temporal.worker.WorkerFactory - shutdownNow
10:32:46.654 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Host Local Workflow Poller
10:32:46.654 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"
10:32:46.654 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Local Activity Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"
10:32:46.654 [main] INFO  io.temporal.internal.worker.Poller - shutdownNow poller=Workflow Poller taskQueue="WorkflowTest-testGetGreeting-246741b2-30c1-4f13-bb3d-e1cf79ac728e", namespace="UnitTest"
10:32:46.654 [main] INFO  io.temporal.worker.WorkerFactory - awaitTermination begin
10:32:46.654 [main] INFO  io.temporal.worker.WorkerFactory - awaitTermination done
10:32:46.654 [main] INFO  i.t.s.WorkflowServiceStubsImpl - shutdown
10:32:46.654 [main] INFO  i.t.s.WorkflowServiceStubsImpl - shutdownNow
```



Thread Dump 1: junit 
	-> call user-defined @WorkflowMethod method for init 
	-> call GenericWorkflowClientExternal.start(..)
	-> WorkflowServiceStubs.startWorkflowExecution(..)
	-> gRPC WorkflowServiceGrpc.WorkflowServiceBlockingStub.startWorkflowExecution(gRPC io.temporal.api.workflowservice.v1.StartWorkflowExecutionRequest)
	
	WorkflowServiceGrpc$WorkflowServiceBlockingStub.startWorkflowExecution(StartWorkflowExecutionRequest) line: 2628	
	GenericWorkflowClientExternalImpl.lambda$start$0(Scope, StartWorkflowExecutionRequest) line: 88	
	26966666.apply() line: not available	
	GrpcSyncRetryer.retry(RpcRetryOptions, RetryableFunc<R,T>) line: 61	
	GrpcRetryer.retryWithResult(RpcRetryOptions, RetryableFunc<R,T>) line: 51	
	GenericWorkflowClientExternalImpl.start(StartWorkflowExecutionRequest) line: 73	
	RootWorkflowClientInvoker.start(WorkflowClientCallsInterceptor$WorkflowStartInput) line: 55	
	WorkflowStubImpl.startWithOptions(WorkflowOptions, Object...) line: 113	
	WorkflowStubImpl.start(Object...) line: 138	
	TestWorkflowEnvironmentInternal$TimeLockingInterceptor$TimeLockingWorkflowStub.start(Object...) line: 272	
	WorkflowInvocationHandler.startWorkflow(WorkflowStub, Object[]) line: 192	
	WorkflowInvocationHandler.access$300(WorkflowStub, Object[]) line: 48	
	WorkflowInvocationHandler$SyncWorkflowInvocationHandler.startWorkflow(WorkflowStub, Method, Object[]) line: 314	
	WorkflowInvocationHandler$SyncWorkflowInvocationHandler.invoke(POJOWorkflowInterfaceMetadata, WorkflowStub, Method, Object[]) line: 270	
	WorkflowInvocationHandler.invoke(Object, Method, Object[]) line: 178	
	$Proxy11.getGreeting(String) line: not available	
	MyWorkflow1Test.testGetGreeting() line: 41	
	
On Server-side ... 

Thread [main] (Suspended (breakpoint at line 292 in TestWorkflowService))	
	owns: InProcessTransport$InProcessStream$InProcessClientStream  (id=3993)	
	TestWorkflowService.startWorkflowExecution(StartWorkflowExecutionRequest, StreamObserver<StartWorkflowExecutionResponse>) line: 292	
	WorkflowServiceGrpc$MethodHandlers<Req,Resp>.invoke(Req, StreamObserver<Resp>) line: 3630	
	ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose() line: 182	

.. register state

		TestWorkflowMutableState mutableState =
	        new TestWorkflowMutableStateImpl(startRequest, runId, retryState, backoffStartInterval, lastCompletionResult, lastFailure, parent, parentChildInitiatedEventId, continuedExecutionRunId, this, store);
	    executionsByWorkflowId.put(workflowId, mutableState);
	    executions.put(executionId, mutableState);
	    mutableState.startWorkflow(continuedExecutionRunId.isPresent(), signalWithStartSignal);

		// new TestWorkflowMutableStateImpl(..)
	    class TestWorkflowMutableStateImpl {
	      private final ExecutionId executionId;
		  private final Optional<TestWorkflowMutableState> parent;
		  private final OptionalLong parentChildInitiatedEventId;
		  private final TestWorkflowStore store;
		  private final TestWorkflowService service;
		  private final StartWorkflowExecutionRequest startRequest;
		  private long nextEventId = 1;
		  private final Map<Long, StateMachine<ActivityTaskData>> activities = new HashMap<>();
		  private final Map<String, Long> activityById = new HashMap<>();
		  private final Map<Long, StateMachine<ChildWorkflowData>> childWorkflows = new HashMap<>();
		  private final Map<String, StateMachine<TimerData>> timers = new HashMap<>();
		  private final Map<String, StateMachine<SignalExternalData>> externalSignals = new HashMap<>();
		  private final Map<String, StateMachine<CancelExternalData>> externalCancellations = new HashMap<>();
		  private final StateMachine<WorkflowData> workflow;
		  /** A single workflow task state machine is used for the whole workflow lifecycle. */
		  private final StateMachine<WorkflowTaskData> workflowTaskStateMachine;
		
		  private final Map<String, CompletableFuture<QueryWorkflowResponse>> queries =
		      new ConcurrentHashMap<>();
		  public StickyExecutionAttributes stickyExecutionAttributes;
		
		}
		
		WorkflowData data =
	        new WorkflowData(retryState, ProtobufTimeUtils.toProtoDuration(backoffStartInterval), startRequest.getCronSchedule(), lastCompletionResult, lastFailure, runId, continuedExecutionRunId);
	    mutableState.workflow = StateMachines.newWorkflowStateMachine(data);
	    mutableState.workflowTaskStateMachine = StateMachines.newWorkflowTaskStateMachine(store, startRequest);

	    // mutableState.workflow = StateMachines.newWorkflowStateMachine(data) => 
		    new StateMachine<>(data)
	        .add(NONE, START, STARTED, StateMachines::startWorkflow)
	        .add(STARTED, COMPLETE, COMPLETED, StateMachines::completeWorkflow)
	        .add(STARTED, CONTINUE_AS_NEW, CONTINUED_AS_NEW, StateMachines::continueAsNewWorkflow)
	        .add(STARTED, FAIL, FAILED, StateMachines::failWorkflow)
	        .add(STARTED, TIME_OUT, TIMED_OUT, StateMachines::timeoutWorkflow)
	        .add(STARTED, REQUEST_CANCELLATION, CANCELLATION_REQUESTED, StateMachines::requestWorkflowCancellation)
	        .add(STARTED, TERMINATE, TERMINATED, StateMachines::terminateWorkflow)
	        .add(CANCELLATION_REQUESTED, COMPLETE, COMPLETED, StateMachines::completeWorkflow)
	        .add(CANCELLATION_REQUESTED, CANCEL, CANCELED, StateMachines::cancelWorkflow)
	        .add(CANCELLATION_REQUESTED, TERMINATE, TERMINATED, StateMachines::terminateWorkflow)
	        .add(CANCELLATION_REQUESTED, FAIL, FAILED, StateMachines::failWorkflow)
	        .add(CANCELLATION_REQUESTED, TIME_OUT, TIMED_OUT, StateMachines::timeoutWorkflow);

		// StateMachines.newWorkflowTaskStateMachine 
	    mutableState.workflow = new StateMachine<>(new WorkflowTaskData(store, startRequest))
	        .add(NONE, INITIATE, INITIATED, StateMachines::scheduleWorkflowTask)
	        .add(INITIATED, INITIATE, INITIATED, StateMachines::noop)
	        .add(INITIATED, QUERY, INITIATED, StateMachines::queryWhileScheduled)
	        .add(INITIATED, START, STARTED, StateMachines::startWorkflowTask)
	        .add(STARTED, QUERY, STARTED, StateMachines::bufferQuery)
	        .add(STARTED, COMPLETE, NONE, StateMachines::completeWorkflowTask)
	        .add(STARTED, FAIL, NONE, StateMachines::failWorkflowTask)
	        .add(STARTED, TIME_OUT, NONE, StateMachines::timeoutWorkflowTask)
	        .add(STARTED, INITIATE, STARTED, StateMachines::needsWorkflowTask);

	        
	        
		mutableState.update(ctx -> {
            workflow.action(StateMachines.Action.START, ctx, startRequest, 0); // NONE -> STARTED
            ..
            scheduleWorkflowTask(ctx);
			..
            workflowTaskStateMachine.action(StateMachines.Action.INITIATE, ctx, startRequest, 0); // NONE -> INITIATED
		    ctx.lockTimer("scheduleWorkflowTask");
			ctx.addTimer(runTimeout, this::timeoutWorkflow, "workflow execution timeout");
			ctx.commitChanges(store);
			
			history = new HistoryStore(executionId, lock);
        	histories.put(executionId, history);
        	history.addAllLocked(events, ctx.currentTime());
        	
      	    WorkflowTask workflowTask = ctx.getWorkflowTask();
		    TaskQueueId id = new TaskQueueId(..)
			BlockingQueue<PollWorkflowTaskQueueResponse.Builder> workflowTaskQueue = getWorkflowTaskQueueQueue(id);
      		workflowTaskQueue.add(workflowTask.getTask());

      		List<ActivityTask> activityTasks = ctx.getActivityTasks(); .. empty
    	    List<Timer> timers = ctx.getTimers();
		    for (Timer t : timers) {
	        	t.setCancellationHandle( timerService.schedule(t.getDelay(), t.getCallback(), t.getTaskInfo()) );
        

Worker ... polling ActivityTask from PollTaskExecutor
	-> calling Impl @WorkflowMethod method 

	Thread [Activity Executor taskQueue="WorkflowTest-testGetGreeting-330cff14-990e-41bd-9fd4-855a50219e6b", namespace="UnitTest": 1] (Suspended (breakpoint at line 10 in MySimpleActivityImpl))	
		MySimpleActivityImpl.sayHello(String) line: 10	
		NativeMethodAccessorImpl.invoke0(Method, Object, Object[]) line: not available [native method]	
		NativeMethodAccessorImpl.invoke(Object, Object[]) line: 62	
		DelegatingMethodAccessorImpl.invoke(Object, Object[]) line: 43	
		Method.invoke(Object, Object...) line: 498	
		POJOActivityTaskHandler$POJOActivityInboundCallsInterceptor.execute(ActivityInboundCallsInterceptor$ActivityInput) line: 286	
		POJOActivityTaskHandler$POJOActivityImplementation.execute(ActivityInfoInternal, Scope) line: 252	
		POJOActivityTaskHandler.handle(ActivityTask, Scope, boolean) line: 209	
		ActivityWorker$TaskHandlerImpl.handle(ActivityTask) line: 193	
		ActivityWorker$TaskHandlerImpl.handle(Object) line: 151	
		PollTaskExecutor<T>.lambda$process$0(Object) line: 73	

    
Thread	
	TestWorkflowService.respondWorkflowTaskCompleted(RespondWorkflowTaskCompletedRequest, StreamObserver<RespondWorkflowTaskCompletedResponse>) line: 490	
	WorkflowServiceGrpc$MethodHandlers<Req,Resp>.invoke(Req, StreamObserver<Resp>) line: 3642	
	ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose() line: 182	

	WorkflowTaskToken taskToken = WorkflowTaskToken.fromBytes(request.getTaskToken());
    TestWorkflowMutableState mutableState = getMutableState(taskToken.getExecutionId());
    mutableState.completeWorkflowTask(taskToken.getHistorySize(), request);        


ActivityWorker thread Activity polling 
	-> invoke method
	-> send reply gRPC respondActivityTaskCompleted(..)

	Thread [Activity Executor taskQueue="WorkflowTest-testGetGreeting-bdfb9f17-448a-435a-b6ab-e061fed866b1", namespace="UnitTest": 1] (Suspended)	
		POJOActivityTaskHandler.handle(ActivityTask, Scope, boolean) line: 188	
		ActivityWorker$TaskHandlerImpl.handle(ActivityTask) line: 193	
		ActivityWorker$TaskHandlerImpl.handle(Object) line: 151	
		PollTaskExecutor<T>.lambda$process$0(Object) line: 73	
		21879689.run() line: not available	
		ThreadPoolExecutor.runWorker(ThreadPoolExecutor$Worker) line: 1149	
		ThreadPoolExecutor$Worker.run() line: 624	
		Thread.run() line: 748	

		ThreadPoolExecutor
			void runWorker(Worker w) {
	            while ((task = getTask()) != null) {
					task.run();
                        
		public void process(T task) {
		    taskExecutor.execute(() -> {
		            handler.handle(task);
	            
		PollActivityTaskQueueResponse r = task.getResponse();
	
		public ActivityTaskHandler.Result execute(ActivityInfoInternal info, Scope metricsScope) {
			Optional<Payloads> input = info.getInput();
			Object[] args = DataConverter.arrayFromPayloads(dataConverter,input, method.getParameterTypes(), method.getGenericParameterTypes());

	        result = method.invoke(.. args)
                
			new ActivityOutput(result);
			
			// sendReply(PollActivityTaskQueueResponse task, ..) {
	            service.respondActivityTaskCompleted(request));



ActivityWorker thread   Activity completed 
	 
	Thread [Activity Executor taskQueue="WorkflowTest-testGetGreeting-330cff14-990e-41bd-9fd4-855a50219e6b", namespace="UnitTest": 1] (Suspended (breakpoint at line 612 in TestWorkflowService))	
		owns: InProcessTransport$InProcessStream$InProcessClientStream  (id=101)	
		TestWorkflowService.respondActivityTaskCompleted(RespondActivityTaskCompletedRequest, StreamObserver<RespondActivityTaskCompletedResponse>) line: 612	
		WorkflowServiceGrpc$MethodHandlers<Req,Resp>.invoke(Req, StreamObserver<Resp>) line: 3662	
		ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose() line: 182	
		ServerCallImpl$ServerStreamListenerImpl<ReqT>.halfClosed() line: 335	
		ServerImpl$JumpToApplicationThreadServerStreamListener$1HalfClosed.runInContext() line: 866	
		ServerImpl$JumpToApplicationThreadServerStreamListener$1HalfClosed(ContextRunnable).run() line: 37	
		SerializeReentrantCallsDirectExecutor.execute(Runnable) line: 49	
		ServerImpl$JumpToApplicationThreadServerStreamListener.halfClosed() line: 877	
		InProcessTransport$InProcessStream$InProcessClientStream.halfClose() line: 799	
		InternalSubchannel$CallTracingTransport$1(ForwardingClientStream).halfClose() line: 72	
		RetriableStream$1HalfCloseEntry.runWith(RetriableStream$Substream) line: 655	
		ManagedChannelImpl$ChannelStreamProvider$1RetryStream<ReqT>(RetriableStream<ReqT>).delayOrExecute(RetriableStream$BufferEntry) line: 526	
		ManagedChannelImpl$ChannelStreamProvider$1RetryStream<ReqT>(RetriableStream<ReqT>).halfClose() line: 659	
		ClientCallImpl<ReqT,RespT>.halfCloseInternal() line: 497	
		ClientCallImpl<ReqT,RespT>.halfClose() line: 486	
		GrpcMetricsInterceptor$MetricsClientCall<ReqT,RespT>(PartialForwardingClientCall<ReqT,RespT>).halfClose() line: 44	
		GrpcMetricsInterceptor$MetricsClientCall<ReqT,RespT>(ForwardingClientCall<ReqT,RespT>).halfClose() line: 22	
		GrpcMetricsInterceptor$MetricsClientCall<ReqT,RespT>(ForwardingClientCall$SimpleForwardingClientCall<ReqT,RespT>).halfClose() line: 44	
		MetadataUtils$HeaderAttachingClientInterceptor$HeaderAttachingClientCall<ReqT,RespT>(PartialForwardingClientCall<ReqT,RespT>).halfClose() line: 44	
		MetadataUtils$HeaderAttachingClientInterceptor$HeaderAttachingClientCall<ReqT,RespT>(ForwardingClientCall<ReqT,RespT>).halfClose() line: 22	
		MetadataUtils$HeaderAttachingClientInterceptor$HeaderAttachingClientCall<ReqT,RespT>(ForwardingClientCall$SimpleForwardingClientCall<ReqT,RespT>).halfClose() line: 44	
		ClientCalls.asyncUnaryRequestCall(ClientCall<ReqT,RespT>, ReqT, StartableListener<RespT>) line: 309	
		ClientCalls.futureUnaryCall(ClientCall<ReqT,RespT>, ReqT) line: 218	
		ClientCalls.blockingUnaryCall(Channel, MethodDescriptor<ReqT,RespT>, CallOptions, ReqT) line: 146	
		WorkflowServiceGrpc$WorkflowServiceBlockingStub.respondActivityTaskCompleted(RespondActivityTaskCompletedRequest) line: 2740	
		ActivityWorker$TaskHandlerImpl.lambda$sendReply$0(Scope, RespondActivityTaskCompletedRequest) line: 267	
		12583371.apply() line: not available	
		GrpcRetryer.lambda$retry$0(GrpcRetryer$RetryableProc) line: 44	
		24412100.apply() line: not available	
		GrpcSyncRetryer.retry(RpcRetryOptions, RetryableFunc<R,T>) line: 61	
		GrpcRetryer.retryWithResult(RpcRetryOptions, RetryableFunc<R,T>) line: 51	
		GrpcRetryer.retry(RpcRetryOptions, RetryableProc<T>) line: 41	
		ActivityWorker$TaskHandlerImpl.sendReply(PollActivityTaskQueueResponse, ActivityTaskHandler$Result, Scope) line: 261	
		ActivityWorker$TaskHandlerImpl.handle(ActivityTask) line: 197	
		ActivityWorker$TaskHandlerImpl.handle(Object) line: 151	
		PollTaskExecutor<T>.lambda$process$0(Object) line: 73	
		20765620.run() line: not available	
		ThreadPoolExecutor.runWorker(ThreadPoolExecutor$Worker) line: 1149	
		ThreadPoolExecutor$Worker.run() line: 624	
		Thread.run() line: 748	


.. continued 2nd step in workflow Impl .. (runHelloThenGoodBye)

	Thread [workflow-method] (Suspended (breakpoint at line 25 in MyWorkflow1Impl))	
		MyWorkflow1Impl.runHelloThenGoodBye(String) line: 25	
		NativeMethodAccessorImpl.invoke0(Method, Object, Object[]) line: not available [native method]	
		NativeMethodAccessorImpl.invoke(Object, Object[]) line: 62	
		DelegatingMethodAccessorImpl.invoke(Object, Object[]) line: 43	
		Method.invoke(Object, Object...) line: 498	
		POJOWorkflowImplementationFactory$POJOWorkflowImplementation$RootWorkflowInboundCallsInterceptor.execute(WorkflowInboundCallsInterceptor$WorkflowInput) line: 317	
		POJOWorkflowImplementationFactory$POJOWorkflowImplementation.execute(Header, Optional<Payloads>) line: 292	
		WorkflowExecuteRunnable.run() line: 72	
		SyncWorkflow.lambda$start$0() line: 133	
		33515775.run() line: not available	
		CancellationScopeImpl.run() line: 101	
		WorkflowThreadImpl$RunnableWrapper.run() line: 110	
		Executors$RunnableAdapter<T>.call() line: 511	
		FutureTask<V>.run() line: 266	
		ThreadPoolExecutor.runWorker(ThreadPoolExecutor$Worker) line: 1149	
		ThreadPoolExecutor$Worker.run() line: 624	
		Thread.run() line: 748	

	