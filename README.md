SBTreeConf
----------

Spring Boot application using a tree structure for configuration instead of class-path scanning.
Using a tree structure for configuration facilitates and promotes test driven development.

To run using Maven:

```sh
sdk use java 25-tem
mvn spring-boot:run -P run
# To run all test, including apidocs:
mvn test
# To run just one test:
mvn test -Dtest=IceCreamMockTest
# Full build, including static apidocs
mvn clean package
# Endpoints:
curl -u reader:reads -svv http://localhost:8080/api/v1/icecream | jq .
curl -u reader:reads -svv http://localhost:8080/api/v1/icecream/1 | jq .
# Also show response time in seconds
curl -u reader:reads -svv -w '\n%{time_starttransfer}' http://localhost:8080/api/v1/icecream/count?flavor="vanilla"; echo
curl -u writer:writes -svv -X PUT -H 'Content-Type: application/json' \
  -d '{"flavor": "test", "shape": "round"}' http://localhost:8080/api/v1/icecream | jq .
curl -u operator:operates -svv -X DELETE http://localhost:8080/api/v1/icecream/1 | jq .
# Virtual thread test
curl -sv http:/localhost:8080/thread ; echo
# Errors
curl -u reader:reads -sv localhost:8080/api/v1/icecream/count?flavor= | jq .
curl -u writer:writes -sv -X PUT  -H 'Content-Type: application/json' localhost:8080/api/v1/icecream -d '{"test": "invalid"}' | jq .
curl -u reader:reads -svg localhost:8080/api/v1/icecream/count?flavor=[ ; echo
```

To start the app from executable jar-file (containing the `PropertiesLauncher`,
see also [executable-jar](https://docs.spring.io/spring-boot/docs/current/reference/html/executable-jar.html)):

```sh
# Build fast without tests or ApiDocs
mvn clean package -Dmaven.test.skip -DskipDocs
# Run app from resulting jar-file
mvn dependency:copy -Dartifact=com.h2database:h2:2.3.232
java --sun-misc-unsafe-memory-access=allow \
  -Dloader.debug=false -Dloader.path=target/dependency,src/test/resources \
  -jar target/sbtreeconf-0.0.1-SNAPSHOT.jar --spring.profiles.active=test,run
```

The apidocs can be found under http://localhost:8080/docs/index.html

All actuator endpoints, except the health-endpoint, require a user with role `manage`.

```sh
curl -u manager:manages -svv http:/localhost:8081/actuator | jq .
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

```sh
mvn test -Dtest=IceCreamApiDocsTest
mvn asciidoctor:process-asciidoc
# open target/classes/static/docs/index.html
```

The resulting html-documentation is available at `target/classes/static/docs`

# Notes and references

### Compiler warnings

To suppress compiler/runtime warnings with Java 25, use:
```sh
export MAVEN_OPTS="-Dguice_custom_class_loading=CHILD --sun-misc-unsafe-memory-access=allow"
```

### Logging colors

To always see color-coded logging output on console, use:
```sh
export SPRING_OUTPUT_ANSI_ENABLED=ALWAYS
```
Other options are `NEVER` and `DETECT` (the default).

### Database autocommit

Some articles like https://vladmihalcea.com/spring-transaction-connection-management/
suggest to put `spring.datasource.hikari.auto-commit` to `false`.
This, however, creates another round-trip to the database for queries
(to submit a `rollback` after the query) which in turn can actually degrade performance (response times are higher).
It is best to follow the advice in the conclusion:  
"The most flexible approach is to design the service layer properly so that the transactional methods are called
as late as possible when executing a given business method."

### Special URL characters

If values in URL query parameters contain special characters (e.g. `%`),
these request will be denied. To allow special characters, 
customize the `StrictHttpFirewall` as described in 
https://www.baeldung.com/spring-security-request-rejected-exception


### `@Import` versus `@ImportAutoConfiguration`

From "Manual Bean Definitions in Spring Boot" 
at [spring-2019-blog](https://spring.io/blog/2019/01/21/manual-bean-definitions-in-spring-boot):

"The effect of `@ImportAutoConfiguration` is to postpone the processing of automatic configuration
until all user configurations are loaded (for example, via `@ComponentScan` or receiving `@Import`)."

### Manual auto-configuration

Manually defining the `AutoConfiguration` classes to import was done up to the branch `spring-boot-3 `.
This approach was abandoned in favor of `@EnableAutoConfiguration` due to concerns that auto-configured
security features might be missed when manually defining the `AutoConfiguration`.  
The test-classes still use excludes for `AutoConfiguration` in several places to speed-up the tests.

The branches in this project show each step that delivers a functional and testable product.
Most of these branches are outdated now since they use Spring Boot 2.

Branches:

  - view from bottom to top in [all branches](https://github.com/fwi/sbtreeconf/branches/all)
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
  - spring-boot-3 ([guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide/))
  - simplify-java25

