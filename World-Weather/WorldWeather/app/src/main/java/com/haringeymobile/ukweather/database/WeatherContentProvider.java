package com.haringeymobile.ukweather.database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class WeatherContentProvider extends ContentProvider {

    private DatabaseHelper databaseHelper;

    /*// Used for the UriMatcher*/
    private static final int CITIES_ALL_ROWS = 1;
    private static final int CITIES_SINGLE_ROW = 2;
    private static final int CITIES_SEARCH = 3;

    public static final String AUTHORITY = "com.haringeymobile.ukweather.provider";

    private static final String PATH_CITY_RECORDS = CityTable.TABLE_CITIES;

    public static final Uri CONTENT_URI_CITY_RECORDS = Uri.parse("content://" + AUTHORITY + "/" +
            PATH_CITY_RECORDS);

    private static final String PROVIDER_SPECIFIC_SUBTYPE_FOR_CITY_RECORDS = "/vnd." + AUTHORITY +
            "." + CityTable.TABLE_CITIES;

    public static final String CONTENT_TYPE_CITY_RECORDS = ContentResolver.CURSOR_DIR_BASE_TYPE +
            PROVIDER_SPECIFIC_SUBTYPE_FOR_CITY_RECORDS;
    public static final String CONTENT_ITEM_TYPE_CITY_RECORDS =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + PROVIDER_SPECIFIC_SUBTYPE_FOR_CITY_RECORDS;

    private static final UriMatcher myURIMatcher;

    static {
        myURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        myURIMatcher.addURI(AUTHORITY, PATH_CITY_RECORDS, CITIES_ALL_ROWS);
        myURIMatcher.addURI(AUTHORITY, PATH_CITY_RECORDS + "/#", CITIES_SINGLE_ROW);
        myURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, CITIES_SEARCH);
        myURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", CITIES_SEARCH);
    }

    /**
     * Used to define aliases for column names, and must include all columns, even if the value is
     * the key. See http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#SuggestionTable
     * for more info.
     */
    private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;

    static {
        SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<>();
        SEARCH_SUGGEST_PROJECTION_MAP.put("_id", CityTable._ID + " AS " + "_id");
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1,
                CityTable.COLUMN_NAME + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                CityTable._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    @Override
    public boolean onCreate() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.database.WeatherContentProvider.onCreate()",this);try{databaseHelper = new DatabaseHelper(getContext());
        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.database.WeatherContentProvider.onCreate()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.database.WeatherContentProvider.onCreate()",this,throwable);throw throwable;}
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.haringeymobile.ukweather.database.WeatherContentProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,uri,projection,selection,selectionArgs,sortOrder);try{SQLiteDatabase db;
        try {
            db = databaseHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = databaseHelper.getReadableDatabase();
        }

        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (myURIMatcher.match(uri)) {
            case CITIES_SINGLE_ROW:
                queryBuilder.appendWhere(CityTable._ID + "=" + uri.getLastPathSegment());
                /*// fall through*/
            case CITIES_ALL_ROWS:
                queryBuilder.setTables(CityTable.TABLE_CITIES);
                break;
            case CITIES_SEARCH:
                queryBuilder.appendWhere(CityTable.COLUMN_NAME + " LIKE \"" +
                        uri.getLastPathSegment() + "%\"");
                queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
                queryBuilder.setTables(CityTable.TABLE_CITIES);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy,
                having, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.haringeymobile.ukweather.database.WeatherContentProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this);return cursor;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.haringeymobile.ukweather.database.WeatherContentProvider.query(android.net.Uri,[java.lang.String,java.lang.String,[java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public String getType(Uri uri) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.database.WeatherContentProvider.getType(android.net.Uri)",this,uri);try{switch (myURIMatcher.match(uri)) {
            case CITIES_SINGLE_ROW:
                {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.WeatherContentProvider.getType(android.net.Uri)",this);return CONTENT_ITEM_TYPE_CITY_RECORDS;}
            case CITIES_ALL_ROWS:
                {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.WeatherContentProvider.getType(android.net.Uri)",this);return CONTENT_TYPE_CITY_RECORDS;}
            case CITIES_SEARCH:
                {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.WeatherContentProvider.getType(android.net.Uri)",this);return SearchManager.SUGGEST_MIME_TYPE;}
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.database.WeatherContentProvider.getType(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.haringeymobile.ukweather.database.WeatherContentProvider.insert(android.net.Uri,android.content.ContentValues)",this,uri,values);try{SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String nullColumnHack = null;
        long id;
        switch (myURIMatcher.match(uri)) {
            case CITIES_ALL_ROWS:
                id = db.insert(CityTable.TABLE_CITIES, nullColumnHack, values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (id > -1) {
            Uri insertedIdUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(insertedIdUri, null);
            {com.mijack.Xlog.logMethodExit("android.net.Uri com.haringeymobile.ukweather.database.WeatherContentProvider.insert(android.net.Uri,android.content.ContentValues)",this);return insertedIdUri;}
        }

        throw new SQLException("Could not insert into table for uri: " + uri);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.haringeymobile.ukweather.database.WeatherContentProvider.insert(android.net.Uri,android.content.ContentValues)",this,throwable);throw throwable;}
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.database.WeatherContentProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this,uri,selection,selectionArgs);try{SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (myURIMatcher.match(uri)) {
            case CITIES_SINGLE_ROW:
                String id_rec = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(CityTable.TABLE_CITIES, CityTable._ID + "=" + id_rec,
                            null);
                } else {
                    rowsDeleted = db.delete(CityTable.TABLE_CITIES, CityTable._ID + "=" + id_rec +
                            " and " + selection, selectionArgs);
                }
                break;
            case CITIES_ALL_ROWS:
                rowsDeleted = db.delete(CityTable.TABLE_CITIES, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.database.WeatherContentProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this);return rowsDeleted;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.database.WeatherContentProvider.delete(android.net.Uri,java.lang.String,[java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.database.WeatherContentProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this,uri,values,selection,selectionArgs);try{SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsUpdated;
        switch (myURIMatcher.match(uri)) {
            case CITIES_SINGLE_ROW:
                String id_rec = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(CityTable.TABLE_CITIES, values, CityTable._ID + "=" +
                            id_rec, null);
                } else {
                    rowsUpdated = db.update(CityTable.TABLE_CITIES, values, CityTable._ID + "=" +
                            id_rec + " and " + selection, selectionArgs);
                }
                break;
            case CITIES_ALL_ROWS:
                rowsUpdated = db.update(CityTable.TABLE_CITIES, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.database.WeatherContentProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this);return rowsUpdated;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.database.WeatherContentProvider.update(android.net.Uri,android.content.ContentValues,java.lang.String,[java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * A test package can call this to get a handle to the database underlying
     * WeatherContentProvider, so it can insert test data into the database. The test case class
     * is responsible for instantiating the provider in a test context;
     * {@link android.test.ProviderTestCase2} does this during the call to setUp()
     *
     * @return a handle to the database helper object for the provider's data.
     */
    public DatabaseHelper getOpenHelperForTest() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.database.DatabaseHelper com.haringeymobile.ukweather.database.WeatherContentProvider.getOpenHelperForTest()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.database.DatabaseHelper com.haringeymobile.ukweather.database.WeatherContentProvider.getOpenHelperForTest()",this);return databaseHelper;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.database.DatabaseHelper com.haringeymobile.ukweather.database.WeatherContentProvider.getOpenHelperForTest()",this,throwable);throw throwable;}
    }

}