package com.incampus.graphql_testing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ramkishorevs.graphqlconverter.converter.GraphQLConverter;
import com.ramkishorevs.graphqlconverter.converter.QueryContainerBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.util.JsonToken.BEGIN_OBJECT;

public class MainActivity extends AppCompatActivity {

    private EditText mtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtext = (EditText)findViewById(R.id.text_view);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("content-type", "application/json")
                        .header("x-hasura-admin-secret", "incampus")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://incampus-test.herokuapp.com/v1/")
                .addConverterFactory(GraphQLConverter.create(this))
                .client(client)
                .build();

        Api apiInterface =retrofit.create(Api.class);
        QueryContainerBuilder queryContainer = new QueryContainerBuilder();
           // Call<Response> hello = RetrofitClient.getInstance(this).getApi().login();
        Call<ResponseBody> register =apiInterface.login(queryContainer);


        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    //Toast.makeText(MainActivity.this, "Hooray!!!!!", Toast.LENGTH_SHORT).show();

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().toString());
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    mtext.setText(response.body().toString());

                }

                //SimpleEntity entity = gson.fromJson(response.body().toString(), SimpleEntity.class);
               // mtext.setText(response.body().toString());
                //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }
}
