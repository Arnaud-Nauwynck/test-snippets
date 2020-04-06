#!/bin/bash

mvn clean package > /dev/null; \
echo "running JUnit tests"; \
mvn surefire:test -DskipTests=false -Dmaven.test.skip=false > log-junits.txt ; \
	grep "app\." log-junits.txt  > app-junits.txt; \
cat app-junits.txt; \
echo ""; \
echo "running main"; \
mvn spring-boot:run > log-main.txt; \
	grep "app\." log-main.txt  > app-main.txt; \
cat app-main.txt;

