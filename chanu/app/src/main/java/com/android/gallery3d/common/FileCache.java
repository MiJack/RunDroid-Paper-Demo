/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.gallery3d.common;

import com.android.gallery3d.common.Entry.Table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileCache {
    private static final int LRU_CAPACITY = 4;
    private static final int MAX_DELETE_COUNT = 16;

    private static final String TAG = "FileCache";
    private static final String TABLE_NAME = FileEntry.SCHEMA.getTableName();
    private static final String FILE_PREFIX = "download";
    private static final String FILE_POSTFIX = ".tmp";

    private static final String QUERY_WHERE =
            FileEntry.Columns.HASH_CODE + "=? AND " + FileEntry.Columns.CONTENT_URL + "=?";
    private static final String ID_WHERE = FileEntry.Columns.ID + "=?";
    private static final String[] PROJECTION_SIZE_SUM =
            {String.format("sum(%s)", FileEntry.Columns.SIZE)};
    private static final String FREESPACE_PROJECTION[] = {
            FileEntry.Columns.ID, FileEntry.Columns.FILENAME,
            FileEntry.Columns.CONTENT_URL, FileEntry.Columns.SIZE};
    private static final String FREESPACE_ORDER_BY =
            String.format("%s ASC", FileEntry.Columns.LAST_ACCESS);

    private final LruCache<String, CacheEntry> mEntryMap =
            new LruCache<String, CacheEntry>(LRU_CAPACITY);

    private File mRootDir;
    private long mCapacity;
    private boolean mInitialized = false;
    private long mTotalBytes;

    private DatabaseHelper mDbHelper;

    public static final class CacheEntry {
        private long id;
        public String contentUrl;
        public File cacheFile;

        private CacheEntry(long id, String contentUrl, File cacheFile) {
            this.id = id;
            this.contentUrl = contentUrl;
            this.cacheFile = cacheFile;
        }
    }

    public static void deleteFiles(Context context, File rootDir, String dbName) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.FileCache.deleteFiles(android.content.Context,java.io.File,java.lang.String)",context,rootDir,dbName);try{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.FileCache.deleteFiles(android.content.Context,java.io.File,java.lang.String)");try {
            context.getDatabasePath(dbName).delete();
            File[] files = rootDir.listFiles();
            if (files == null) {return;}
            for (File file : rootDir.listFiles()) {
                String name = file.getName();
                if (file.isFile() && name.startsWith(FILE_PREFIX)
                        && name.endsWith(FILE_POSTFIX)) {file.delete();}
            }
        } catch (Throwable t) {
            Log.w(TAG, "cannot reset database", t);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.FileCache.deleteFiles(android.content.Context,java.io.File,java.lang.String)",throwable);throw throwable;}
    }

    public FileCache(Context context, File rootDir, String dbName, long capacity) {
        mRootDir = Utils.checkNotNull(rootDir);
        mCapacity = capacity;
        mDbHelper = new DatabaseHelper(context, dbName);
    }

    public void store(String downloadUrl, File file) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache.store(java.lang.String,java.io.File)",this,downloadUrl,file);try{if (!mInitialized) {initialize();}

        Utils.assertTrue(file.getParentFile().equals(mRootDir));
        FileEntry entry = new FileEntry();
        entry.hashCode = Utils.crc64Long(downloadUrl);
        entry.contentUrl = downloadUrl;
        entry.filename = file.getName();
        entry.size = file.length();
        entry.lastAccess = System.currentTimeMillis();
        if (entry.size >= mCapacity) {
            file.delete();
            throw new IllegalArgumentException("file too large: " + entry.size);
        }
        synchronized (this) {
            FileEntry original = queryDatabase(downloadUrl);
            if (original != null) {
                file.delete();
                entry.filename = original.filename;
                entry.size = original.size;
            } else {
                mTotalBytes += entry.size;
            }
            FileEntry.SCHEMA.insertOrReplace(
                    mDbHelper.getWritableDatabase(), entry);
            if (mTotalBytes > mCapacity) {freeSomeSpaceIfNeed(MAX_DELETE_COUNT);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache.store(java.lang.String,java.io.File)",this,throwable);throw throwable;}
    }

    public CacheEntry lookup(String downloadUrl) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this,downloadUrl);try{if (!mInitialized) {initialize();}
        CacheEntry entry;
        synchronized (mEntryMap) {
            entry = mEntryMap.get(downloadUrl);
        }

        if (entry != null) {
            synchronized (this) {
                updateLastAccess(entry.id);
            }
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this);return entry;}
        }

        synchronized (this) {
            FileEntry file = queryDatabase(downloadUrl);
            if (file == null) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this);return null;}}
            entry = new CacheEntry(
                    file.id, downloadUrl, new File(mRootDir, file.filename));
            if (!entry.cacheFile.isFile()) { /*// file has been removed*/
                try {
                    mDbHelper.getWritableDatabase().delete(
                            TABLE_NAME, ID_WHERE, new String[] {String.valueOf(file.id)});
                    mTotalBytes -= file.size;
                } catch (Throwable t) {
                    Log.w(TAG, "cannot delete entry: " + file.filename, t);
                }
                {com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this);return null;}
            }
            synchronized (mEntryMap) {
                mEntryMap.put(downloadUrl, entry);
            }
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this);return entry;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.common.FileCache$CacheEntry com.android.gallery3d.common.FileCache.lookup(java.lang.String)",this,throwable);throw throwable;}
    }

    private FileEntry queryDatabase(String downloadUrl) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.common.FileCache$FileEntry com.android.gallery3d.common.FileCache.queryDatabase(java.lang.String)",this,downloadUrl);try{long hash = Utils.crc64Long(downloadUrl);
        String whereArgs[] = new String[] {String.valueOf(hash), downloadUrl};
        Cursor cursor = mDbHelper.getReadableDatabase().query(TABLE_NAME,
                FileEntry.SCHEMA.getProjection(),
                QUERY_WHERE, whereArgs, null, null, null);
        try {
            if (!cursor.moveToNext()) {{com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$FileEntry com.android.gallery3d.common.FileCache.queryDatabase(java.lang.String)",this);return null;}}
            FileEntry entry = new FileEntry();
            FileEntry.SCHEMA.cursorToObject(cursor, entry);
            updateLastAccess(entry.id);
            {com.mijack.Xlog.logMethodExit("com.android.gallery3d.common.FileCache$FileEntry com.android.gallery3d.common.FileCache.queryDatabase(java.lang.String)",this);return entry;}
        } finally {
            cursor.close();
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.common.FileCache$FileEntry com.android.gallery3d.common.FileCache.queryDatabase(java.lang.String)",this,throwable);throw throwable;}
    }

    private void updateLastAccess(long id) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache.updateLastAccess(long)",this,id);try{ContentValues values = new ContentValues();
        values.put(FileEntry.Columns.LAST_ACCESS, System.currentTimeMillis());
        mDbHelper.getWritableDatabase().update(TABLE_NAME,
                values,  ID_WHERE, new String[] {String.valueOf(id)});com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.FileCache.updateLastAccess(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache.updateLastAccess(long)",this,throwable);throw throwable;}
    }

    public File createFile() throws IOException {
        com.mijack.Xlog.logMethodEnter("java.io.File com.android.gallery3d.common.FileCache.createFile()",this);try{com.mijack.Xlog.logMethodExit("java.io.File com.android.gallery3d.common.FileCache.createFile()",this);return File.createTempFile(FILE_PREFIX, FILE_POSTFIX, mRootDir);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.io.File com.android.gallery3d.common.FileCache.createFile()",this,throwable);throw throwable;}
    }

    private synchronized void initialize() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache.initialize()",this);try{if (mInitialized) {{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.FileCache.initialize()",this);return;}}
        mInitialized = true;

        if (!mRootDir.isDirectory()) {
            mRootDir.mkdirs();
            if (!mRootDir.isDirectory()) {
                throw new RuntimeException("cannot create: " + mRootDir.getAbsolutePath());
            }
        }

        Cursor cursor = mDbHelper.getReadableDatabase().query(
                TABLE_NAME, PROJECTION_SIZE_SUM,
                null, null, null, null, null);
        try {
            if (cursor.moveToNext()) {mTotalBytes = cursor.getLong(0);}
        } finally {
            cursor.close();
        }
        if (mTotalBytes > mCapacity) {freeSomeSpaceIfNeed(MAX_DELETE_COUNT);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache.initialize()",this,throwable);throw throwable;}
    }

    private void freeSomeSpaceIfNeed(int maxDeleteFileCount) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache.freeSomeSpaceIfNeed(int)",this,maxDeleteFileCount);try{Cursor cursor = mDbHelper.getReadableDatabase().query(
                TABLE_NAME, FREESPACE_PROJECTION,
                null, null, null, null, FREESPACE_ORDER_BY);
        try {
            while (maxDeleteFileCount > 0
                    && mTotalBytes > mCapacity && cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String path = cursor.getString(1);
                String url = cursor.getString(2);
                long size = cursor.getLong(3);

                synchronized (mEntryMap) {
                    /*// if some one still uses it*/
                    if (mEntryMap.containsKey(url)) {continue;}
                }

                --maxDeleteFileCount;
                if (new File(mRootDir, path).delete()) {
                    mTotalBytes -= size;
                    mDbHelper.getWritableDatabase().delete(TABLE_NAME,
                            ID_WHERE, new String[]{String.valueOf(id)});
                } else {
                    Log.w(TAG, "unable to delete file: " + path);
                }
            }
        } finally {
            cursor.close();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.FileCache.freeSomeSpaceIfNeed(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache.freeSomeSpaceIfNeed(int)",this,throwable);throw throwable;}
    }

    @Table("files")
    private static class FileEntry extends Entry {
        public static final EntrySchema SCHEMA = new EntrySchema(FileEntry.class);

        public interface Columns extends Entry.Columns {
            public static final String HASH_CODE = "hash_code";
            public static final String CONTENT_URL = "content_url";
            public static final String FILENAME = "filename";
            public static final String SIZE = "size";
            public static final String LAST_ACCESS = "last_access";
        }

        @Column(value = Columns.HASH_CODE, indexed = true)
        public long hashCode;

        @Column(Columns.CONTENT_URL)
        public String contentUrl;

        @Column(Columns.FILENAME)
        public String filename;

        @Column(Columns.SIZE)
        public long size;

        @Column(value = Columns.LAST_ACCESS, indexed = true)
        public long lastAccess;

        @Override
        public String toString() {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.common.FileCache$FileEntry.toString()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.common.FileCache$FileEntry.toString()",this);return new StringBuilder()
                    .append("hash_code: ").append(hashCode).append(", ")
                    .append("content_url").append(contentUrl).append(", ")
                    .append("last_access").append(lastAccess).append(", ")
                    .append("filename").append(filename).toString();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.common.FileCache$FileEntry.toString()",this,throwable);throw throwable;}
        }
    }

    private final class DatabaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context, String dbName) {
            super(context, dbName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache$DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,db);try{FileEntry.SCHEMA.createTables(db);

            /*// delete old files*/
            for (File file : mRootDir.listFiles()) {
                if (!file.delete()) {
                    Log.w(TAG, "fail to remove: " + file.getAbsolutePath());
                }
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.FileCache$DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache$DatabaseHelper.onCreate(android.database.sqlite.SQLiteDatabase)",this,throwable);throw throwable;}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.common.FileCache$DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,db,oldVersion,newVersion);try{/*//reset everything*/
            FileEntry.SCHEMA.dropTables(db);
            onCreate(db);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.common.FileCache$DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.common.FileCache$DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase,int,int)",this,throwable);throw throwable;}
        }
    }
}
