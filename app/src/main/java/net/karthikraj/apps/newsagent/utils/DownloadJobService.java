package net.karthikraj.apps.newsagent.utils;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.NewsAgentDBHelper;
import net.karthikraj.apps.newsagent.feeds.DownloadHelper;

/**
 * Created by karthik on 4/11/17.
 */

public class DownloadJobService extends JobService {

    private static final String TAG = DownloadJobService.class.getSimpleName();
    @Override
    public boolean onStartJob(final JobParameters params) {
        //Offloading work to a new thread.
        Log.w(TAG, "OnstartJob - Job Started to download");
        new Thread(new Runnable() {
            @Override
            public void run() {
                completeJob(params);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.w(TAG, "Job Stop to download");
        return false;
    }

    public void completeJob(final JobParameters parameters) {
        Log.w(TAG, "Job Started to download");
        try {
            SQLiteDatabase db = new NewsAgentDBHelper(getApplicationContext()).getReadableDatabase();
            if (DatabaseUtils.queryNumEntries(db, ArticlesContract.ArticleEntry.TABLE_ARTICLES) == 0) {
                String[] categoriesArray = getResources().getStringArray(R.array.available_catgories);
                DownloadHelper downloadHelper = new DownloadHelper(getApplicationContext(), null);
                for (int i = 0; i < categoriesArray.length; i++) {
                    DownloadHelper.ArticlesCategory articlesCategory = DownloadHelper.ArticlesCategory.GENERAL;
                    articlesCategory = getArticlesCategory(categoriesArray[i], articlesCategory);
                    downloadHelper.downloadArtciles(articlesCategory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and doesnot needs to be reschedule
            jobFinished(parameters, false);
        }
    }

    private DownloadHelper.ArticlesCategory getArticlesCategory(String s, DownloadHelper.ArticlesCategory articlesCategory) {
        if(s.equals("GENERAL")) {
            articlesCategory = DownloadHelper.ArticlesCategory.GENERAL;
        } else if (s.equals("SPORTS")) {
            articlesCategory = DownloadHelper.ArticlesCategory.SPORTS;
        } else if (s.equals("TECHNOLOGY")) {
            articlesCategory = DownloadHelper.ArticlesCategory.TECHNOLOGY;
        } else if (s.equals("ENTERTAINMENT")) {
            articlesCategory = DownloadHelper.ArticlesCategory.ENTERTAINMENT;
        } else if (s.equals("BUSINESS")) {
            articlesCategory = DownloadHelper.ArticlesCategory.BUSINESS;
        } else if (s.equals("MUSIC")) {
            articlesCategory = DownloadHelper.ArticlesCategory.MUSIC;
        }
        return articlesCategory;
    }

}