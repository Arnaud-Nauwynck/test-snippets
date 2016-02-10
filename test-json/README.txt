

tests:

curl http://0.0.0.0:8080/springweb/hello/helloString

curl http://0.0.0.0:8080/springweb/hello/helloArray

curl http://0.0.0.0:8080/springweb/hello/helloMap

curl http://0.0.0.0:8080/springweb/hello/helloObj

curl http://0.0.0.0:8080/cxf/hello/helloString

curl http://0.0.0.0:8080/cxf/hello/helloArray

curl http://0.0.0.0:8080/cxf/hello/helloMap

curl http://0.0.0.0:8080/cxf/hello/helloObj


curl http://0.0.0.0:8080/cxf/file/file1.txt
# => get content "test"


# curl -x POST http://0.0.0.0:8080/cxf/file/file2.txt -b "test2"
