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

To run using Maven:

```
sdk use java 17.0.1-tem
mvn test
mvn spring-boot:run -Prun
# Test endpoints:
curl -svv http://localhost:8080/api/v1/icecream | jq .
curl -svv http://localhost:8080/api/v1/icecream/1 | jq .
curl -svv http://localhost:8080/api/v1/icecream/count?flavor="vanilla"
curl -svv -X PUT -H 'Content-Type: application/json' \
  -d '{"flavor": "test", "shape": "round"}' http://localhost:8080/api/v1/icecream | jq .
```

Start the app from executable jar-file (containing the `PropertiesLauncher`,
see also [executable-jar](https://docs.spring.io/spring-boot/docs/current/reference/html/executable-jar.html))

```
mvn clean package -DskipTests
mvn dependency:copy -Dartifact=com.h2database:h2:1.4.200
java -Dloader.debug=true -Dloader.path=target/dependency,src/test/resources \
  -jar target/sbtreeconf-0.0.1-SNAPSHOT.jar --spring.profiles.active=run
```

Actuator endpoints:

```
curl -svv http:/localhost:8081/actuator/ | jq .
curl -svv http:/localhost:8081/actuator/health | jq .
curl -svv http:/localhost:8081/actuator/health/liveness
curl -svv http:/localhost:8081/actuator/health/readiness
curl -svv http:/localhost:8081/actuator/info | jq .
curl -svv http:/localhost:8081/actuator/metrics/system.cpu.count | jq .
curl -svv http:/localhost:8081/actuator/prometheus
curl -vv http:/localhost:8081/actuator/env | jq .
```

# Notes and references

### To @Import or @ImportAutoConfiguration

From "Manual Bean Definitions in Spring Boot" 
at [spring-2019-blog](https://spring.io/blog/2019/01/21/manual-bean-definitions-in-spring-boot):

"The effect of @ImportAutoConfiguration is to postpone the processing of automatic configuration until all user configurations are loaded (for example, via @ComponentScan or receiving @Import)."
