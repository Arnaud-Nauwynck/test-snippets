package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import io.swagger.client.model.TodoEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface V2TodoRestControllerApi {
  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoEntity&gt;
   */
  @DELETE("api/v2/todo/{id}")
  Call<TodoEntity> deleteTodo2(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoEntity&gt;
   */
  @GET("api/v2/todo/{id}")
  Call<TodoEntity> get2(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @return Call&lt;List&lt;TodoEntity&gt;&gt;
   */
  @GET("api/v2/todo")
  Call<List<TodoEntity>> list2();
    

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;TodoEntity&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/v2/todo")
  Call<TodoEntity> postTodo2(
                    @retrofit2.http.Body TodoEntity body    
  );

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;TodoEntity&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("api/v2/todo")
  Call<TodoEntity> putTodo2(
                    @retrofit2.http.Body TodoEntity body    
  );

}
