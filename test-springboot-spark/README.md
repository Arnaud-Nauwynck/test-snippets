
# Running with springboot-dependencies 2.7

Simplest maven pom.xml works fine with (very old) BOM spring-boot-dependencies 2.7.x  and spark 3.5

```xml
    <properties>
        <spark.version>3.5.0</spark.version>
        <scala.version>2.13</scala.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.7.14</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
    </dependencies>
```

=> run OK... but outdated springboot 2.7.*, should migrate to springboot 3.*

Notice... springboot 2.7 can work with jdk < 17, therefore not requiring jvm options


# Upgrading JDK >= 17  ... required jvm options

```
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/sun.nio.cs=ALL-UNNAMED
--add-opens=java.base/sun.security.action=ALL-UNNAMED
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED
--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED
```


# Error while Running with springboot-dependencies 3.1

```xml
    <properties>
        <spark.version>3.5.0</spark.version>
        <scala.version>2.13</scala.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>3.1.2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
    </dependencies>
```

=> ERROR 

```
SLF4J: Class path contains multiple SLF4J providers.
SLF4J: Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@10959ece]
SLF4J: Found provider [org.apache.logging.slf4j.SLF4JServiceProvider@3a6bb9bf]
SLF4J: See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@10959ece]

....

Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sparkSession' defined in class path resource [org/example/SparkConfiguration.class]: Failed to instantiate [org.apache.spark.sql.SparkSession]: Factory method 'sparkSession' threw exception with message: javax/servlet/Servlet
	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:659) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:493) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1332) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1162) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:560) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:520) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1417) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1337) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:888) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791) ~[spring-beans-6.0.11.jar:6.0.11]
	... 19 common frames omitted
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.apache.spark.sql.SparkSession]: Factory method 'sparkSession' threw exception with message: javax/servlet/Servlet
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:171) ~[spring-beans-6.0.11.jar:6.0.11]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:655) ~[spring-beans-6.0.11.jar:6.0.11]
	... 33 common frames omitted
Caused by: java.lang.NoClassDefFoundError: javax/servlet/Servlet
	at org.apache.spark.ui.SparkUI$.create(SparkUI.scala:239) ~[spark-core_2.13-3.5.0.jar:3.5.0]
	at org.apache.spark.SparkContext.<init>(SparkContext.scala:503) ~[spark-core_2.13-3.5.0.jar:3.5.0]
	at org.apache.spark.SparkContext$.getOrCreate(SparkContext.scala:2888) ~[spark-core_2.13-3.5.0.jar:3.5.0]
	at org.apache.spark.sql.SparkSession$Builder.$anonfun$getOrCreate$2(SparkSession.scala:1099) ~[spark-sql_2.13-3.5.0.jar:3.5.0]
	at scala.Option.getOrElse(Option.scala:201) ~[scala-library-2.13.8.jar:na]
	at org.apache.spark.sql.SparkSession$Builder.getOrCreate(SparkSession.scala:1093) ~[spark-sql_2.13-3.5.0.jar:3.5.0]
	at org.example.SparkConfiguration.sparkSession(Main.java:29) ~[classes/:na]
	at org.example.SparkConfiguration$$SpringCGLIB$$0.CGLIB$sparkSession$0(<generated>) ~[classes/:na]
	at org.example.SparkConfiguration$$SpringCGLIB$$2.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invokeSuper(MethodProxy.java:258) ~[spring-core-6.0.11.jar:6.0.11]
	at org.springframework.context.annotation.ConfigurationClassEnhancer$BeanMethodInterceptor.intercept(ConfigurationClassEnhancer.java:331) ~[spring-context-6.0.11.jar:6.0.11]
	at org.example.SparkConfiguration$$SpringCGLIB$$0.sparkSession(<generated>) ~[classes/:na]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:578) ~[na:na]
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:139) ~[spring-beans-6.0.11.jar:6.0.11]
	... 34 common frames omitted
Caused by: java.lang.ClassNotFoundException: javax.servlet.Servlet
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641) ~[na:na]
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188) ~[na:na]
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521) ~[na:na]
	... 49 common frames omitted
```

Root cause: major change with springboot 3: 'javax.servlet.Servlet' class is NO more defined in package 'javax.*', but migrated to 'jakarta.*'

The compiled dependency from spark-core to javax-servlet-api is replaced by maven dependencyManagement



# Running with springboot3, no dependenciesManagement

```xml
    <properties>
        <spark.version>3.5.0</spark.version>
        <scala.version>2.13</scala.version>
        <springboot.version>3.1.2</springboot.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${springboot.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
    </dependencies>
```

=> ERROR

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Exception in thread "main" java.lang.IllegalArgumentException: LoggerFactory is not a Logback LoggerContext but Logback is on the classpath. Either remove Logback or the competing implementation (class org.slf4j.helpers.NOPLoggerFactory loaded from file:/C:/Users/arnaud/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar). If you are using WebLogic you will need to add 'org.slf4j' to prefer-application-packages in WEB-INF/weblogic.xml: org.slf4j.helpers.NOPLoggerFactory
at org.springframework.util.Assert.instanceCheckFailed(Assert.java:713)
at org.springframework.util.Assert.isInstanceOf(Assert.java:632)
at org.springframework.boot.logging.logback.LogbackLoggingSystem.getLoggerContext(LogbackLoggingSystem.java:381)
at org.springframework.boot.logging.logback.LogbackLoggingSystem.beforeInitialize(LogbackLoggingSystem.java:122)
at org.springframework.boot.context.logging.LoggingApplicationListener.onApplicationStartingEvent(LoggingApplicationListener.java:238)
at org.springframework.boot.context.logging.LoggingApplicationListener.onApplicationEvent(LoggingApplicationListener.java:220)
at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:143)
at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:131)
at org.springframework.boot.context.event.EventPublishingRunListener.multicastInitialEvent(EventPublishingRunListener.java:136)
at org.springframework.boot.context.event.EventPublishingRunListener.starting(EventPublishingRunListener.java:75)
at org.springframework.boot.SpringApplicationRunListeners.lambda$starting$0(SpringApplicationRunListeners.java:54)
at java.base/java.lang.Iterable.forEach(Iterable.java:75)
at org.springframework.boot.SpringApplicationRunListeners.doWithListeners(SpringApplicationRunListeners.java:118)
at org.springframework.boot.SpringApplicationRunListeners.starting(SpringApplicationRunListeners.java:54)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:304)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1306)
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1295)
at org.example.Springboot3SparkNoDepMgtMain.main(Springboot3SparkNoDepMgtMain.java:15)
```

Root cause? some slf4j / Logback / Log4j2  dependency HELL

spark-core comes with org.apache.logging.log4j:log4j-slf4j2-impl  (bad idea... Developpers community prefers compile-time dependencies to slf4j-api only, and runtime to logback)


# Running with springboot3, no <dependenciesManagement>, force version slf4j-api, <exclusion> org.apache.logging.log4j:log4j-slf4j2-impl

```xml
    <properties>
        <spark.version>3.5.0</spark.version>
        <scala.version>2.13</scala.version>
        <springboot.version>3.1.2</springboot.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
                <version>2.0.9</version>
            </dependency>
            <dependency>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-slf4j2-impl</artifactId>
                <version>__invalid_version_to_force_exclude</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${springboot.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>log4j-slf4j2-impl</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
    </dependencies>

```

=> OK !!!

BUT NOT PRACTICAL to not being able to use maven <dependencyManagement> and springboot-dependencies BOM !! 

Need to maintain tones of maven <version> manually in applications.


# Springboot3 (with dependencyManagement BOM) and forced override for glassfish + servlet-api + exclude log4j-slf4j2-impl

```xml
    <properties>
        <spark.version>3.5.0</spark.version>
        <scala.version>2.13</scala.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>3.1.2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.glassfish.jersey.containers</groupId>
                <artifactId>jersey-container-servlet-core</artifactId>
                <version>2.41</version>
            </dependency>
            <dependency>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
                <version>2.0.9</version>
            </dependency>
            <dependency>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-slf4j2-impl</artifactId>
                <version>__invalid_version_to_force_exclude</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>log4j-slf4j2-impl</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>4.0.4</version>
        </dependency>

    </dependencies>
```

=> OK !!!

Luckily the default implementation of springboot-starter-web is Tomcat, and NOT glassfish.

Overriding glassfish version is OK, because it is used only by Spark and not by spring.

Adding very old version of Servlet api is OK, because it is NOT used by Spring. 

cf also possibility to repackaged jar of servlet, and giving a different groupId:artifactId, not colliding with BOM and spring-web.


But LOT of tricky code in pom.xml to setup !!
