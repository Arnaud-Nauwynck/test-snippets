
# Test for Springboot WebSocket

Starting server..

```
INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService 'messageBrokerTaskScheduler'
INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'brokerChannelExecutor'
INFO  o.s.m.s.b.SimpleBrokerMessageHandler - Starting...
INFO  o.s.m.s.b.SimpleBrokerMessageHandler - BrokerAvailabilityEvent[available=true, SimpleBrokerMessageHandler [DefaultSubscriptionRegistry[cache[0 destination(s)], registry[0 sessions]]]]
INFO  o.s.m.s.b.SimpleBrokerMessageHandler - Started.
```


Basic test with curl
```
$ curl -v http://localhost:8080/socket

*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /socket HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.55.1
> Accept: */*
>
< HTTP/1.1 200
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 19
< Date: Fri, 17 May 2019 20:58:23 GMT
<
Welcome to SockJS!
* Connection #0 to host localhost left intact
```

