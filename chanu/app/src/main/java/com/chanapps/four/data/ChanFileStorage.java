package com.chanapps.four.data;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.chanapps.four.activity.*;
import com.chanapps.four.component.NotificationComponent;
import com.chanapps.four.service.BoardParserService;
import com.chanapps.four.widget.WidgetProviderUtils;
import com.nostra13.universalimageloader.utils.L;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.chanapps.four.service.FileSaverService;
import com.chanapps.four.service.FileSaverService.FileType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ChanFileStorage {
    private static final String TAG = ChanFileStorage.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final int MAX_BOARDS_IN_CACHE = 100;
    private static final int MAX_THREADS_IN_CACHE = 200;

    @SuppressWarnings("serial")
    private static Map<String, ChanBoard> boardCache = new LinkedHashMap<String, ChanBoard>(MAX_BOARDS_IN_CACHE + 1, .75F, true) {
        /*// This method is called just after a new entry has been added*/
        public boolean removeEldestEntry(Map.Entry<String, ChanBoard> eldest) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanFileStorage$1.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this,eldest);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanFileStorage$1.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this);return size() > MAX_BOARDS_IN_CACHE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage$1.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this,throwable);throw throwable;}
        }
    };

    @SuppressWarnings("serial")
    private static Map<String, ChanThread> threadCache = new LinkedHashMap<String, ChanThread>(MAX_THREADS_IN_CACHE + 1, .75F, true) {
        /*// This method is called just after a new entry has been added*/
        public boolean removeEldestEntry(Map.Entry<String, ChanThread> eldest) {
            com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanFileStorage$2.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this,eldest);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanFileStorage$2.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this);return size() > MAX_THREADS_IN_CACHE;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage$2.removeEldestEntry(com.chanapps.four.activity.Map.Entry)",this,throwable);throw throwable;}
        }
    };

    private static final String ANDROID_ROOT = "Android";
    private static final String ANDROID_DATA_DIR = "data";
    private static final String CACHE_PKG_DIR = "cache";
    private static final String WALLPAPER_DIR = "wallpapers";
    private static final String FILE_SEP = "/";
    private static final String CACHE_EXT = ".txt";
    private static final String WALLPAPER_EXT = ".jpg";
    private static final String USER_STATS_FILENAME = "userstats.txt";

    public static boolean isBoardCachedOnDisk(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.isBoardCachedOnDisk(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{File boardDir = getBoardCacheDirectory(context, boardCode);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isBoardCachedOnDisk(android.content.Context,com.chanapps.four.activity.String)");return boardDir != null && boardDir.exists();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.isBoardCachedOnDisk(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static String getRootCacheDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootCacheDirectory(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootCacheDirectory(android.content.Context)");return ANDROID_ROOT + FILE_SEP
                + ANDROID_DATA_DIR + FILE_SEP
                + context.getPackageName() + FILE_SEP
                + CACHE_PKG_DIR;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootCacheDirectory(android.content.Context)",throwable);throw throwable;}
    }

    private static String getRootPersistentDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootPersistentDirectory(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootPersistentDirectory(android.content.Context)");return ANDROID_ROOT + FILE_SEP
                + ANDROID_DATA_DIR + FILE_SEP
                + context.getPackageName();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getRootPersistentDirectory(android.content.Context)",throwable);throw throwable;}
    }

    public static File getCacheDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getCacheDirectory(android.content.Context)",context);try{String cacheDir = getRootCacheDirectory(context);
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getCacheDirectory(android.content.Context)");return StorageUtils.getOwnCacheDirectory(context, cacheDir);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getCacheDirectory(android.content.Context)",throwable);throw throwable;}
    }

    private static File getPersistentDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getPersistentDirectory(android.content.Context)",context);try{String persistentDir = getRootPersistentDirectory(context);
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getPersistentDirectory(android.content.Context)");return getOwnPersistentDirectory(context, persistentDir);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getPersistentDirectory(android.content.Context)",throwable);throw throwable;}
    }

    private static File getOwnPersistentDirectory(Context context, String persistentDir) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getOwnPersistentDirectory(android.content.Context,com.chanapps.four.activity.String)",context,persistentDir);try{File appPersistentDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appPersistentDir = new File(Environment.getExternalStorageDirectory(), persistentDir);
        }
        if (appPersistentDir == null || (!appPersistentDir.exists() && !appPersistentDir.mkdirs())) {
            appPersistentDir = context.getFilesDir();
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getOwnPersistentDirectory(android.content.Context,com.chanapps.four.activity.String)");return appPersistentDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getOwnPersistentDirectory(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static File getWallpaperCacheDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getWallpaperCacheDirectory(android.content.Context)",context);try{String cacheDir = getRootCacheDirectory(context) + FILE_SEP + WALLPAPER_DIR;
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getWallpaperCacheDirectory(android.content.Context)");return StorageUtils.getOwnCacheDirectory(context, cacheDir);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getWallpaperCacheDirectory(android.content.Context)",throwable);throw throwable;}
    }

    private static String getLegacyBoardCachePath(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLegacyBoardCachePath(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLegacyBoardCachePath(android.content.Context,com.chanapps.four.activity.String)");return getRootCacheDirectory(context) + FILE_SEP + boardCode;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLegacyBoardCachePath(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static File getLegacyBoardCacheFile(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyBoardCacheFile(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{String cacheDir = getLegacyBoardCachePath(context, boardCode);
        File boardDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            boardDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (boardDir == null || !boardDir.exists()) {
            boardDir = context.getCacheDir();
        }
        File boardFile = boardDir != null && boardDir.exists() ? new File(boardDir, boardCode + CACHE_EXT) : null;
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyBoardCacheFile(android.content.Context,com.chanapps.four.activity.String)");return boardFile;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyBoardCacheFile(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static File getBoardCacheDirectory(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{File boardDir;
        if (ChanBoard.isPersistentBoard(boardCode)) {
            String persistentDir = getRootPersistentDirectory(context) + FILE_SEP + boardCode;
            boardDir = getOwnPersistentDirectory(context, persistentDir);
        }
        else {
            String cacheDir = getRootCacheDirectory(context) + FILE_SEP + boardCode;
            boardDir = StorageUtils.getOwnCacheDirectory(context, cacheDir);
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)");return boardDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static File getHiddenBoardCacheDirectory(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getHiddenBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{final File boardDir = getBoardCacheDirectory(context, boardCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanFileStorage$3.run()",this);try{if (!boardDir.exists()) {
                    if (DEBUG) {Log.i(TAG, "created board cache directory " + boardDir);}
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getHiddenBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)");{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanFileStorage$3.run()",this);return;}}
                }
                try {
                    File f = new File(boardDir, ".nomedia");
                    if (!f.exists()) {
                        if (!f.createNewFile()) {
                            Log.e(TAG, "couldn't create .nomedia in board cache directory " + boardDir);
                            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getHiddenBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)");{com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanFileStorage$3.run()",this);return;}}
                        }
                        if (DEBUG) {Log.i(TAG, "created .nomedia in board cache directory " + boardDir);}
                    }
                    else {
                        if (DEBUG) {Log.i(TAG, "file .nomedia already exists in board cache directory " + boardDir);}
                    }
                } catch (IOException e) {
                    L.i("Can't create \".nomedia\" file in board cache dir " + boardDir);
                }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage$3.run()",this,throwable);throw throwable;}
            }
        }).start();
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getHiddenBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)");return boardDir;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getHiddenBoardCacheDirectory(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static File getLegacyUserStatsFile(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyUserStatsFile(android.content.Context)",context);try{String cacheDir = getRootCacheDirectory(context);
        File cacheFolder = StorageUtils.getOwnCacheDirectory(context, cacheDir);
        if (cacheFolder != null) {
            File userPrefsFile = new File(cacheFolder, USER_STATS_FILENAME);
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyUserStatsFile(android.content.Context)");return userPrefsFile;}
        } else {
            Log.e(TAG, "Cache folder returned empty");
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyUserStatsFile(android.content.Context)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getLegacyUserStatsFile(android.content.Context)",throwable);throw throwable;}
    }

    private static File getUserStatsFile(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getUserStatsFile(android.content.Context)",context);try{File cacheFolder = getPersistentDirectory(context);
        if (cacheFolder != null) {
            File userPrefsFile = new File(cacheFolder, USER_STATS_FILENAME);
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getUserStatsFile(android.content.Context)");return userPrefsFile;}
        } else {
            Log.e(TAG, "Cache folder returned empty");
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getUserStatsFile(android.content.Context)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getUserStatsFile(android.content.Context)",throwable);throw throwable;}
    }

    public static void storeBoardData(Context context, ChanBoard board) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard)",context,board);try{storeBoardData(context, board, -1);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard)",throwable);throw throwable;}
    }

    public static void storeBoardData(Context context, ChanBoard board, long threadNo) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard,long)",context,board,threadNo);try{if (board.defData) {
            Log.i(TAG, "Default data found, not storing board=" + board.link);
            /*// default data should never be stored*/
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard,long)");return;}
        }
        File boardDir = getBoardCacheDirectory(context, board.link);
        if (boardDir != null && (boardDir.exists() || boardDir.mkdirs())) {
            ObjectMapper mapper = BoardParserService.getJsonMapper();
            mapper.writeValue(new File(boardDir, board.link + CACHE_EXT), board);
            if (DEBUG) {Log.i(TAG, "Stored " + board.threads.length + " threads for board '" + board.link + "'");}
            if (!board.isVirtualBoard()) {
                updateWatchedThread(context, board);
            }
            if (DEBUG) {Log.i(TAG, "updating board /" + board.link + "/" + (threadNo > -1 ? threadNo : ""));}
        } else {
            Log.e(TAG, "Cannot create board cache folder. " + (boardDir == null ? "null" : boardDir.getAbsolutePath()));
        }
        if (!board.isVirtualBoard())
            {addMissingWatchedThreads(context, board);}
        boardCache.put(board.link, board);
        if (DEBUG) {Log.i(TAG, "put cached board=" + board.link + " threadCount=" + board.threads.length);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.storeBoardData(android.content.Context,com.chanapps.four.activity.ChanBoard,long)",throwable);throw throwable;}
    }

    public static File getBoardFile(Context context, String boardName, int page) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardFile(android.content.Context,com.chanapps.four.activity.String,int)",context,boardName,page);try{File boardDir = getBoardCacheDirectory(context, boardName);
        if (boardDir != null && (boardDir.exists() || boardDir.mkdirs())) {
            if (page >= 0) {
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardFile(android.content.Context,com.chanapps.four.activity.String,int)");return new File(boardDir, boardName + "_page" + page + CACHE_EXT);}
            } else {
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardFile(android.content.Context,com.chanapps.four.activity.String,int)");return new File(boardDir, boardName + "_catalog" + CACHE_EXT);}
            }
        } else {
            if (DEBUG) {Log.w(TAG, "Board folder could not be created: " + boardName);}
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardFile(android.content.Context,com.chanapps.four.activity.String,int)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getBoardFile(android.content.Context,com.chanapps.four.activity.String,int)",throwable);throw throwable;}
    }

    public static long storeBoardFile(Context context, String boardName, int page, BufferedInputStream stream) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("long com.chanapps.four.data.ChanFileStorage.storeBoardFile(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.BufferedInputStream)",context,boardName,page,stream);try{File boardFile = getBoardFile(context, boardName, page);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(boardFile, false));
            IOUtils.copy(stream, writer);
        } finally {
            IOUtils.closeQuietly(stream);
            writer.flush();
            IOUtils.closeQuietly(writer);
        }
        if (DEBUG) {Log.i(TAG, "Stored file for board " + boardName + (page == -1 ? " catalog" : " page " + page));}
        {com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.data.ChanFileStorage.storeBoardFile(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.BufferedInputStream)");return boardFile.length();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.chanapps.four.data.ChanFileStorage.storeBoardFile(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.BufferedInputStream)",throwable);throw throwable;}
    }

    public static File getThreadFile(Context context, String boardName, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getThreadFile(android.content.Context,com.chanapps.four.activity.String,long)",context,boardName,threadNo);try{File boardDir = getBoardCacheDirectory(context, boardName);
        if (boardDir != null && (boardDir.exists() || boardDir.mkdirs())) {
            File boardFile = new File(boardDir, "t_" + threadNo + "f" + CACHE_EXT);
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getThreadFile(android.content.Context,com.chanapps.four.activity.String,long)");return boardFile;}
        } else {
            if (DEBUG) {Log.w(TAG, "Board folder could not be created: " + boardName);}
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getThreadFile(android.content.Context,com.chanapps.four.activity.String,long)");return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getThreadFile(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    public static long storeThreadFile(Context context, String boardName, long threadNo, BufferedInputStream stream) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("long com.chanapps.four.data.ChanFileStorage.storeThreadFile(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.BufferedInputStream)",context,boardName,threadNo,stream);try{File threadFile = getThreadFile(context, boardName, threadNo);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(threadFile, false));
            IOUtils.copy(stream, writer);
        } finally {
            IOUtils.closeQuietly(stream);
            writer.flush();
            IOUtils.closeQuietly(writer);
        }
        if (DEBUG) {Log.i(TAG, "Stored file for thread " + boardName + "/" + threadNo);}
        {com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.data.ChanFileStorage.storeThreadFile(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.BufferedInputStream)");return threadFile.length();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.chanapps.four.data.ChanFileStorage.storeThreadFile(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.BufferedInputStream)",throwable);throw throwable;}
    }

    public static void resetLastFetched(String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String,long)",boardCode,threadNo);try{ChanThread currentThread = threadCache.get(boardCode + "/" + threadNo);
        if (currentThread != null) {
            currentThread.lastFetched = 0;
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String,long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    public static void resetLastFetched(String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String)",boardCode);try{ChanBoard currentBoard = boardCache.get(boardCode);
        if (currentBoard != null) {
            currentBoard.lastFetched = 0;
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.resetLastFetched(com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static void storeThreadData(Context context, ChanThread thread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.storeThreadData(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{if (thread.defData) {
            /*// default data should never be stored*/
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.storeThreadData(android.content.Context,com.chanapps.four.activity.ChanThread)");return;}
        }
        ChanThread currentThread = threadCache.get(thread.board + "/" + thread.no);
        if (currentThread != null && currentThread.lastFetched > thread.lastFetched) {
            if (DEBUG)
                {Log.i(TAG, "skipping thread cached time=" + currentThread.lastFetched + " newer than storing time=" + thread.lastFetched);}
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.storeThreadData(android.content.Context,com.chanapps.four.activity.ChanThread)");return;}
        }
        threadCache.put(thread.board + "/" + thread.no, thread);
        File boardDir = getBoardCacheDirectory(context, thread.board);
        if (boardDir != null && (boardDir.exists() || boardDir.mkdirs())) {
            File threadFile = new File(boardDir, "t_" + thread.no + CACHE_EXT);
            try {
                ObjectMapper mapper = BoardParserService.getJsonMapper();
                mapper.writeValue(threadFile, thread);
            } finally {
            }
            updateBoardThread(context, thread);
            updateWatchedThread(context, thread);
            if (DEBUG)
                {Log.i(TAG, "Stored " + thread.posts.length + " posts for thread '" + thread.board + FILE_SEP + thread.no + "'");}
        } else {
            Log.e(TAG, "Cannot create board cache folder. " + (boardDir == null ? "null" : boardDir.getAbsolutePath()));
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.storeThreadData(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static ChanBoard loadBoardData(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.loadBoardData(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{if (boardCode == null) {
            Log.e(TAG, "Trying to load 'null' board! Check stack trace why has it happened.", new Exception());
            throw new RuntimeException("Null board code was passed!");
        }
        if (boardCache.containsKey(boardCode)) {
            ChanBoard cachedBoard = boardCache.get(boardCode);
            if (cachedBoard != null && cachedBoard.threads != null
                    && cachedBoard.threads.length > 0 && !cachedBoard.defData) {
                if (DEBUG) {Log.i(TAG, "Returning board " + boardCode
                        + " data from cache threads=" + cachedBoard.threads.length
                        + " loadedthreads=" + cachedBoard.loadedThreads.length
                        + " newThreads=" + cachedBoard.newThreads
                        + " updatedThreads=" + cachedBoard.updatedThreads
                );}
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.loadBoardData(android.content.Context,com.chanapps.four.activity.String)");return cachedBoard;}
            } else {
                if (DEBUG) {Log.i(TAG, "Ignoring missing data cached board " + boardCode
                        + " data from cache threads=" + cachedBoard.threads.length
                        + " loadedthreads=" + cachedBoard.loadedThreads.length
                        + " newThreads=" + cachedBoard.newThreads
                        + " updatedThreads=" + cachedBoard.updatedThreads
                );}
            }
        }
        File boardFile = null;
        try {
            File boardDir = getBoardCacheDirectory(context, boardCode);
            if (boardDir != null && (boardDir.exists() || boardDir.mkdirs())) {
                boardFile = new File(boardDir, boardCode + CACHE_EXT);
                if (boardFile != null && boardFile.exists()) {
                    ObjectMapper mapper = BoardParserService.getJsonMapper();
                    ChanBoard board = mapper.readValue(boardFile, ChanBoard.class);
                    if (DEBUG) {Log.i(TAG, "Loaded " + board.threads.length + " threads for board '" + board.link
                            + "' isFile=" + boardFile.isFile() + " size=" + boardFile.length() / 1000 + "KB");}
                    /*
                    if (board.hasNewBoardData()) {
                        board.swapLoadedThreads();
                        boardCache.put(boardCode, board);
                        FileSaverService.startService(context, FileType.BOARD_SAVE, boardCode);
                    }
                    else {
                    */
                    if (!board.isVirtualBoard())
                        {addMissingWatchedThreads(context, board);}
                    boardCache.put(boardCode, board);
                    /*//}*/
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.loadBoardData(android.content.Context,com.chanapps.four.activity.String)");return board;}
                } else {
                    if (DEBUG) {Log.i(TAG, "File for board '" + boardCode + "' doesn't exist");}
                }
            } else {
                Log.e(TAG, "Cannot create board cache folder. " + (boardDir == null ? "null" : boardDir.getAbsolutePath()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while loading board '" + boardCode + "' data. ", e);
            if (boardFile != null) {
                boardFile.delete();
            }
        }
        ChanBoard board = prepareDefaultBoardData(context, boardCode);
        if (board != null && !board.isVirtualBoard())
            {addMissingWatchedThreads(context, board);}
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.loadBoardData(android.content.Context,com.chanapps.four.activity.String)");return board;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.loadBoardData(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static void addMissingWatchedThreads(Context context, ChanBoard board) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.addMissingWatchedThreads(android.content.Context,com.chanapps.four.activity.ChanBoard)",context,board);try{if (DEBUG) {Log.i(TAG, "addMissingWatchedThreads /" + board.link + "/ start #threads = " + board.threads.length);}
        ChanBoard watchlist = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        if (watchlist == null || watchlist.defData
                || watchlist.threads == null || watchlist.threads.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.addMissingWatchedThreads(android.content.Context,com.chanapps.four.activity.ChanBoard)");return;}}
        Set<Long> watchedNos = new HashSet<Long>();
        Map<Long, ChanThread> watchedThreads = new HashMap<Long, ChanThread>();
        Map<Long, ChanThread> watchedLoadedThreads = new HashMap<Long, ChanThread>();
        for (ChanThread thread : watchlist.threads) {
            if (thread.board.equals(board.link)) {
                watchedNos.add(thread.no);
                watchedThreads.put(thread.no, thread);
                watchedLoadedThreads.put(thread.no, thread);
            }
        }
        if (DEBUG) {Log.i(TAG, "addMissingWatchedThreads size=" + watchedNos.size());}
        if (watchedNos.size() == 0)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.addMissingWatchedThreads(android.content.Context,com.chanapps.four.activity.ChanBoard)");return;}}
        synchronized (board) {
            for (ChanThread thread : board.threads) {
                if (watchedNos.contains(thread.no))
                    {watchedThreads.remove(thread.no);}
            }
            for (ChanThread thread : board.loadedThreads) {
                if (watchedNos.contains(thread.no))
                    {watchedLoadedThreads.remove(thread.no);}
            }
            if (DEBUG) {Log.i(TAG, "addMissingWatchedThreads missing size=" + watchedThreads.size());}
            /*// watchedThreads is now the list of missing board threads*/
            if (watchedThreads.size() > 0) { /*// add to end of board.threads*/
                List<ChanThread> threads = new ArrayList<ChanThread>(Arrays.asList(board.threads));
                threads.addAll(watchedThreads.values());
                board.threads = threads.toArray(board.threads);
            }
            if (DEBUG) {Log.i(TAG, "addMissingWatchedThreads missing loaded size=" + watchedLoadedThreads.size());}
            if (board.loadedThreads.length > 0 && watchedLoadedThreads.size() > 0) { /*// add to end of board.loadedThreads*/
                List<ChanThread> threads = new ArrayList<ChanThread>(Arrays.asList(board.loadedThreads));
                threads.addAll(watchedLoadedThreads.values());
                board.loadedThreads = threads.toArray(board.loadedThreads);
            }
        }
        if (DEBUG) {Log.i(TAG, "addMissingWatchedThreads /" + board.link + "/ end #threads = " + board.threads.length);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.addMissingWatchedThreads(android.content.Context,com.chanapps.four.activity.ChanBoard)",throwable);throw throwable;}
    }

    public static boolean hasNewBoardData(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.hasNewBoardData(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{ChanBoard board = loadBoardData(context, boardCode);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.hasNewBoardData(android.content.Context,com.chanapps.four.activity.String)");return board == null ? false : board.hasNewBoardData();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.hasNewBoardData(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static ChanBoard prepareDefaultBoardData(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.prepareDefaultBoardData(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{ChanBoard board = ChanBoard.getBoardByCode(context, boardCode);
        if (board == null) {
            ChanBoard.initBoards(context);
            board = ChanBoard.getBoardByCode(context, boardCode);
        }
        if (board == null)
            {{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.prepareDefaultBoardData(android.content.Context,com.chanapps.four.activity.String)");return null;}}
        board = board.copy();
        ChanThread thread = new ChanThread();
        thread.board = boardCode;
        thread.closed = 0;
        thread.created = new Date();
        thread.images = 1;
        thread.no = -100;
        thread.tim = thread.created.getTime() * 1000;
        thread.tn_w = 240;
        thread.tn_h = 240;
        thread.defData = true;

        board.defData = true;
        board.threads = new ChanThread[]{thread};
        board.lastFetched = 0;

        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.prepareDefaultBoardData(android.content.Context,com.chanapps.four.activity.String)");return board;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanBoard com.chanapps.four.data.ChanFileStorage.prepareDefaultBoardData(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static ChanThread getCachedThreadData(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getCachedThreadData(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{/*// WARNING: loads only cached copy of the data*/
        /*// data may be stale or thread may be null, handle this situation*/
        /*// only call if you are in a non-backgroundable UI mode and must avoid file access*/
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getCachedThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return threadCache.get(boardCode + "/" + threadNo);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getCachedThreadData(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    public static ChanThread loadThreadData(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{if (boardCode == null || threadNo <= 0) {
            if (DEBUG)
                {Log.w(TAG, "Trying to load '" + boardCode + FILE_SEP + threadNo + "' thread! Check stack trace why has it happened.", new Exception());}
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return null;}
        }
        if (threadCache.containsKey(boardCode + "/" + threadNo)) {
            ChanThread thread = threadCache.get(boardCode + "/" + threadNo);
            if (thread == null || thread.defData) {
                if (DEBUG) {Log.w(TAG, "Null thread " + boardCode + "/" + threadNo + " stored in cache, removing key");}
                threadCache.remove(boardCode + "/" + threadNo);
            } else {
                if (DEBUG)
                    {Log.i(TAG, "Returning thread " + boardCode + FILE_SEP + threadNo + " data from cache, posts: " + thread.posts.length);}
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}
            }
        }
        File threadFile = null;
        try {
            threadFile = new File(getBoardCacheDirectory(context, boardCode), "t_" + threadNo + CACHE_EXT);
            if (!threadFile.exists()) {
                if (DEBUG) {Log.d(TAG, "Thread '" + boardCode + FILE_SEP + threadNo + "' doesn't exist.");}
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return getThreadFromBoard(context, boardCode, threadNo);}
            }
            ObjectMapper mapper = BoardParserService.getJsonMapper();
            ChanThread thread = mapper.readValue(threadFile, ChanThread.class);
            thread.loadedFromBoard = false;
            threadCache.put(thread.board + "/" + thread.no, thread);
            if (DEBUG)
                {Log.i(TAG, "Loaded thread '" + boardCode + FILE_SEP + threadNo + "' with " + thread.posts.length + " posts detail=" + thread);}
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}
        } catch (Exception e) {
            if (DEBUG) {Log.w(TAG, "Error while loading thread '" + boardCode + FILE_SEP + threadNo + "' data. ", e);}
            {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return getThreadFromBoard(context, boardCode, threadNo);}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.loadThreadData(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    private static ChanThread getThreadFromBoard(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{ChanThread thread = makeFirstThreadFromBoard(context, boardCode, threadNo);
        if (thread == null) {
            thread = makeFirstThreadFromBoard(context, ChanBoard.POPULAR_BOARD_CODE, threadNo);
        }
        if (thread == null) {
            thread = makeFirstThreadFromSpecialBoard(context, ChanBoard.LATEST_BOARD_CODE, threadNo);
        }
        if (thread == null) {
            thread = makeFirstThreadFromSpecialBoard(context, ChanBoard.LATEST_IMAGES_BOARD_CODE, threadNo);
        }
        if (thread == null) {
            thread = prepareDefaultThreadData(context, boardCode, threadNo);
        }
        thread.loadedFromBoard = true;
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.getThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    private static ChanThread makeFirstThreadFromBoard(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{ChanBoard board = loadBoardData(context, boardCode);
        if (board != null && !board.defData && board.threads != null) {
            for (ChanPost post : board.threads) {
                if (post.no == threadNo) {
                    ChanThread thread = new ChanThread();
                    thread.board = boardCode;
                    thread.no = threadNo;
                    thread.lastFetched = 0;
                    thread.posts = new ChanPost[]{post};
                    thread.closed = post.closed;
                    thread.loadedFromBoard = true;
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}
                }
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromBoard(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    private static ChanThread makeFirstThreadFromSpecialBoard(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromSpecialBoard(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{ChanBoard board = loadBoardData(context, boardCode);
        if (board != null && !board.defData && board.threads != null) {
            for (ChanPost post : board.threads) {
                if (post.no == threadNo) {
                    ChanThread thread = new ChanThread();
                    thread.board = boardCode;
                    thread.tim = post.tim;
                    thread.ext = post.ext;
                    thread.no = threadNo;
                    thread.lastFetched = 0;
                    thread.posts = new ChanPost[]{post};
                    thread.closed = post.closed;
                    thread.loadedFromBoard = true;
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromSpecialBoard(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}
                }
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromSpecialBoard(android.content.Context,com.chanapps.four.activity.String,long)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.makeFirstThreadFromSpecialBoard(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    private static ChanThread prepareDefaultThreadData(Context context, String boardCode, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.prepareDefaultThreadData(android.content.Context,com.chanapps.four.activity.String,long)",context,boardCode,threadNo);try{ChanThread thread = new ChanThread();
        thread.board = boardCode;
        thread.closed = 0;
        thread.created = new Date();
        thread.images = 0;
        thread.replies = 0;
        thread.no = threadNo;
        thread.tim = thread.created.getTime() * 1000;
        thread.tn_w = 0;
        thread.tn_h = 0;

        ChanPost post = new ChanPost();
        post.no = threadNo;
        post.board = boardCode;
        post.closed = 0;
        post.created = new Date();
        post.images = 0;
        post.no = threadNo;
        post.tim = thread.created.getTime() * 1000;
        post.tn_w = 0;
        post.tn_h = 0;
        post.defData = true;

        thread.posts = new ChanPost[]{post};
        thread.lastFetched = 0;
        thread.defData = true;

        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.prepareDefaultThreadData(android.content.Context,com.chanapps.four.activity.String,long)");return thread;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.ChanThread com.chanapps.four.data.ChanFileStorage.prepareDefaultThreadData(android.content.Context,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    public static void storeUserStats(Context context, UserStatistics userStats) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.storeUserStats(android.content.Context,com.chanapps.four.activity.UserStatistics)",context,userStats);try{try {
            File userPrefsFile = getUserStatsFile(context);
            if (userPrefsFile != null) {
                try {
                    userStats.compactThreads();
                    userStats.lastStored = new Date().getTime();
                    ObjectMapper mapper = BoardParserService.getJsonMapper();
                    mapper.writeValue(userPrefsFile, userStats);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while writing user preferences", e);
                }
                if (DEBUG) {Log.i(TAG, "Stored user statistics to file, last updated " + userStats.lastUpdate);}
            } else {
                Log.e(TAG, "Cannot store user statistics");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while storing user statistics", e);
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.storeUserStats(android.content.Context,com.chanapps.four.activity.UserStatistics)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.storeUserStats(android.content.Context,com.chanapps.four.activity.UserStatistics)",throwable);throw throwable;}
    }

    public static UserStatistics loadUserStats(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)",context);try{try {
            File userStatsFile = getUserStatsFile(context);
            if (userStatsFile != null && userStatsFile.exists() && userStatsFile.canRead() && userStatsFile.length() > 0) {
                ObjectMapper mapper = BoardParserService.getJsonMapper();
                UserStatistics userPrefs = mapper.readValue(userStatsFile, UserStatistics.class);
                if (userPrefs == null) {
                    Log.e(TAG, "Couldn't load user statistics, null returned");
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)");return new UserStatistics();}
                } else {
                    if (DEBUG)
                        {Log.i(TAG, "Loaded user statistics, last updated " + userPrefs.lastUpdate + ", last stored " + userPrefs.lastStored);}
                    if (userPrefs.convertThreadStats()) {
                        FileSaverService.startService(context, FileType.USER_STATISTICS);
                    }
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)");return userPrefs;}
                }
            } else {
                if (DEBUG) {Log.w(TAG, "File for user statistics doesn't exist");}
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)");return new UserStatistics();}
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while loading user statistics", e);
        }
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)");return new UserStatistics();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.UserStatistics com.chanapps.four.data.ChanFileStorage.loadUserStats(android.content.Context)",throwable);throw throwable;}
    }

    private static final String RM_CMD = "/system/bin/rm -r";

    public static boolean deleteCacheDirectory(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.deleteCacheDirectory(android.content.Context)",context);try{/*// do this jazz to save widget conf even on clear because you can't programmatically remove widgets*/
        /*//Set<String> savedWidgetConf = WidgetProviderUtils.getActiveWidgetPref(context);*/
        try {
            String cacheDir = getRootCacheDirectory(context);
            File cacheFolder = StorageUtils.getOwnCacheDirectory(context, cacheDir);

            String cmd = RM_CMD + " " + cacheFolder.getAbsolutePath();
            if (DEBUG) {Log.i(TAG, "Running delete cache command: " + cmd);}
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            process.waitFor();
            int exitVal = process.exitValue();
            String outputStr = output.toString();
            if (DEBUG) {Log.i(TAG, "Finished deleting cache exitValue=" + exitVal + " output=" + outputStr);}

            if (exitVal == 0) {
                {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.deleteCacheDirectory(android.content.Context)");return true;}
            } else {
                Log.e(TAG, "Error deleting cache exitValue=" + exitVal + " output=" + outputStr);
                {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.deleteCacheDirectory(android.content.Context)");return false;}
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception deleting cache", e);
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.deleteCacheDirectory(android.content.Context)");return false;}
        } finally {
            /*// add back user data*/
            /*//if (savedWidgetConf != null && savedWidgetConf.size() > 0)*/
            /*//    WidgetProviderUtils.saveWidgetBoardPref(context, savedWidgetConf);*/
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.deleteCacheDirectory(android.content.Context)",throwable);throw throwable;}
    }

    public static String getLocalGalleryImageFilename(ChanPost post) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLocalGalleryImageFilename(com.chanapps.four.activity.ChanPost)",post);try{com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLocalGalleryImageFilename(com.chanapps.four.activity.ChanPost)");return post.board + "_" + post.imageName();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getLocalGalleryImageFilename(com.chanapps.four.activity.ChanPost)",throwable);throw throwable;}
    }

    public static Uri getHiddenLocalImageUri(Context context, String boardCode, long postNo, String ext) {
        com.mijack.Xlog.logStaticMethodEnter("android.net.Uri com.chanapps.four.data.ChanFileStorage.getHiddenLocalImageUri(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)",context,boardCode,postNo,ext);try{com.mijack.Xlog.logStaticMethodExit("android.net.Uri com.chanapps.four.data.ChanFileStorage.getHiddenLocalImageUri(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)");return Uri.parse("file://" + getHiddenBoardCacheDirectory(context, boardCode) + FILE_SEP + postNo + ext);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.net.Uri com.chanapps.four.data.ChanFileStorage.getHiddenLocalImageUri(android.content.Context,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static final String CHANU_FOLDER = "Chanu";

    public static File getDownloadFolder(Context context, String boardCode, long threadNo, boolean isSingleImage) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getDownloadFolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)",context,boardCode,threadNo,isSingleImage);try{String configuredPath = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.PREF_DOWNLOAD_LOCATION, null);
        String defaultPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + FILE_SEP + CHANU_FOLDER;
        String suffix = getDownloadSubfolder(context, boardCode, threadNo, isSingleImage);
        String downloadPath = configuredPath != null ? configuredPath + suffix : defaultPath + suffix;
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getDownloadFolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return new File(downloadPath);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.getDownloadFolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)",throwable);throw throwable;}
    }

    private static String getDownloadSubfolder(Context context, String boardCode, long threadNo, boolean isSingleImage) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)",context,boardCode,threadNo,isSingleImage);try{SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SettingsActivity.DownloadImages downloadType = SettingsActivity.DownloadImages.valueOf(prefs.getString(
                SettingsActivity.PREF_DOWNLOAD_IMAGES, SettingsActivity.DownloadImages.STANDARD.toString()));
        switch(downloadType) {
            case ALL_IN_ONE:
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return "";}
            case PER_BOARD:
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return FILE_SEP + "board_" + boardCode;}
            case PER_THREAD:
/*//				Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");*/
/*//				String now = formatter.format(scheduleTime);*/
                if (threadNo > 0) {
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return FILE_SEP + boardCode + "_" + threadNo;}
                } else {
                    /*// offline mode doesn't provide thread info so download defaults to PER_BOARD*/
                    {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return FILE_SEP + "board_" + boardCode;}
                }
            case STANDARD:
            default:
                {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)");return (!isSingleImage && boardCode != null && !boardCode.isEmpty() && threadNo > 0)
                        ? FILE_SEP + boardCode + "_" + threadNo
                        : "";}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.String com.chanapps.four.data.ChanFileStorage.getDownloadSubfolder(android.content.Context,com.chanapps.four.activity.String,long,boolean)",throwable);throw throwable;}
	}

    public static File createWallpaperFile(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.createWallpaperFile(android.content.Context)",context);try{File dir = getWallpaperCacheDirectory(context);
        String name = UUID.randomUUID().toString() + WALLPAPER_EXT;
        {com.mijack.Xlog.logStaticMethodExit("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.createWallpaperFile(android.content.Context)");return new File(dir, name);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.chanapps.four.activity.File com.chanapps.four.data.ChanFileStorage.createWallpaperFile(android.content.Context)",throwable);throw throwable;}
    }

    public static int deletePosts(Context context, String boardCode, long threadNo, long[] postNos, boolean imageOnly) {
        com.mijack.Xlog.logStaticMethodEnter("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)",context,boardCode,threadNo,postNos,imageOnly);try{ChanThread thread = loadThreadData(context, boardCode, threadNo);
        if (thread == null)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)");return 1;}}
        ChanPost[] posts = thread.posts;
        if (posts == null)
            {{com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)");return 2;}}

        Set<Long> deletePostNos = new HashSet<Long>(postNos.length);
        for (long postNo : postNos)
            {deletePostNos.add(postNo);}

        List<ChanPost> postList = new ArrayList<ChanPost>(posts.length);
        for (ChanPost post : posts) {
            boolean found = deletePostNos.contains(post.no);
            if (found && !imageOnly) {
                /*// don't add it, thus it will be deleted*/
            } else if (found && imageOnly) {
                post.clearImageInfo();
                postList.add(post);
            } else {
                postList.add(post);
            }
        }

        ChanPost[] survivingPosts = new ChanPost[postList.size()];
        int i = 0;
        for (ChanPost post : postList)
            {survivingPosts[i++] = post;}
        thread.posts = survivingPosts;

        try {
            if (DEBUG) {Log.i(TAG, "After delete calling storeThreadData for /" + thread.board + "/" + thread.no);}
            storeThreadData(context, thread);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't store thread data after post delete", e);
            {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)");return 4;}
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)");return 0;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.chanapps.four.data.ChanFileStorage.deletePosts(android.content.Context,com.chanapps.four.activity.String,long,[long,boolean)",throwable);throw throwable;}
    }

    public static void addWatchedThread(Context context, ChanThread thread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.addWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{ChanBoard board = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        if (isThreadWatched(board, thread)) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.addWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)");return;}
        }
        List<ChanPost> newThreads = null;
        if (board.defData || board.threads == null || board.threads.length == 0 || board.threads[0].defData) {
            newThreads = new ArrayList<ChanPost>();
            board.defData = false;
        } else {
            newThreads = new ArrayList<ChanPost>(Arrays.asList(board.threads));
        }
        if (DEBUG) {Log.i(TAG, "Before adding to watchlist: " + thread);}
        newThreads.add(0, thread.cloneForWatchlist());
        board.threads = newThreads.toArray(new ChanThread[]{});

        if (DEBUG) {
            Log.i(TAG, "After adding to watchlist: " + board.threads[board.threads.length - 1]);
            Log.i(TAG, "After adding to watchlist threads: " + board.threads[0]);
            Log.i(TAG, "After adding to watchlist defData: " + board.threads[0].defData);
        }

        storeBoardData(context, board);
        WidgetProviderUtils.updateAll(context, ChanBoard.WATCHLIST_BOARD_CODE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.addWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static void addFavoriteBoard(Context context, ChanThread thread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.addFavoriteBoard(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{if (DEBUG) {Log.i(TAG, "addFavoriteBoard /" + thread.board + "/");}
        ChanBoard board = loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        if (isFavoriteBoard(board, thread)) {
            if (DEBUG) {Log.i(TAG, "addFavoriteBoard /" + thread.board + "/ already favorite, exiting");}
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.addFavoriteBoard(android.content.Context,com.chanapps.four.activity.ChanThread)");return;}
        }
        List<ChanPost> newThreads = null;
        if (board.defData || board.threads == null || board.threads.length == 0 || board.threads[0].defData) {
            newThreads = new ArrayList<ChanPost>();
            board.defData = false;
        } else {
            newThreads = new ArrayList<ChanPost>(Arrays.asList(board.threads));
        }
        if (DEBUG) {Log.i(TAG, "Before adding to favorites: " + thread);}
        newThreads.add(0, thread);
        board.threads = newThreads.toArray(new ChanThread[]{});

        if (DEBUG) {
            Log.i(TAG, "After adding to favorites: " + board.threads[board.threads.length - 1]);
            Log.i(TAG, "After adding to favorites threads: " + board.threads[0]);
            Log.i(TAG, "After adding to favorites defData: " + board.threads[0].defData);
        }
        storeBoardData(context, board);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.addFavoriteBoard(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static void deleteWatchedThread(Context context, ChanThread thread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.deleteWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{ChanBoard board = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        List<ChanPost> newThreads = new ArrayList<ChanPost>(Arrays.asList(board.threads));
        for (ChanPost post : board.threads) {
            if (post.board != null && post.board.equals(thread.board) && post.no == thread.no) {
                newThreads.remove(post);
            }
        }
        board.threads = newThreads.toArray(new ChanThread[]{});

        storeBoardData(context, board);
        WidgetProviderUtils.updateAll(context, ChanBoard.WATCHLIST_BOARD_CODE);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.deleteWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.deleteWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static void deleteFavoritesBoard(Context context, ChanThread thread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.deleteFavoritesBoard(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{ChanBoard board = loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        List<ChanPost> newThreads = new ArrayList<ChanPost>(Arrays.asList(board.threads));
        for (ChanPost post : board.threads) {
            if (post.board != null && post.board.equals(thread.board)) {
                newThreads.remove(post);
            }
        }
        board.threads = newThreads.toArray(new ChanThread[]{});
        storeBoardData(context, board);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.deleteFavoritesBoard(android.content.Context,com.chanapps.four.activity.ChanThread)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.deleteFavoritesBoard(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static void clearWatchedThreads(Context context) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.clearWatchedThreads(android.content.Context)",context);try{ChanBoard board = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        board.threads = new ChanThread[]{};
        storeBoardData(context, board);
        WidgetProviderUtils.updateAll(context, ChanBoard.WATCHLIST_BOARD_CODE);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.clearWatchedThreads(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.clearWatchedThreads(android.content.Context)",throwable);throw throwable;}
    }

    public static void cleanDeadWatchedThreads(Context context) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.cleanDeadWatchedThreads(android.content.Context)",context);try{ChanBoard board = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        List<ChanThread> cleanedThreads = new ArrayList<ChanThread>();
        for (ChanThread thread : board.threads) {
            if (!thread.isDead)
                {cleanedThreads.add(thread);}
        }
        board.threads = cleanedThreads.size() == 0 ? new ChanThread[]{} : cleanedThreads.toArray(new ChanThread[cleanedThreads.size()]);
        storeBoardData(context, board);
        WidgetProviderUtils.updateAll(context, ChanBoard.WATCHLIST_BOARD_CODE);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.cleanDeadWatchedThreads(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.cleanDeadWatchedThreads(android.content.Context)",throwable);throw throwable;}
    }

    public static void clearFavorites(Context context) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.clearFavorites(android.content.Context)",context);try{ChanBoard board = loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        board.threads = new ChanThread[]{};
        storeBoardData(context, board);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.clearFavorites(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.clearFavorites(android.content.Context)",throwable);throw throwable;}
    }

    private static void updateBoardThread(Context context, ChanThread loadedThread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.updateBoardThread(android.content.Context,com.chanapps.four.activity.ChanThread)",context,loadedThread);try{/*// store updated status into board thread record*/
        ChanBoard board = loadBoardData(context, loadedThread.board);
        if (board == null || board.threads == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.updateBoardThread(android.content.Context,com.chanapps.four.activity.ChanThread)");return;}}
        int found = -1;
        for (int i = 0; i < board.threads.length; i++) {
            if (board.threads[i] != null && board.threads[i].no == loadedThread.no) {
                found = i;
                break;
            }
        }
        if (found >= 0) {
            if (DEBUG) {Log.i(TAG, "updateBoardThread found thread=[" + board.threads[found] + "] merging=[" + loadedThread + "]");}
            board.threads[found].copyUpdatedInfoFields(loadedThread);
            storeBoardData(context, board, board.threads[found].no);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.updateBoardThread(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    private static void updateWatchedThread(Context context, ChanThread loadedThread) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",context,loadedThread);try{ChanBoard watchlistBoard = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        for (int i = 0; i < watchlistBoard.threads.length; i++) {
            ChanThread watchedThread = watchlistBoard.threads[i];
            if (watchedThread.no == loadedThread.no && watchedThread.board.equals(loadedThread.board)) {
                NotificationComponent.notifyNewReplies(context, watchedThread, loadedThread);
                watchlistBoard.threads[i].updateThreadData(loadedThread);
                if (DEBUG) {Log.i(TAG, "Updating watched thread " + watchedThread.board + "/" + watchedThread.no
                        + " replies: " + watchedThread.replies + " images: " + watchedThread.images);}
                storeBoardData(context, watchlistBoard);
                BoardActivity.refreshWatchlist(context);
            }
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    private static void updateWatchedThread(Context context, ChanBoard loadedBoard) throws IOException {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanBoard)",context,loadedBoard);try{if (loadedBoard.defData || loadedBoard.loadedThreads == null || loadedBoard.loadedThreads.length == 0
                || loadedBoard.loadedThreads[0].defData) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanBoard)");return;}
        }
        ChanBoard watchlist = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        if (watchlist == null || watchlist.threads == null || watchlist.threads.length == 0)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanBoard)");return;}}
        boolean updateWatchlist = false;
        for (int i = 0; i < watchlist.threads.length; i++) {
            ChanThread watchedThread = watchlist.threads[i];
            if (watchedThread.board.equals(loadedBoard.link)) {
                for (int t = 0; t < loadedBoard.loadedThreads.length; t++) {
                    ChanThread loadedThread = loadedBoard.loadedThreads[t];
                    if (watchedThread.no == loadedThread.no) {
                        NotificationComponent.notifyNewReplies(context, watchedThread, loadedThread);
                        watchedThread.updateThreadDataWithPost(loadedThread);
                        if (DEBUG) {Log.i(TAG, "Updating watched thread " + watchedThread.board + "/" + watchedThread.no
                                + " replies: " + watchedThread.replies + " images: " + watchedThread.images);}
                        updateWatchlist = true;
                    }
                }
            }
        }
        if (updateWatchlist) {
            storeBoardData(context, watchlist);
            if (PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getBoolean(SettingsActivity.PREF_AUTOMATICALLY_MANAGE_WATCHLIST, true))
                {ChanFileStorage.cleanDeadWatchedThreads(context);}
            BoardActivity.refreshWatchlist(context);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.updateWatchedThread(android.content.Context,com.chanapps.four.activity.ChanBoard)",throwable);throw throwable;}
    }

    private static boolean isThreadWatched(ChanBoard board, ChanThread thread) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)",board,thread);try{if (board == null || board.threads == null || thread == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return false;}}
        for (ChanPost post : board.threads) {
            if (post.board != null && post.board.equals(thread.board) && post.no == thread.no) {
                {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return true;}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static boolean isFavoriteBoard(ChanBoard board, ChanThread thread) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.isFavoriteBoard(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)",board,thread);try{if (board == null || board.threads == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isFavoriteBoard(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return false;}}
        for (ChanPost post : board.threads) {
            if (post.board != null && post.board.equals(thread.board)) {
                {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isFavoriteBoard(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return true;}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isFavoriteBoard(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.isFavoriteBoard(com.chanapps.four.activity.ChanBoard,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static boolean isThreadWatched(Context context, ChanThread thread) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(android.content.Context,com.chanapps.four.activity.ChanThread)",context,thread);try{ChanBoard board = loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(android.content.Context,com.chanapps.four.activity.ChanThread)");return isThreadWatched(board, thread);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.isThreadWatched(android.content.Context,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    public static void migrateIfNecessary(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.migrateIfNecessary(android.content.Context)",context);try{migrateUserStats(context);
        migrateBoard(context, ChanBoard.WATCHLIST_BOARD_CODE);
        migrateBoard(context, ChanBoard.FAVORITES_BOARD_CODE);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateIfNecessary(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.migrateIfNecessary(android.content.Context)",throwable);throw throwable;}
    }
    
    private static void migrateUserStats(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.migrateUserStats(android.content.Context)",context);try{File legacyUserStatsFile = getLegacyUserStatsFile(context);
        if (legacyUserStatsFile != null && legacyUserStatsFile.exists()) {
            File userStatsDir = getPersistentDirectory(context);
            moveFileToDir(legacyUserStatsFile, userStatsDir);
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateUserStats(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.migrateUserStats(android.content.Context)",throwable);throw throwable;}
    }
    
    private static void migrateBoard(Context context, String boardCode) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)",context,boardCode);try{File legacyBoardFile = getLegacyBoardCacheFile(context, boardCode);
        if (legacyBoardFile == null || !legacyBoardFile.exists())
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)");return;}}
        File boardDir = getBoardCacheDirectory(context, boardCode);
        if (!moveFileToDir(legacyBoardFile, boardDir))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)");return;}}
        String cacheDir = getLegacyBoardCachePath(context, boardCode);
        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)");return;}}
        File legacyBoardDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        if (legacyBoardDir == null || !legacyBoardDir.exists())
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)");return;}}
        legacyBoardDir.delete();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.data.ChanFileStorage.migrateBoard(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }
    
    private static boolean moveFileToDir(File sourceFile, File destDir) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)",sourceFile,destDir);try{FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;

        File destFile = null;
        try
        {
            destFile = new File(destDir.getAbsolutePath() + FILE_SEP + sourceFile.getName());
            if (!destFile.createNewFile())
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return false;}}

            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);
            in = fis.getChannel();
            out = fos.getChannel();

            long size = in.size();
            long bytes = in.transferTo(0, size, out);
            if (bytes < size) { /*// transfer failed*/
                Log.e(TAG, "didn't transfer full size of file " + sourceFile + " to " + destDir + ", deleting");
                destFile.delete();    
                {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return false;}
            }
            if (!destFile.exists()) /*// transfer failed*/
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return false;}}
            sourceFile.delete();
            if (sourceFile.exists()) /*// delete failed*/
                {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return false;}}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return true;}
        }
        catch (Throwable e)
        {
            Log.e(TAG, "Exception moving file " + sourceFile + " to " + destDir);
            if (destFile != null)
                {destFile.delete();}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)");return false;}
        }
        finally
        {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(fis);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.data.ChanFileStorage.moveFileToDir(com.chanapps.four.activity.File,com.chanapps.four.activity.File)",throwable);throw throwable;}
    }

}
