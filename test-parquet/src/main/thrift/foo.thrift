namespace java fr.an.test.parquet.thrift

struct ThriftFoo {
  1: i32 intField = 0;
  // 2: optional string comment;
}

service MyThriftService {

   void addFoo(1: ThriftFoo foo);

}
