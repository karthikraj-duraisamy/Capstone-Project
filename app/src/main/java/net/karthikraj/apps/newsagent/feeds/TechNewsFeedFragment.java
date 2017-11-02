package net.karthikraj.apps.newsagent.feeds;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TechNewsFeedFragment extends Fragment implements NewsFeedsAdapter.ArticleClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = NewsFeedsTabsFragment.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private NewsFeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Article> mArticleList;

    public TechNewsFeedFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_feeds, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    public void onArticleClicked(Article article) {
        Snackbar.make(mRecyclerView, "We need to call detailView from here", Snackbar.LENGTH_LONG).show();
        Intent launchIntent = new Intent(getActivity(), ArticleDetailsActivity.class);
        launchIntent.putExtra(ArticleDetailsActivity.EXTRA_SELECTED_ARTICLE, article);
        getActivity().startActivity(launchIntent);
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

            String[] selectionArgs = getActivity().getResources().getStringArray(R.array.category_technology);
            List<Article> tempArticleList = new ArrayList<>();
            for (Article article:mArticleList) {
                for(int i = 0; i < selectionArgs.length; i++) {
                    if(article.getSourceName().equals(selectionArgs[i]))
                        tempArticleList.add(article);
                }
            }

            mAdapter.updateDataSet(tempArticleList);
            mAdapter.notifyDataSetChanged();

        } 
    }

}
