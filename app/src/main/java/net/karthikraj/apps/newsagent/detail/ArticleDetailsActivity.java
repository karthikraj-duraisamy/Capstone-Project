package net.karthikraj.apps.newsagent.detail;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.model.Article;

/**
 * Created by karthik on 1/11/17.
 */

public class ArticleDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String EXTRA_SELECTED_ARTICLE = "EXTRA_SELECTED_ARTICLE";
    public final static String EXTRA_SELECTED_ID = "EXTRA_SELECTED_ID";

    private Cursor mCursor;
    private long mSelectedItemId;
    private Article mSelectedArticle;
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mSelectedArticle = (Article) getIntent().getParcelableExtra(EXTRA_SELECTED_ARTICLE);
                mSelectedItemId = mSelectedArticle.getRowId();
            }
        }
        else {
            mSelectedItemId = savedInstanceState.getLong(EXTRA_SELECTED_ID);
        }

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewPagerDetailActivity);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_SELECTED_ID, mSelectedItemId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(ArticleDetailsActivity.this,
                ArticlesContract.ArticleEntry.CONTENT_URI,
                Query.PROJECTION,
                null,
                null,
                null);


    }

    public interface Query {
        String[] PROJECTION = {
                ArticlesContract.ArticleEntry._ID,
                ArticlesContract.ArticleEntry.TITLE,
                ArticlesContract.ArticleEntry.AUTHOR_NAME,
                ArticlesContract.ArticleEntry.SOURCE_NAME,
                ArticlesContract.ArticleEntry.DESCRIPTION,
                ArticlesContract.ArticleEntry.PUBLISHED_AT,
                ArticlesContract.ArticleEntry.URL,
                ArticlesContract.ArticleEntry.URL_TO_IMAGE,
        };

        int _ID = 0;
        int TITLE = 1;
        int AUTHOR_NAME = 2;
        int SOURCE_NAME = 3;
        int DESCRIPTION = 4;
        int PUBLISHED_AT = 5;
        int URL = 6;
        int URL_TO_IMAGE = 7;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mSelectedItemId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(Query._ID) == mSelectedItemId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mSelectedItemId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
