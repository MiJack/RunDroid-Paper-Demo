package com.chanapps.four.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.GlobalAlarmReceiver;
import com.chanapps.four.loader.ChanImageLoader;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/15/13
 * Time: 11:07 PM
 */
public class WidgetConfigureActivity extends AbstractWidgetConfigureActivity {

    public static final String TAG = WidgetConfigureActivity.class.getSimpleName();
    private static final boolean DEBUG = false;

    @Override
    protected int getContentViewLayout() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.widget.WidgetConfigureActivity.getContentViewLayout()",this);try{com.mijack.Xlog.logMethodExit("int com.chanapps.four.widget.WidgetConfigureActivity.getContentViewLayout()",this);return R.layout.widget_configure_layout;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.widget.WidgetConfigureActivity.getContentViewLayout()",this,throwable);throw throwable;}
    }

    @Override
    protected String getWidgetType() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.widget.WidgetConfigureActivity.getWidgetType()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.widget.WidgetConfigureActivity.getWidgetType()",this);return WidgetConstants.WIDGET_TYPE_BOARD;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.widget.WidgetConfigureActivity.getWidgetType()",this,throwable);throw throwable;}
    }

    @Override
    protected void setBoardImages() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity.setBoardImages()",this);try{final Context context = getApplicationContext();
        final String boardCode = widgetConf.boardCode;
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity$1.run()",this);try{final int[] imageIds = {R.id.image_left, R.id.image_center, R.id.image_right};
                final String[] urls = boardThreadUrls(context, boardCode, imageIds.length);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity$1$1.run()",this);try{for (int i = 0; i < imageIds.length; i++) {
                            final int imageResourceId = imageIds[i];
                            final ImageView iv = (ImageView) findViewById(imageResourceId);
                            iv.setImageBitmap(null);
                            if (DEBUG) {Log.i(TAG, "Calling displayImage i=" + i + " url=" + urls[i]);}
                            ChanImageLoader.getInstance(context).displayImage(urls[i], iv);
                        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity$1$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity$1$1.run()",this,throwable);throw throwable;}
                    }
                });com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity$1.run()",this,throwable);throw throwable;}
            }
        }).start();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity.setBoardImages()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity.setBoardImages()",this,throwable);throw throwable;}
    }

    @Override
    protected void addDoneClickHandler() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity.addDoneClickHandler()",this);try{Button doneButton = (Button) findViewById(R.id.done);
        if (doneButton == null)
            {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity.addDoneClickHandler()",this);return;}}
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity$2.onClick(android.view.View)",this,v);try{if (DEBUG)
                    {Log.i(TAG, "Configured widget=" + appWidgetId + " configuring for board=" + widgetConf.boardCode);}
                WidgetProviderUtils.storeWidgetConf(WidgetConfigureActivity.this, widgetConf);
                Intent intent = new Intent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent.putExtra(WidgetProviderUtils.WIDGET_PROVIDER_UTILS, WidgetConfigureActivity.this.getWidgetType());
                WidgetConfigureActivity.this.setResult(Activity.RESULT_OK, intent);
                Intent updateWidget = new Intent(WidgetConfigureActivity.this, BoardWidgetProvider.class);
                updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = {appWidgetId};
                updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                updateWidget.putExtra(WidgetProviderUtils.WIDGET_PROVIDER_UTILS, WidgetConfigureActivity.this.getWidgetType());
                WidgetConfigureActivity.this.sendBroadcast(updateWidget);
                WidgetConfigureActivity.this.finish();
                GlobalAlarmReceiver.scheduleGlobalAlarm(getApplicationContext()); /*// will deschedule if appropriate*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity.addDoneClickHandler()",this,throwable);throw throwable;}

    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity.onStart()",this);try{super.onStart();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.widget.WidgetConfigureActivity.onStop()",this);try{super.onStop();com.mijack.Xlog.logMethodExit("void com.chanapps.four.widget.WidgetConfigureActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.widget.WidgetConfigureActivity.onStop()",this,throwable);throw throwable;}
    }

}
