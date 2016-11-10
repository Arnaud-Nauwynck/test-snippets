
# start neo4j server
cf neo4j-.../
./bin/neo4j start

# connect to browser
http://localhost:7474/browser/

# queries

match(n) return n limit 1000

match(r:SymbolicRepoRef) return r

match(HEAD:SymbolicRepoRef {name:"HEAD"}) return HEAD

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x) return HEAD,x

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci) return HEAD,x,ci

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent]->(ci2) return HEAD,x,ci,ci2

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent*]->(ci2) return HEAD,x,ci,ci2 limit 10

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent*]->(ci2)-[:author]->(auth) return HEAD,x,ci,ci2,auth limit 10




MATCH(c: RevCi) return c limit 100

MATCH(c: RevCi)-[r]-(t) return c,r,t limit 100

MATCH(c: RevCi)-[p:parent]-(c2: RevCi)  return c,p,c2 limit 100


match(dir:DirTree) -[de:entries]- (child) where ID(dir)=14950 return dir,de,child

start n=node(14950) return n


# Create / Update

create(n1: DirTree) create(n2: DirTree) create (n1)-[:Link]->(n2)


# delete ...
MATCH (e: RevCi) -[p]- (e2) DELETE e,p
match(dir:DirTree) -[rel]-(x) delete rel,dir
match(de:DirEntry) -[rel]-(x) delete rel,de
match(blob:Blob) -[rel]-(x) delete rel,blob

