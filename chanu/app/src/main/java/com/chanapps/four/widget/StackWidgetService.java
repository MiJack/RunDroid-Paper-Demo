package com.chanapps.four.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created with IntelliJ IDEA.
 * User: mpop
 * Date: 6/3/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        com.mijack.Xlog.logMethodEnter("RemoteViewsFactory com.chanapps.four.widget.StackWidgetService.onGetViewFactory(android.content.Intent)",this,intent);try{com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.chanapps.four.widget.StackWidgetService.onGetViewFactory(android.content.Intent)",this);return new StackRemoteViewsFactory(this.getApplicationContext(), intent);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("RemoteViewsFactory com.chanapps.four.widget.StackWidgetService.onGetViewFactory(android.content.Intent)",this,throwable);throw throwable;}
    }
}
