package net.karthikraj.apps.newsagent.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by karthik on 30/10/17.
 */

public class ArticlesContract {
    public static final String CONTENT_AUTHORITY = "net.karthikraj.apps.newsagent.data.articlesprovider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class ArticleEntry implements BaseColumns {
        // table name
        public static final String TABLE_ARTICLES = "articles";
        // columns
        public static final String _ID = "_id";
        public static final String AUTHOR_NAME = "author_name";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String URL = "url";
        public static final String URL_TO_IMAGE = "url_to_image";
        public static final String PUBLISHED_AT = "published_at";
        public static final String SOURCE_NAME = "source_name";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_ARTICLES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_ARTICLES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_ARTICLES;

        // for building URIs on insertion
        public static Uri buildArticlesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}