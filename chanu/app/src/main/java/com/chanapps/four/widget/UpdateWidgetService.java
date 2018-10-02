package com.chanapps.four.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/13/13
 * Time: 10:54 PM
 */
public class UpdateWidgetService extends RemoteViewsService {

    public static final String TAG = UpdateWidgetService.class.getSimpleName();
    private static final boolean DEBUG = false;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        com.mijack.Xlog.logMethodEnter("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this,intent);try{Log.d(TAG, "onGetViewFactory");
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "onGetViewFactory() invalid app widget id passed, returning null");
            {com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this);return null;}
        }
        WidgetConf widgetConf = WidgetProviderUtils.loadWidgetConf(this, appWidgetId);
        if (widgetConf == null)
            {widgetConf = new WidgetConf(appWidgetId);} /*// new widget or no config;*/
        if (WidgetConstants.WIDGET_TYPE_COVER_FLOW.equals(widgetConf.widgetType)) {
            if (DEBUG) {Log.i(TAG, "onGetViewFactory() returning StackRemoteViewsFactory");}
            {com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this);return new StackRemoteViewsFactory(getApplicationContext(), intent);}
        }
        else if (WidgetConstants.WIDGET_TYPE_COVER_FLOW_CARD.equals(widgetConf.widgetType)) {
            if (DEBUG) {Log.i(TAG, "onGetViewFactory() returning CardStackRemoteViewsFactory");}
            {com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this);return new CardStackRemoteViewsFactory(getApplicationContext(), intent);}
        }
        else {
            if (DEBUG) {Log.i(TAG, "onGetViewFactory() non-matching card flow type=" + widgetConf.widgetType + " returning null");}
            {com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this);return null;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("RemoteViewsFactory com.chanapps.four.widget.UpdateWidgetService.onGetViewFactory(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.UpdateWidgetService.onStartCommand(android.content.Intent,int,int)",this,intent,flags,startId);try{int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "onStartCommand() invalid app widget id passed, service terminating");
        } else {
            WidgetConf widgetConf = WidgetProviderUtils.loadWidgetConf(this, appWidgetId);
            if (widgetConf == null)
                {widgetConf = new WidgetConf(appWidgetId);} /*// new widget or no config;*/
            if (DEBUG) {Log.i(TAG, "onStartCommand() id=" + appWidgetId + " /" + widgetConf.boardCode + "/");}
            (new WidgetUpdateTask(getApplicationContext(), widgetConf)).execute();
        }
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.UpdateWidgetService.onStartCommand(android.content.Intent,int,int)",this);return Service.START_NOT_STICKY;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.UpdateWidgetService.onStartCommand(android.content.Intent,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        com.mijack.Xlog.logMethodEnter("android.os.IBinder com.chanapps.four.widget.UpdateWidgetService.onBind(android.content.Intent)",this,intent);try{com.mijack.Xlog.logMethodExit("android.os.IBinder com.chanapps.four.widget.UpdateWidgetService.onBind(android.content.Intent)",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.IBinder com.chanapps.four.widget.UpdateWidgetService.onBind(android.content.Intent)",this,throwable);throw throwable;}
    }

}
