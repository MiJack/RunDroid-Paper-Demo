package com.haringeymobile.ukweather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.weather.ThreeHourlyForecastDisplayMode;
import com.haringeymobile.ukweather.weather.WeatherInfoType;

public class SharedPrefsHelper {

    public static final String SHARED_PREFS_KEY =
            "com.haringeymobile.ukweather.PREFERENCE_FILE_KEY";

    private static final String LAST_SELECTED_CITY_ID = "city id";
    private static final String LAST_SELECTED_WEATHER_INFO_TYPE = "weather info type";
    private static final String PERSONAL_API_KEY = "personal api key";

    /**
     * Obtains the ID of the city that was last queried by the user.
     */
    public static int getCityIdFromSharedPrefs(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("int com.haringeymobile.ukweather.utils.SharedPrefsHelper.getCityIdFromSharedPrefs(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("int com.haringeymobile.ukweather.utils.SharedPrefsHelper.getCityIdFromSharedPrefs(android.content.Context)");return getSharedPreferences(context).getInt(LAST_SELECTED_CITY_ID,
                CityTable.CITY_ID_DOES_NOT_EXIST);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.haringeymobile.ukweather.utils.SharedPrefsHelper.getCityIdFromSharedPrefs(android.content.Context)",throwable);throw throwable;}
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("android.content.SharedPreferences com.haringeymobile.ukweather.utils.SharedPrefsHelper.getSharedPreferences(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("android.content.SharedPreferences com.haringeymobile.ukweather.utils.SharedPrefsHelper.getSharedPreferences(android.content.Context)");return context.getSharedPreferences(SHARED_PREFS_KEY, Activity.MODE_PRIVATE);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.SharedPreferences com.haringeymobile.ukweather.utils.SharedPrefsHelper.getSharedPreferences(android.content.Context)",throwable);throw throwable;}
    }

    /**
     * Saves the specified city ID.
     *
     * @param cityId OpenWeatherMap city ID
     * @param commit commit if true, apply if false
     */
    public static void putCityIdIntoSharedPrefs(Context context, int cityId, boolean commit) {
        com.mijack.Xlog.logStaticMethodEnter("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putCityIdIntoSharedPrefs(android.content.Context,int,boolean)",context,cityId,commit);try{SharedPreferences.Editor editor = getEditor(context).putInt(LAST_SELECTED_CITY_ID, cityId);
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }com.mijack.Xlog.logStaticMethodExit("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putCityIdIntoSharedPrefs(android.content.Context,int,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putCityIdIntoSharedPrefs(android.content.Context,int,boolean)",throwable);throw throwable;}
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("SharedPreferences.Editor com.haringeymobile.ukweather.utils.SharedPrefsHelper.getEditor(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("SharedPreferences.Editor com.haringeymobile.ukweather.utils.SharedPrefsHelper.getEditor(android.content.Context)");return getSharedPreferences(context).edit();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("SharedPreferences.Editor com.haringeymobile.ukweather.utils.SharedPrefsHelper.getEditor(android.content.Context)",throwable);throw throwable;}
    }

    /**
     * Obtains the {@link WeatherInfoType} for the last user's query.
     */
    public static WeatherInfoType getLastWeatherInfoTypeFromSharedPrefs(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.utils.SharedPrefsHelper.getLastWeatherInfoTypeFromSharedPrefs(android.content.Context)",context);try{int lastSelectedWeatherInfoTypeId = getSharedPreferences(context).getInt(
                LAST_SELECTED_WEATHER_INFO_TYPE, WeatherInfoType.CURRENT_WEATHER.getId());
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.utils.SharedPrefsHelper.getLastWeatherInfoTypeFromSharedPrefs(android.content.Context)");return WeatherInfoType.getTypeById(lastSelectedWeatherInfoTypeId);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.utils.SharedPrefsHelper.getLastWeatherInfoTypeFromSharedPrefs(android.content.Context)",throwable);throw throwable;}
    }

    /**
     * Saves the {@link WeatherInfoType} that was last queried by the user.
     *
     * @param weatherInfoType a kind of weather information
     */
    public static void putLastWeatherInfoTypeIntoSharedPrefs(Context context,
                                                             WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logStaticMethodEnter("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putLastWeatherInfoTypeIntoSharedPrefs(android.content.Context,com.haringeymobile.ukweather.weather.WeatherInfoType)",context,weatherInfoType);try{getEditor(context).putInt(LAST_SELECTED_WEATHER_INFO_TYPE, weatherInfoType.getId()).apply();com.mijack.Xlog.logStaticMethodExit("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putLastWeatherInfoTypeIntoSharedPrefs(android.content.Context,com.haringeymobile.ukweather.weather.WeatherInfoType)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putLastWeatherInfoTypeIntoSharedPrefs(android.content.Context,com.haringeymobile.ukweather.weather.WeatherInfoType)",throwable);throw throwable;}
    }

    public static String getPersonalApiKeyFromSharedPrefs(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.haringeymobile.ukweather.utils.SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(android.content.Context)",context);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.haringeymobile.ukweather.utils.SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(android.content.Context)");return getSharedPreferences(context).getString(PERSONAL_API_KEY, "");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.utils.SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(android.content.Context)",throwable);throw throwable;}
    }

    /**
     * Saves users personal API key.
     *
     * @param key OWM developer key
     */
    public static void putPersonalApiKeyIntoSharedPrefs(Context context, String key) {
        com.mijack.Xlog.logStaticMethodEnter("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putPersonalApiKeyIntoSharedPrefs(android.content.Context,java.lang.String)",context,key);try{getEditor(context).putString(PERSONAL_API_KEY, key).apply();com.mijack.Xlog.logStaticMethodExit("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putPersonalApiKeyIntoSharedPrefs(android.content.Context,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.SharedPrefsHelper.putPersonalApiKeyIntoSharedPrefs(android.content.Context,java.lang.String)",throwable);throw throwable;}
    }

    /**
     * Obtains the three-hourly forecast display mode from the shared preferences.
     *
     * @return weather forecast display mode preferred by the user, such as a list, or a horizontal
     * swipe view
     */
    public static ThreeHourlyForecastDisplayMode getForecastDisplayMode(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.ThreeHourlyForecastDisplayMode com.haringeymobile.ukweather.utils.SharedPrefsHelper.getForecastDisplayMode(android.content.Context)",context);try{String forecastDisplayModeIdString = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.PREF_FORECAST_DISPLAY_MODE, context.getResources()
                        .getString(R.string.pref_forecast_display_mode_id_default));
        int forecastDisplayModeId = Integer.parseInt(forecastDisplayModeIdString);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.ThreeHourlyForecastDisplayMode com.haringeymobile.ukweather.utils.SharedPrefsHelper.getForecastDisplayMode(android.content.Context)");return ThreeHourlyForecastDisplayMode.getForecastDisplayModeById(forecastDisplayModeId);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.ThreeHourlyForecastDisplayMode com.haringeymobile.ukweather.utils.SharedPrefsHelper.getForecastDisplayMode(android.content.Context)",throwable);throw throwable;}
    }

    /**
     * Determines if cities are deleted using button.
     */
    public static boolean isRemovalModeButton(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.haringeymobile.ukweather.utils.SharedPrefsHelper.isRemovalModeButton(android.content.Context)",context);try{SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        String cityRemovalModeIdString = preferences.getString(SettingsActivity.
                        PREF_CITY_REMOVAL_MODE,
                res.getString(R.string.pref_city_removal_mode_id_default));
        {com.mijack.Xlog.logStaticMethodExit("boolean com.haringeymobile.ukweather.utils.SharedPrefsHelper.isRemovalModeButton(android.content.Context)");return cityRemovalModeIdString.equals(res.getString(
                R.string.pref_city_removal_mode_button_id));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.haringeymobile.ukweather.utils.SharedPrefsHelper.isRemovalModeButton(android.content.Context)",throwable);throw throwable;}
    }

}