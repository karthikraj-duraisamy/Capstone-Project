package net.karthikraj.apps.newsagent.feeds;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.NewsAgentDBHelper;
import net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity;
import net.karthikraj.apps.newsagent.model.Article;
import net.karthikraj.apps.newsagent.utils.DownloadJobService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class GeneralNewsFeedFragment extends Fragment implements NewsFeedsAdapter.ArticleClickListener, LoaderManager.LoaderCallbacks<Cursor>, DownloadHelper.DownloadListener{

    private static final String TAG = NewsFeedsTabsFragment.class.getSimpleName();
    private static final int CURSOR_LOADER = 001;
    private static final String EXTRA_POSITION = "extra_position";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private NewsFeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Article> mArticleList;

    public GeneralNewsFeedFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_POSITION, mRecyclerView.getVerticalScrollbarPosition());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_feeds, container, false);
        ButterKnife.bind(this,rootView);
            mArticleList = new ArrayList<>();
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new NewsFeedsAdapter(getActivity(), this);
            //mAdapter.updateDataSet(mArticleList);
            mRecyclerView.setAdapter(mAdapter);
            getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this).forceLoad();
        scheduleJobToDownloadArticles();

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onArticleClicked(Article article, View imageView, View titleTextView, View authorNameTextView, View pushlishDateTextView) {
        Intent launchIntent = new Intent(getActivity(), ArticleDetailsActivity.class);
        launchIntent.putExtra(ArticleDetailsActivity.EXTRA_SELECTED_ARTICLE, article);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View, String> p1 = Pair.create((View) imageView, "newsarticle");
            Pair<View, String> p2 = Pair.create((View) titleTextView, "newsarticletitle");
            Pair<View, String> p3 = Pair.create((View) authorNameTextView, "newsarticleauthor");
            Pair<View, String> p4 = Pair.create((View) pushlishDateTextView, "newsarticledate");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), p1, p2, p3, p4);

            getActivity().startActivity(launchIntent, options.toBundle());
        } else {
            startActivity(launchIntent);
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

    //AsyncLoader Callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                ArticlesContract.ArticleEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null)
            return;

        Cursor cursor = (Cursor) data;
        mArticleList.clear();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Article article = new Article();
                article.setRowId(cursor.getLong(cursor.getColumnIndex(ArticlesContract.ArticleEntry._ID)));
                article.setDescription(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.DESCRIPTION)));
                article.setUrl(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.URL)));
                article.setAuthor(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.AUTHOR_NAME)));
                article.setPublishedAt(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.PUBLISHED_AT)));
                article.setSourceName(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.SOURCE_NAME)));
                article.setTitle(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.TITLE)));
                article.setUrlToImage(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.URL_TO_IMAGE)));
                //Log.i(TAG, "Articles ready to lead into Adapter: ");
                //Log.i(TAG, article.toString());
                mArticleList.add(article);
                cursor.moveToNext();
            }

            String[] selectionArgs = getActivity().getResources().getStringArray(R.array.category_general);
            List<Article> tempArticleList = new ArrayList<>();
            for (Article article:mArticleList) {
                for(int i = 0; i < selectionArgs.length; i++) {
                    if(article.getSourceName().equals(selectionArgs[i]))
                        tempArticleList.add(article);
                }
            }

            mAdapter.updateDataSet(tempArticleList);
            mAdapter.notifyDataSetChanged();

        } else {
            SQLiteDatabase db = new NewsAgentDBHelper(getActivity()).getReadableDatabase();
            if (DatabaseUtils.queryNumEntries(db, ArticlesContract.ArticleEntry.TABLE_ARTICLES) == 0) {
                String[] categoriesArray = getResources().getStringArray(R.array.available_catgories);
                DownloadHelper downloadHelper = new DownloadHelper(getActivity(), this);
                for (int i = 0; i < categoriesArray.length; i++) {
                    DownloadHelper.ArticlesCategory articlesCategory = DownloadHelper.ArticlesCategory.GENERAL;
                    articlesCategory = getArticlesCategory(categoriesArray[i], articlesCategory);
                    downloadHelper.downloadArtciles(articlesCategory);
                }
            }
        }
    }

    @Override
    public void onDownloadComplete() {
        getActivity().getSupportLoaderManager().restartLoader(CURSOR_LOADER, null, this).forceLoad();
    }

    //scheduleJobToDownloadArticles();

    private void scheduleJobToDownloadArticles() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
        Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){
        Job job = dispatcher.newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                // Call this service when the criteria are met.
                .setService(DownloadJobService.class)
                // unique id of the task
                .setTag("ArticlsDownloadJob")
                // We are mentioning that the job is not periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow((3*3600), (3*3600)+30))
                //Run this job only when the network is avaiable.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        return job;
    }
}
