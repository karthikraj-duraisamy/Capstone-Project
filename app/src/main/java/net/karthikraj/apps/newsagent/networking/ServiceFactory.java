package net.karthikraj.apps.newsagent.networking;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.karthikraj.apps.newsagent.BuildConfig.BASE_URL;

/**
 * Created by karthik on 31/10/17.
 */

public class ServiceFactory {

    /**
     * Creates a retrofit service from an arbitrary class (clazz)
     * @return retrofit service with defined endpoint
     */
    public static ApiInterface createRetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        return service;
    }
}
