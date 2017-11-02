package net.karthikraj.apps.newsagent.networking;

import net.karthikraj.apps.newsagent.model.Articles;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by karthik on 30/10/17.
 */

public interface ApiInterface {

    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("articles")
    Call<Articles> getArticles(@Query("source") String source, @Query("apiKey") String apiKey);

    @GET("articles")
    Observable<Articles> getArticlesRx(@Query("source") String source, @Query("apiKey") String apiKey);


}
