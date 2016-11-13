
# start neo4j server
cf neo4j-.../
./bin/neo4j start

# connect to browser
http://localhost:7474/browser/

# queries

match(n) return n limit 1000

match(r:SymbolicRepoRef) return r

match(HEAD:SymbolicRepoRef {name:"HEAD"}) return HEAD

match(from {name: "HEAD"}) return from

match(ref: SymbolicRepoRef) return ref union all match(ref: RepoRef) return ref

match(HEAD:SymbolicRepoRef{name: "HEAD"}),(originHead:SymbolicRepoRef {name: "refs/remotes/origin/HEAD"}) return HEAD,originHead

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x) return HEAD,x

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci) return HEAD,x,ci

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent]->(ci2) return HEAD,x,ci,ci2

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent*]->(ci2) return HEAD,x,ci,ci2 limit 10

match(HEAD:SymbolicRepoRef {name:"HEAD"}) -[:target]->(x)-[:refCommit]->(ci)-[:parent*]->(ci2)-[:author]->(auth) return HEAD,x,ci,ci2,auth limit 10



match(head: SymbolicRepoRef{name:'HEAD'})-[target]-(headTarget)-[refCommit]->(ci) return head,target,headTarget,ci



MATCH(c: RevCi) return c limit 100

MATCH(c: RevCi)-[r]-(t) return c,r,t limit 100

MATCH(c: RevCi)-[p:parent]-(c2: RevCi)  return c,p,c2 limit 100


match(dir:DirTree) -[de:has_entry]- (child) where ID(dir)=14950 return dir,de,child

start n=node(14950) return n


# Create / Update

create(n1: DirTree) create(n2: DirTree) create (n1)-[:Link]->(n2)

match(HEAD:SymbolicRepoRef{name: "HEAD"}),(originHead:SymbolicRepoRef {name: "refs/remotes/origin/HEAD"}) create (HEAD)-[:MyLink]->(originHead)

match(HEAD:SymbolicRepoRef{name: "HEAD"}),(originHead:SymbolicRepoRef {name: "refs/remotes/origin/HEAD"}),(HEAD)-[x]->(originHead) return HEAD,x,originHead


# current Tree of HEAD
match(head: SymbolicRepoRef{name:'HEAD'})-[target]-(headTarget)-[refCommit]->(ci)-[ciRevTree:revTree]->(revTree) return revTree

# current child tree of head
match(head: SymbolicRepoRef{name:'HEAD'})-[target]-(headTarget)-[refCommit]->(ci)-[ciRevTree:revTree]->(revTree)
  with revTree match (revTree)-[child:has_entry]->(t) return revTree,child,t



# delete ...
MATCH (e: RevCi) -[p]- (e2) DELETE e,p
match(dir:DirTree) -[rel]-(x) delete rel,dir
match(de:DirEntry) -[rel]-(x) delete rel,de
match(blob:Blob) -[rel]-(x) delete rel,blob

