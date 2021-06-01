
http://localhost:8092/graphiql

sample graphql queries:

{
  a {
    id,
    srv2Name
  }
}


{
  a {
    id,
    srv2Name,
    bs {
      id,
      srv2Name
    }
  }
}


{
	aById(id:1) {
    srv2Name
  }
}


query($aId: Int!) 
{
	aById(id: $aId) {
    srv2Name
  }
}
... with variables:
{
  "aId": 1
}


