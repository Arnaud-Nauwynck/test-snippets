
test for hibernate + DTO mapping, using Lazy Loading on ManyToOne-Id (FK)
using springboot spring-data + Orika mapping

=> show several DTO mapping strategies:

1/ DTO with id only, and mapping @Entity class with redundant (updatable=false, insertable=false) @ManyToOne "<fk>_id" field 

2/ mapping DTO with ligtweight IdDTO class (DTO containing only id)

3/ mapping DTO with ligtweight NameIdDTO class (DTO containing disaplyable id+name fields) 
  .. with dreadfull 1+N sql queries performance problem 

4/ repository @Query with LEFT JOIN for projection with ligtweight NameIdDTO class

5/ full Entity to DTO mapping
  ... with dreadfull 1+N sql queries performance problem ... and maybe infinite loop in json
