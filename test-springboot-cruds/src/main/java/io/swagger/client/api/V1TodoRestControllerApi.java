package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import io.swagger.client.model.TodoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface V1TodoRestControllerApi {
  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @DELETE("api/v1/todo/{id}")
  Call<TodoDTO> deleteTodo3(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @GET("api/v1/todo/{id}")
  Call<TodoDTO> get3(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @return Call&lt;List&lt;TodoDTO&gt;&gt;
   */
  @GET("api/v1/todo")
  Call<List<TodoDTO>> list3();
    

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/v1/todo")
  Call<TodoDTO> postTodo3(
                    @retrofit2.http.Body TodoDTO body    
  );

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("api/v1/todo")
  Call<TodoDTO> putTodo3(
                    @retrofit2.http.Body TodoDTO body    
  );

}
