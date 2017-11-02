package net.karthikraj.apps.newsagent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by karthik on 30/10/17.
 */

public class NewsAgentDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = NewsAgentDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "newsagent.db";
    private static final int DATABASE_VERSION = 13;

    public NewsAgentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createArticlesTable(sqLiteDatabase);
        createLikedArticlesTable(sqLiteDatabase);
    }

    private void createArticlesTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                ArticlesContract.ArticleEntry.TABLE_ARTICLES + "(" + ArticlesContract.ArticleEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArticlesContract.ArticleEntry.AUTHOR_NAME + " TEXT NULL, " +
                ArticlesContract.ArticleEntry.TITLE +
                " TEXT UNIQUE NULL, " +
                ArticlesContract.ArticleEntry.DESCRIPTION +
                " TEXT NULL, " +
                ArticlesContract.ArticleEntry.URL +
                " TEXT NULL, " +
                ArticlesContract.ArticleEntry.URL_TO_IMAGE +
                " TEXT NULL, " +
                ArticlesContract.ArticleEntry.SOURCE_NAME +
                " TEXT NULL, " +
                ArticlesContract.ArticleEntry.PUBLISHED_AT +
                " TEXT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    private void createLikedArticlesTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                LikedArticlesContract.ArticleEntry.TABLE_LIKED_ARTICLES + "(" + LikedArticlesContract.ArticleEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LikedArticlesContract.ArticleEntry.AUTHOR_NAME + " TEXT NULL, " +
                LikedArticlesContract.ArticleEntry.TITLE +
                " TEXT UNIQUE NULL, " +
                LikedArticlesContract.ArticleEntry.DESCRIPTION +
                " TEXT NULL, " +
                LikedArticlesContract.ArticleEntry.URL +
                " TEXT NULL, " +
                LikedArticlesContract.ArticleEntry.URL_TO_IMAGE +
                " TEXT NULL, " +
                LikedArticlesContract.ArticleEntry.SOURCE_NAME +
                " TEXT NULL, " +
                LikedArticlesContract.ArticleEntry.PUBLISHED_AT +
                " TEXT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.ArticleEntry.TABLE_ARTICLES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                ArticlesContract.ArticleEntry.TABLE_ARTICLES + "'");

        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LikedArticlesContract.ArticleEntry.TABLE_LIKED_ARTICLES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                LikedArticlesContract.ArticleEntry.TABLE_LIKED_ARTICLES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
