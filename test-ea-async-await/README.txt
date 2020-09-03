
see https://github.com/electronicarts/ea-async

The code is doing basically

	String res1 = await(asyncBar("init", "1")); // ... scheduled concatenation to get "init,1"
    String res2 = await(asyncBar(res1, "2"));   // ... scheduled concatenation to get "init,1,2"
	System.out.println("run => " + res2);

where asyncBar(x,y) is the scheduled (1 second later) concatenation of ( x + "," + y)

To run, 

	mvn clean package
	java -jar ./target/test-ea-async-await-0.0.1-SNAPSHOT.jar


To see instrumented code after ea-async-maven-plugin, execute shell script

	mvn clean package; \	
	( rm -rf tmp; mkdir tmp; cd tmp; \
		jar xf ../target/test-ea-async-await-0.0.1-SNAPSHOT.jar; \
		cd BOOT-INF/classes/fr/an/tests/; \
		javap -c EaAsyncAwaitAppMain.class; \
		cd ../../../../../..; rm -rf tmp; \
	) >  EaAsyncAwaitAppMain-javap.txt


The final code does not contains "com.ea.async.Async.await()" method !
... only "thenCompose()", "exceptionally()", "apply()", "join()" from java.util.CompletableFuture


See also: https://github.com/vsilaev/tascalate-async-await
