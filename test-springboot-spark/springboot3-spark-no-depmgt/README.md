

# Running with springboot3, no <dependenciesManagement>, force version slf4j-api, <exclusion> org.apache.logging.log4j:log4j-slf4j2-impl

warning on Logback + log4j-slf4j2-impl

```
SLF4J: Class path contains multiple SLF4J providers.
SLF4J: Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@10959ece]
SLF4J: Found provider [org.apache.logging.slf4j.SLF4JServiceProvider@3a6bb9bf]
SLF4J: See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@10959ece]
```


