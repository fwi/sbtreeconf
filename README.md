SBTreeConf
----------

Spring Boot application using a tree structure for configuration instead of classpath scanning.

Promotes test driven development.

To run using Maven:

```
sdk use java 17.0.1-tem
mvn test
mvn spring-boot:run -Prun
# Test endpoint:
curl -svv http://localhost:8080/api/v1/icecream/find | jq .
curl -svv http://localhost:8080/api/v1/icecream/count/flavor/vanilla
```

