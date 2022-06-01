package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import io.swagger.client.model.ResponseDTO;
import io.swagger.client.model.TodoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TodoRestControllerApi {
  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @DELETE("api/todo/{id}")
  Call<TodoDTO> deleteTodo4(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;TodoDTO&gt;
   */
  @GET("api/todo/{id}")
  Call<TodoDTO> get5(
            @retrofit2.http.Path("id") Integer id            
  );

  /**
   * 
   * 
   * @return Call&lt;List&lt;TodoDTO&gt;&gt;
   */
  @GET("api/todo")
  Call<List<TodoDTO>> list4();
    

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;ResponseDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/todo")
  Call<ResponseDTO> postTodo4(
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
  @PUT("api/todo")
  Call<TodoDTO> putTodo4(
                    @retrofit2.http.Body TodoDTO body    
  );

}
