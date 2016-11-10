
# start neo4j server
cf neo4j-.../
./bin/neo4j start

# connect to browser
http://localhost:7474/browser/

# queries

match(r:SymbolicRepoRef) return r

MATCH(c: RevCi) return c limit 100

MATCH(c: RevCi)-[r]-(t) return c,r,t limit 100

MATCH(c: RevCi)-[p:parent]-(c2: RevCi)  return c,p,c2 limit 100

# delete ...
MATCH (e: RevCi) -[p]- (e2) DELETE e,p
