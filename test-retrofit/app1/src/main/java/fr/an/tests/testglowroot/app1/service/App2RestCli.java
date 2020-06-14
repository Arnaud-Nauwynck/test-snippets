package fr.an.tests.testglowroot.app1.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;

@Service
public class App2RestCli {

    public static class App2FooResponseDTO {
        public String msg;
    }
    
    public static interface App2Rest {
        
        @GET("/api/app2/foo")
        Call<App2FooResponseDTO> foo();
        
    }
    
    private App2Rest app2Rest;
    
    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:8082")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

        this.app2Rest = retrofit.create(App2Rest.class);
    }
    
    public App2FooResponseDTO foo() {
        Call<App2FooResponseDTO> req = app2Rest.foo();
        try {
            Response<App2FooResponseDTO> resp = req.execute();
            return resp.body();
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

}
