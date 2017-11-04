package net.karthikraj.apps.newsagent.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.data.ArticlesContract;
import net.karthikraj.apps.newsagent.feeds.MainActivity;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by karthik on 2/11/17.
 */

public class NewsAppWidgetService extends RemoteViewsService {

    /**
     * Lock to avoid race condition between widgets.
     */
    private static final Object sWidgetLock = new Object();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }


    class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        //private final ImageDownloader imageDownloader = new ImageDownloader();
        private static final int MAX_ARTICLES_COUNT = 25;
        private Context mContext;
        private int mAppWidgetId;
        private boolean mShouldShowViewMore;
        private Cursor mArticlesCursor;
        private List<Article> mArticleList;
        private final AppWidgetManager mAppWidgetManager;

        public StackRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mAppWidgetManager = AppWidgetManager.getInstance(context);
        }

        public void onCreate() {
        }

        public void onDestroy() {
            synchronized (sWidgetLock) {
                if (mArticlesCursor != null && !mArticlesCursor.isClosed()) {
                    mArticlesCursor.close();
                    mArticlesCursor = null;
                }
            }
        }

        public void onDataSetChanged() {
            synchronized (sWidgetLock) {
                if (mArticlesCursor != null) {
                    mArticlesCursor.close();
                    mArticlesCursor = null;
                }
                mArticleList = new ArrayList<>();
                mArticlesCursor = queryAllConversations();
                if (mArticlesCursor.getCount() > 0) {
                    mArticlesCursor.moveToFirst();
                    while (!mArticlesCursor.isAfterLast()) {
                        Article article = new Article();
                        article.setRowId(mArticlesCursor.getLong(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry._ID)));
                        article.setDescription(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.DESCRIPTION)));
                        article.setUrl(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.URL)));
                        article.setAuthor(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.AUTHOR_NAME)));
                        article.setPublishedAt(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.PUBLISHED_AT)));
                        article.setSourceName(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.SOURCE_NAME)));
                        article.setTitle(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.TITLE)));
                        article.setUrlToImage(mArticlesCursor.getString(mArticlesCursor.getColumnIndex(ArticlesContract.ArticleEntry.URL_TO_IMAGE)));
                        //Log.i(TAG, "Articles ready to lead into Adapter: ");
                        //Log.i(TAG, article.toString());
                        if (article.getDescription() != null &&
                                article.getUrl() != null &&
                                article.getAuthor() != null &&
                                article.getPublishedAt() != null &&
                                article.getSourceName() != null &&
                                article.getTitle() != null &&
                                article.getUrlToImage() != null) {
                            mArticleList.add(article);
                        }
                        mArticlesCursor.moveToNext();
                    }
                }

                onLoadComplete();
            }
        }

        private  Cursor queryAllConversations() {
            return mContext.getContentResolver().query(ArticlesContract.ArticleEntry.CONTENT_URI, null,
                    null, null, null);
        }

        private  void onLoadComplete() {
            Log.v(TAG, "onLoadComplete");
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_stackview);

            mAppWidgetManager.partiallyUpdateAppWidget(mAppWidgetId, remoteViews);
        }

        /**
         * Returns the number of items should be shown in the widget list.  This method also updates
         * the boolean that indicates whether the "show more" item should be shown.
         *
         * @return the number of items to be displayed in the list.
         */
        @Override
        public  int getCount() {
            Log.v(TAG, "getCount");
            synchronized (sWidgetLock) {
                if (mArticleList == null) {
                    return 0;
                }
                final int count = getConversationCount();
                mShouldShowViewMore = count < mArticleList.size();
                return count + (mShouldShowViewMore ? 1 : 0);
            }
        }

        /**
         * Returns the number of conversations that should be shown in the widget.  This method
         * doesn't update the boolean that indicates that the "show more" item should be included
         * in the list.
         *
         * @return
         */
        private  int getConversationCount() {
            Log.v(TAG, "getConversationCount");

            return Math.min(mArticleList.size(), MAX_ARTICLES_COUNT);
        }


        /*
         * Add color to a given text
         */
        private  SpannableStringBuilder addColor(CharSequence text, int color) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            if (color != 0) {
                builder.setSpan(new ForegroundColorSpan(color), 0, text.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return builder;
        }


        public RemoteViews getViewAt(int position) {
            synchronized (sWidgetLock) {
                RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stackview);

                try {
                    Article article = mArticleList.get(position);
                    if (position <= getCount()) {
                       if (article.getAuthor()!= null && !article.getAuthor().isEmpty()) {
                            rv.setTextViewText(R.id.stackWidgetItemUsername, article.getAuthor());
                        }
                        rv.setTextViewText(R.id.stackWidgetItemContent, Html.fromHtml(article.getDescription()));


                        Bundle extras = new Bundle();
                        extras.putLong(NewsAppWidget.EXTRA_WIDGET_SELECTION_ARTICLE_ID, article.getRowId());
                        Intent fillInIntent = new Intent();
                        fillInIntent.putExtras(extras);
                        rv.setOnClickFillInIntent(R.id.stackWidgetItem, fillInIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rv;
            }
        }

        public RemoteViews getLoadingView() {
            return null;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public long getItemId(int position) {
            return position;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}