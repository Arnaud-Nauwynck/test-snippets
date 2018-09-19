
http://localhost:8090/graphiql

sample graphql queries:

{
  a {
    id,
    name
  }
}


{
  a {
    id,
    name,
    bs {
      id,
      name
    }
  }
}


{
	aById(id:1) {
    name
  }
}


query($aId: Int!) 
{
	aById(id: $aId) {
    name
  }
}
... with variables:
{
  "aId": 1
}


