package com.chanapps.four.component;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 3/31/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class CheckableFrameLayout extends FrameLayout implements Checkable {

    private static final String TAG = CheckableFrameLayout.class.getSimpleName();
    private static final boolean DEBUG = false;

    boolean isChecked = false;

    public CheckableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableFrameLayout.onFinishInflate()",this);try{super.onFinishInflate();
        setBackground(isSelected());com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.onFinishInflate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableFrameLayout.onFinishInflate()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isChecked() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.CheckableFrameLayout.isChecked()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.CheckableFrameLayout.isChecked()",this);return isChecked;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.CheckableFrameLayout.isChecked()",this,throwable);throw throwable;}
    }

    @Override
    public void setChecked(boolean checked) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableFrameLayout.setChecked(boolean)",this,checked);try{isChecked = checked;
        setBackground(isSelected());com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.setChecked(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableFrameLayout.setChecked(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void toggle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableFrameLayout.toggle()",this);try{isChecked = !isChecked;
        setBackground(isSelected());com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.toggle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableFrameLayout.toggle()",this,throwable);throw throwable;}
    }

    protected void setBackground(boolean highlight) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableFrameLayout.setBackground(boolean)",this,highlight);try{if (DEBUG) {Log.i(TAG, "setBackground() highlight=" + highlight);}
        if (highlight) {
            for (int i = 0; i < getChildCount(); i++) {
                if (i > 0)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.setBackground(boolean)",this);return;}}
                View v = getChildAt(i);
                if (v != null && v instanceof ViewGroup && ((ViewGroup)v).getChildCount() > 0) {
                    v = ((ViewGroup)v).getChildAt(0);
                    if (v != null && v instanceof ViewGroup && ((ViewGroup)v).getChildCount() > 1) {
                        v = ((ViewGroup)v).getChildAt(1);
                        if (v != null)
                            {v.setVisibility(View.VISIBLE);}
                    }
                }
            }
            /*//setForeground(getResources().getDrawable(R.color.PaletteSelectorFourth));*/
        }
        else {
            /*//setForeground(null);*/
            for (int i = 0; i < getChildCount(); i++) {
                if (i > 0)
                    {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.setBackground(boolean)",this);return;}}
                View v = getChildAt(i);
                if (v != null && v instanceof ViewGroup && ((ViewGroup)v).getChildCount() > 0) {
                    v = ((ViewGroup)v).getChildAt(0);
                    if (v != null && v instanceof ViewGroup && ((ViewGroup)v).getChildCount() > 1) {
                        v = ((ViewGroup)v).getChildAt(1);
                        if (v != null)
                            {v.setVisibility(View.GONE);}
                    }
                }
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableFrameLayout.setBackground(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void setSelected(boolean selected) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableFrameLayout.setSelected(boolean)",this,selected);try{super.setSelected(selected);
        setBackground(selected);
        if (DEBUG) {Log.i(TAG, "setSelected selected=" + selected);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableFrameLayout.setSelected(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableFrameLayout.setSelected(boolean)",this,throwable);throw throwable;}
    }

}