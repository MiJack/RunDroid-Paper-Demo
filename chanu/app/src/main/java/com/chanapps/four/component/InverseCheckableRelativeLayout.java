package com.chanapps.four.component;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 3/31/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.chanapps.four.activity.R;

public class InverseCheckableRelativeLayout extends RelativeLayout implements Checkable {

    boolean isChecked = false;
    int checkedBackgroundDrawable;
    int inverseBackgroundDrawable;
    int inverseForegroundDrawable;

    public InverseCheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.InverseCheckableRelativeLayout,
                0,
                0
        );
        try {
            checkedBackgroundDrawable = a.getResourceId(R.styleable.InverseCheckableRelativeLayout_checkedBackground, 0);
            inverseBackgroundDrawable = a.getResourceId(R.styleable.InverseCheckableRelativeLayout_inverseBackground, 0);
            inverseForegroundDrawable = a.getResourceId(R.styleable.InverseCheckableRelativeLayout_inverseForeground, 0);
        }
        finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.InverseCheckableRelativeLayout.onFinishInflate()",this);try{super.onFinishInflate();
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.InverseCheckableRelativeLayout.onFinishInflate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.InverseCheckableRelativeLayout.onFinishInflate()",this,throwable);throw throwable;}
    }

    @Override
    public boolean isChecked() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.component.InverseCheckableRelativeLayout.isChecked()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.component.InverseCheckableRelativeLayout.isChecked()",this);return isChecked;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.component.InverseCheckableRelativeLayout.isChecked()",this,throwable);throw throwable;}
    }

    @Override
    public void setChecked(boolean checked) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.InverseCheckableRelativeLayout.setChecked(boolean)",this,checked);try{isChecked = checked;
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.InverseCheckableRelativeLayout.setChecked(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.InverseCheckableRelativeLayout.setChecked(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void toggle() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.InverseCheckableRelativeLayout.toggle()",this);try{isChecked = !isChecked;
        setBackground();com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.InverseCheckableRelativeLayout.toggle()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.InverseCheckableRelativeLayout.toggle()",this,throwable);throw throwable;}
    }

    protected void setBackground() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.InverseCheckableRelativeLayout.setBackground()",this);try{FrameLayout child = (FrameLayout)this.findViewById(R.id.frame_child);
        if (isChecked) {
            setBackgroundResource(checkedBackgroundDrawable);
            if (child != null)
                {child.setForeground(getResources().getDrawable(R.color.PaletteDrawerUncheckedFg));}
        }
        else {
            setBackgroundResource(inverseBackgroundDrawable);
            if (child != null)
                {child.setForeground(getResources().getDrawable(inverseForegroundDrawable));}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.InverseCheckableRelativeLayout.setBackground()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.InverseCheckableRelativeLayout.setBackground()",this,throwable);throw throwable;}
    }
}