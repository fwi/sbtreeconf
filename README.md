SBTreeConf
----------

Spring Boot application using a tree structure for configuration instead of class-path scanning.

Using a tree structure for configuration facilitates and promotes test driven development.
The branches in this project show this process where each branch is a step that delivers a functional and testable product.

Branches:

  - view from bottom to top in [active branches](https://github.com/fwi/sbtreeconf/branches/active)
  - beginnings
  - first-endpoint
  - json-with-service
  - db-with-jdbc
  - db-with-jpa
  - validation-and-error-handling
  - auto-configuration-reporting
  - actuator-endpoints
  - apidocs
  - websecurity
  - TODO: webflux

To run using Maven:

```
sdk use java 17.0.1-tem
mvn spring-boot:run -P run
# To run all test, including apidocs:
mvn test
# To run just one test:
mvn test -Dtest=IceCreamMockTest -DskipDocs
# Endpoints:
curl -u reader:reads -svv http://localhost:8080/api/v1/icecream | jq .
curl -u reader:reads -svv http://localhost:8080/api/v1/icecream/1 | jq .
# Also show response time in seconds
curl -u reader:reads -svv -w '\n%{time_starttransfer}' http://localhost:8080/api/v1/icecream/count?flavor="vanilla"; echo;
curl -u writer:writes -svv -X PUT -H 'Content-Type: application/json' \
  -d '{"flavor": "test", "shape": "round"}' http://localhost:8080/api/v1/icecream | jq .
curl -u operator:operates -svv -X DELETE http://localhost:8080/api/v1/icecream/1 | jq .
```

To start the app from executable jar-file (containing the `PropertiesLauncher`,
see also [executable-jar](https://docs.spring.io/spring-boot/docs/current/reference/html/executable-jar.html)):

```
# Build fast without tests or ApiDocs
mvn clean package -Dmaven.test.skip -DskipDocs
# Build with ApiDocs
mvn clean package -DskipTests
mvn dependency:copy -Dartifact=com.h2database:h2:1.4.200
java -Dloader.debug=true -Dloader.path=target/dependency,src/test/resources \
  -jar target/sbtreeconf-0.0.1-SNAPSHOT.jar --spring.profiles.active=run
```

The apidocs can be found under http://localhost:8080/docs/index.html

All actuator endpoints, except the health-endpoint, require a user with role `manage`.

```
curl -u manager:manages -svv http:/localhost:8081/actuator/ | jq .
# Health requires no acces.
curl -svv http:/localhost:8081/actuator/health | jq .
curl -svv http:/localhost:8081/actuator/health/liveness; echo;
curl -svv http:/localhost:8081/actuator/health/readiness; echo;
curl -u manager:manages -svv http:/localhost:8081/actuator/info | jq .
curl -u manager:manages -svv http:/localhost:8081/actuator/metrics/system.cpu.count | jq .
curl -u manager:manages -svv http:/localhost:8081/actuator/prometheus
# "env" endpoint is disabled by default in ActuatorsConfig.
curl -u manager:manages -svv http:/localhost:8081/actuator/env | jq .
```

### ApiDocs

ApiDocs can be compiled separately using:

```
mvn package -P apidocs
```

The resulting html-documentation is available at `target/classes/static/docs`

# Notes and references

### To @Import or @ImportAutoConfiguration

From "Manual Bean Definitions in Spring Boot" 
at [spring-2019-blog](https://spring.io/blog/2019/01/21/manual-bean-definitions-in-spring-boot):

"The effect of @ImportAutoConfiguration is to postpone the processing of automatic configuration until all user configurations are loaded (for example, via @ComponentScan or receiving @Import)."
