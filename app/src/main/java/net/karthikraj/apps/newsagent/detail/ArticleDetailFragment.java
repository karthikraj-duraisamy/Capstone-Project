package net.karthikraj.apps.newsagent.detail;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    private Cursor mCursor;
    private long mItemId;
    @BindView(R.id.ivArticleImage)
    ImageView mPhotoView;
    @BindView(R.id.article_date)
    TextView dateView;
    @BindView(R.id.article_title)
    TextView titleView;
    @BindView(R.id.article_author)
    TextView authorView;
    @BindView(R.id.article_body)
    TextView bodyView;
    @BindView(R.id.share_fab)
    FloatingActionButton mShareButton;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        return mRootView;
    }

    private void bindViews() {

        if (mCursor != null) {
            final String title = mCursor.getString(ArticleDetailsActivity.Query.TITLE);

            String date = DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(ArticleDetailsActivity.Query.PUBLISHED_AT),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString();

            String author = mCursor.getString(ArticleDetailsActivity.Query.AUTHOR_NAME);
            final String body = Html.fromHtml(mCursor.getString(ArticleDetailsActivity.Query.TITLE) +
                    "</br></br></br>" + mCursor.getString(ArticleDetailsActivity.Query.DESCRIPTION) +
                    "</br></br></br> Read the complete story " + mCursor.getString(ArticleDetailsActivity.Query.URL) +
                    "</br></br></br> Original news source is " + mCursor.getString(ArticleDetailsActivity.Query.SOURCE_NAME)).toString();
            String photo = mCursor.getString(ArticleDetailsActivity.Query.URL_TO_IMAGE);

            if (mToolbar != null) {
                mToolbar.setTitle(title);
            }

            Picasso.with(mPhotoView.getContext())
                    .load(mCursor.getString(ArticleDetailsActivity.Query.URL_TO_IMAGE))
                    .placeholder(R.drawable.news_feed_placeholder)
                    .error(R.drawable.news_feed_placeholder)
                    .into(mPhotoView);


            titleView.setText(title);
            dateView.setText(date);
            authorView.setText(author);
            bodyView.setText(body);

            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(body)
                            .getIntent(), "Share"));
                }
            });

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                ArticlesContract.ArticleEntry.CONTENT_URI,
                ArticleDetailsActivity.Query.PROJECTION,
                ArticlesContract.ArticleEntry._ID + " = ?",
                new String[]{mItemId+""},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


}

