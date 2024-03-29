
# 5 tastes of a RestController, from naive in-memory to standard JPA+DTO

## RestControler version1 : using in-memory entity=dto

```
@RestController
@RequestMapping("/api/v1/todo")
public class V1TodoRestController {

	private List<TodoDTO> entities = createInit();

	private static int idGenerator = 0;

	@PostMapping
	public TodoDTO postTodo(@RequestBody TodoDTO req) {
		System.out.println("called http POST /api/todo");
		req.id = ++idGenerator;
		entities.add(req);
		return req;
	}
	
	// ... more CRUD... using in-memory object
	// idGenerator = static int field incremented  (non-static are reset to 0 while debugging??)
	
```

## Testing with curl

```
export apiVersion=v1

curl http://localhost:8080/api/${apiVersion}/todo  | jq '.'
[
  {
    "id": 1,
    "label": "Apprendre Maven",
    "creationDate": null,
    "priority": 0
  },
  {
    "id": 2,
    "label": "Apprendre Spring-boot",
    "creationDate": null,
    "priority": 0
  }
]

curl -X POST -H "content-type: application/json" http://localhost:8080/api/${apiVersion}/todo -d '{ "label": "apprendre typescript" }'[

curl http://localhost:8080/api/${apiVersion}/todo/3  | jq '.'
{
	"id": 3,
    "label": "apprendre typescript",
    "creationDate": null,
    "priority": 0
}


curl -X PUT -H "content-type: application/json" http://localhost:8080/api/${apiVersion}/todo -d '{ "id": 3, "label": "apprendre typescript + javascript" }'

curl http://localhost:8080/api/${apiVersion}/todo  | jq '.'
[
  {
    "id": 1,
    "label": "Apprendre Maven",
    "creationDate": null,
    "priority": 0
  },
  {
    "id": 2,
    "label": "Apprendre Spring-boot",
    "creationDate": null,
    "priority": 0
  },
  {
    "id": 3,
    "label": "apprendre typescript + javascript",
    "creationDate": null,
    "priority": 0
  }
]

curl -X DELETE http://localhost:8080/api/${apiVersion}/todo/2  | jq '.'

curl http://localhost:8080/api/${apiVersion}/todo  | jq '.'
[
  {
    "id": 1,
    "label": "Apprendre Maven",
    "creationDate": null,
    "priority": 0
  },
  {
    "id": 3,
    "label": "apprendre typescript + javascript",
    "creationDate": null,
    "priority": 0
  }
]
```


## RestControler version2 : using JPA but no dto

This is the shortest / simplest code... even simpler than in-memory, but it is INVALID !

```
@RestController
@RequestMapping("/api/v2/todo")
@Transactional
public class V2TodoRestController {
	
	@Autowired
	private TodoRepository repository;

	@PostMapping
	public TodoEntity postTodo(@RequestBody TodoEntity req) {
		System.out.println("called http POST /api/todo");
		TodoEntity res = repository.save(req);
		return res;
	}

	// ... more CRUD... using JPA entity

```

Some methods work but not all
Example of error code, using POST:

```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: com.example.demo.rest.TodoEntity$HibernateProxy$1bRAtx1F["hibernateLazyInitializer"])
```


## RestControler version3 : using JPA entity+dto, explicit converter entity2dto and dto2entity

This is the simplest correct code, but not the shortest .. The converters are stupid annoying methods to write, and to debug

```
@RestController
@RequestMapping("/api/v3/todo")
@Transactional
public class V3TodoRestController {

	@Autowired
	private TodoRepository repository;

	@PostMapping
	public TodoDTO postTodo(@RequestBody TodoDTO req) {
		System.out.println("called http POST /api/todo");
		TodoEntity res = repository.save(dto2Entity(req));
		return entity2Dto(res);
	}

	// ... more CRUD... using JPA entity

	public static TodoDTO entity2Dto(TodoEntity src) {
		TodoDTO res = new TodoDTO();
		res.id = src.getId();
		res.label = src.getLabel();
		res.priority = src.getPriority();
		return res;
	}

	public static List<TodoDTO> entity2Dtos(Collection<TodoEntity> src) {
		return src.stream().map(e -> entity2Dto(e)).collect(Collectors.toList());
	}
	
	public static TodoEntity dto2Entity(TodoDTO src) {
		TodoEntity res = new TodoEntity();
		// .. do not update res.id!
		res.label = src.label;
		res.priority = src.priority;
		return res;
	}
```

## RestControler version4 : using JPA entity+dto, Orika converter

This is the simplest correct code, almost the shortest .. The converters are same as in v3, but automatically generated by Orika at runtime

```
add dependency in pom.xml:
		<dependency>
			<groupId>ma.glasnost.orika</groupId>
			<artifactId>orika-core</artifactId>
			<version>1.5.4</version>
		</dependency>
```

```
@RestController
@RequestMapping("/api/v4/todo")
@Transactional
public class V4TodoRestController {

	@Autowired
	private TodoRepository repository;
	
	@Autowired
	private DtoConverter dtoConverter;
	
	@PostMapping
	public TodoDTO postTodo(@RequestBody TodoDTO req) {
		System.out.println("called http POST /api/todo");
		TodoEntity res = repository.save(dto2Entity(req));
		return entity2Dto(res);
	}

	// ... more CRUD... using JPA entity
		
	public TodoDTO entity2Dto(TodoEntity src) {
		return dtoConverter.map(src, TodoDTO.class);
	}

	public List<TodoDTO> entity2Dtos(Collection<TodoEntity> src) {
		return dtoConverter.mapAsList(src, TodoDTO.class);
	}
	
	public TodoEntity dto2Entity(TodoDTO src) {
		return dtoConverter.map(src, TodoEntity.class);
	}
	
... where 	

@Component
public class DtoConverter {
	
	private MapperFacade mapper = createMapper();
	
	private MapperFacade createMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		return mapperFactory.getMapperFacade();
	}
	
	public <S, D> D map(S sourceObject, Class<D> destinationClass) {
		return mapper.map(sourceObject, destinationClass);
	}
	
	public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
		return mapper.mapAsList(source, destinationClass);
	}
	
}

```

## RestControler version5 : using generated Hateos @RepositoryRestResource ... shortest but NO business logic

```
add dependency in pom.xml
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-data-rest</artifactId>
		<dependency>
```

```
@RepositoryRestResource(path = "/api/v5/todo")
public interface V5TodoRepositoryRestResource extends PagingAndSortingRepository<TodoEntity, Integer> {
}
```

The Rest interface is not exactly the same... it is HATEOS.

Data is contained in a wrapper "_embdedded" field , and there is also a meta-data "_links", and "page".
By default, only the first 20 (first page) items is displayed.

### Get list:
```
curl http://localhost:8080/todo | jq '.'
{
  "_embedded": {
    "todos": [
      {
        "label": "Apprendre Maven",
        "creationDate": null,
        "priority": 0,
        "_links": {
          "self": {
            "href": "http://localhost:8080/todo/1"
          },
          "todoEntity": {
            "href": "http://localhost:8080/todo/1"
          }
        }
      },
      {
        "label": "Apprendre Spring-boot",
        "creationDate": null,
        "priority": 0,
        "_links": {
          "self": {
            "href": "http://localhost:8080/todo/2"
          },
          "todoEntity": {
            "href": "http://localhost:8080/todo/2"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/todo"
    },
    "profile": {
      "href": "http://localhost:8080/profile/todo"
    }
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  }
}
```

### Get items

This looks like plain old Rest, with an extra field "_links"

```
$ curl http://localhost:8080/todo/1 | jq '.'
{
  "label": "Apprendre Maven",
  "creationDate": null,
  "priority": 0,
  "_links": {
    "self": {
      "href": "http://localhost:8080/todo/1"
    },
    "todoEntity": {
      "href": "http://localhost:8080/todo/1"
    }
  }
}
```

The hateos is usefull for generating completely "anemic" model, with no business logic.
It is also usefull for a "generic" client, that follows links, without business logic.
