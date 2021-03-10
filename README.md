### Version

1.0

### Author

Richard Guzman

### Technology Stack
`Java 11`
`Spring Boot 2.4.3`
`H2 1.4.200`
`Flyway 7.1.1`


### Description
- Database version control with Flyway
- H2 in memory database

### Swagger

/swagger-ui.html

### Actuators

/actuator  
/actuator/health,  
/actuator/info


  
### Application execution

    mvn clean install
    mvn spring-boot:run

### Deployment steps

Build Docker Image:

	docker build -t "diffjson:LOCAL-BUILD" .

Docker Compose start:

	docker-compose -f docker-compose-prod.yml up -d

Docker Compose stop:

	docker-compose -f docker-compose-prod.yml down
	
___
###API 

- Endpoint Left:

```
URL: /v1/diff/{id}/left
Method: POST
Body Structure: 
{ 
"fileContent": <json file in base64>  
}
Status codes:
- 200: success 
- 400: Bad Request. In case the fileContent is either an empty or a not valid json based64 encoded 
- 500: internal error

Invocation example:
- Body:
{ 
"fileContent": "ewogICJmaXJzdG5hbWUiOiAiUmljaGFyZCIsCiAgImxhc3RuYW1lIjogIkd1em1hbiIKfQ=="  
}

-Expected result: 200

```
- Endpoint right:
```
URL: /v1/diff/{id}/right
Method: POST
Body Structure: 
{ 
"fileContent": <json file in base64>  
}
Status codes:
- 200: success 
- 400: Bad Request. In case the fileContent is either an empty or a not valid json based64 encoded 
- 500: internal error

Invocation example:
- Body:
{ 
"fileContent": "ewogICJmaXJzdG5hbWUiOiAiUmljaGFyZCIsCiAgImxhc3RuYW1lIjogIkd1em1hbiIKfQ=="  
}

-Expected result: 200

```
  
- Endpoint diff
```
URL: /v1/diff/{id}
Method: GET
Status codes:
- 200: success 
- 404: Not Found. In case the id does not exist in data base 
- 500: internal error

Expected Response:
- Body:
{
  "status": string,
  "message": string",
  "offsetList": [
    {
      "offset": number,
      "length": number
    }
  ]
}

-Example Response:

{
  "status": "DIFFERENT_CONTENT",
  "message": "Different Content",
  "offsetList": [
    {
      "offset": 15,
      "length": 3
    }
  ]
} 

Note: 
Possible Status:
    EQUALS: Both files are equals
    NOT_EQUAL_SIZE: Files have not equal size
    DIFFERENT_CONTENT: Files have different content
  
```


### Future Enhancements

- Current implementation is using H2 as an in-memory database. As this project is intended to be a demo, it might be independent of any no-embedded database.
  When this application is thought to be promoted to production, another database solution,which allows for a reactive interaction with data layer and large
  data volume handling, should be used, such as Redis, MongoDB or Cassandra.

- Include Authentication/Authorization mechanism (Spring Security)

- Include distributing tracing to facilitate bottleneck detections and logs review

- Include metrics collection that supports performance and request monitoring