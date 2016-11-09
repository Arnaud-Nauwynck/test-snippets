
# start neo4j server
cf neo4j-.../
./bin/neo4j start

# connect to browser
http://localhost:7474/browser/

# queries

MATCH(c: RevCommitEntity) return c

MATCH(c: RevCommitEntity)-[r]-(t) return c,r,t

MATCH(c: RevCommitEntity)-[p:parent]-(c2: RevCommitEntity)  return c,p,c2

# delete ...
MATCH (e: RevCommitEntity) -[p]- (e2) DELETE e,p
