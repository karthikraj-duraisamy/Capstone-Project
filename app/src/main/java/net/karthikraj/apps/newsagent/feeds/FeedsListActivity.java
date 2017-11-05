package net.karthikraj.apps.newsagent.feeds;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class FeedsListActivity extends AppCompatActivity implements NewsFeedsAdapter.ArticleClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = FeedsListActivity.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;
    public static final String SELECTED_CATEGORY = "selected_category";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private NewsFeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Article> mArticleList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_lists);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mArticleList = new ArrayList<>();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new NewsFeedsAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this).forceLoad();

    }

    @Override
    public void onArticleClicked(Article article, View imageView, View titleTextView, View authorNameTextView, View pushlishDateTextView) {
        Intent launchIntent = new Intent(FeedsListActivity.this, ArticleDetailsActivity.class);
        launchIntent.putExtra(ArticleDetailsActivity.EXTRA_SELECTED_ARTICLE, article);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View, String> p1 = Pair.create(imageView, "newsarticle");
            Pair<View, String> p2 = Pair.create(titleTextView, "newsarticletitle");
            Pair<View, String> p3 = Pair.create(authorNameTextView, "newsarticleauthor");
            Pair<View, String> p4 = Pair.create(pushlishDateTextView, "newsarticledate");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(FeedsListActivity.this, p1, p2, p3, p4);

            startActivity(launchIntent, options.toBundle());
        } else {
            startActivity(launchIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
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

        Cursor cursor = data;
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

            String[] selectionArgs;
            switch (getIntent().getIntExtra(SELECTED_CATEGORY, 0)) {
                case 0:
                    selectionArgs = getResources().getStringArray(R.array.category_general);
                    Log.i(TAG, "Articles ready to lead into Adapter for general");
                    actionBar.setTitle("General");
                    break;
                case 1:
                    selectionArgs = getResources().getStringArray(R.array.category_sports);
                    Log.i(TAG, "Articles ready to lead into Adapter for sports");
                    actionBar.setTitle("Sports");
                    break;
                case 2:
                    selectionArgs = getResources().getStringArray(R.array.category_entertainment);
                    Log.i(TAG, "Articles ready to lead into Adapter for entertainment");
                    actionBar.setTitle("Entertainment");
                    break;
                case 3:
                    selectionArgs = getResources().getStringArray(R.array.category_business);
                    Log.i(TAG, "Articles ready to lead into Adapter for business");
                    actionBar.setTitle("Business");
                    break;
                case 4:
                    selectionArgs = getResources().getStringArray(R.array.category_technology);
                    Log.i(TAG, "Articles ready to lead into Adapter for technology");
                    actionBar.setTitle("Technology");
                    break;
                case 5:
                    selectionArgs = getResources().getStringArray(R.array.category_music);
                    Log.i(TAG, "Articles ready to lead into Adapter for music");
                    actionBar.setTitle("Music");
                    break;
                default:
                    selectionArgs = getResources().getStringArray(R.array.category_general);
                    Log.i(TAG, "Articles ready to lead into Adapter for General default");
                    actionBar.setTitle("General");
                    break;
            }
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
            Snackbar.make(mRecyclerView, R.string.msg_no_articles_found, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
