
#TODO.. currently fails

spark-submit \
	--master k8s://http://localhost:8001 \
	--deploy-mode client \
	--name test-k8s-spark  \
	--class fr.an.tests.testk8sspark.SparkApp \
	--conf spark.executor.instances=1 \
	--conf spark.kubernetes.executor.request.cores=0 \
	--conf spark.kubernetes.driver.label.my-spark-label=test-k8s-spark \
	--conf spark.kubernetes.container.image=localhost:5000/a-test-k8s-spark:3 \
	--conf spark.kubernetes.container.image.pullPolicy=Never \
	--conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
	target/test-k8s-spark-1.0-SNAPSHOT.jar

