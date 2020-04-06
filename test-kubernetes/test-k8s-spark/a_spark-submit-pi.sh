spark-submit \
	--master k8s://http://localhost:8001 \
	--deploy-mode cluster \
	--name test-k8s-spark \
	--class org.apache.spark.examples.SparkPi \
	--conf spark.executor.instances=2 \
	--conf spark.kubernetes.executor.request.cores=0 \
	--conf spark.kubernetes.driver.label.my-spark-label=mypi \
	--conf spark.kubernetes.container.image=localhost:5000/spark:a_3.1.0_2 \
	--conf spark.kubernetes.container.image.pullPolicy=Never \
	--conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
	local:///opt/spark/examples/jars/spark-examples_2.12-3.1.0-SNAPSHOT.jar 10000

