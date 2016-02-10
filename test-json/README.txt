

# tests:

curl http://0.0.0.0:8080/springweb/hello/helloString
# => Hello World!

curl http://0.0.0.0:8080/springweb/hello/helloArray
# => ["Hello","World!"]

curl http://0.0.0.0:8080/springweb/hello/helloMap
# => {"en":"Hello World"}
 
curl http://0.0.0.0:8080/springweb/hello/helloObj
# => {"msg":"Hello"}

curl http://0.0.0.0:8080/cxf/hello/helloString
# => Hello World!

curl http://0.0.0.0:8080/cxf/hello/helloArray
# => ["Hello","World!"]

curl http://0.0.0.0:8080/cxf/hello/helloMap
# => {"en":"Hello World"}

curl http://0.0.0.0:8080/cxf/hello/helloObj
# => {"msg":"Hello"}



curl http://0.0.0.0:8080/cxf/file/file1.txt/content
# => get content "test"

gnome-open "http://0.0.0.0:8080/cxf/file/file1.txt/content"
#  => browser propose "Save As" file content dialog

curl http://0.0.0.0:8080/cxf/file/file1.txt/contentText
# => get content "test"

gnome-open "http://0.0.0.0:8080/cxf/file/file1.txt/contentText"
#  => browser display text


curl -X POST -H "Content-Type: application/octet-stream" -d "test234" http://0.0.0.0:8080/cxf/file/file2.txt/content
# => ok

curl -X POST -d "test234" http://0.0.0.0:8080/cxf/file/file3.txt/content
# ==> KO empty result!! cf logs using  cxf.log.requests=true   in  src/main/resources/application.properties 
# Http-Method: POST
# Content-Type: application/x-www-form-urlencoded
# Headers: {Accept=[*/*], Content-Length=[7], content-type=[application/x-www-form-urlencoded], host=[0.0.0.0:8080], user-agent=[curl/7.38.0]}


curl -X POST -H "Content-Type: application/octet-stream" -d "test234" http://0.0.0.0:8080/cxf/git/file2.txt/content
# => 
# postFile 'file2.txt' content...
# copyed 7 bytes

( cd src/test/testJGitBaseDir; git log )
# => 
# commit ebd4ea02cdb310aeddc8d61b267ac579768beea6
# 
#    postFile 'file1.txt' => commit


curl -X GET http://0.0.0.0:8080/cxf/git/file2.txt/log
