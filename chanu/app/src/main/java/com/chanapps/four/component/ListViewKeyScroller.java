package com.chanapps.four.component;

import android.view.KeyEvent;
import android.widget.AbsListView;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 9/13/13
* Time: 8:42 PM
* To change this template use File | Settings | File Templates.
*/
public class ListViewKeyScroller {

    protected static final int PAGE_SCROLL_OFFSET_PX = 200;
    protected static final int PAGE_SCROLL_DURATION_MS = 1000;

    public static boolean dispatchKeyEvent(KeyEvent event, AbsListView absListView) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)",event,absListView);try{if (absListView == null)
            {{com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)");return false;}}
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    scrollToPrevious(absListView);
                    {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)");return true;}
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    scrollToNext(absListView);
                    {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)");return true;}
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP
                && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)");return true;}
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.chanapps.four.component.ListViewKeyScroller.dispatchKeyEvent(android.view.KeyEvent,android.widget.AbsListView)",throwable);throw throwable;}
    }

    private static void scrollToPrevious(AbsListView absListView) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.ListViewKeyScroller.scrollToPrevious(android.widget.AbsListView)",absListView);try{if (absListView == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.ListViewKeyScroller.scrollToPrevious(android.widget.AbsListView)");return;}}
        int height = absListView.getHeight() - PAGE_SCROLL_OFFSET_PX;
        if (height <= 0)
            {height = absListView.getHeight();}
        absListView.smoothScrollBy(-1 * height, PAGE_SCROLL_DURATION_MS);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.ListViewKeyScroller.scrollToPrevious(android.widget.AbsListView)",throwable);throw throwable;}
    }

    private static void scrollToNext(AbsListView absListView) {
        com.mijack.Xlog.logStaticMethodEnter("void com.chanapps.four.component.ListViewKeyScroller.scrollToNext(android.widget.AbsListView)",absListView);try{if (absListView == null)
            {{com.mijack.Xlog.logStaticMethodExit("void com.chanapps.four.component.ListViewKeyScroller.scrollToNext(android.widget.AbsListView)");return;}}
        int height = absListView.getHeight() - PAGE_SCROLL_OFFSET_PX;
        if (height <= 0)
            {height = absListView.getHeight();}
        absListView.smoothScrollBy(height, PAGE_SCROLL_DURATION_MS);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.chanapps.four.component.ListViewKeyScroller.scrollToNext(android.widget.AbsListView)",throwable);throw throwable;}
    }
}
