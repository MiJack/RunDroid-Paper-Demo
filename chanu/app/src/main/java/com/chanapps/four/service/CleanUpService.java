/**
 *
 */
package com.chanapps.four.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.CacheSizePreference;
import com.chanapps.four.data.*;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.widget.AbstractBoardWidgetProvider;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class CleanUpService extends BaseChanService {
    protected static final String TAG = CleanUpService.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final long MIN_DELAY_BETWEEN_CLEANUPS_MS = 4 * 60 * 1000; /*// 2min*/
    private static final long MAX_DELAY_BETWEEN_CLEANUPS_MS = 30 * 60 * 1000; /*// 10min*/
    private static long scheduleDelay = MIN_DELAY_BETWEEN_CLEANUPS_MS;
    private static long lastScheduled = 0;

    private static final long ONE_MIN_MS = 60L * 60L * 1000L;
    private static final long ONE_HOUR_MS = 60L * ONE_MIN_MS;
    private static final long ONE_DAY_MS = 24L * ONE_HOUR_MS;
    private static final long ONE_MIN_KEEP_SIZE_BYTES = 250L * 1024L;
    private static final long ONE_HOUR_KEEP_SIZE_BYTES = 500L * 1024L;
    private static final long ONE_DAY_KEEP_SIZE_BYTES = 1000L * 1024L;
    private static final long ONE_MB_BYTES = 1024L * 1024L;

    private static final String NOMEDIA_FILE_PATTERN = ".*/\\.nomedia";
    private static final Pattern nomediaFilePattern = Pattern.compile(NOMEDIA_FILE_PATTERN);
    private static final String BOARD_FILE_PATTERN_FORMAT = ".*/%s(_catalog)?\\.txt";
    private static final String THREAD_FILE_PATTERN = ".*/t_([0-9]*)\\.txt";
    private static final Pattern threadFilePattern = Pattern.compile(THREAD_FILE_PATTERN);

    private File cacheFolder = null;
    private List<FileDesc> otherFiles = null;
    private Map<String, Long> sizeByBoard = null;
    private Map<String, List<FileDesc>> filesByBoard = null;
    private long sizeOfWidget = 0;
    private List<FileDesc> filesOfWidget = null;
    private Map<String, Set<Long>> watchedThreads = new HashMap<String, Set<Long>>();
    private Set<String> watchedImagePath = new HashSet<String>();

    private long targetCacheSize = 0;
    private long totalSize = 0;
    private int totalFiles = 0;
    private int totalDeletedFiles = 0;
    private long otherSize = 0;

    public static void startService(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.service.CleanUpService.startService(android.content.Context)",context);try{/*
        if (lastScheduled > Calendar.getInstance().getTimeInMillis() - scheduleDelay) {
            if (DEBUG) Log.i(TAG, "Cleanup service was called less than " + (scheduleDelay / 1000) + "s ago, skipping call");
            return;
        }
        lastScheduled = Calendar.getInstance().getTimeInMillis();
        scheduleDelay += MIN_DELAY_BETWEEN_CLEANUPS_MS;
        if (scheduleDelay > MAX_DELAY_BETWEEN_CLEANUPS_MS) {
        	scheduleDelay = MIN_DELAY_BETWEEN_CLEANUPS_MS;
        }
        */
        if (DEBUG) {Log.w(TAG, "Scheduling clean up service");}
        lastScheduled = Calendar.getInstance().getTimeInMillis();
        Intent intent = new Intent(context, CleanUpService.class);
        intent.putExtra(PRIORITY_MESSAGE_FETCH, 1);
        context.startService(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.service.CleanUpService.startService(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.startService(android.content.Context)",throwable);throw throwable;}
    }

    public CleanUpService() {
        super("cleanup");
    }

    protected CleanUpService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.onHandleIntent(android.content.Intent)",this,intent);try{try {
            cleanUpCache();
        } catch (Exception e) {
            Log.e(TAG, "Error in clean up service", e);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.onHandleIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.onHandleIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    private void cleanUpCache() throws Exception {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanUpCache()",this);try{long startTime = Calendar.getInstance().getTimeInMillis();
        prepareCache();
        prepareTopWatchedBoards();
        scanFiles();
        long endTime = Calendar.getInstance().getTimeInMillis();
        if (DEBUG) {logCacheFileInfo("ClearUp init.", startTime, endTime);}
        long diffSec = (endTime - startTime) / 1000;
        if (DEBUG) {Log.i(TAG, "cache files=" + totalFiles + " size=" + (totalSize / 1024) + "KB scanned in " +  diffSec + "s.");}

        startTime = Calendar.getInstance().getTimeInMillis();
        long maxCacheSize = getPreferredCacheSize() * ONE_MB_BYTES;
        targetCacheSize = maxCacheSize * 80 / 100;
        if (DEBUG) {Log.i(TAG, "cache size current=" + (totalSize/ONE_MB_BYTES) + "MB target=" + (targetCacheSize/ONE_MB_BYTES) + "MB  max=" + (maxCacheSize / ONE_MB_BYTES) + "MB");}

        /*
        for (int daysAgo = 6; daysAgo > 0; daysAgo--)
            cleanUp(daysAgo * ONE_DAY_MS, DeleteType.BY_DATE);
        //cleanUp(ONE_DAY_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        for (int hoursAgo = 12; hoursAgo > 0; hoursAgo-=2)
            cleanUp(hoursAgo * ONE_HOUR_MS, DeleteType.BY_DATE);
        //cleanUp(ONE_HOUR_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        for (int minsAgo = 30; minsAgo > 0; minsAgo-=5)
            cleanUp(minsAgo * ONE_MIN_MS, DeleteType.BY_DATE);
        cleanUp(ONE_MIN_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        for (int daysAgo = 6; daysAgo > 0; daysAgo--)
            cleanUp(daysAgo * ONE_DAY_MS, DeleteType.BY_DATE_INCL_WATCHED);
        cleanUp(ONE_MIN_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        cleanUp(ONE_HOUR_MS, DeleteType.BY_DATE_INCL_WATCHED);
        cleanUp(ONE_MIN_MS, DeleteType.BY_DATE_INCL_WATCHED);
        */
        cleanUp(ONE_DAY_MS, DeleteType.BY_DATE);
        cleanUp(ONE_DAY_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        cleanUp(ONE_HOUR_MS, DeleteType.BY_DATE);
        cleanUp(ONE_HOUR_KEEP_SIZE_BYTES, DeleteType.BY_SIZE);
        cleanUp(ONE_HOUR_MS, DeleteType.BY_DATE_INCL_WATCHED);
        cleanUp(0, DeleteType.BY_DATE_INCL_WATCHED);

        endTime = Calendar.getInstance().getTimeInMillis();
        if (DEBUG) {logCacheFileInfo("Deletion report.", startTime, endTime);}
        diffSec = (endTime - startTime) / 1000;
        if (DEBUG) {Log.i(TAG, "deleted cache files=" + totalDeletedFiles + " out of original=" + totalFiles + " in " + diffSec + "s");}
        if (DEBUG) {Log.i(TAG, "final cache size=" + (totalSize/ONE_MB_BYTES) + "MB target=" + (targetCacheSize/ONE_MB_BYTES) + "MB  max=" + (maxCacheSize / ONE_MB_BYTES) + "MB");}

        cleanVars();com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanUpCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanUpCache()",this,throwable);throw throwable;}
    }

    private void cleanUp(long ago, DeleteType deleteType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanUp(long,com.chanapps.four.data.DeleteType)",this,ago,deleteType);try{cleanUpBoards(ago, deleteType);
        cleanUpOthers(ago, deleteType);
        cleanUpWidgets(ago, deleteType);com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanUp(long,com.chanapps.four.data.DeleteType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanUp(long,com.chanapps.four.data.DeleteType)",this,throwable);throw throwable;}
    }

    private int getPreferredCacheSize() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.CleanUpService.getPreferredCacheSize()",this);try{SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int prefSize = prefs.getInt(SettingsActivity.PREF_CACHE_SIZE, CacheSizePreference.DEFAULT_VALUE);
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.getPreferredCacheSize()",this);return prefSize < CacheSizePreference.MIN_VALUE ? CacheSizePreference.MIN_VALUE : prefSize;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.CleanUpService.getPreferredCacheSize()",this,throwable);throw throwable;}
    }

    private void prepareTopWatchedBoards() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.prepareTopWatchedBoards()",this);try{Context context = getBaseContext();
        DiscCacheAware imageCache = ChanImageLoader.getInstance(context).getDiscCache();
        watchedThreads = new HashMap<String, Set<Long>>();
        watchedImagePath = new HashSet<String>();
        List<String> watchedOrTopBoardCode = new ArrayList<String>();
        watchedThreads = new HashMap<String, Set<Long>>();
        ChanBoard board = ChanFileStorage.loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        if (board != null && board.threads != null) {
            for (ChanPost threadPost : board.threads) {
                if (!watchedOrTopBoardCode.contains(threadPost.board))
                    {watchedOrTopBoardCode.add(threadPost.board);}
                if (!watchedThreads.containsKey(threadPost.board))
                    {watchedThreads.put(threadPost.board, new HashSet<Long>());}
                watchedThreads.get(threadPost.board).add(threadPost.no);
                addWatchedImagePaths(context, imageCache, threadPost.board, threadPost.no);
            }
            for (String boardCode : watchedThreads.keySet())
            {if (DEBUG) {Log.i(TAG, "watchedThreads /" + boardCode + "/ = "
                    + Arrays.toString(watchedThreads.get(boardCode).toArray()));}}
        }
        if (DEBUG) {Log.i(TAG, "watchedImagePaths = "
                + Arrays.toString(watchedImagePath.toArray()));}

        UserStatistics userStats = NetworkProfileManager.instance().getUserStatistics();
        if (userStats != null) {
            for (ChanBoardStat stat : userStats.topBoards()) {
                if (!watchedOrTopBoardCode.contains(stat.board)) {
                    watchedOrTopBoardCode.add(stat.board);
                }
            }
        }

        board = ChanFileStorage.loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        if (board != null && board.threads != null) {
            for (ChanPost thread : board.threads) {
                if (!watchedOrTopBoardCode.contains(thread.board)) {
                    watchedOrTopBoardCode.add(thread.board);
                }
            }
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.prepareTopWatchedBoards()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.prepareTopWatchedBoards()",this,throwable);throw throwable;}
    }

    private void addWatchedImagePaths(Context context, DiscCacheAware imageCache, String boardCode, long threadNo) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.addWatchedImagePaths(android.content.Context,com.nostra13.universalimageloader.cache.disc.DiscCacheAware,com.chanapps.four.data.String,long)",this,context,imageCache,boardCode,threadNo);try{if (imageCache == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.addWatchedImagePaths(android.content.Context,com.nostra13.universalimageloader.cache.disc.DiscCacheAware,com.chanapps.four.data.String,long)",this);return;}}
        ChanThread thread = ChanFileStorage.loadThreadData(context, boardCode, threadNo);
        if (thread == null || thread.posts == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.addWatchedImagePaths(android.content.Context,com.nostra13.universalimageloader.cache.disc.DiscCacheAware,com.chanapps.four.data.String,long)",this);return;}}

        try {
            for (ChanPost post : thread.posts) {

                String thumb = post.thumbnailUrl(context);
                if (thumb == null)
                    {continue;}
                File thumbImage = imageCache.get(thumb);
                if (thumbImage == null)
                    {continue;}
                String thumbPath = thumbImage.getAbsolutePath();
                watchedImagePath.add(thumbPath);

                String full = post.imageUrl(context);
                if (full == null)
                    {continue;}
                File fullImage = imageCache.get(full); /*// FIXME: doesn't work for web-cache-stored animated gifs*/
                if (fullImage == null)
                    {continue;}
                String fullPath = fullImage.getAbsolutePath();
                watchedImagePath.add(fullPath);

            }
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "out of memory adding watched files, skipping more files");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.addWatchedImagePaths(android.content.Context,com.nostra13.universalimageloader.cache.disc.DiscCacheAware,com.chanapps.four.data.String,long)",this,throwable);throw throwable;}
    }

    private void logCacheFileInfo(String logMsg, long startTime, long endTime) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.logCacheFileInfo(com.chanapps.four.data.String,long,long)",this,logMsg,startTime,endTime);try{Log.i(TAG, logMsg + " Cache folder contains " + totalFiles + " files of size " + (totalSize / ONE_MB_BYTES )
                + "MB. Calculated in " + (endTime - startTime) + "ms.");
        for (Map.Entry<String, Long> entry : sizeByBoard.entrySet()) {
            if (filesByBoard.get(entry.getKey()).size() == 0)
                {continue;}
            Log.i(TAG, "Board " + entry.getKey() + " size=" + (entry.getValue() / ONE_MB_BYTES) + "MB "
                    + filesByBoard.get(entry.getKey()).size() + " files.");
        }
        Log.i(TAG, "Other files' size=" + (otherSize / ONE_MB_BYTES) + "MB " + otherFiles.size() + " files.");com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.logCacheFileInfo(com.chanapps.four.data.String,long,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.logCacheFileInfo(com.chanapps.four.data.String,long,long)",this,throwable);throw throwable;}
    }

    private int trimByDate(List<FileDesc> files, long timeOffset) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this,files,timeOffset);try{if (files == null || files.size() == 0) {
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this);return 0;}
        }
        try {
            Collections.sort(files, new Comparator<FileDesc>() {
                public int compare(FileDesc o1, FileDesc o2) {
                    com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.CleanUpService$1.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this,o1,o2);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService$1.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this);{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this);return o1.lastModified > o2.lastModified ? 1
                            : o1.lastModified < o2.lastModified ? -1 : 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.CleanUpService$1.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this,throwable);throw throwable;}
                }
            });
        }
        catch (Error e) {
            Log.e(TAG, "error while trimBySize()", e);
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this);return 0;}
        }

        int i = 0;
        long olderThan = new Date().getTime() - timeOffset;
        Iterator<FileDesc> iter = files.iterator();
        while (iter.hasNext()) {
            FileDesc file = iter.next();
            if (olderThan > file.lastModified) {
                new File(file.path).delete();
                totalSize -= file.size;
                iter.remove();
                if (DEBUG) {Log.i(TAG, "removed old file: " + file);}
                i++;
            }
            if (totalSize < targetCacheSize)
                {break;}
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this);return i;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.CleanUpService.trimByDate(com.chanapps.four.data.List,long)",this,throwable);throw throwable;}
    }

    private int trimBySize(List<FileDesc> files, long size) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this,files,size);try{if (files == null || files.size() == 0) {
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this);return 0;}
        }
        try {
            Collections.sort(files, new Comparator<FileDesc>() {
                public int compare(FileDesc o1, FileDesc o2) {
                    com.mijack.Xlog.logMethodEnter("int com.chanapps.four.service.CleanUpService$2.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this,o1,o2);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService$2.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this);{com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this);return o1.size > o2.size ? -1
                            : o1.size < o2.size ? 1 : 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.CleanUpService$2.compare(com.chanapps.four.data.FileDesc,com.chanapps.four.data.FileDesc)",this,throwable);throw throwable;}
                }
            });
        }
        catch (Error e) {
            Log.e(TAG, "error while trimBySize()", e);
            {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this);return 0;}
        }

        int i = 0;
        Iterator<FileDesc> iter = files.iterator();
        while (iter.hasNext()) {
            FileDesc file = iter.next();
            if (size < file.size) {
                new File(file.path).delete();
                totalSize -= file.size;
                iter.remove();
                if (DEBUG) {Log.i(TAG, "removed large file: " + file);}
                i++;
            }
            if (totalSize < targetCacheSize)
                {break;}
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this);return i;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.service.CleanUpService.trimBySize(com.chanapps.four.data.List,long)",this,throwable);throw throwable;}
    }

    private void prepareCache() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.prepareCache()",this);try{cacheFolder = ChanFileStorage.getCacheDirectory(getBaseContext());
        if (DEBUG) {Log.i(TAG, "Cache dir: " + cacheFolder.getAbsolutePath());}
        otherFiles = new ArrayList<FileDesc>();
        sizeByBoard = new HashMap<String, Long>();
        filesByBoard = new HashMap<String, List<FileDesc>>();
        sizeOfWidget = 0;
        filesOfWidget = null;

        totalSize = 0;
        totalFiles = 0;
        totalDeletedFiles = 0;
        otherSize = 0;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.prepareCache()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.prepareCache()",this,throwable);throw throwable;}
    }

    private void scanFiles() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.scanFiles()",this);try{File[] children = cacheFolder.listFiles();
        if (children == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.scanFiles()",this);return;}}
        try{
            for (File child : children) {
                if (child.isDirectory()) {
                    if (ChanBoard.getBoardByCode(getBaseContext(), child.getName()) != null) {
                        List<FileDesc> boardData = new ArrayList<FileDesc>();
                        long boardSize = addFiles(child, boardData);

                        sizeByBoard.put(child.getName(), boardSize);
                        filesByBoard.put(child.getName(), boardData);
                        totalSize += boardSize;
                        totalFiles += boardData.size();
                    } else if (AbstractBoardWidgetProvider.WIDGET_CACHE_DIR.equals(child.getName())) {
                        List<FileDesc> widgetData = new ArrayList<FileDesc>();
                        long widgetSize = addFiles(child, widgetData);
                        sizeOfWidget = widgetSize;
                        filesOfWidget = widgetData;
                        totalSize += widgetSize;
                        totalFiles += widgetData.size();
                    } else {
                        long folderSize = addFiles(child, otherFiles);
                        otherSize += folderSize;
                        totalSize += folderSize;
                    }
                } else {
                    FileDesc desc = new FileDesc(child);
                    totalSize += desc.size;
                    otherSize += desc.size;
                    otherFiles.add(desc);
                }
            }
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "out of memory adding files, skipping more files");
        }
        totalFiles += otherFiles.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.scanFiles()",this,throwable);throw throwable;}
    }

    private long addFiles(File file, List<FileDesc> all) {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.service.CleanUpService.addFiles(java.io.File,com.chanapps.four.data.List)",this,file,all);try{long totalSize = 0;
        if (DEBUG) {Log.i(TAG, "Checking folder " + file.getAbsolutePath());}
        File[] children = file.listFiles();
        if (children == null)
            {{com.mijack.Xlog.logMethodExit("long com.chanapps.four.service.CleanUpService.addFiles(java.io.File,com.chanapps.four.data.List)",this);return totalSize;}}
        try {
            for (File child : children) {
                if (child.isDirectory()) {
                    totalSize += addFiles(child, all);
                } else {
                    FileDesc desc = new FileDesc(child);
                    totalSize += desc.size;
                    all.add(desc);
                }
            }
        }
        catch (OutOfMemoryError e) {
            Log.e(TAG, "out of memory adding files, skipping more files");
        }
        {com.mijack.Xlog.logMethodExit("long com.chanapps.four.service.CleanUpService.addFiles(java.io.File,com.chanapps.four.data.List)",this);return totalSize;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.service.CleanUpService.addFiles(java.io.File,com.chanapps.four.data.List)",this,throwable);throw throwable;}
    }

    private static enum DeleteType {
        BY_DATE,
        BY_SIZE,
        BY_DATE_INCL_WATCHED
    }

    private void cleanUpBoards(long olderThanMsOrMaxKeepSizeBytes, DeleteType deleteType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanUpBoards(long,com.chanapps.four.data.DeleteType)",this,olderThanMsOrMaxKeepSizeBytes,deleteType);try{if (totalSize < targetCacheSize)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanUpBoards(long,com.chanapps.four.data.DeleteType)",this);return;}}
        for (String board : filesByBoard.keySet()) {
            if (totalSize < targetCacheSize)
                {break;}
            /*//if (watchedOrTopBoardCode.contains(board))*/
            /*//    continue;*/

            List<FileDesc> preBoardFiles = filesByBoard.get(board);
            List<FileDesc> boardFiles = new ArrayList<FileDesc>();
            Set<Long> watchedBoardThreadNos = watchedThreads.get(board);
            String boardFilePatternString = String.format(BOARD_FILE_PATTERN_FORMAT, board);
            Pattern boardFilePattern = Pattern.compile(boardFilePatternString);

            try {
                for (FileDesc d : preBoardFiles) {
                    if (isNomediaFile(d))
                        {continue;}
                    if (isRootBoardFile(d, boardFilePattern))
                        {continue;}
                    if (isWatchedThreadFile(d, watchedBoardThreadNos))
                        {continue;}
                    boardFiles.add(d);
                }
            }
            catch (OutOfMemoryError e) {
                Log.e(TAG, "out of memory adding board files, skipping more files");
            }

            deleteByType(boardFiles, olderThanMsOrMaxKeepSizeBytes, deleteType);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanUpBoards(long,com.chanapps.four.data.DeleteType)",this,throwable);throw throwable;}
    }

    private void deleteByType(List<FileDesc> inFiles, long olderThanMsOrMaxKeepSizeBytes, DeleteType deleteType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.deleteByType(com.chanapps.four.data.List,long,com.chanapps.four.data.DeleteType)",this,inFiles,olderThanMsOrMaxKeepSizeBytes,deleteType);try{List<FileDesc> files;
        if (deleteType == DeleteType.BY_DATE_INCL_WATCHED) {
            files = inFiles;
        }
        else {
            files = new ArrayList<FileDesc>();
            try {
                for (FileDesc inFile : inFiles) {
                    if (!watchedImagePath.contains(inFile.path))
                        {files.add(inFile);}
                }
            }
            catch (OutOfMemoryError e) {
                Log.e(TAG, "out of memory adding files, skipping more files");
            }
        }
        int numDeletedFiles;
        switch (deleteType) {
            case BY_DATE:
            case BY_DATE_INCL_WATCHED:
                numDeletedFiles = trimByDate(files, olderThanMsOrMaxKeepSizeBytes);
                if (DEBUG)
                    {Log.i(TAG, "Deleted " + numDeletedFiles + " old files");}
                break;
            case BY_SIZE:
            default:
                numDeletedFiles = trimBySize(files, olderThanMsOrMaxKeepSizeBytes);
                if (DEBUG)
                    {Log.i(TAG, "Deleted " + numDeletedFiles + " large files");}
        }
        totalDeletedFiles += numDeletedFiles;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.deleteByType(com.chanapps.four.data.List,long,com.chanapps.four.data.DeleteType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.deleteByType(com.chanapps.four.data.List,long,com.chanapps.four.data.DeleteType)",this,throwable);throw throwable;}
    }

    private void cleanUpOthers(long olderThanDateOrMaxKeepSizeBytes, DeleteType deleteType) { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanUpOthers(long,com.chanapps.four.data.DeleteType)",this,olderThanDateOrMaxKeepSizeBytes,deleteType);try{/*// e.g. thumbnails*/
        if (totalSize < targetCacheSize)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanUpOthers(long,com.chanapps.four.data.DeleteType)",this);return;}}
        if (otherFiles != null && otherFiles.size() > 0) {
            if (DEBUG) {Log.i(TAG, "deleting other files...");}
            deleteByType(otherFiles, olderThanDateOrMaxKeepSizeBytes, deleteType);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanUpOthers(long,com.chanapps.four.data.DeleteType)",this,throwable);throw throwable;}
    }

    private void cleanUpWidgets(long olderThanDateOrMaxKeepSizeBytes, DeleteType deleteType) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanUpWidgets(long,com.chanapps.four.data.DeleteType)",this,olderThanDateOrMaxKeepSizeBytes,deleteType);try{if (totalSize < targetCacheSize)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanUpWidgets(long,com.chanapps.four.data.DeleteType)",this);return;}}
        if (sizeOfWidget > 0 && filesOfWidget != null && filesOfWidget.size() > 0) {
            if (DEBUG) {Log.i(TAG, "deleting widget files...");}
            deleteByType(filesOfWidget, olderThanDateOrMaxKeepSizeBytes, deleteType);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanUpWidgets(long,com.chanapps.four.data.DeleteType)",this,throwable);throw throwable;}
    }

    private boolean isNomediaFile(FileDesc d) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.service.CleanUpService.isNomediaFile(com.chanapps.four.data.FileDesc)",this,d);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isNomediaFile(com.chanapps.four.data.FileDesc)",this);return nomediaFilePattern.matcher(d.path).matches();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.service.CleanUpService.isNomediaFile(com.chanapps.four.data.FileDesc)",this,throwable);throw throwable;}
    }

    private boolean isRootBoardFile(FileDesc d, Pattern boardFilePattern) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.service.CleanUpService.isRootBoardFile(com.chanapps.four.data.FileDesc,java.util.regex.Pattern)",this,d,boardFilePattern);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isRootBoardFile(com.chanapps.four.data.FileDesc,java.util.regex.Pattern)",this);return boardFilePattern.matcher(d.path).matches();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.service.CleanUpService.isRootBoardFile(com.chanapps.four.data.FileDesc,java.util.regex.Pattern)",this,throwable);throw throwable;}
    }

    private boolean isWatchedThreadFile(FileDesc d, Set<Long> threadNos) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this,d,threadNos);try{if (threadNos == null || threadNos.size() == 0)
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this);return false;}}
        Matcher m = threadFilePattern.matcher(d.path);
        if (!m.matches())
            {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this);return false;}}
        long threadNo = 0;
        try {
            String threadStr = m.group(1);
            threadNo = Long.valueOf(threadNo);
            if (threadNos.contains(threadNo))
                {{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this);return true;}}
        }
        catch (IllegalStateException e) {
            if (DEBUG) {Log.i(TAG, "bad match thread number, adding file to cleanup: " + d.path, e);}
        }
        catch (NumberFormatException e) {
            if (DEBUG) {Log.i(TAG, "unmatched thread number, adding file to cleanup: " + d.path, e);}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.service.CleanUpService.isWatchedThreadFile(com.chanapps.four.data.FileDesc,com.chanapps.four.data.Set)",this,throwable);throw throwable;}
    }

    protected void cleanVars() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.service.CleanUpService.cleanVars()",this);try{cacheFolder = null;
        otherFiles = null;
        sizeByBoard = null;
        filesByBoard = null;
        sizeOfWidget = 0;
        filesOfWidget = null;
        watchedThreads = null;
        watchedImagePath = null;
        targetCacheSize = 0;
        totalSize = 0;
        totalFiles = 0;
        totalDeletedFiles = 0;
        otherSize = 0;com.mijack.Xlog.logMethodExit("void com.chanapps.four.service.CleanUpService.cleanVars()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.service.CleanUpService.cleanVars()",this,throwable);throw throwable;}
    }


}
