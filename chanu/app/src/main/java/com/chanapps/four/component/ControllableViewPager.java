package com.chanapps.four.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 10/7/13
 * Time: 10:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControllableViewPager extends ViewPager {

    private boolean enabled;

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.ControllableViewPager.onTouchEvent(android.view.MotionEvent)",this,event);try{if (this.enabled) {
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ControllableViewPager.onTouchEvent(android.view.MotionEvent)",this);return super.onTouchEvent(event);}
        }

        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ControllableViewPager.onTouchEvent(android.view.MotionEvent)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.ControllableViewPager.onTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.ControllableViewPager.onInterceptTouchEvent(android.view.MotionEvent)",this,event);try{if (this.enabled) {
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ControllableViewPager.onInterceptTouchEvent(android.view.MotionEvent)",this);return super.onInterceptTouchEvent(event);}
        }

        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.ControllableViewPager.onInterceptTouchEvent(android.view.MotionEvent)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.ControllableViewPager.onInterceptTouchEvent(android.view.MotionEvent)",this,throwable);throw throwable;}
    }

    public void setPagingEnabled(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.ControllableViewPager.setPagingEnabled(boolean)",this,enabled);try{this.enabled = enabled;com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.ControllableViewPager.setPagingEnabled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.ControllableViewPager.setPagingEnabled(boolean)",this,throwable);throw throwable;}
    }

}

