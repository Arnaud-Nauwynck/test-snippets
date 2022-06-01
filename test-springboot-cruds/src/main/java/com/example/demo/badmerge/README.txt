

curl -H "content-type: application/json" -H "accept: application/json" \
	-X POST http://localhost:8080/api/v1/bad-merge/create-bug \
	-d '{ "ref": { "id": -1000 }}'

curl -H "content-type: application/json" -H "accept: application/json" \
	-X POST http://localhost:8080/api/v1/bad-merge/create-bug \
	-d '{ "ref": { "id": 3 }}'

curl -H "content-type: application/json" -H "accept: application/json" \
	-X POST http://localhost:8080/api/v1/bad-merge/create-ok \
	-d '{ "ref": { "id": 3 }}'

# expect 404 EntityNotfoundException
curl -H "content-type: application/json" -H "accept: application/json" \
	-X POST http://localhost:8080/api/v1/bad-merge/create-ok \
	-d '{ "ref": { "id": -1000 }}'


curl -H "accept: application/json" \
	http://localhost:8080/api/v1/bad-merge/13

curl -H "accept: application/json" \
	http://localhost:8080/api/v1/bad-merge/ref/3

	
	