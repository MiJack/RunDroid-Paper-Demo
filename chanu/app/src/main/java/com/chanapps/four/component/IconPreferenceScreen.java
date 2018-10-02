package com.chanapps.four.component;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 4/15/13
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.chanapps.four.activity.R;
import com.chanapps.four.loader.ChanImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class IconPreferenceScreen extends Preference {

    private Context context;
    private TypedArray typedArray;

    public IconPreferenceScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconPreferenceScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setLayoutResource(R.layout.about_preference_icon);
        typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.IconPreferenceScreen, defStyle, 0);
    }

    @Override
    public void onBindView(View view) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.IconPreferenceScreen.onBindView(android.view.View)",this,view);try{super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        int resourceId = typedArray.getResourceId(R.styleable.IconPreferenceScreen_icon, 0);
        if (imageView != null && resourceId > 0) {
            String uri = "drawable://" + resourceId;
            DisplayImageOptions options = (new DisplayImageOptions.Builder())
                    .cacheInMemory()
                    .build();
            ChanImageLoader.getInstance(context).displayImage(uri, imageView, options);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.IconPreferenceScreen.onBindView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.IconPreferenceScreen.onBindView(android.view.View)",this,throwable);throw throwable;}
    }

    public void setIcon(Drawable icon) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.IconPreferenceScreen.setIcon(android.graphics.drawable.Drawable)",this,icon);try{/*// ignore*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.IconPreferenceScreen.setIcon(android.graphics.drawable.Drawable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.IconPreferenceScreen.setIcon(android.graphics.drawable.Drawable)",this,throwable);throw throwable;}
    }

    public Drawable getIcon() {
        com.mijack.Xlog.logMethodEnter("android.graphics.drawable.Drawable com.chanapps.four.component.IconPreferenceScreen.getIcon()",this);try{/*// ignore*/
        {com.mijack.Xlog.logMethodExit("android.graphics.drawable.Drawable com.chanapps.four.component.IconPreferenceScreen.getIcon()",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.drawable.Drawable com.chanapps.four.component.IconPreferenceScreen.getIcon()",this,throwable);throw throwable;}
    }
}
