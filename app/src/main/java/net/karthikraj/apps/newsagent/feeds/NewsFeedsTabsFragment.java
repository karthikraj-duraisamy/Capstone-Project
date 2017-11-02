package net.karthikraj.apps.newsagent.feeds;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.NewsAgentDBHelper;
import net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class NewsFeedsTabsFragment extends Fragment implements NewsFeedsAdapter.ArticleClickListener, LoaderManager.LoaderCallbacks<Cursor>, DownloadHelper.DownloadListener{

    private static final String TAG = NewsFeedsTabsFragment.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private NewsFeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Article> mArticleList;

    private static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public NewsFeedsTabsFragment() {
    }

    public static NewsFeedsTabsFragment newInstance(final int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NewsFeedsTabsFragment fragment = new NewsFeedsTabsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
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

            String[] selectionArgs;
            switch (getArguments().getInt(ARG_PAGE)) {
                case 0:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_general);
                    Log.i(TAG, "Articles ready to lead into Adapter for general");
                    break;
                case 1:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_sports);
                    Log.i(TAG, "Articles ready to lead into Adapter for sports");
                    break;
                case 2:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_entertainment);
                    Log.i(TAG, "Articles ready to lead into Adapter for entertainment");
                    break;
                case 3:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_business);
                    Log.i(TAG, "Articles ready to lead into Adapter for business");
                    break;
                case 4:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_technology);
                    Log.i(TAG, "Articles ready to lead into Adapter for technology");
                    break;
                case 5:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_music);
                    Log.i(TAG, "Articles ready to lead into Adapter for music");
                    break;
                default:
                    selectionArgs = getActivity().getResources().getStringArray(R.array.category_general);
                    Log.i(TAG, "Articles ready to lead into Adapter for General default");
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

}
