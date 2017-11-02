package net.karthikraj.apps.newsagent.feeds;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;

/**
 * Created by karthik on 1/11/17.
 */

public class NewsFeedsTabsAdapter extends FragmentPagerAdapter {

    private int mPageCount = 0;
    private String tabTitles[];
    private Context context;

    public NewsFeedsTabsAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
        String[] categoriesArray = context.getResources().getStringArray(R.array.available_catgories);
        mPageCount = categoriesArray.length;
        tabTitles = new String[mPageCount];

        for(int i = 0; i < mPageCount; i++){
            tabTitles[i] = categoriesArray[i];
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new GeneralNewsFeedFragment();
            case 1:
                return new SportsNewsFeedFragment();
            case 2:
                return new EntertainmentNewsFeedFragment();
            case 3:
                return new BusinessNewsFeedFragment();
            case 4:
                return new TechNewsFeedFragment();
            case 5:
                return new MusicNewsFeedFragment();
            default:
                return new GeneralNewsFeedFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}