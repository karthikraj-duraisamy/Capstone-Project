package net.karthikraj.apps.newsagent.feeds;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.karthikraj.apps.newsagent.BuildConfig;
import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.model.Article;
import net.karthikraj.apps.newsagent.model.Articles;
import net.karthikraj.apps.newsagent.networking.ApiInterface;
import net.karthikraj.apps.newsagent.networking.ServiceFactory;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by karthik on 31/10/17.
 */

public class DownloadHelper {

    private static final String TAG = "MainActivity" + DownloadHelper.class.getSimpleName();
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final String API_KEY = BuildConfig.API_KEY;


    private Context mContext;
    public enum ArticlesCategory {
        GENERAL,
        SPORTS,
        TECHNOLOGY,
        ENTERTAINMENT,
        BUSINESS,
        MUSIC
    }
    private DownloadListener downloadListener;
    private ApiInterface apiInterface;
    public interface DownloadListener {
        void onDownloadComplete();
    }

    public DownloadHelper(Context callingActivityParam, DownloadListener downloadListenerParam) {
        this.mContext = callingActivityParam;
        this.downloadListener = downloadListenerParam;
        apiInterface = ServiceFactory.createRetrofitService();
    }

    public void downloadArtciles(ArticlesCategory category) {

        apiInterface = ServiceFactory.createRetrofitService();
        String[] categorySourceArray;
        categorySourceArray = getCategorySources(category);

        for(int i = 0; i < categorySourceArray.length; i++) {
            String categorySource = categorySourceArray[i];
            apiInterface.getArticlesRx(categorySource, API_KEY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Articles>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Articles articles) {

                            ContentValues[] contentValues = new ContentValues[articles.getArticles().size()];
                            int i = 0;

                            for(Article article:articles.getArticles()) {
                                if(article != null)
                                if(article.getUrlToImage() != null && article.getUrlToImage().length() > 0) {
                                    contentValues[i] = new ContentValues();
                                    contentValues[i].put(ArticlesContract.ArticleEntry.AUTHOR_NAME, article.getAuthor());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.TITLE, article.getTitle());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.DESCRIPTION, article.getDescription());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.URL, article.getUrl());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.URL_TO_IMAGE, article.getUrlToImage());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.SOURCE_NAME, articles.getSource());
                                    contentValues[i].put(ArticlesContract.ArticleEntry.PUBLISHED_AT, article.getPublishedAt());
                                    Log.w(TAG, contentValues[i].toString());
                                }
                                i++;
                            }
                            try {
                                mContext.getContentResolver().bulkInsert(ArticlesContract.ArticleEntry.CONTENT_URI,
                                        contentValues);
                            } catch (IllegalStateException e) {
                                Log.w(TAG, "Database insertion failed because of the content values may null");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.v(TAG, e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            downloadListener.onDownloadComplete();
                        }
                    });
        }
    }

    @android.support.annotation.NonNull
    private String[] getCategorySources(ArticlesCategory category) {
        String[] categorySourceArray;
        switch (category) {
            case GENERAL:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_general);
                break;
            case SPORTS:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_sports);
                break;
            case TECHNOLOGY:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_technology);
                break;
            case ENTERTAINMENT:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_entertainment);
                break;
            case BUSINESS:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_business);
                break;
            case MUSIC:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_music);
                break;
            default:
                categorySourceArray = mContext.getResources().getStringArray(R.array.category_general);
                break;
        }
        return categorySourceArray;
    }
}
