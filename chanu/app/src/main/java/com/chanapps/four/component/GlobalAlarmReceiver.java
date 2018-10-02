package com.chanapps.four.component;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.data.ChanBoard;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.service.CleanUpService;
import com.chanapps.four.service.FetchChanDataService;
import com.chanapps.four.service.NetworkProfileManager;
import com.chanapps.four.service.profile.NetworkProfile;
import com.chanapps.four.widget.WidgetProviderUtils;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/13/13
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GlobalAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = GlobalAlarmReceiver.class.getSimpleName();

    public static final String GLOBAL_ALARM_RECEIVER_SCHEDULE_ACTION = "com.chanapps.four.component.GlobalAlarmReceiver.schedule";

    private static final long WIDGET_UPDATE_INTERVAL_MS = AlarmManager.INTERVAL_HOUR; /*// FIXME should be configurable*/
    /*//private static final long WIDGET_UPDATE_INTERVAL_MS = 60000; // 60 sec, just for testing*/
    private static final boolean DEBUG = false;

    @Override
    public void onReceive(final Context context, Intent intent) { com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this,context,intent);try{/*// when first boot up, default and then schedule for refresh*/
        String action = intent.getAction();
        if (DEBUG) {Log.i(TAG, "Received action: " + action);}
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            scheduleGlobalAlarm(context);
        }
        else if (GLOBAL_ALARM_RECEIVER_SCHEDULE_ACTION.equals(action)) {
            /* use this when you screw up widgets and need to reset
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.remove(SettingsActivity.PREF_WIDGET_BOARDS);
            editor.commit();
            */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver$1.run()",this);try{CleanUpService.startService(context);
                    updateAndFetch(context);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver$1.run()",this,throwable);throw throwable;}
                }
            }).start();
        } else {
            Log.e(TAG, "Received unknown action: " + action);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.onReceive(android.content.Context,android.app.PendingIntent)",this,throwable);throw throwable;}
    }

    private static void updateAndFetch(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.updateAndFetch(android.content.Context)",context);try{/*//if (!ChanBoard.hasWatchlist(context) && !WidgetProviderUtils.hasWidgets(context)) {*/
        /*//    if (DEBUG) Log.i(TAG, "updateAndFetch no watchlist or widgets, cancelling global alarm");*/
        /*//    cancelGlobalAlarm(context);*/
        /*//    return;*/
        /*//}*/
        WidgetProviderUtils.updateAll(context);
        NetworkProfileManager.NetworkBroadcastReceiver.checkNetwork(context); /*// always check since state may have changed*/
        NetworkProfile profile = NetworkProfileManager.instance().getCurrentProfile();
        boolean backgroundDataOnMobile = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_BACKGROUND_DATA_ON_MOBILE, false);
        if (DEBUG) {Log.i(TAG, "updateAndFetch network profile=" + profile + " health=" + profile.getConnectionHealth());}
        if (profile.getConnectionHealth() == NetworkProfile.Health.NO_CONNECTION ||
                profile.getConnectionHealth() == NetworkProfile.Health.BAD) {
            if (DEBUG) {Log.i(TAG, "updateAndFetch no connection, skipping fetch");}
        }
        else if (profile.getConnectionType() == NetworkProfile.Type.MOBILE && !backgroundDataOnMobile) {
            if (DEBUG) {Log.i(TAG, "updateAndFetch background data is set to disabled on mobile, skipping fetch");}
        }
        else {
            if (DEBUG) {Log.i(TAG, "updateAndFetch fetching watchlist threads and widget boards");}
            fetchWatchlistThreads(context);
            WidgetProviderUtils.fetchAllWidgets(context);
        }
        /*
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_AUTOMATICALLY_MANAGE_WATCHLIST, true))
            try {
                ChanFileStorage.cleanDeadWatchedThreads(context);
            }
            catch (IOException e) {
                Log.e(TAG, "Exception clearing watchlist", e);
            }
        */com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.updateAndFetch(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.updateAndFetch(android.content.Context)",throwable);throw throwable;}
    }

    public static void fetchWatchlistThreads(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.fetchWatchlistThreads(android.content.Context)",context);try{ChanBoard board = ChanFileStorage.loadBoardData(context, ChanBoard.WATCHLIST_BOARD_CODE);
        if (board == null || board.threads == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.fetchWatchlistThreads(android.content.Context)");return;}}
        for (ChanPost thread : board.threads) {
            FetchChanDataService.scheduleThreadFetch(context, thread.board, thread.no, false, true);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.fetchWatchlistThreads(android.content.Context)",throwable);throw throwable;}
    }

    public static void fetchFavoriteBoards(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.fetchFavoriteBoards(android.content.Context)",context);try{ChanBoard board = ChanFileStorage.loadBoardData(context, ChanBoard.FAVORITES_BOARD_CODE);
        if (!board.hasData())
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.fetchFavoriteBoards(android.content.Context)");return;}}
        for (ChanPost thread : board.threads) {
            FetchChanDataService.scheduleBoardFetch(context, thread.board, false, true);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.fetchFavoriteBoards(android.content.Context)",throwable);throw throwable;}
    }

    public static void scheduleGlobalAlarm(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.scheduleGlobalAlarm(android.content.Context)",context);try{if (DEBUG) {Log.i(TAG, "scheduleGlobalAlarm interval ms=" + WIDGET_UPDATE_INTERVAL_MS);}
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long currentElapsed = SystemClock.elapsedRealtime();
        long scheduleAt = currentElapsed + WIDGET_UPDATE_INTERVAL_MS / 2; /*// at least wait a while before scheduling*/
        PendingIntent pendingIntent = getPendingIntentForGlobalAlarm(context);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, scheduleAt, WIDGET_UPDATE_INTERVAL_MS, pendingIntent);
        if (DEBUG)
            {Log.i(TAG, "scheduleGlobalAlarm currentElapsed=" + currentElapsed
                    + " scheduled GlobalAlarmReceiver"
                    + " scheduleAt=" + scheduleAt
                    + " repeating every delta=" + WIDGET_UPDATE_INTERVAL_MS + "ms");}com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.scheduleGlobalAlarm(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.scheduleGlobalAlarm(android.content.Context)",throwable);throw throwable;}
    }

    private static PendingIntent getPendingIntentForGlobalAlarm(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("android.app.PendingIntent com.chanapps.four.component.GlobalAlarmReceiver.getPendingIntentForGlobalAlarm(android.content.Context)",context);try{Intent intent = new Intent(context, GlobalAlarmReceiver.class);
        intent.setAction(GLOBAL_ALARM_RECEIVER_SCHEDULE_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        {com.mijack.Xlog.logStaticMethodExit("android.app.PendingIntent com.chanapps.four.component.GlobalAlarmReceiver.getPendingIntentForGlobalAlarm(android.content.Context)");return pendingIntent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.app.PendingIntent com.chanapps.four.component.GlobalAlarmReceiver.getPendingIntentForGlobalAlarm(android.content.Context)",throwable);throw throwable;}
    }

    public static void cancelGlobalAlarm(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.GlobalAlarmReceiver.cancelGlobalAlarm(android.content.Context)",context);try{if (DEBUG) {Log.i(TAG, "cancelGlobalAlarm");}
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntentForGlobalAlarm(context);
        alarmManager.cancel(pendingIntent);
        if (DEBUG) {Log.i(TAG, "Canceled alarms for UpdateWidgetService");}com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.GlobalAlarmReceiver.cancelGlobalAlarm(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.GlobalAlarmReceiver.cancelGlobalAlarm(android.content.Context)",throwable);throw throwable;}
    }

}
