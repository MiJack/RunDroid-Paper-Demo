package com.chanapps.four.component;

import android.content.Context;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/13/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToastRunnable implements Runnable {
    Context ctx;
    String text;
    int strId;

    public ToastRunnable(Context ctx, String text) {
        this.ctx = ctx;
        this.text = text;
    }

    public ToastRunnable(Context ctx, int strId) {
        this.ctx = ctx;
        this.strId = strId;
    }

    @Override
    public void run(){
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ToastRunnable.run()",this);try{if (text == null) {
            Toast.makeText(ctx, strId, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ToastRunnable.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ToastRunnable.run()",this,throwable);throw throwable;}
    }
}

