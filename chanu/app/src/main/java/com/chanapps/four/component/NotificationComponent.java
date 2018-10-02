package com.chanapps.four.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.chanapps.four.activity.*;
import com.chanapps.four.data.*;
import com.chanapps.four.loader.ChanImageLoader;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.ThreadImageDownloadService;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 11/18/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotificationComponent {

    private static final String TAG = NotificationComponent.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final int CLEAR_CACHE_NOTIFY_ID = 0x870932; /*// a unique notify idea is needed for each notify to "clump" together*/
    private static final long NOTIFICATION_UPDATE_TIME = 3000;  /*// 1s*/

    public static void notifyNewReplies(Context context, ChanPost watchedThread, ChanThread loadedThread) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)",context,watchedThread,loadedThread);try{if (DEBUG) {Log.i(TAG, "notifyNewReplies() watched=" + watchedThread + " loaded=" + loadedThread);}
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}}
        if (watchedThread == null || loadedThread == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}}
        if (loadedThread.posts == null || loadedThread.posts.length == 0 || loadedThread.posts[0] == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}}

        String board = loadedThread.board;
        long threadNo = loadedThread.no;
        ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        ChanActivityId aid = NetworkProfileManager.instance().getActivityId();
        /*// limit notification when on watchlist or active thread*/
        if (activity != null && activity.getChanHandler() != null && aid != null) {
            if (board.equals(aid.boardCode) && threadNo == aid.threadNo) {
                if (DEBUG) {Log.i(TAG, "notifyNewReplies /" + board + "/" + threadNo + " user on thread, skipping notification");}
                {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}
            }
            else if (ChanBoard.WATCHLIST_BOARD_CODE.equals(aid.boardCode)) {
                if (DEBUG) {Log.i(TAG, "notifyNewReplies /" + board + "/" + threadNo + " user on watchlist, skipping notification");}
                {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}
            }
        }

        int numNewReplies = loadedThread.posts[0].replies - (watchedThread.replies >= 0 ? watchedThread.replies : 0);
        if (DEBUG) {Log.i(TAG, "notifyNewReplies() /" + board + "/" + threadNo + " newReplies=" + numNewReplies);}
        if (numNewReplies <= 0 && !loadedThread.isDead)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)");return;}}
        int notificationId = board.hashCode() + (int)threadNo;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        String postText;
        if (loadedThread.isDead) {
            postText = context.getString(R.string.mobile_profile_fetch_dead_thread);
        }
        else {
            String postPlurals = context.getResources().getQuantityString(R.plurals.thread_activity_updated, numNewReplies);
            /*//String imagePlurals = context.getResources().getQuantityString(R.plurals.thread_num_images, numNewImages);*/
            postText = String.format(postPlurals, 0).replace("0", "");
        }
        String threadId = "/" + board + "/" + threadNo;
        /*//String imageText = String.format(imagePlurals, numNewImages);*/
        /*//String text = String.format("%s and %s for %s/%d", postText, imageText, board, threadNo);*/
        String text = (postText + " " + threadId).trim();

        Intent threadActivityIntent = ThreadActivity.createIntent(context, board, threadNo, "");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(),
                threadActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification.Builder notifBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        Bitmap largeIcon = loadLargeIcon(context, loadedThread.posts[0]);
        if (largeIcon != null)
            {notifBuilder.setLargeIcon(largeIcon);}
        if (numNewReplies > 0)
            {notifBuilder.setNumber(numNewReplies);}
        Notification noti = buildNotification(notifBuilder);
        if (DEBUG) {Log.i(TAG, "notifyNewReplies() sending notification for " + numNewReplies + " new replies for /" + board + "/" + threadNo);}
        notificationManager.notify(notificationId, noti);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyNewReplies(android.content.Context,com.chanapps.four.activity.ChanPost,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    protected static Notification buildNotification(Notification.Builder notifBuilder) {
        com.mijack.Xlog.logStaticMethodEnter("android.app.Notification com.chanapps.four.component.NotificationComponent.buildNotification(com.chanapps.four.activity.Notification.Builder)",notifBuilder);try{if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {{com.mijack.Xlog.logStaticMethodExit("android.app.Notification com.chanapps.four.component.NotificationComponent.buildNotification(com.chanapps.four.activity.Notification.Builder)");return deprecatedBuildNotification(notifBuilder);}}
        else
            {{com.mijack.Xlog.logStaticMethodExit("android.app.Notification com.chanapps.four.component.NotificationComponent.buildNotification(com.chanapps.four.activity.Notification.Builder)");return notifBuilder.build();}}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.app.Notification com.chanapps.four.component.NotificationComponent.buildNotification(com.chanapps.four.activity.Notification.Builder)",throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected static Notification deprecatedBuildNotification(Notification.Builder notifBuilder) {
        com.mijack.Xlog.logStaticMethodEnter("android.app.Notification com.chanapps.four.component.NotificationComponent.deprecatedBuildNotification(com.chanapps.four.activity.Notification.Builder)",notifBuilder);try{com.mijack.Xlog.logStaticMethodExit("android.app.Notification com.chanapps.four.component.NotificationComponent.deprecatedBuildNotification(com.chanapps.four.activity.Notification.Builder)");return notifBuilder.getNotification();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.app.Notification com.chanapps.four.component.NotificationComponent.deprecatedBuildNotification(com.chanapps.four.activity.Notification.Builder)",throwable);throw throwable;}
    }

    public static void notifyClearCacheCancelled(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyClearCacheCancelled(android.content.Context)",context);try{if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyClearCacheCancelled(android.content.Context)");return;}}
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CLEAR_CACHE_NOTIFY_ID, makeNotification(context, context.getString(R.string.pref_clear_cache_error)));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyClearCacheCancelled(android.content.Context)",throwable);throw throwable;}
    }

    public static void notifyClearCacheResult(Context context, String result) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyClearCacheResult(android.content.Context,com.chanapps.four.activity.String)",context,result);try{if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyClearCacheResult(android.content.Context,com.chanapps.four.activity.String)");return;}}
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CLEAR_CACHE_NOTIFY_ID, makeNotification(context, result));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyClearCacheResult(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static Notification makeNotification(Context context, String contentText) {
        com.mijack.Xlog.logStaticMethodEnter("android.app.Notification com.chanapps.four.component.NotificationComponent.makeNotification(android.content.Context,com.chanapps.four.activity.String)",context,contentText);try{Intent intent = BoardActivity.createIntent(context, ChanBoard.defaultBoardCode(context), "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(context.getString(R.string.pref_clear_cache_notification_title))
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .build();
        {com.mijack.Xlog.logStaticMethodExit("android.app.Notification com.chanapps.four.component.NotificationComponent.makeNotification(android.content.Context,com.chanapps.four.activity.String)");return notification;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.app.Notification com.chanapps.four.component.NotificationComponent.makeNotification(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static void notifyDownloadScheduled(Context context, int notificationId, String board, long threadNo) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyDownloadScheduled(android.content.Context,int,com.chanapps.four.activity.String,long)",context,notificationId,board,threadNo);try{if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyDownloadScheduled(android.content.Context,int,com.chanapps.four.activity.String,long)");return;}}

        String titleText = context.getString(R.string.download_all_images_to_gallery_menu);
        String threadText = "/" + board + "/" + threadNo;
        String text = titleText + " " + threadText;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.app_icon_notification);

        notificationManager.notify(notificationId, notifBuilder.build());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyDownloadScheduled(android.content.Context,int,com.chanapps.four.activity.String,long)",throwable);throw throwable;}
    }

    public static void notifyDownloadFinished(Context context, int notificationId, DownloadImageTargetType downloadImageTargetType,
                                              ChanThread thread, String board, long threadNo, String targetFile) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyDownloadFinished(android.content.Context,int,com.chanapps.four.activity.DownloadImageTargetType,com.chanapps.four.activity.ChanThread,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)",context,notificationId,downloadImageTargetType,thread,board,threadNo,targetFile);try{if (ThreadImageDownloadService.checkIfStopped(notificationId)) {
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyDownloadFinished(android.content.Context,int,com.chanapps.four.activity.DownloadImageTargetType,com.chanapps.four.activity.ChanThread,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)");return;}
        }
        if (DEBUG) {Log.i(TAG, "notifyDownloadFinished " + downloadImageTargetType + " " + board + "/" + threadNo
                + " " + thread.posts.length + " posts, file " + targetFile);}

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyDownloadFinished(android.content.Context,int,com.chanapps.four.activity.DownloadImageTargetType,com.chanapps.four.activity.ChanThread,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)");return;}}

        /*//boolean useFriendlyIds = prefs.getBoolean(SettingsActivity.PREF_USE_FRIENDLY_IDS, true);*/
        boolean useFriendlyIds = false;
        if (thread != null)
            {thread.useFriendlyIds = useFriendlyIds;}

        Intent threadActivityIntent = null;
        switch(downloadImageTargetType) {
            case TO_BOARD:
            case TO_ZIP:
                threadActivityIntent = ThreadActivity.createIntent(context, board, threadNo, "");
                break;
            case TO_GALLERY:
                threadActivityIntent = GalleryViewActivity.getAlbumViewIntent(context, board, threadNo);
                break;
        }

        if (downloadImageTargetType != DownloadImageTargetType.TO_BOARD) { /*// notify except on board auto-download*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder notifBuilder = new Notification.Builder(context);
            notifBuilder.setSmallIcon(R.drawable.app_icon_notification);
            notifBuilder.setWhen(Calendar.getInstance().getTimeInMillis());
            notifBuilder.setAutoCancel(true);
            notifBuilder.setContentTitle(context.getString(R.string.download_all_images_complete));
            notifBuilder.setContentText
                    (String.format(context.getString(R.string.download_all_images_complete_detail), board, threadNo));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    threadActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK | PendingIntent.FLAG_UPDATE_CURRENT);
            notifBuilder.setContentIntent(pendingIntent);
            Notification noti = buildNotification(notifBuilder);
            notificationManager.notify(notificationId, noti);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyDownloadFinished(android.content.Context,int,com.chanapps.four.activity.DownloadImageTargetType,com.chanapps.four.activity.ChanThread,com.chanapps.four.activity.String,long,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    private static Bitmap loadLargeIcon(Context context, ChanPost loadedThread) {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadLargeIcon(android.content.Context,com.chanapps.four.activity.ChanPost)",context,loadedThread);try{Bitmap largeIcon = null;
        try {
            try {
                largeIcon = loadIconBitmap(context, loadedThread);
                if (DEBUG) {Log.i(TAG, "loadLargeIcon() loaded notification large icon=" + largeIcon);}
            }
            catch (Exception e) {
                Log.e(TAG, "loadLargeIcon() exception loading thumbnail for notification for thread=" + loadedThread.no, e);
                if (DEBUG) {Log.i(TAG, "using default notification large icon");}
            }
            catch (Error e) {
                Log.e(TAG, "loadLargeIcon() error loading thumbnail for notification for thread=" + loadedThread.no, e);
                if (DEBUG) {Log.i(TAG, "using default notification large icon");}
            }
            if (largeIcon == null) {
                if (DEBUG) {Log.i(TAG, "loadLargeIcon() null bitmap, loading board default");}
                int drawableId = ChanBoard.getRandomImageResourceId(loadedThread.board, loadedThread.no);
                largeIcon = BitmapFactory.decodeResource(context.getResources(), drawableId);
            }
            if (largeIcon == null) {
                if (DEBUG) {Log.i(TAG, "loadLargeIcon() null bitmap, loading app-wide resource");}
                largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon_notification_large);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "loadLargeIcon() exception loading default thumbnail for notification for thread=" + loadedThread.no, e);
        }
        catch (Error e) {
            Log.e(TAG, "loadLargeIcon() error loading default thumbnail for notification for thread=" + loadedThread.no, e);
        }
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadLargeIcon(android.content.Context,com.chanapps.four.activity.ChanPost)");return largeIcon;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadLargeIcon(android.content.Context,com.chanapps.four.activity.ChanPost)",throwable);throw throwable;}
    }

    private static Bitmap loadIconBitmap(Context context, ChanPost loadedThread) throws Exception {
        com.mijack.Xlog.logStaticMethodEnter("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadIconBitmap(android.content.Context,com.chanapps.four.activity.ChanPost)",context,loadedThread);try{if (loadedThread == null)
            {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadIconBitmap(android.content.Context,com.chanapps.four.activity.ChanPost)");return null;}}
        String imageUrl = loadedThread.thumbnailUrl(context);
        if (DEBUG) {Log.i(TAG, "loadLargeIcon() imageUrl=" + imageUrl);}
        if (imageUrl == null)
            {{com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadIconBitmap(android.content.Context,com.chanapps.four.activity.ChanPost)");return null;}}
        File bitmapFile = ChanImageLoader.getInstance(context).getDiscCache().get(imageUrl);
        if (DEBUG) {Log.i(TAG, "loadLargeIcon() bitmapFile=" + bitmapFile);}
        Bitmap tmp = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
        int widthPx = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        int heightPx = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        int offsetX = (tmp.getWidth() - widthPx) / 2;
        int offsetY = (tmp.getHeight() - heightPx) / 2;
        {com.mijack.Xlog.logStaticMethodExit("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadIconBitmap(android.content.Context,com.chanapps.four.activity.ChanPost)");return Bitmap.createBitmap(tmp, offsetX, offsetY, widthPx, heightPx);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.graphics.Bitmap com.chanapps.four.component.NotificationComponent.loadIconBitmap(android.content.Context,com.chanapps.four.activity.ChanPost)",throwable);throw throwable;}
    }

    public static long notifyDownloadUpdated(Context context, int notificationId, String board, long threadNo,
                                             int totalNumImages, int downloadedImages, long lastUpdateTime) {
        com.mijack.Xlog.logStaticMethodEnter("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)",context,notificationId,board,threadNo,totalNumImages,downloadedImages,lastUpdateTime);try{if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)");return lastUpdateTime;}}
        if (ThreadImageDownloadService.checkIfStopped(notificationId)) {
			{com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)");return lastUpdateTime;}
		}
		long now = new Date().getTime();
		if (now - lastUpdateTime < NOTIFICATION_UPDATE_TIME) {
			{com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)");return lastUpdateTime;}
		}
		lastUpdateTime = now;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String titleText = totalNumImages > 1
                ? context.getString(R.string.download_all_images_to_gallery_menu)
                : context.getString(R.string.download_images_to_gallery_menu);
        String threadText = "/" + board + "/" + threadNo;
        String downloadText = downloadedImages + "/" + totalNumImages;
        String text = titleText + " " + threadText + " " + downloadText;

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setProgress(totalNumImages, downloadedImages, false)
                .setSmallIcon(R.drawable.app_icon_notification);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                CancelDownloadActivity.createIntent(context, notificationId, board, threadNo),
        		Intent.FLAG_ACTIVITY_NEW_TASK | PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder.setContentIntent(pendingIntent);

		notificationManager.notify(notificationId, notifBuilder.build());
        {com.mijack.Xlog.logStaticMethodExit("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)");return lastUpdateTime;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.chanapps.four.component.NotificationComponent.notifyDownloadUpdated(android.content.Context,int,com.chanapps.four.activity.String,long,int,int,long)",throwable);throw throwable;}
	}

    public static void notifyDownloadError(Context context, int notificationId, ChanThread thread) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyDownloadError(android.content.Context,int,com.chanapps.four.activity.ChanThread)",context,notificationId,thread);try{if (thread == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyDownloadError(android.content.Context,int,com.chanapps.four.activity.ChanThread)");return;}}
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyDownloadError(android.content.Context,int,com.chanapps.four.activity.ChanThread)");return;}}

        /*//boolean useFriendlyIds = prefs.getBoolean(SettingsActivity.PREF_USE_FRIENDLY_IDS, true);*/
        boolean useFriendlyIds = false;
        if (thread != null)
            {thread.useFriendlyIds = useFriendlyIds;}

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notifBuilder = new Notification.Builder(context);
        notifBuilder.setWhen(Calendar.getInstance().getTimeInMillis());
        notifBuilder.setAutoCancel(true);
        notifBuilder.setContentTitle(context.getString(R.string.thread_image_download_error));
        notifBuilder.setContentText(thread.board + "/" + thread.no);
        notifBuilder.setSmallIcon(R.drawable.app_icon_notification);

        Intent threadActivityIntent = ThreadActivity.createIntent(context, thread.board, thread.no, "");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                threadActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        notifBuilder.setContentIntent(pendingIntent);

        Notification noti = buildNotification(notifBuilder);
        notificationManager.notify(notificationId, noti);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyDownloadError(android.content.Context,int,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

    private static final int FAVORITES_NOTIFICATION_TOKEN = 0x13;

    public static void notifyNewThreads(final Context context, final String boardCode, final int numNewThreads,
                                        final ChanThread newThread) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.NotificationComponent.notifyNewThreads(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.ChanThread)",context,boardCode,numNewThreads,newThread);try{if (DEBUG) {Log.i(TAG, "notifyNewThreads() /" + boardCode + "/ numThreads=" + numNewThreads);}
        /*
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true))
            return;
        if (boardCode == null || boardCode.isEmpty())
            return;
        if (numNewThreads <= 0)
            return;

        ChanIdentifiedActivity activity = NetworkProfileManager.instance().getActivity();
        ChanActivityId aid = NetworkProfileManager.instance().getActivityId();
        // limit notification when on watchlist or active thread
        if (activity != null && activity.getChanHandler() != null && aid != null) {
            if (boardCode.equals(aid.boardCode)) {
                if (DEBUG) Log.i(TAG, "notifyNewThreads() /" + boardCode + "/ user on board, skipping notification");
                return;
            }
        }

        int notificationId = boardCode.hashCode() + FAVORITES_NOTIFICATION_TOKEN;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        String postPlurals = context.getResources().getQuantityString(R.plurals.board_activity_updated, numNewThreads);
        String text = String.format(postPlurals, boardCode);

        ChanPost iconThread;
        if (newThread == null)
            iconThread = null;
        else if (newThread.posts == null || newThread.posts.length == 0 || newThread.posts[0] == null)
            iconThread = newThread;
        else
            iconThread = newThread.posts[0];
        Bitmap largeIcon = loadLargeIcon(context, iconThread);

        Intent boardActivityIntent = BoardActivity.createIntent(context, boardCode, "");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(),
                boardActivityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification.Builder notifBuilder = new Notification.Builder(context)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setNumber(numNewThreads)
                ;
        Notification noti = notifBuilder.getNotification();
        if (DEBUG) Log.i(TAG, "notifyNewThreads() sending notification for " + numNewThreads
                + " new threads for /" + boardCode + "/");
        notificationManager.notify(notificationId, noti);
        */com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.NotificationComponent.notifyNewThreads(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.ChanThread)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.NotificationComponent.notifyNewThreads(android.content.Context,com.chanapps.four.activity.String,int,com.chanapps.four.activity.ChanThread)",throwable);throw throwable;}
    }

}
