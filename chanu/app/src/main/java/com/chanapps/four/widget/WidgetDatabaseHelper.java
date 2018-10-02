/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chanapps.four.widget;

import com.android.gallery3d.common.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class WidgetDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "PhotoDatabaseHelper";
    private static final String DATABASE_NAME = "photoapp-widget.db";

    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_WIDGETS = "widgets";

    private static final String FIELD_APPWIDGET_ID = "appWidgetId";
    private static final String FIELD_IMAGE_URI = "imageUri";
    private static final String FIELD_PHOTO_BLOB = "photoBlob";
    private static final String FIELD_WIDGET_TYPE = "widgetType";
    private static final String FIELD_ALBUM_PATH = "albumPath";

    public static final int TYPE_SINGLE_PHOTO = 0;
    public static final int TYPE_SHUFFLE = 1;
    public static final int TYPE_ALBUM = 2;

    private static final String[] PROJECTION = {
            FIELD_WIDGET_TYPE, FIELD_IMAGE_URI, FIELD_PHOTO_BLOB, FIELD_ALBUM_PATH};
    private static final int INDEX_WIDGET_TYPE = 0;
    private static final int INDEX_IMAGE_URI = 1;
    private static final int INDEX_PHOTO_BLOB = 2;
    private static final int INDEX_ALBUM_PATH = 3;
    private static final String WHERE_CLAUSE = FIELD_APPWIDGET_ID + " = ?";

    public static class Entry {
        public int widgetId;
        public int type;
        public String imageUri;
        public byte imageData[];
        public String albumPath;

        private Entry() {}

        private Entry(int id, Cursor cursor) {
            widgetId = id;
            type = cursor.getInt(INDEX_WIDGET_TYPE);
            if (type == TYPE_SINGLE_PHOTO) {
                imageUri = cursor.getString(INDEX_IMAGE_URI);
                imageData = cursor.getBlob(INDEX_PHOTO_BLOB);
            } else if (type == TYPE_ALBUM) {
                albumPath = cursor.getString(INDEX_ALBUM_PATH);
            }
        }
    }

    public WidgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetDatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,db);try{db.execSQL("CREATE TABLE " + TABLE_WIDGETS + " ("
                + FIELD_APPWIDGET_ID + " INTEGER PRIMARY KEY, "
                + FIELD_WIDGET_TYPE + " INTEGER DEFAULT 0, "
                + FIELD_IMAGE_URI + " TEXT, "
                + FIELD_ALBUM_PATH + " TEXT, "
                + FIELD_PHOTO_BLOB + " BLOB)");com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetDatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,throwable);throw throwable;}
    }

    private void saveData(SQLiteDatabase db, int oldVersion, ArrayList<Entry> data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetDatabaseHelper.saveData(android.database.sqlite.SQLiteDatabase,int,java.util.ArrayList)",this,db,oldVersion,data);try{if (oldVersion <= 2) {
            Cursor cursor = db.query("photos",
                    new String[] {FIELD_APPWIDGET_ID, FIELD_PHOTO_BLOB},
                    null, null, null, null, null);
            if (cursor == null) {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.saveData(android.database.sqlite.SQLiteDatabase,int,java.util.ArrayList)",this);return;}}
            try {
                while (cursor.moveToNext()) {
                    Entry entry = new Entry();
                    entry.type = TYPE_SINGLE_PHOTO;
                    entry.widgetId = cursor.getInt(0);
                    entry.imageData = cursor.getBlob(1);
                    data.add(entry);
                }
            } finally {
                cursor.close();
            }
        } else if (oldVersion == 3) {
            Cursor cursor = db.query("photos",
                    new String[] {FIELD_APPWIDGET_ID, FIELD_PHOTO_BLOB, FIELD_IMAGE_URI},
                    null, null, null, null, null);
            if (cursor == null) {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.saveData(android.database.sqlite.SQLiteDatabase,int,java.util.ArrayList)",this);return;}}
            try {
                while (cursor.moveToNext()) {
                    Entry entry = new Entry();
                    entry.type = TYPE_SINGLE_PHOTO;
                    entry.widgetId = cursor.getInt(0);
                    entry.imageData = cursor.getBlob(1);
                    entry.imageUri = cursor.getString(2);
                    data.add(entry);
                }
            } finally {
                cursor.close();
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetDatabaseHelper.saveData(android.database.sqlite.SQLiteDatabase,int,java.util.ArrayList)",this,throwable);throw throwable;}
    }

    private void restoreData(SQLiteDatabase db, ArrayList<Entry> data) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetDatabaseHelper.restoreData(android.database.sqlite.SQLiteDatabase,java.util.ArrayList)",this,db,data);try{db.beginTransaction();
        try {
            for (Entry entry : data) {
                ContentValues values = new ContentValues();
                values.put(FIELD_APPWIDGET_ID, entry.widgetId);
                values.put(FIELD_WIDGET_TYPE, entry.type);
                values.put(FIELD_IMAGE_URI, entry.imageUri);
                values.put(FIELD_PHOTO_BLOB, entry.imageData);
                values.put(FIELD_ALBUM_PATH, entry.albumPath);
                db.insert(TABLE_WIDGETS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.restoreData(android.database.sqlite.SQLiteDatabase,java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetDatabaseHelper.restoreData(android.database.sqlite.SQLiteDatabase,java.util.ArrayList)",this,throwable);throw throwable;}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetDatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,db,oldVersion,newVersion);try{int version = oldVersion;

        if (version != DATABASE_VERSION) {
            ArrayList<Entry> data = new ArrayList<Entry>();
            saveData(db, oldVersion, data);

            Log.w(TAG, "destroying all old data.");
            /*// Table "photos" is renamed to "widget" in version 4*/
            db.execSQL("DROP TABLE IF EXISTS photos");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIDGETS);
            onCreate(db);

            restoreData(db, data);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetDatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,throwable);throw throwable;}
    }

    /**
     * Store the given bitmap in this database for the given appWidgetId.
     */
    public boolean setPhoto(int appWidgetId, Uri imageUri, Bitmap bitmap) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setPhoto(int,android.net.Uri,android.graphics.Bitmap)",this,appWidgetId,imageUri,bitmap);try{try {
            /*// Try go guesstimate how much space the icon will take when*/
            /*// serialized to avoid unnecessary allocations/copies during*/
            /*// the write.*/
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            ByteArrayOutputStream out = new ByteArrayOutputStream(size);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            ContentValues values = new ContentValues();
            values.put(FIELD_APPWIDGET_ID, appWidgetId);
            values.put(FIELD_WIDGET_TYPE, TYPE_SINGLE_PHOTO);
            values.put(FIELD_IMAGE_URI, imageUri.toString());
            values.put(FIELD_PHOTO_BLOB, out.toByteArray());

            SQLiteDatabase db = getWritableDatabase();
            db.replaceOrThrow(TABLE_WIDGETS, null, values);
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setPhoto(int,android.net.Uri,android.graphics.Bitmap)",this);return true;}
        } catch (Throwable e) {
            Log.e(TAG, "set widget photo fail", e);
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setPhoto(int,android.net.Uri,android.graphics.Bitmap)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setPhoto(int,android.net.Uri,android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    public boolean setWidget(int id, int type, String albumPath) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setWidget(int,int,java.lang.String)",this,id,type,albumPath);try{try {
            ContentValues values = new ContentValues();
            values.put(FIELD_APPWIDGET_ID, id);
            values.put(FIELD_WIDGET_TYPE, type);
            values.put(FIELD_ALBUM_PATH, Utils.ensureNotNull(albumPath));
            getWritableDatabase().replaceOrThrow(TABLE_WIDGETS, null, values);
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setWidget(int,int,java.lang.String)",this);return true;}
        } catch (Throwable e) {
            Log.e(TAG, "set widget fail", e);
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setWidget(int,int,java.lang.String)",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.widget.WidgetDatabaseHelper.setWidget(int,int,java.lang.String)",this,throwable);throw throwable;}
    }

    public Entry getEntry(int appWidgetId) {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.widget.WidgetDatabaseHelper$Entry com.chanapps.four.widget.WidgetDatabaseHelper.getEntry(int)",this,appWidgetId);try{Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(TABLE_WIDGETS, PROJECTION,
                    WHERE_CLAUSE, new String[] {String.valueOf(appWidgetId)},
                    null, null, null);
            if (cursor == null || !cursor.moveToNext()) {
                Log.e(TAG, "query fail: empty cursor: " + cursor, new Exception("location"));
                {com.mijack.Xlog.logMethodExit("com.chanapps.four.widget.WidgetDatabaseHelper$Entry com.chanapps.four.widget.WidgetDatabaseHelper.getEntry(int)",this);return null;}
            }
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.widget.WidgetDatabaseHelper$Entry com.chanapps.four.widget.WidgetDatabaseHelper.getEntry(int)",this);return new Entry(appWidgetId, cursor);}
        } catch (Throwable e) {
            Log.e(TAG, "Could not load photo from database", e);
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.widget.WidgetDatabaseHelper$Entry com.chanapps.four.widget.WidgetDatabaseHelper.getEntry(int)",this);return null;}
        } finally {
            Utils.closeSilently(cursor);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.widget.WidgetDatabaseHelper$Entry com.chanapps.four.widget.WidgetDatabaseHelper.getEntry(int)",this,throwable);throw throwable;}
    }

    /**
     * Remove any bitmap associated with the given appWidgetId.
     */
    public void deleteEntry(int appWidgetId) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetDatabaseHelper.deleteEntry(int)",this,appWidgetId);try{try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_WIDGETS, WHERE_CLAUSE,
                    new String[] {String.valueOf(appWidgetId)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not delete photo from database", e);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetDatabaseHelper.deleteEntry(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetDatabaseHelper.deleteEntry(int)",this,throwable);throw throwable;}
    }
}