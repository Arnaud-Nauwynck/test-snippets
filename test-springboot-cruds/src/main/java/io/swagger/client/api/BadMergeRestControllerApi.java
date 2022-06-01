package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import io.swagger.client.model.BadMergeReferenceDTO;
import io.swagger.client.model.BadMergeSourceDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BadMergeRestControllerApi {
  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;BadMergeSourceDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/v1/bad-merge/create-bug")
  Call<BadMergeSourceDTO> createBug(
                    @retrofit2.http.Body BadMergeSourceDTO body    
  );

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;BadMergeSourceDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/v1/bad-merge/create-ok")
  Call<BadMergeSourceDTO> createOk(
                    @retrofit2.http.Body BadMergeSourceDTO body    
  );

  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;BadMergeSourceDTO&gt;
   */
  @GET("api/v1/bad-merge/{id}")
  Call<BadMergeSourceDTO> get4(
            @retrofit2.http.Path("id") Long id            
  );

  /**
   * 
   * 
   * @param id  (required)
   * @return Call&lt;BadMergeReferenceDTO&gt;
   */
  @GET("api/v1/bad-merge/ref/{id}")
  Call<BadMergeReferenceDTO> getRef(
            @retrofit2.http.Path("id") Long id            
  );

  /**
   * 
   * 
   * @param body  (required)
   * @return Call&lt;BadMergeSourceDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("api/v1/bad-merge")
  Call<BadMergeSourceDTO> update(
                    @retrofit2.http.Body BadMergeSourceDTO body    
  );

}
