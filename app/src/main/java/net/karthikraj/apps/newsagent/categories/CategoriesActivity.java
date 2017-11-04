package net.karthikraj.apps.newsagent.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.feeds.FeedsListActivity;
import net.karthikraj.apps.newsagent.utils.DrawarListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 31/10/17.
 */

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.category));

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        DrawarListener drawarListener = new DrawarListener(drawer, this);
        navigationView.setNavigationItemSelectedListener(drawarListener);

    }


    public void onGeneralClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 0);
        startActivity(launchIntent);
    }

    public void onSportsClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 1);
        startActivity(launchIntent);
    }

    public void onEntertainmentClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 2);
        startActivity(launchIntent);
    }

    public void onBusinessClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 3);
        startActivity(launchIntent);
    }

    public void onTechnologyClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 4);
        startActivity(launchIntent);
    }

    public void onMusicClick(View view) {
        Intent launchIntent = new Intent(CategoriesActivity.this, FeedsListActivity.class);
        launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 5);
        startActivity(launchIntent);
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

