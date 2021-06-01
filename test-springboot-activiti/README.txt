
Step 1:
edit the process workflow:

download activity + unzip
+ download tomcat + unzip
+ mv activiti/*.war in tomcat/webapps/
+ set JAVA_HOME=...jdk8
+ set PATH
+ cd tomcat; bin/startup.sh

edit new workflow process
export to file myprocess.bpmm20.xml
copy in src/main/resources/processes/


Step 2: start server


Step 3: test http request

$ curl -u admin:admin -H 'Accept:application/json' http://localhost:8081/repository/process-definitions | jq '.'
{
  "data": [
    {
      "id": "myprocess:1:4",
      "url": "http://localhost:8081/repository/process-definitions/myprocess:1:4",
      "key": "myprocess",
      "version": 1,
      "name": "myprocess",
      "description": null,
      "tenantId": "",
      "deploymentId": "1",
      "deploymentUrl": "http://localhost:8081/repository/deployments/1",
      "resource": "http://localhost:8081/repository/deployments/1/resources/C:\\test-springboot-activiti\\target\\classes\\processes\\myprocess.bpmn20.xml",
      "diagramResource": "http://localhost:8081/repository/deployments/1/resources/C:\\test-springboot-activiti\\target\\classes\\processes\\myprocess.myprocess.png",
      "category": "http://www.activiti.org/processdef",
      "graphicalNotationDefined": true,
      "suspended": false,
      "startFormDefined": false
    }
  ],
  "total": 1,
  "start": 0,
  "sort": "name",
  "order": "asc",
  "size": 1
}
