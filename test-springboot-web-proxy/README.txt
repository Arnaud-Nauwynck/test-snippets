
curl -H 'Accept: application/json' http://localhost:8080/api/v1/foo/getFoo

curl -H 'Accept: application/json' http://localhost:8081/api-proxy/foo/getFoo

curl -X POST -H 'Accept: application/json'  -H 'Content-Type: application/json' http://localhost:8080/api/v1/foo/postFoo -d '{"strValue":"hello","intValue":123}'

curl -X POST -H 'Accept: application/json'  -H 'Content-Type: application/json' http://localhost:8081/api-proxy/foo/postFoo -d '{"strValue":"hello","intValue":123}'
