package net.karthikraj.apps.newsagent.likes;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.LikedArticlesContract;
import net.karthikraj.apps.newsagent.feeds.NewsFeedsAdapter;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class LikedArticlesActivity extends AppCompatActivity implements NewsFeedsAdapter.ArticleClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = LikedArticlesActivity.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private NewsFeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Article> mArticleList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_lists);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Liked Articles");

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
    public void onArticleClicked(Article article) {
        Snackbar.make(mRecyclerView, "We need to call detailView from here", Snackbar.LENGTH_LONG).show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                LikedArticlesContract.ArticleEntry.CONTENT_URI,
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

            mAdapter.updateDataSet(mArticleList);
            mAdapter.notifyDataSetChanged();

        } else {
            Snackbar.make(mRecyclerView, "You have no liked articles yet.", Snackbar.LENGTH_LONG).show();
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
