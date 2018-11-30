
# Springboot Gateway test snippet

Launch 2 servers:
- this server running on port 8081
- another rest server, listening on 8091
 
open http://localhost:8081/index.html
=> static page served from the wateway itself

you see 2 links: 
<A href="http://localhost:8081/rest/a">http://localhost:8081/rest/a</A>

cf redirect routes:
<A href="http://localhost:8091/rest/a">http://localhost:8091/rest/a</A>


see config/application.yml for static routing 
```
server:
  port: 8081
    
spring:

  cloud:
    gateway:
      routes:
      - id: route1 /rest/a*
        uri: http://localhost:8091
        predicates:
        - Path=/rest/a*
```
