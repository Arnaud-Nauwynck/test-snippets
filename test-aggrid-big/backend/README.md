
# Test Angular AG-Grid + SpringBoot Swagger

# Setup Springboot

pom.xml

```
```

Java Springboot config

```
```



# Test

http://localhost:8080/swagger-ui/index.html

http://localhost:8080/v3/api-docs


curl http://localhost:8080/v3/api-docs | jq '.' > api-docs.json



# Swagger CodeGen

```
```

# Testing generated Angular code ...

```
mvn -Pswagger-gen generate-sources 

```