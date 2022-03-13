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

