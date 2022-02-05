SBTreeConf
----------

Spring Boot application using a tree structure for configuration instead of class-path scanning.

Using a tree structure for configuration facilitates and promotes test driven development.
The branches in this project show this process where each branch is a step that delivers a functional and testable product.

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

# Notes and references

### To @Import or @ImportAutoConfiguration

From https://blog.actorsfit.com/a?ID=00950-bf191026-a7da-4a2a-91c4-97f6f9e10bc1 (can't find original source):

"The effect of @ImportAutoConfiguration is to postpone the processing of automatic configuration until all user configurations are loaded (for example, via @ComponentScan or receiving @Import)."
