package com.haringeymobile.ukweather;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

public class WorldWeatherApplication extends Application {

    public static String systemLocaleCode;

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.WorldWeatherApplication.onCreate()",this);try{super.onCreate();
        Locale locale = Locale.getDefault();
        systemLocaleCode = getSystemLocaleCode(locale);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.WorldWeatherApplication.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.WorldWeatherApplication.onCreate()",this,throwable);throw throwable;}
    }

    /**
     * Obtains code for user-chosen device locale.
     *
     * @param locale system locale
     * @return ISO-defined language and (optionally) country codes
     */
    private String getSystemLocaleCode(Locale locale) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.WorldWeatherApplication.getSystemLocaleCode(java.util.Locale)",this,locale);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.WorldWeatherApplication.getSystemLocaleCode(java.util.Locale)",this);return locale.getLanguage() + "-r" + locale.getCountry();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.WorldWeatherApplication.getSystemLocaleCode(java.util.Locale)",this,throwable);throw throwable;}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.WorldWeatherApplication.onConfigurationChanged(android.content.res.Configuration)",this,newConfig);try{super.onConfigurationChanged(newConfig);
        Locale locale = newConfig.locale;
        systemLocaleCode = getSystemLocaleCode(locale);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.WorldWeatherApplication.onConfigurationChanged(android.content.res.Configuration)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.WorldWeatherApplication.onConfigurationChanged(android.content.res.Configuration)",this,throwable);throw throwable;}
    }

}