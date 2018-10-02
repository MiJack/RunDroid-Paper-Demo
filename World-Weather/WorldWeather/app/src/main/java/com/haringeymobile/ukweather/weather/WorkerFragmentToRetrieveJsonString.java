package com.haringeymobile.ukweather.weather;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.JsonFetcher;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.SqlOperation;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * A fragment to asynchronously obtain the specified JSON weather data. This fragment has no UI,
 * and simply acts as an executor of the {@link WorkerFragmentToRetrieveJsonString.RetrieveWeatherInformationJsonStringTask}.
 */
public class WorkerFragmentToRetrieveJsonString extends Fragment {

    /**
     * A listener for the requested JSON weather data retrieval.
     */
    public interface OnJsonStringRetrievedListener {

        /**
         * Reacts to the recent JSON weather information retrieval.
         *
         * @param jsonString        weather data in JSON format
         * @param weatherInfoType   a kind of weather information
         * @param shouldSaveLocally whether the retrieved data should be saved in the database
         */
        void onRecentJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
                                         boolean shouldSaveLocally);

        /**
         * * Reacts to the old JSON weather information retrieval.
         *
         * @param jsonString      weather data in JSON format
         * @param weatherInfoType a kind of weather information
         * @param queryTime       the time in millis the weather data were stored
         */
        void onOldJsonStringRetrieved(String jsonString, WeatherInfoType weatherInfoType,
                                      long queryTime);

    }

    private static final String OPEN_WEATHER_MAP_API_HTTP_CODE_KEY = "cod";

    private Activity parentActivity;
    private OnJsonStringRetrievedListener listener;
    /**
     * An Open Weather Map city ID.
     */
    private int cityId;
    private WeatherInfoType weatherInfoType;
    private RetrieveWeatherInformationJsonStringTask retrieveWeatherInformationJsonStringTask;

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        parentActivity = activity;
        listener = (OnJsonStringRetrievedListener) activity;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setRetainInstance(true);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Repeats the last weather data request, i.e. retrieves the weather information using
     * parameters (city and information type) used for the last attempt to obtain weather
     * information.
     */
    public void retrieveLastRequestedWeatherInfo() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveLastRequestedWeatherInfo()",this);try{int lastCityId = SharedPrefsHelper.getCityIdFromSharedPrefs(parentActivity);
        if (lastCityId != CityTable.CITY_ID_DOES_NOT_EXIST) {
            WeatherInfoType lastWeatherInfoType = SharedPrefsHelper
                    .getLastWeatherInfoTypeFromSharedPrefs(parentActivity);
            retrieveWeatherInfoJsonString(lastCityId, lastWeatherInfoType);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveLastRequestedWeatherInfo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveLastRequestedWeatherInfo()",this,throwable);throw throwable;}
    }

    /**
     * Starts an {@link android.os.AsyncTask} to obtain the requested JSON weather data.
     *
     * @param cityId          an Open Weather Map city ID
     * @param weatherInfoType a type of the requested weather data
     */
    public void retrieveWeatherInfoJsonString(int cityId, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveWeatherInfoJsonString(int,WeatherInfoType)",this,cityId,weatherInfoType);try{this.cityId = cityId;
        this.weatherInfoType = weatherInfoType;

        URL openWeatherMapUrl = weatherInfoType.getOpenWeatherMapUrl(parentActivity, cityId);
        retrieveWeatherInformationJsonStringTask =
                new RetrieveWeatherInformationJsonStringTask();
        retrieveWeatherInformationJsonStringTask.setContext(parentActivity);
        retrieveWeatherInformationJsonStringTask.execute(openWeatherMapUrl);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveWeatherInfoJsonString(int,WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.retrieveWeatherInfoJsonString(int,WeatherInfoType)",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDestroy()",this);try{super.onDestroy();
        if (retrieveWeatherInformationJsonStringTask != null) {
            retrieveWeatherInformationJsonStringTask.cancel(true);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDestroy()",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDetach()",this);try{super.onDetach();
        parentActivity = null;
        listener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString.onDetach()",this,throwable);throw throwable;}
    }

    /**
     * A task to obtain JSON weather data from the provided Open Weather Map
     * URL.
     * <p/>
     * <p/>
     * Since weather data don't change that often, retrieved JSON strings are saved locally, and
     * reused for a short period of time (chosen by user). So the task first checks if there
     * already exists a recently requested and saved data in the local SQLite Database, and
     * connects to internet only if such data is too old or does not exist.
     */
    private class RetrieveWeatherInformationJsonStringTask extends
            AsyncTaskWithProgressBar<URL, Void, Pair<String, Long>> {

        private final Long CURRENT_TIME_SQL = 0L;
        private final Long CURRENT_TIME_WEB = 1L;

        @Override
        protected Pair<String, Long> doInBackground(URL... params) {
            com.mijack.Xlog.logMethodEnter("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this,params);try{SqlOperation sqlOperation = new SqlOperation(parentActivity, weatherInfoType);
            Pair<String, Long> storedWeatherInfo = sqlOperation.getJsonStringForWeatherInfo(cityId);
            long lastQueryTime = storedWeatherInfo.second;

            if (!(lastQueryTime == CityTable.CITY_NEVER_QUERIED ||
                    recordNeedsToBeUpdatedForWeatherInfo(lastQueryTime))) {
                /*// recent data already stored locally*/
                {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this);return Pair.create(storedWeatherInfo.first, CURRENT_TIME_SQL);}
            } else if (!isCancelled()) {
                String jsonDataObtainedFromWebService = getJsonStringFromWebService(params[0]);
                if (jsonDataObtainedFromWebService == null) {
                    /*// data from web not available*/
                    if (lastQueryTime == CityTable.CITY_NEVER_QUERIED) {
                        /*// no data available at all - should display an error message*/
                        {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this);return Pair.create(null, null);}
                    } else {
                        /*// there is an old record that may be offered to user*/
                        {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this);return Pair.create(storedWeatherInfo.first, lastQueryTime);}
                    }
                } else {
                    /*// show record obtained from the web*/
                    {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this);return Pair.create(jsonDataObtainedFromWebService, CURRENT_TIME_WEB);}
                }
            } else {
                {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this);return null;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.util.Pair com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.doInBackground([java.net.URL)",this,throwable);throw throwable;}
        }

        /**
         * Determines whether the weather records are outdated and should be renewed.
         *
         * @param lastUpdateTime when was this type of record last updated locally
         * @return true, if current records are too old; false otherwise
         */
        private boolean recordNeedsToBeUpdatedForWeatherInfo(long lastUpdateTime) {
            com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.recordNeedsToBeUpdatedForWeatherInfo(long)",this,lastUpdateTime);try{if (lastUpdateTime == CityTable.CITY_NEVER_QUERIED) {
                {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.recordNeedsToBeUpdatedForWeatherInfo(long)",this);return true;}
            } else {
                long currentTime = System.currentTimeMillis();
                {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.recordNeedsToBeUpdatedForWeatherInfo(long)",this);return currentTime - lastUpdateTime > getWeatherDataCachePeriod();}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.recordNeedsToBeUpdatedForWeatherInfo(long)",this,throwable);throw throwable;}
        }

        /**
         * Obtains the time period (which can be specified by a user) for which the cached weather data
         * can be reused.
         *
         * @return a time in milliseconds
         */
        private long getWeatherDataCachePeriod() {
            com.mijack.Xlog.logMethodEnter("long com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getWeatherDataCachePeriod()",this);try{String minutesString = PreferenceManager.getDefaultSharedPreferences(parentActivity)
                    .getString(SettingsActivity.PREF_DATA_CACHE_PERIOD, getResources().getString(
                            R.string.pref_data_cache_period_default));
            int minutes = Integer.parseInt(minutesString);
            {com.mijack.Xlog.logMethodExit("long com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getWeatherDataCachePeriod()",this);return minutes * 60 * 1000;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getWeatherDataCachePeriod()",this,throwable);throw throwable;}
        }

        /**
         * Attempts to obtain the weather data from the provided Open Weather Map URL.
         *
         * @param url Open Weather Map URL
         * @return Json data for the requested city and weather information type, or {@code null}
         * in case of network problems
         */
        private String getJsonStringFromWebService(URL url) {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getJsonStringFromWebService(java.net.URL)",this,url);try{try {
                {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getJsonStringFromWebService(java.net.URL)",this);return new JsonFetcher().getJsonString(url);}
            } catch (IOException e) {
                {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getJsonStringFromWebService(java.net.URL)",this);return null;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.getJsonStringFromWebService(java.net.URL)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(Pair<String, Long> weatherInfo) {
            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.onPostExecute(android.support.v4.util.Pair)",this,weatherInfo);try{super.onPostExecute(weatherInfo);

            String jsonString = weatherInfo.first;
            if (jsonString == null) {
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, R.string.error_message_no_connection,
                            Toast.LENGTH_SHORT).show();
                }
            } else if (listener != null && isWeatherDataAvailable(jsonString)) {
                long time = weatherInfo.second;
                if (CURRENT_TIME_WEB == time) {
                    listener.onRecentJsonStringRetrieved(jsonString, weatherInfoType, true);
                } else if (CURRENT_TIME_SQL == time) {
                    listener.onRecentJsonStringRetrieved(jsonString, weatherInfoType, false);
                } else {
                    listener.onOldJsonStringRetrieved(jsonString, weatherInfoType, time);
                }
            } else if (parentActivity != null) {
                Toast.makeText(parentActivity, R.string.error_message_no_data,
                        Toast.LENGTH_LONG).show();
            }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.onPostExecute(android.support.v4.util.Pair)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.onPostExecute(android.support.v4.util.Pair)",this,throwable);throw throwable;}
        }

        /**
         * Checks if the obtained JSON contains any useful data. This is necessary due to
         * a strange way HTTP status codes are handled in the Open Weather Map API - see
         * https://claudiosparpaglione.wordpress.com/tag/openweathermap for more details.
         *
         * @param jsonString JSON data for the requested city and weather information type
         * @return true if there are meaningful data to display, false otherwise
         */
        private boolean isWeatherDataAvailable(String jsonString) {
            com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.isWeatherDataAvailable(java.lang.String)",this,jsonString);try{try {
                JSONObject obj = new JSONObject(jsonString);
                if (obj.has(OPEN_WEATHER_MAP_API_HTTP_CODE_KEY)) {
                    int httpStatusCode = obj.getInt(OPEN_WEATHER_MAP_API_HTTP_CODE_KEY);
                    if (JsonFetcher.HTTP_STATUS_CODE_OK != httpStatusCode) {
                        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.isWeatherDataAvailable(java.lang.String)",this);return false;}
                    }
                }
            } catch (JSONException e) {
                {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.isWeatherDataAvailable(java.lang.String)",this);return false;}
            }
            {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.isWeatherDataAvailable(java.lang.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString$RetrieveWeatherInformationJsonStringTask.isWeatherDataAvailable(java.lang.String)",this,throwable);throw throwable;}
        }

    }

}