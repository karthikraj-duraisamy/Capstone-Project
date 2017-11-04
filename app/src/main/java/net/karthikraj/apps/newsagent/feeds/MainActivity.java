package net.karthikraj.apps.newsagent.feeds;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity;
import net.karthikraj.apps.newsagent.model.Article;
import net.karthikraj.apps.newsagent.utils.DrawarListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.karthikraj.apps.newsagent.detail.ArticleDetailsActivity.EXTRA_SELECTED_ARTICLE;
import static net.karthikraj.apps.newsagent.widget.NewsAppWidget.EXTRA_WIDGET_SELECTION_ARTICLE_ID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionBarDrawerToggle toggle;

    @BindView(R.id.viewpagerMainActivity)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabsMainActivity)
    TabLayout tabsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseCrash.log("main Activity created");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("NewsAgent");

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        DrawarListener drawarListener = new DrawarListener(drawer, this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(drawarListener);
        if(getIntent().getExtras() != null) {
            FirebaseCrash.log("main Activity created from Widgets");

            Bundle bundle = getIntent().getExtras();
            long articleBundleId = bundle.getLong(EXTRA_WIDGET_SELECTION_ARTICLE_ID, 0);
            Cursor cursor = getContentResolver().query(ArticlesContract.ArticleEntry.CONTENT_URI, null, ArticlesContract.ArticleEntry._ID+" LIKE  ?", new String[]{articleBundleId+""}, null);

            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                Article article = new Article();
                article.setRowId(cursor.getLong(cursor.getColumnIndex(ArticlesContract.ArticleEntry._ID)));
                article.setDescription(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.DESCRIPTION)));
                article.setUrl(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.URL)));
                article.setAuthor(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.AUTHOR_NAME)));
                article.setPublishedAt(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.PUBLISHED_AT)));
                article.setSourceName(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.SOURCE_NAME)));
                article.setTitle(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.TITLE)));
                article.setUrlToImage(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.URL_TO_IMAGE)));

                Intent detailActivityIntent = new Intent(MainActivity.this, ArticleDetailsActivity.class);
                detailActivityIntent.putExtra(EXTRA_SELECTED_ARTICLE, article);
                startActivity(detailActivityIntent);
                Bundle bundleAnalytics = new Bundle();
                bundleAnalytics.putString(FirebaseAnalytics.Param.ITEM_NAME, article.getTitle());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleAnalytics);

            }
            cursor.close();
        }
        //if(savedInstanceState == null) {
            NewsFeedsTabsAdapter newsFeedsTabsAdapter = new NewsFeedsTabsAdapter(getSupportFragmentManager(),
                    MainActivity.this);
            viewPager.setAdapter(newsFeedsTabsAdapter);
            // Give the TabLayout the ViewPager
            tabsLayout.setupWithViewPager(viewPager);
        //}
    }

    @Override
    protected void onResume() {
        drawer.setSelected(true);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
