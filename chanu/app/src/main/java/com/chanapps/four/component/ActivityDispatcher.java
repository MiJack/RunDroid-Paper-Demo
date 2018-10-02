package com.chanapps.four.component;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import com.chanapps.four.activity.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 12/18/12
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityDispatcher {

    private static final String TAG = ActivityDispatcher.class.getSimpleName();
    private static final String LAST_ACTIVITY = "ActivityDispatcherLastActivity";
    private static final boolean DEBUG = false;
    public static final String IGNORE_DISPATCH = "ignoreDispatch";

    public static void store(ChanIdentifiedActivity activity) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.ActivityDispatcher.store(com.chanapps.four.activity.ChanIdentifiedActivity)",activity);try{ChanActivityId activityId = activity.getChanActivityId();
        String serialized = activityId.serialize();
        if (serialized == null || serialized.isEmpty()) {
            if (DEBUG) {Log.e(TAG, "store() serialize empty");}
            {com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.ActivityDispatcher.store(com.chanapps.four.activity.ChanIdentifiedActivity)");return;}
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext()).edit();
        editor.putString(LAST_ACTIVITY, serialized);
        editor.commit();
        if (DEBUG) {Log.i(TAG, "store() stored " + activityId);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.ActivityDispatcher.store(com.chanapps.four.activity.ChanIdentifiedActivity)",throwable);throw throwable;}
    }

    /*
    public static boolean isDispatchable(ChanIdentifiedActivity activity) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity.getBaseContext())
                .getString(LAST_ACTIVITY, null)
                != null;
    }
    */

    /*
    public static void dispatch(final ChanIdentifiedActivity activity) {
        if (activity == null || !(activity instanceof Activity)) {
            Log.e(TAG, "dispatch() prematurely terminating since called with invalid activity=" + activity);
            return;
        }

        //boolean startWithFavorites = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext())
        //        .getBoolean(SettingsActivity.PREF_START_WITH_FAVORITES, true);
        //if (startWithFavorites) {

        if (DEBUG) Log.i(TAG, "dispatch() startWithFavorites=true, starting async dispatch");
            asyncDispatch((Activity)activity);

        //}
        //else {
        //    if (DEBUG) Log.i(TAG, "dispatch() startWithFavorites=false, starting dispatch process immediately");
        //    syncDispatch(activity);
        //}
    }

    protected static void asyncDispatch(final Activity activity) { // assume only called if startWithFavorites is true
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChanBoard board = ChanFileStorage.loadBoardData(activity, ChanBoard.FAVORITES_BOARD_CODE);
                if (board != null && board.hasData()) {
                    if (DEBUG) Log.i(TAG, "asyncDispatch found non-empty favorites board, dispatching to favorites");
                    dispatchToBoard(activity, ChanBoard.FAVORITES_BOARD_CODE);
                }
                else {
                    if (DEBUG) Log.i(TAG, "asyncDispatch found empty favorites board, dispatching to allBoards");
                    dispatchToBoard(activity, ChanBoard.ALL_BOARDS_BOARD_CODE);
                }
            }
        }).start();
    }
    */
    /*
    protected static boolean syncDispatch(final ChanIdentifiedActivity activity) {
        Intent intent = ((Activity)activity).getIntent();
        if (intent.hasExtra(IGNORE_DISPATCH) && intent.getBooleanExtra(IGNORE_DISPATCH, false)) {
            if (DEBUG) Log.i(TAG, "dispatch ignored by intent");
            return false;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        String serialized = prefs.getString(LAST_ACTIVITY, null);
        if (serialized == null || serialized.isEmpty()) {
            if (DEBUG) Log.e(TAG, "dispatch() deserialize empty");
            return false;
        }

        ChanActivityId activityId = ChanActivityId.deserialize(serialized);
        if (activityId == null) {
            if (DEBUG) Log.e(TAG, "dispatch() deserialize null");
            return false;
        }

        if (DEBUG) Log.i(TAG, "dispatch() deserialized " + activityId);
        Intent newIntent = activityId.createIntent((Activity)activity);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (DEBUG) Log.i(TAG, "dispatch() created intent=" + newIntent
                + " boardCode=" + newIntent.getStringExtra("boardCode"));
        ((Activity)activity).startActivity(newIntent);
        ((Activity)activity).finish();
        return true;
    }

    protected static void dispatchToBoard(Activity activity, String boardCode) {
        Intent newIntent = BoardActivity.createIntent(activity, boardCode, "");
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (DEBUG) Log.i(TAG, "dispatchToBoard() /" + boardCode + "/");
        activity.startActivity(newIntent);
        activity.finish();
    }
    */

    public static void launchUrlInBrowser(Context context, String url) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.ActivityDispatcher.launchUrlInBrowser(android.content.Context,com.chanapps.four.activity.String)",context,url);try{Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities != null && activities.size() > 0;
        boolean showChooser = isIntentSafe && activities != null && activities.size() > 1;
        if (showChooser) {
            Intent chooser = Intent.createChooser(intent, context.getString(R.string.dialog_choose));
            context.startActivity(chooser);
        }
        else if (isIntentSafe) {
            context.startActivity(intent);
        }
        else {
            Log.e(TAG, "error: no application found for url=[" + url + "]");
        }com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.ActivityDispatcher.launchUrlInBrowser(android.content.Context,com.chanapps.four.activity.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.ActivityDispatcher.launchUrlInBrowser(android.content.Context,com.chanapps.four.activity.String)",throwable);throw throwable;}
    }

    public static void exitApplication(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.ActivityDispatcher.exitApplication(android.content.Context)",context);try{Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.ActivityDispatcher.exitApplication(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.ActivityDispatcher.exitApplication(android.content.Context)",throwable);throw throwable;}
    }

    public static boolean onUIThread() {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.component.ActivityDispatcher.onUIThread()");try{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ActivityDispatcher.onUIThread()");return Looper.getMainLooper().equals(Looper.myLooper());}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.component.ActivityDispatcher.onUIThread()",throwable);throw throwable;}
    }

    private static final int MAX_RUNNING_TASKS = 2;

    public static Pair<Integer, ActivityManager.RunningTaskInfo> safeGetRunningTasks(final Context context) {
        com.mijack.Xlog.logStaticMethodEnter("android.util.Pair com.chanapps.four.component.ActivityDispatcher.safeGetRunningTasks(android.content.Context)",context);try{try {
            ActivityManager manager = (ActivityManager)context.getSystemService( Activity.ACTIVITY_SERVICE );
            List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(MAX_RUNNING_TASKS);
            int numTasks = tasks == null ? 0 : tasks.size();
            ActivityManager.RunningTaskInfo topTask = (tasks == null || tasks.size() == 0) ? null : tasks.get(0);
            {com.mijack.Xlog.logStaticMethodExit("android.util.Pair com.chanapps.four.component.ActivityDispatcher.safeGetRunningTasks(android.content.Context)");return new Pair(numTasks, topTask);}
        }
        catch (Exception e) {
            Log.e(TAG, "Exception getting running task", e);
            {com.mijack.Xlog.logStaticMethodExit("android.util.Pair com.chanapps.four.component.ActivityDispatcher.safeGetRunningTasks(android.content.Context)");return new Pair(0, null);}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.util.Pair com.chanapps.four.component.ActivityDispatcher.safeGetRunningTasks(android.content.Context)",throwable);throw throwable;}
    }

    public static boolean safeGetIsChanForegroundActivity(final Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.component.ActivityDispatcher.safeGetIsChanForegroundActivity(android.content.Context)",context);try{boolean isFg = true;
        Pair<Integer, ActivityManager.RunningTaskInfo> p = ActivityDispatcher.safeGetRunningTasks(context);
        int numTasks = p.first;
        ActivityManager.RunningTaskInfo task = p.second;
        if (task != null && task.topActivity != null) {
            if (DEBUG) {Log.d(TAG, "foreground activity: " + task.topActivity.getClass().getSimpleName());}
            ComponentName componentInfo = task.topActivity;
            isFg = componentInfo != null && componentInfo.getPackageName().startsWith("com.chanapps");
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ActivityDispatcher.safeGetIsChanForegroundActivity(android.content.Context)");return isFg;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.component.ActivityDispatcher.safeGetIsChanForegroundActivity(android.content.Context)",throwable);throw throwable;}
    }

}
