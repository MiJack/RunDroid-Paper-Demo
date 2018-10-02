package com.haringeymobile.ukweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 2;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,database);try{CityTable.onCreate(database);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,throwable);throw throwable;}
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,database,oldVersion,newVersion);try{CityTable.onUpgrade(database, oldVersion, newVersion);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,throwable);throw throwable;}
    }

}