h1. Test Atomix


start 3 nodes server
(using Eclipse launcher: AtomixAppMain-node1-2-3.launch)
or explicitly...

	for node in node1 node2 node3; do
		( cd "nodes/${node}"
		java -cp ../../target/classes fr.an.test.atomix.AtomixAppMain
		)
	done


h2. Test AtomicValue Integer

	$ curl -X POST http://localhost:8081/api/atomix/atomicInt/var1/getAndSet/123
	
	<empty>
	
	$ curl -X POST http://localhost:8081/api/atomix/atomicInt/var1/getAndSet/124
	123
	
	$ curl -X POST http://localhost:8083/api/atomix/atomicInt/var1/getAndSet/125
	124
	
	$ curl http://localhost:8082/api/atomix/atomicInt/var1
	125


h2. Test Leader Election + handle if current master (and broadcast changes) or redispatch to Master


	$ curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8081/api/foo/meth1 -d '{"topic":"topic1", "msg":"msg1"}'
	{"msg":" from node2:msg1"}
	
	$ curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8082/api/foo/meth1 -d '{"topic":"topic1", "msg":"msg1"}'
	{"msg":" from node2:msg1"}
	
	$ curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/api/foo/meth1 -d '{"topic":"topic1", "msg":"msg1"}'
	{"msg":" from node2:msg1"}


example of logs:

case 1: Http request received on master

	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - handle as master meth1 req msg:msg2


case 2: Http request received on follower (=> redispatch to master, and also receive side-effects)

	INFO  f.a.t.atomix.rest.FooRestController - meth1
	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - meth1 topic:topic1 => partitionIndex:0 => curr leader: node1
	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - curr follower .. redispath to leader
	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - return (from leader):  from node1:msg2
	INFO  f.a.t.atomix.rest.FooRestController - meth1 => FooMeth1ResponseDTO(msg= from node1:msg2)
	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - notifyMeth1_asFollower topic:topic1 msg:msg2 , resp:msg2


case 3: event notification (for side-effects) received on all other nodes

	INFO  f.a.t.a.s.FooDispatchLeaderStatefullService - notifyMeth1_asFollower topic:topic1 msg:msg2 , resp:msg2
