package com.haringeymobile.ukweather.weather;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

/**
 * A fragment to store weather icon cache during orientation changes.
 */
public class IconCacheRetainFragment extends Fragment {

    private static final String TAG = "Cache fragment";
    public LruCache<String, Bitmap> iconCache;

    public static IconCacheRetainFragment findOrCreateRetainFragment(
            FragmentManager fragmentManager) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.IconCacheRetainFragment com.haringeymobile.ukweather.weather.IconCacheRetainFragment.findOrCreateRetainFragment(android.support.v4.app.FragmentManager)",fragmentManager);try{IconCacheRetainFragment fragment = (IconCacheRetainFragment) fragmentManager
                .findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new IconCacheRetainFragment();
            fragmentManager.beginTransaction().add(fragment, TAG).commit();
        }
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.IconCacheRetainFragment com.haringeymobile.ukweather.weather.IconCacheRetainFragment.findOrCreateRetainFragment(android.support.v4.app.FragmentManager)");return fragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.IconCacheRetainFragment com.haringeymobile.ukweather.weather.IconCacheRetainFragment.findOrCreateRetainFragment(android.support.v4.app.FragmentManager)",throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.IconCacheRetainFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setRetainInstance(true);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.IconCacheRetainFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.IconCacheRetainFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

}