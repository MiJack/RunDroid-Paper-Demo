package com.chanapps.four;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.gallery3d.app.GalleryAppImpl;
import com.android.gallery3d.data.DataManager;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.gallery.ChanOffLineSource;
import com.chanapps.four.gallery.ChanSource;
import com.chanapps.four.service.NetworkProfileManager;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */

public class ChanuApp extends GalleryAppImpl
{

    private static final boolean DEBUG = false;
    private static final String TAG = ChanuApp.class.getSimpleName();
    private static Locale locale = null;

    public synchronized DataManager getDataManager() {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.data.DataManager com.chanapps.four.ChanuApp.getDataManager()",this);try{if (mDataManager == null) {
            mDataManager = new DataManager(this);
            mDataManager.initializeSourceMap();
            mDataManager.addSource(new ChanSource(this));
            mDataManager.addSource(new ChanOffLineSource(this));
        }
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.data.DataManager com.chanapps.four.ChanuApp.getDataManager()",this);return mDataManager;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.data.DataManager com.chanapps.four.ChanuApp.getDataManager()",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.ChanuApp.onCreate()",this);try{super.onCreate();
        ChanFileStorage.migrateIfNecessary(getApplicationContext());
        forceLocaleIfConfigured();
        if (DEBUG) {Log.i(TAG, "onCreate() activity=" + NetworkProfileManager.instance().getActivityId());}com.mijack.Xlog.logMethodExit("void com.chanapps.four.ChanuApp.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.ChanuApp.onCreate()",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.ChanuApp.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.ChanuApp.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.ChanuApp.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

    private void forceLocaleIfConfigured() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.ChanuApp.forceLocaleIfConfigured()",this);try{SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        boolean forceEnglish = settings.getBoolean(SettingsActivity.PREF_FORCE_ENGLISH, false);
        String lang = getString(R.string.pref_force_english_lang);
        if (forceEnglish && ! "".equals(lang) && ! config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.ChanuApp.forceLocaleIfConfigured()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.ChanuApp.forceLocaleIfConfigured()",this,throwable);throw throwable;}
    }
}
