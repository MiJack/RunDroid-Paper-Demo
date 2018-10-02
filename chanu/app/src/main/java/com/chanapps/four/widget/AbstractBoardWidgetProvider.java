package com.chanapps.four.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.GlobalAlarmReceiver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: mpop
 * Date: 11/22/12
 * Time: 11:30 PM
 */
public abstract class AbstractBoardWidgetProvider extends AppWidgetProvider {

    public static final String TAG = AbstractBoardWidgetProvider.class.getSimpleName();

    public static final String WIDGET_CACHE_DIR = "widgets";

    private static final boolean DEBUG = false;

    protected abstract String getWidgetType();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onUpdate(android.content.Context,android.appwidget.AppWidgetManager,[int)",this,context,appWidgetManager,appWidgetIds);try{for (int i = 0; i < appWidgetIds.length; i++)
            {WidgetProviderUtils.update(context, appWidgetIds[i], getWidgetType());}
        super.onUpdate(context, appWidgetManager, appWidgetIds);com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onUpdate(android.content.Context,android.appwidget.AppWidgetManager,[int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onUpdate(android.content.Context,android.appwidget.AppWidgetManager,[int)",this,throwable);throw throwable;}
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDeleted(android.content.Context,[int)",this,context,appWidgetIds);try{if (DEBUG) {Log.i(TAG, "deleting widgets: " + Arrays.toString(appWidgetIds));}
        Set<Integer> widgetsToDelete = new HashSet<Integer>();
        for (int i = 0; i < appWidgetIds.length; i++)
            {widgetsToDelete.add(appWidgetIds[i]);}
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> widgetBoards = prefs.getStringSet(SettingsActivity.PREF_WIDGET_BOARDS, new HashSet<String>());
        Set<String> newWidgetBoards = new HashSet<String>();
        for (String widgetBoard : widgetBoards) {
            String[] components = widgetBoard.split(WidgetConf.DELIM);
            int widgetId = Integer.valueOf(components[0]);
            if (!widgetsToDelete.contains(widgetId))
                {newWidgetBoards.add(widgetBoard);}
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SettingsActivity.PREF_WIDGET_BOARDS, newWidgetBoards);
        editor.commit();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDeleted(android.content.Context,[int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDeleted(android.content.Context,[int)",this,throwable);throw throwable;}
    }

    @Override
    public void onEnabled(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onEnabled(android.content.Context)",this,context);try{/*// handled by config task*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onEnabled(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onEnabled(android.content.Context)",this,throwable);throw throwable;}
    }

    @Override
    public void onDisabled(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDisabled(android.content.Context)",this,context);try{if (DEBUG) {Log.i(TAG, "disabled all widgets");}
        GlobalAlarmReceiver.scheduleGlobalAlarm(context); /*// will deschedule if appropriate*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDisabled(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.AbstractBoardWidgetProvider.onDisabled(android.content.Context)",this,throwable);throw throwable;}
    }

}