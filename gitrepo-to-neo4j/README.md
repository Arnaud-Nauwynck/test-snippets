
# start neo4j server
cf neo4j-.../
./bin/neo4j start

# connect to browser
http://localhost:7474/browser/

# queries

match(c: RevCommitEntity) return c
match(c: RevCommitEntity) -[r]-(t) return c,r,t
match(c: RevCommitEntity) -[p:parent]-(c2: RevCommitEntity)  return c,p,c2

