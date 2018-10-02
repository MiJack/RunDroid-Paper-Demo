package com.haringeymobile.ukweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;

import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.weather.IconCacheRetainFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoType;
import com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment;
import com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString;

/**
 * An activity that may display some weather data (e.g. current weather, or weather forecast), and
 * if it does so, it refreshes the data each time it becomes visible.
 */
public abstract class RefreshingActivity extends ThemedActivity implements
        WorkerFragmentToRetrieveJsonString.OnJsonStringRetrievedListener,
        WeatherInfoFragment.IconCacheRequestListener,
        WeatherThreeHourlyForecastChildListFragment.IconCacheRequestListener {

    public static final String WEATHER_INFORMATION_TYPE = "weather info type";
    public static final String WEATHER_INFO_JSON_STRING = "json string";

    protected WorkerFragmentToRetrieveJsonString workerFragment;
    /**
     * LruCache storing icons illustrating weather conditions. The key is the OWM icon code name:
     * https://openweathermap.org/weather-conditions
     */
    protected LruCache<String, Bitmap> iconCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setIconMemoryCache();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Obtains or creates a new memory cache to store the weather icons.
     */
    private void setIconMemoryCache() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity.setIconMemoryCache()",this);try{IconCacheRetainFragment retainFragment =
                IconCacheRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        iconCache = retainFragment.iconCache;
        if (iconCache == null) {
            /*// maximum available VM memory, stored in kilobytes*/
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            /*// we use 1/8th of the available memory for this memory cache*/
            int cacheSize = maxMemory / 8;

            iconCache = new LruCache<String, Bitmap>(cacheSize) {

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.RefreshingActivity$1.sizeOf(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,android.graphics.Bitmap)",this,key,bitmap);try{/*// the cache size will be measured in kilobytes rather than number of items*/
                    {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity.setIconMemoryCache()",this);{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.RefreshingActivity$1.sizeOf(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,android.graphics.Bitmap)",this);return bitmap.getByteCount() / 1024;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.RefreshingActivity$1.sizeOf(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,android.graphics.Bitmap)",this,throwable);throw throwable;}
                }
            };

        }
        retainFragment.iconCache = iconCache;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity.setIconMemoryCache()",this,throwable);throw throwable;}
    }

    @Override
    public void onRecentJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
                                            boolean shouldSaveLocally) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity.onRecentJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,boolean)",this,jsonString,weatherInfoType,shouldSaveLocally);try{if (shouldSaveLocally) {
            saveRetrievedData(jsonString, weatherInfoType);
        }
        displayRetrievedData(jsonString, weatherInfoType);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity.onRecentJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity.onRecentJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,boolean)",this,throwable);throw throwable;}
    }

    /**
     * Saves the retrieved data in the database, so that it could be reused for a short period of
     * time.
     *
     * @param jsonString      Weather information data in JSON format
     * @param weatherInfoType type of the retrieved weather data
     */
    protected void saveRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity.saveRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,jsonString,weatherInfoType);try{Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_UPDATE_WEATHER_INFO);
        intent.putExtra(WEATHER_INFO_JSON_STRING, jsonString);
        intent.putExtra(WEATHER_INFORMATION_TYPE, (Parcelable) weatherInfoType);
        startService(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity.saveRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity.saveRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    /**
     * @param jsonString      weather information data in JSON format
     * @param weatherInfoType type of the retrieved weather data
     */
    protected abstract void displayRetrievedData(String jsonString, WeatherInfoType
            weatherInfoType);

    @Override
    public void onOldJsonStringRetrieved(final String jsonString,
                                         final WeatherInfoType weatherInfoType, long queryTime) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity.onOldJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,long)",this,jsonString,weatherInfoType,queryTime);try{new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_no_network_connection)
                .setIcon(R.drawable.ic_alert_error)
                .setMessage(getAlertDialogMessage(queryTime))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{dialog.dismiss();
                        onRecentJsonStringRetrieved(jsonString, weatherInfoType, false);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.RefreshingActivity$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{dialog.dismiss();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .create()
                .show();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.RefreshingActivity.onOldJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.RefreshingActivity.onOldJsonStringRetrieved(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType,long)",this,throwable);throw throwable;}
    }

    /**
     * Parses the message to be shown to user when there is no network access, but the old weather
     * data stored locally still can be displayed.
     *
     * @param queryTime the time when the old weather data were obtained
     * @return time in millis
     */
    @NonNull
    private String getAlertDialogMessage(long queryTime) {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString com.haringeymobile.ukweather.RefreshingActivity.getAlertDialogMessage(long)",this,queryTime);try{long weatherDataAge = System.currentTimeMillis() - queryTime;
        int hours = (int) (weatherDataAge / (3600 * 1000));
        int days = hours / 24;
        hours %= 24;
        if (days == 0 && hours == 0) {
            hours = 1;
        }

        Resources res = getResources();
        String daysPlural = res.getQuantityString(R.plurals.days, days);
        String hoursPlural = res.getQuantityString(R.plurals.hours, hours);

        if (days > 0 && hours > 0) {
            {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString com.haringeymobile.ukweather.RefreshingActivity.getAlertDialogMessage(long)",this);return String.format(res.getString(R.string.old_data_message_x_days_and_y_hours_ago),
                    days, daysPlural, hours, hoursPlural);}
        } else {
            int number = days > 0 ? days : hours;
            String plural = days > 0 ? daysPlural : hoursPlural;
            {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString com.haringeymobile.ukweather.RefreshingActivity.getAlertDialogMessage(long)",this);return String.format(res.getString(R.string.old_data_message_x_days_or_hours_ago),
                    number, plural);}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString com.haringeymobile.ukweather.RefreshingActivity.getAlertDialogMessage(long)",this,throwable);throw throwable;}

    }

    @Override
    public LruCache<String, Bitmap> getIconMemoryCache() {
        com.mijack.Xlog.logMethodEnter("android.support.v4.util.LruCache com.haringeymobile.ukweather.RefreshingActivity.getIconMemoryCache()",this);try{com.mijack.Xlog.logMethodExit("android.support.v4.util.LruCache com.haringeymobile.ukweather.RefreshingActivity.getIconMemoryCache()",this);return iconCache;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.util.LruCache com.haringeymobile.ukweather.RefreshingActivity.getIconMemoryCache()",this,throwable);throw throwable;}
    }

}
