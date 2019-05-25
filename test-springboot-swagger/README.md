
# Test SpringBoot webmvc for Swagger + Swagger CodeGen maven-plugin

# Setup Springboot

pom.xml

```
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
</dependency>
```

Java Springboot config

```
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.ant("/api/**"))
          .build();                                           
    }
```



# Test 

http://localhost:8080/swagger-ui.html

http://localhost:8080/v2/api-docs


# Swagger CodeGen

```
<plugin>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <configuration>
        <inputSpec>http://localhost:8080/v2/api-docs</inputSpec>
    </configuration>
    <executions>
        <execution>
            <id>generate-swagger-typescript-angular-7</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <language>typescript-angular</language>
                <output>${basedir}/target/generated-typescript-angular7</output>
                <configOptions>
                    <ngVersion>7.2.12</ngVersion>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

