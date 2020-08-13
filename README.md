![build/test](https://github.com/jburgess/jwt-extractor/workflows/BUILD/badge.svg)
# jwt-extractor
This service provides methods to decode a JWT inspired by [jwt.io](https://jwt.io).

## Provide a JWT as a runtime parameter
```bash
 docker run --rm jburgess/jwt-extractor ./jwt-extractor [JWT]
```

## JWT Extract via REST Service
```bash
docker run jburgess/jwt-extractor -p 8080:8080
```
```bash
curl --location --request POST 'http://localhost:8080/extractJwt' \
--header 'Content-Type: application/json' \
--data-raw '{ "token": "[JWT]" }'
```