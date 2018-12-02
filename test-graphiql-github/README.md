
# Test for Github GraphQL API using graphiql + http call with token bearer

1/ generate you Github Token credential, and store it in your $HOME/.ssh/githubToken (or modify config/application.yml)

2/ Launch App

3/ open browser http://localhost:8080/graphiql

4/ play with graphQL queries

`̀``
query {
  repositoryOwner(login: "Arnaud-Nauwynck") {
    login,
    repositories(last: 7) {
      nodes {
        url
      }
    }
  }
}
`̀``

=> result:
`̀``
{
  "data": {
    "repositoryOwner": {
      "login": "Arnaud-Nauwynck",
      "repositories": {
        "nodes": [
          {
            "url": "https://github.com/Arnaud-Nauwynck/teststartjhipster"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/hadoop-unit"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/jsonnet-java"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/jsonnet4j"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/thread-dump"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/arith-math4j"
          },
          {
            "url": "https://github.com/Arnaud-Nauwynck/jdbc-logger"
          }
        ]
      }
    }
  },
  "errors": null,
  "extensions": null
}
`̀``


same using bind-variable
`̀``
query($login: String!) {
  repositoryOwner(login: $login) {
    login,
    repositories(last: 100) {
      nodes {
        url
      }
    }
  }
}
variable {
	"login": "Arnaud-Nauwynck"
}
`̀``


5/ using curl (json) + jq 
`̀``
curl -v -X POST -H "Accept: application/json" -H "Content-type: application/json" \
	http://localhost:8080/graphql -d @src/test/req1.json \
| jq '.data.repositoryOwner.repositories.nodes[].url'
`̀``

=> result:
`̀``
"https://github.com/Arnaud-Nauwynck/sef4j"
"https://github.com/Arnaud-Nauwynck/mytoolbox"
"https://github.com/Arnaud-Nauwynck/test-sound-decomp"
"https://github.com/Arnaud-Nauwynck/test-snippets"
"https://github.com/Arnaud-Nauwynck/JNI-wrapper-for-GMP-Gnu-Multi-Precision-Library"
"https://github.com/Arnaud-Nauwynck/test-dotnet-snippets"
"https://github.com/Arnaud-Nauwynck/Arnaud-Nauwynck.github.io"
"https://github.com/SELVANADIN/LPCSID"
"https://github.com/Arnaud-Nauwynck/cmdb4j"
"https://github.com/Arnaud-Nauwynck/js-mda4j"
"https://github.com/Arnaud-Nauwynck/jhipster-bankapp"
"https://github.com/Arnaud-Nauwynck/screencast-compression-tool"
"https://github.com/Arnaud-Nauwynck/bitwise4j"
"https://github.com/Arnaud-Nauwynck/fx-tree"
"https://github.com/StarGroove/Carpooling"
"https://github.com/adminCarpoolingMCBD/adminCarpooling_MCDB"
"https://github.com/Arnaud-Nauwynck/mem4j"
"https://github.com/Arnaud-Nauwynck/reverse-jpa-2-jhipster-jdl"
"https://github.com/Arnaud-Nauwynck/spring-boot"
"https://github.com/Arnaud-Nauwynck/qrcode-channel"
"https://github.com/Arnaud-Nauwynck/jhipster-all-deps"
"https://github.com/Arnaud-Nauwynck/neo4j-ogm"
"https://github.com/Arnaud-Nauwynck/yarn"
"https://github.com/Arnaud-Nauwynck/swagger-codegen"
"https://github.com/Arnaud-Nauwynck/teststartjhipster"
"https://github.com/Arnaud-Nauwynck/hadoop-unit"
"https://github.com/Arnaud-Nauwynck/jsonnet-java"
"https://github.com/Arnaud-Nauwynck/jsonnet4j"
"https://github.com/Arnaud-Nauwynck/thread-dump"
"https://github.com/Arnaud-Nauwynck/arith-math4j"
"https://github.com/Arnaud-Nauwynck/jdbc-logger"
`̀``
 