package com.chanapps.four.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.ThreadActivity;
import com.chanapps.four.data.ChanPost;
import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 8/26/13
* Time: 11:18 AM
* To change this template use File | Settings | File Templates.
*/
public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = StackRemoteViewsFactory.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Context context;
    private int appWidgetId;
    private WidgetConf widgetConf;
    private List<ChanPost> threads = new ArrayList<ChanPost>();

    public StackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        initWidget();
    }

    private void initWidget() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.StackRemoteViewsFactory.initWidget()",this);try{widgetConf = WidgetProviderUtils.loadWidgetConf(context, appWidgetId);
        if (widgetConf == null)
            {widgetConf = new WidgetConf(appWidgetId);} /*// new widget or no config;*/
        threads = WidgetProviderUtils.viableThreads(context, widgetConf.boardCode, BoardCoverFlowWidgetProvider.MAX_THREADS);
        if (DEBUG) {Log.i(TAG, "initWidget() threadCount=" + threads.size());}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.StackRemoteViewsFactory.initWidget()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.StackRemoteViewsFactory.initWidget()",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.StackRemoteViewsFactory.onCreate()",this);try{if (DEBUG) {Log.i(TAG, "onCreate() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.StackRemoteViewsFactory.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.StackRemoteViewsFactory.onCreate()",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.StackRemoteViewsFactory.onDestroy()",this);try{if (DEBUG) {Log.i(TAG, "onDestroy() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.StackRemoteViewsFactory.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.StackRemoteViewsFactory.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    public int getCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.StackRemoteViewsFactory.getCount()",this);try{if (DEBUG) {Log.i(TAG, "getCount() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/ count=" + threads.size());}
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.StackRemoteViewsFactory.getCount()",this);return threads.size();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.StackRemoteViewsFactory.getCount()",this,throwable);throw throwable;}
    }

    @Override
    public RemoteViews getViewAt(int position) {
        com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getViewAt(int)",this,position);try{if (DEBUG) {Log.i(TAG, "getViewAt() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/ pos=" + position);}
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_coverflow_item);
        int i = position;
        if (i >= 0 && i< threads.size() && threads.get(i) != null) {
            ChanPost thread = threads.get(i);
            String url = thread.thumbnailUrl(context);
            WidgetProviderUtils.safeSetRemoteViewThumbnail(context, widgetConf, views, R.id.image_coverflow_item, url, position);
            Bundle extras = new Bundle();
            extras.putLong(ThreadActivity.THREAD_NO, thread.no);
            Intent intent = new Intent();
            intent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.image_coverflow_item, intent);
        }
        {com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getViewAt(int)",this);return views;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getViewAt(int)",this,throwable);throw throwable;}
    }

    @Override
    public RemoteViews getLoadingView() {
        com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getLoadingView()",this);try{com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getLoadingView()",this);return null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.chanapps.four.widget.StackRemoteViewsFactory.getLoadingView()",this,throwable);throw throwable;}
    }

    @Override
    public int getViewTypeCount() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.StackRemoteViewsFactory.getViewTypeCount()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.StackRemoteViewsFactory.getViewTypeCount()",this);return 1;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.StackRemoteViewsFactory.getViewTypeCount()",this,throwable);throw throwable;}
    }

    @Override
    public long getItemId(int position) {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.widget.StackRemoteViewsFactory.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.chanapps.four.widget.StackRemoteViewsFactory.getItemId(int)",this);return position;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.widget.StackRemoteViewsFactory.getItemId(int)",this,throwable);throw throwable;}
    }

    @Override
    public boolean hasStableIds() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.widget.StackRemoteViewsFactory.hasStableIds()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.widget.StackRemoteViewsFactory.hasStableIds()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.widget.StackRemoteViewsFactory.hasStableIds()",this,throwable);throw throwable;}
    }

    @Override
    public void onDataSetChanged() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.StackRemoteViewsFactory.onDataSetChanged()",this);try{if (DEBUG) {Log.i(TAG, "onDataSetChanged() id=" + widgetConf.appWidgetId + " /" + widgetConf.boardCode + "/");}
        initWidget();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.StackRemoteViewsFactory.onDataSetChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.StackRemoteViewsFactory.onDataSetChanged()",this,throwable);throw throwable;}
    }

}
