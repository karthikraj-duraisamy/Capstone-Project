package net.karthikraj.apps.newsagent.likes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.LikedArticlesContract;
import net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity;
import net.karthikraj.apps.newsagent.feeds.FeedsListActivity;
import net.karthikraj.apps.newsagent.feeds.NewsFeedsAdapter;
import net.karthikraj.apps.newsagent.model.Article;
import net.karthikraj.apps.newsagent.utils.DrawarListener;

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
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;
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

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        DrawarListener drawarListener = new DrawarListener(drawer, this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(drawarListener);

    }

    @Override
    public void onArticleClicked(Article article, View imageView, View titleTextView, View authorNameTextView, View pushlishDateTextView) {
        Intent launchIntent = new Intent(LikedArticlesActivity.this, ArticleDetailsActivity.class);
        launchIntent.putExtra(ArticleDetailsActivity.EXTRA_SELECTED_ARTICLE, article);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View, String> p1 = Pair.create((View) imageView, "newsarticle");
            Pair<View, String> p2 = Pair.create((View) titleTextView, "newsarticletitle");
            Pair<View, String> p3 = Pair.create((View) authorNameTextView, "newsarticleauthor");
            Pair<View, String> p4 = Pair.create((View) pushlishDateTextView, "newsarticledate");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(LikedArticlesActivity.this, p1, p2, p3, p4);

            startActivity(launchIntent, options.toBundle());
        } else {
            startActivity(launchIntent);
        }
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }}
