package net.karthikraj.apps.newsagent.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.categories.CategoriesActivity;
import net.karthikraj.apps.newsagent.feeds.FeedsListActivity;
import net.karthikraj.apps.newsagent.feeds.MainActivity;
import net.karthikraj.apps.newsagent.likes.LikedArticlesActivity;

/**
 * Created by karthik on 31/10/17.
 */

public class DrawarListener implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public Activity ownerActivity;

    public DrawarListener(DrawerLayout drawer, Activity ownerActivity) {
        this.drawer = drawer;
        this.ownerActivity = ownerActivity;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent launchIntent;
        if (id == R.id.nav_feeds) {
            launchIntent = new Intent(ownerActivity, MainActivity.class);
            ownerActivity.startActivity(launchIntent);
            ownerActivity.finish();
        } else if (id == R.id.nav_category) {
            launchIntent = new Intent(ownerActivity, CategoriesActivity.class);
            ownerActivity.startActivity(launchIntent);
            ownerActivity.finish();
        } else if (id == R.id.nav_likes) {
            launchIntent = new Intent(ownerActivity, LikedArticlesActivity.class);
            launchIntent.putExtra(FeedsListActivity.SELECTED_CATEGORY, 0);
            ownerActivity.startActivity(launchIntent);
        } else if (id == R.id.nav_about) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
