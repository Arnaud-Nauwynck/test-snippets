package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import io.swagger.client.model.CustomQueryResultDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DirectQueryRestControllerApi {
  /**
   * 
   * 
   * @return Call&lt;List&lt;CustomQueryResultDTO&gt;&gt;
   */
  @GET("api/v1/directquery/query1")
  Call<List<CustomQueryResultDTO>> query1();
    

}
