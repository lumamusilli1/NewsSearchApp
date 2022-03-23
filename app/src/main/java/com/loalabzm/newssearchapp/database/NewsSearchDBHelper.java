package com.loalabzm.newssearchapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.loalabzm.newssearchapp.model.Article;

import java.util.ArrayList;
import java.util.List;

public class NewsSearchDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "news_search_db";

    public static final String TABLE_NAME_ARTICLES = "articles";
    public static final String TABLE_NAME_FAVORITES = "favorites";

    public static final String LIST = "article";

    public static final String ID = "articleId";
    public static final String TYPE = "type";
    public static final String SECTION_ID = "sectionId";
    public static final String SECTION_NAME = "sectionName";
    public static final String WEB_DATE = "webPublicationDate";
    public static final String WEB_URL = "webUrl";
    public static final String WEB_TITLE = "webTitle";
    public static final String API_URL = "apiUrl";
    public static final String IS_HOSTED = "isHosted";
    public static final String PILLAR_ID = "pillarId";
    public static final String PILLAR_NAME = "pillarName";


    public static final int DB_VERSION = 1;

    public NewsSearchDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryArticles = "CREATE TABLE articles(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "article TEXT)";
        String queryFavorites = "CREATE TABLE favorites(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "articleId TEXT, type TEXT, sectionId TEXT, sectionName TEXT, webPublicationDate TEXT," +
                "webTitle TEXT, webUrl TEXT, apiUrl TEXT, isHosted TEXT, pillarId TEXT, pillarName TEXT)";

        sqLiteDatabase.execSQL(queryArticles);
        sqLiteDatabase.execSQL(queryFavorites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ARTICLES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAVORITES);
        onCreate(sqLiteDatabase);
    }

    public void saveArticle(Article article) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LIST, new Gson().toJson(article));
        sqLiteDatabase.insert(TABLE_NAME_ARTICLES, null, cv);
    }

    @SuppressLint("Range")
    public List<Article> getLastSavedArticles() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT * FROM articles ORDER BY id";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext() && cursor.getCount() > 0) {
            String articlesJSON = cursor.getString(cursor.getColumnIndex(LIST));
            articles.add(new Gson().fromJson(articlesJSON, Article.class));
        }

        cursor.close();
        return articles;
    }

    public void clearSavedList() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME_ARTICLES, "", null);
    }


    public long makeFavorite(Article article) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, article.getId());
        cv.put(TYPE, article.getType());
        cv.put(SECTION_ID, article.getSectionId());
        cv.put(SECTION_NAME, article.getSectionName());
        cv.put(WEB_URL, article.getWebUrl());
        cv.put(WEB_TITLE, article.getWebTitle());
        cv.put(WEB_DATE, article.getWebPublicationDate());
        cv.put(API_URL, article.getApiUrl());
        cv.put(IS_HOSTED, article.getIsHosted());
        cv.put(PILLAR_ID, article.getPillarId());
        cv.put(PILLAR_NAME, article.getPillarName());

        return sqLiteDatabase.insert(TABLE_NAME_FAVORITES, null, cv);
    }

    @SuppressLint("Range")
    public List<Article> getAllFavorites() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT * FROM favorites ORDER BY id";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext() && cursor.getCount() > 0) {
            String id = cursor.getString(cursor.getColumnIndex(ID));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String sectionId = cursor.getString(cursor.getColumnIndex(SECTION_ID));
            String sectionName = cursor.getString(cursor.getColumnIndex(SECTION_NAME));
            String webTitle = cursor.getString(cursor.getColumnIndex(WEB_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(WEB_DATE));
            String webUrl = cursor.getString(cursor.getColumnIndex(WEB_URL));
            String apiUrl = cursor.getString(cursor.getColumnIndex(API_URL));
            String pillarId = cursor.getString(cursor.getColumnIndex(PILLAR_ID));
            String isHosted = cursor.getString(cursor.getColumnIndex(IS_HOSTED));
            String pillarName = cursor.getString(cursor.getColumnIndex(PILLAR_NAME));
            articles.add(new Article(id, type, sectionId, sectionName, date, webTitle, webUrl, apiUrl, isHosted, pillarId, pillarName));
        }

        cursor.close();
        return articles;
    }


    public int unFavorite(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME_FAVORITES, "articleId='" + id + "'", null);
    }

    public boolean isArticleAlreadyInFavorites(String id) {
        SQLiteDatabase database = getReadableDatabase();
        String Query = "SELECT * FROM favorites WHERE " + ID + " = '" + id + "'";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
