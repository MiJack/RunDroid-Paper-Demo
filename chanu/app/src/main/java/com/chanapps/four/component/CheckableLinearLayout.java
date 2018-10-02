package com.chanapps.four.component;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 3/31/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    boolean isChecked = false;
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableLinearLayout.onFinishInflate()",this);try{super.onFinishInflate();
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableLinearLayout.onFinishInflate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableLinearLayout.onFinishInflate()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isChecked() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.CheckableLinearLayout.isChecked()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.CheckableLinearLayout.isChecked()",this);return isChecked;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.CheckableLinearLayout.isChecked()",this,throwable);throw throwable;}
    }

    @Override
    public void setChecked(boolean checked) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableLinearLayout.setChecked(boolean)",this,checked);try{isChecked = checked;
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableLinearLayout.setChecked(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableLinearLayout.setChecked(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void toggle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableLinearLayout.toggle()",this);try{isChecked = !isChecked;
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableLinearLayout.toggle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableLinearLayout.toggle()",this,throwable);throw throwable;}
    }

    protected void setBackground() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableLinearLayout.setBackground()",this);try{if (isChecked) {
            /*//setBackgroundColor(R.color.PaletteChanuGreenHalfOpacity);*/
            /*
            setBackgroundColor(R.color.blue_base);
            View v = getChildAt(0);
            if (v != null)
                v.setBackgroundColor(R.color.blue_base);
                */
            ;
        }
        else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {deprecatedSetBackgroundDrawable(null);}
            else
                {setBackground(null);}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableLinearLayout.setBackground()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableLinearLayout.setBackground()",this,throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected void deprecatedSetBackgroundDrawable(Drawable d) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CheckableLinearLayout.deprecatedSetBackgroundDrawable(android.graphics.drawable.Drawable)",this,d);try{setBackgroundDrawable(d);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CheckableLinearLayout.deprecatedSetBackgroundDrawable(android.graphics.drawable.Drawable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CheckableLinearLayout.deprecatedSetBackgroundDrawable(android.graphics.drawable.Drawable)",this,throwable);throw throwable;}
    }

}