
Metastore for BigData (Spark,Hive,..) - replacement of Hive Metastore DB
--------------



Big Picture:

       Api (DTO)         +---------------------+          Spark ExternalCatalog
           ------->      | metastore-model     |     <---   ... run embeded in Spark
                         |                     |
                         | catalog             |
                         |  - database         |
       ConfigFile        |   - table           |
          (Yaml)         |     - tablePartition|
           ------->      |     - stats         |
                         |   - function        |
                         +---------------------+
                                 /\
                                  |
                               Efficient Storage
                           ( model file + partitions )   
                                /---\
                                |   |
                                \---/
                                
                                 