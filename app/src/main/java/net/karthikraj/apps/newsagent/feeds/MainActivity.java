package net.karthikraj.apps.newsagent.feeds;

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

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.utils.DrawarListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_ID = "extra_id";

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


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("NewsAgent");


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        DrawarListener drawarListener = new DrawarListener(drawer, this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(drawarListener);

        NewsFeedsTabsAdapter newsFeedsTabsAdapter = new NewsFeedsTabsAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(newsFeedsTabsAdapter);
        // Give the TabLayout the ViewPager
        tabsLayout.setupWithViewPager(viewPager);
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
