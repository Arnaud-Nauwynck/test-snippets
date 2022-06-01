package com.example.demo;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.swagger.client.ApiClient;
import io.swagger.client.api.TodoRestControllerApi;
import io.swagger.client.model.TodoDTO;
import retrofit2.Call;
import retrofit2.Response;

public class Retrofit2ClientTest {

	@Test
	public void testSwaggerCliRetrofit2() {
		ApiClient apiClient = new ApiClient();
		TodoRestControllerApi svc = apiClient.createService(TodoRestControllerApi.class);
		
		Call<List<TodoDTO>> call = svc.list4();
		List<TodoDTO> res = executeGetBody(call, "GET /api/todo");

		System.out.println("http GET /api/todo => got " + res.size() + " elts");
	}

	private static <T> T executeGetBody(Call<T> call, String callDescr) {
		Response<T> resp;
		try {
			resp = call.execute();
		} catch (IOException ex) {
			throw new RuntimeException("can not call http " + callDescr, ex);
		}
		if (! resp.isSuccessful()) {
			throw new RuntimeException("Failure in http " + callDescr + ", " + 
					"code:" + resp.code() + " message:" + resp.message());
		}
		return resp.body();
	}
}
