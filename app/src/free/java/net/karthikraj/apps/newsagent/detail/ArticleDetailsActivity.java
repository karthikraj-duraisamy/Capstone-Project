package net.karthikraj.apps.newsagent.detail;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.data.LikedArticlesContract;
import net.karthikraj.apps.newsagent.model.Article;

import butterknife.BindView;
import butterknife.ButterKnife;

public class    ArticleDetailsActivity extends AppCompatActivity {

    public final static String TAG = net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity.class.getSimpleName();

    public final static String EXTRA_SELECTED_ARTICLE = "EXTRA_SELECTED_ARTICLE";
    public final static String EXTRA_SELECTED_CATEGORY = "EXTRA_SELECTED_CATEGORY";
    public final static String EXTRA_SELECTED_ID = "EXTRA_SELECTED_ID";

    private long mSelectedItemId;
    private int mSelectedCategory;
    private Article mSelectedArticle;
    private String articleBody;

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
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
    @BindView(R.id.fabLike)
    FloatingActionButton mLikeButton;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.adView)
    AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(EXTRA_SELECTED_ARTICLE)) {
                mSelectedArticle = (Article) getIntent().getParcelableExtra(EXTRA_SELECTED_ARTICLE);
                mSelectedItemId = mSelectedArticle.getRowId();
                mSelectedCategory = getIntent().getIntExtra(EXTRA_SELECTED_CATEGORY, 0);
            }
        } else {
            mSelectedArticle = savedInstanceState.getParcelable(EXTRA_SELECTED_ARTICLE);
            mSelectedCategory = savedInstanceState.getInt(EXTRA_SELECTED_CATEGORY, 0);
            mSelectedItemId = savedInstanceState.getLong(EXTRA_SELECTED_ID);
        }

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportFinishAfterTransition();
                }
            });
        }


        final String title = mSelectedArticle.getTitle();

        String date = mSelectedArticle.getPublishedAt();

        String author = mSelectedArticle.getAuthor();
        articleBody = mSelectedArticle.getTitle() +
                "\n\n" + mSelectedArticle.getDescription()+
                "\n\n Read the complete story " + mSelectedArticle.getUrl() +
                "\n\n Original news source is " + mSelectedArticle.getSourceName();

        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }

        Picasso.with(mPhotoView.getContext())
                .load(mSelectedArticle.getUrlToImage())
                .placeholder(R.drawable.news_feed_placeholder)
                .error(R.drawable.news_feed_placeholder)
                .fit()
                .into(mPhotoView);


        titleView.setText(title);
        dateView.setText(date);
        authorView.setText(author);
        bodyView.setText(articleBody);

        if(isTheArticleLiked()) {
            mLikeButton.setImageResource(R.drawable.ic_heart);
        } else {
            mLikeButton.setImageResource(R.drawable.ic_heart_outline);
        }

        // Create an ad request. Check logcat output for the hashed device ID to
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_SELECTED_ARTICLE, mSelectedArticle);
        outState.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
        outState.putLong(EXTRA_SELECTED_ID, mSelectedItemId);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onShareButtonClick(View view) {
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailsActivity.this)
                .setType("text/plain")
                .setText(articleBody)
                .getIntent(), getString(R.string.action_share)));
    }

    public void onLikeButtonClick(View view) {
        if(!isTheArticleLiked()) {
            addToLikesTable();
        } else {
            removeFromLikesTable();
        }
    }

    private void addToLikesTable() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LikedArticlesContract.ArticleEntry.AUTHOR_NAME, mSelectedArticle.getAuthor());
        contentValues.put(LikedArticlesContract.ArticleEntry.TITLE, mSelectedArticle.getTitle());
        contentValues.put(LikedArticlesContract.ArticleEntry.DESCRIPTION, mSelectedArticle.getDescription());
        contentValues.put(LikedArticlesContract.ArticleEntry.URL, mSelectedArticle.getUrl());
        contentValues.put(LikedArticlesContract.ArticleEntry.URL_TO_IMAGE, mSelectedArticle.getUrlToImage());
        contentValues.put(LikedArticlesContract.ArticleEntry.SOURCE_NAME, mSelectedArticle.getSourceName());
        contentValues.put(LikedArticlesContract.ArticleEntry.PUBLISHED_AT, mSelectedArticle.getPublishedAt());
        Log.w(TAG, contentValues.toString());
        try {
            getContentResolver().insert(LikedArticlesContract.ArticleEntry.CONTENT_URI,
                    contentValues);
        } catch (Exception e){}

        mLikeButton.setImageResource(R.drawable.ic_heart);
        Snackbar.make(bodyView, R.string.alert_msg_likes_addition_success, Snackbar.LENGTH_LONG).setAction(R.string.action_remove, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromLikesTable();
            }
        }).show();
    }

    private void removeFromLikesTable() {
        getContentResolver().delete(LikedArticlesContract.ArticleEntry.CONTENT_URI,
                LikedArticlesContract.ArticleEntry.TITLE + " LIKE ? ", new String[]{mSelectedArticle.getTitle()});
        mLikeButton.setImageResource(R.drawable.ic_heart_outline);
        Snackbar.make(bodyView, R.string.alert_likes_remove_success, Snackbar.LENGTH_LONG).setAction(R.string.action_add_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToLikesTable();
            }
        }).show();
    }

    public boolean isTheArticleLiked() {
        Cursor cursor = getContentResolver().query(LikedArticlesContract.ArticleEntry.CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String articleTitle = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.TITLE));
                if(articleTitle.equals(mSelectedArticle.getTitle())) {
                    cursor.close();
                    return true;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return false;
    }

}
