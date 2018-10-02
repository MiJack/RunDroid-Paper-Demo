package com.haringeymobile.ukweather.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.haringeymobile.ukweather.utils.SharedPrefsHelper;
import com.haringeymobile.ukweather.weather.WeatherInfoType;

/**
 * A layer between the app and the SQLite database, responsible for the CRUD functions.
 */
public class SqlOperation {

    private Context context;
    /**
     * The name of the {@link com.haringeymobile.ukweather.database.CityTable} column holding
     * weather information as a JSON string.
     */
    private String columnNameForJsonString;
    /**
     * The name of the {@link com.haringeymobile.ukweather.database.CityTable} column holding the
     * time for the last weather information update.
     */
    private String columnNameForLastQueryTime;

    /**
     * A constructor of SQLOperation, where weather information type is not important (for
     * instance, an operation to delete a record).
     */
    public SqlOperation(Context context) {
        this.context = context;
    }

    /**
     * A constructor of SQLOperation, where the specified weather information type determines
     * which columns will be queried or updated.
     *
     * @param weatherInfoType a kind of weather information
     */
    public SqlOperation(Context context, WeatherInfoType weatherInfoType) {
        this.context = context;
        switch (weatherInfoType) {
            case CURRENT_WEATHER:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_CURRENT;
                columnNameForLastQueryTime = CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER;
                break;
            case DAILY_WEATHER_FORECAST:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_DAILY_FORECAST;
                columnNameForLastQueryTime =
                        CityTable.COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST;
                break;
            case THREE_HOURLY_WEATHER_FORECAST:
                columnNameForJsonString = CityTable.COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST;
                columnNameForLastQueryTime =
                        CityTable.COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST;
                break;
            default:
                throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(
                        weatherInfoType);
        }
    }

    /**
     * Updates current weather record for the specified city if it already exists in the database,
     * otherwise inserts a new record.
     *
     * @param cityId         Open Weather Map city ID
     * @param cityName       Open Weather Map city name
     * @param currentWeather Json string for the current city weather
     */
    void updateOrInsertCityWithCurrentWeather(int cityId, String cityName, String currentWeather) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.updateOrInsertCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this,cityId,cityName,currentWeather);try{if (!CityTable.COLUMN_CACHED_JSON_CURRENT.equals(columnNameForJsonString)) {
            throw new IllegalStateException(
                    "This method is expected to deal with current weather information only");
        }

        Cursor cursor = getCursorWithCityId(cityId);
        if (cursor == null) {
            {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.updateOrInsertCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this);return;}
        }
        boolean cityIdExists = cursor.moveToFirst();
        if (cityIdExists) {
            Uri rowUri = getRowUri(cursor);
            ContentValues newValues =
                    createContentValuesWithDateAndWeatherJsonString(currentWeather);
            context.getContentResolver().update(rowUri, newValues, null, null);
            cursor.close();
        } else {
            insertNewCityWithCurrentWeather(cityId, cityName, currentWeather);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.updateOrInsertCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Inserts a new current weather record for the specified city.
     *
     * @param cityId         Open Weather Map city ID
     * @param cityName       Open Weather Map city name
     * @param currentWeather Json string for the current city weather
     */
    private void insertNewCityWithCurrentWeather(int cityId, String cityName, String
            currentWeather) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.insertNewCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this,cityId,cityName,currentWeather);try{ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_CITY_ID, cityId);
        newValues.put(CityTable.COLUMN_NAME, cityName);
        long currentTime = System.currentTimeMillis();
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_CURRENT_WEATHER, currentTime);
        newValues.put(CityTable.COLUMN_ORDERING_VALUE, currentTime);
        newValues.put(CityTable.COLUMN_CACHED_JSON_CURRENT, currentWeather);
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_DAILY_WEATHER_FORECAST,
                CityTable.CITY_NEVER_QUERIED);
        newValues.putNull(CityTable.COLUMN_CACHED_JSON_DAILY_FORECAST);
        newValues.put(CityTable.COLUMN_LAST_QUERY_TIME_FOR_THREE_HOURLY_WEATHER_FORECAST,
                CityTable.CITY_NEVER_QUERIED);
        newValues.putNull(CityTable.COLUMN_CACHED_JSON_THREE_HOURLY_FORECAST);
        context.getContentResolver().insert(WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                newValues);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.insertNewCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.insertNewCityWithCurrentWeather(int,java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Obtains a cursor over the specified city record ID in the
     * {@link com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    private Cursor getCursorWithCityId(int cityId) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithCityId(int)",this,cityId);try{if (context == null) {
            {com.mijack.Xlog.logMethodExit("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithCityId(int)",this);return null;}
        }
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithCityId(int)",this);return context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, CityTable.COLUMN_CITY_ID},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithCityId(int)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the uri of the row pointed to by the provided cursor.
     */
    private Uri getRowUri(Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(android.database.Cursor)",this,cursor);try{int columnIndex = cursor.getColumnIndexOrThrow(CityTable._ID);
        long rowId = cursor.getLong(columnIndex);
        {com.mijack.Xlog.logMethodExit("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(android.database.Cursor)",this);return getRowUri(rowId);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(android.database.Cursor)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the uri of the row with the given ID.
     */
    private Uri getRowUri(long rowId) {
        com.mijack.Xlog.logMethodEnter("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(long)",this,rowId);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(long)",this);return ContentUris.withAppendedId(WeatherContentProvider.CONTENT_URI_CITY_RECORDS, rowId);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.haringeymobile.ukweather.database.SqlOperation.getRowUri(long)",this,throwable);throw throwable;}
    }

    /**
     * Updates the specified city with new weather data.
     *
     * @param cityId     Open Weather Map city ID
     * @param jsonString JSON string for the weather information of some kind
     */
    void updateWeatherInfo(int cityId, String jsonString) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.updateWeatherInfo(int,java.lang.String)",this,cityId,jsonString);try{Cursor cursor = getCursorWithWeatherInfo(cityId);
        if (cursor == null) {
            {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.updateWeatherInfo(int,java.lang.String)",this);return;}
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.updateWeatherInfo(int,java.lang.String)",this);return;}
        }
        Uri rowUri = getRowUri(cursor);
        ContentValues newValues = createContentValuesWithDateAndWeatherJsonString(jsonString);
        context.getContentResolver().update(rowUri, newValues, null, null);
        cursor.close();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.updateWeatherInfo(int,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Obtains a cursor over the specified city weather information record in
     * the {@link com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    private Cursor getCursorWithWeatherInfo(int cityId) {
        com.mijack.Xlog.logMethodEnter("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithWeatherInfo(int)",this,cityId);try{if (context == null) {
            {com.mijack.Xlog.logMethodExit("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithWeatherInfo(int)",this);return null;}
        }
        {com.mijack.Xlog.logMethodExit("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithWeatherInfo(int)",this);return context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, columnNameForLastQueryTime,
                        columnNameForJsonString,
                        CityTable.COLUMN_ORDERING_VALUE},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.database.Cursor com.haringeymobile.ukweather.database.SqlOperation.getCursorWithWeatherInfo(int)",this,throwable);throw throwable;}
    }

    /**
     * Creates {@link android.content.ContentValues} with the added time and weather information
     * values.
     *
     * @param jsonString JSON string for the weather information of some kind
     */
    private ContentValues createContentValuesWithDateAndWeatherJsonString(String jsonString) {
        com.mijack.Xlog.logMethodEnter("android.content.ContentValues com.haringeymobile.ukweather.database.SqlOperation.createContentValuesWithDateAndWeatherJsonString(java.lang.String)",this,jsonString);try{ContentValues newValues = new ContentValues();
        long currentTime = System.currentTimeMillis();
        newValues.put(columnNameForLastQueryTime, currentTime);
        newValues.put(CityTable.COLUMN_ORDERING_VALUE, currentTime);
        newValues.put(columnNameForJsonString, jsonString);
        {com.mijack.Xlog.logMethodExit("android.content.ContentValues com.haringeymobile.ukweather.database.SqlOperation.createContentValuesWithDateAndWeatherJsonString(java.lang.String)",this);return newValues;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.ContentValues com.haringeymobile.ukweather.database.SqlOperation.createContentValuesWithDateAndWeatherJsonString(java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Obtains cached JSON data for the specified city.
     *
     * @param cityId Open Weather Map city ID
     * @return a string, representing JSON weather data, or null, if no cached data is stored
     */
    public Pair<String, Long> getJsonStringForWeatherInfo(int cityId) {
        com.mijack.Xlog.logMethodEnter("android.support.v4.util.Pair com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(int)",this,cityId);try{Cursor cursor = getCursorWithWeatherInfo(cityId);
        if (cursor == null) {
            {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(int)",this);return null;}
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(int)",this);return null;}
        }

        String weatherInfoJson = getJsonStringForWeatherInfo(cursor);
        Long lastQueryTime = CityTable.CITY_NEVER_QUERIED;
        if (weatherInfoJson != null) {
            int columnIndexForLastQueryTime = cursor.getColumnIndexOrThrow(
                    columnNameForLastQueryTime);
            lastQueryTime = cursor.getLong(columnIndexForLastQueryTime);

            int columnIndex = cursor.getColumnIndexOrThrow(CityTable._ID);
            long rowId = cursor.getLong(columnIndex);
            setLastOverallQueryTimeToCurrentTime(rowId);
        }

        Pair<String, Long> storedWeatherInfo = Pair.create(weatherInfoJson, lastQueryTime);
        cursor.close();
        {com.mijack.Xlog.logMethodExit("android.support.v4.util.Pair com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(int)",this);return storedWeatherInfo;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.util.Pair com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(int)",this,throwable);throw throwable;}
    }

    /**
     * Obtains cached JSON data using the specified cursor.
     *
     * @param cursor a cursor pointing to the {@link
     *               com.haringeymobile.ukweather.database.CityTable} row with the cached
     *               weather data
     * @return a string, representing JSON weather data, or null, if the cached weather data is
     * outdated
     */
    private String getJsonStringForWeatherInfo(Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(android.database.Cursor)",this,cursor);try{int columnIndexForWeatherInfo = cursor.getColumnIndexOrThrow(columnNameForJsonString);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(android.database.Cursor)",this);return cursor.getString(columnIndexForWeatherInfo);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.getJsonStringForWeatherInfo(android.database.Cursor)",this,throwable);throw throwable;}
    }

    /**
     * Removes the specified city record from the {@link
     * com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId Open Weather Map city ID
     */
    void deleteCity(int cityId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.deleteCity(int)",this,cityId);try{context.getContentResolver().delete(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)});com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.deleteCity(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.deleteCity(int)",this,throwable);throw throwable;}
    }

    /**
     * Changes the name of the specified city in the {@link
     * com.haringeymobile.ukweather.database.CityTable}.
     *
     * @param cityId  Open Weather Map city ID
     * @param newName the new (user-chosen) name for the city
     */
    void renameCity(int cityId, String newName) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.renameCity(int,java.lang.String)",this,cityId,newName);try{ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_NAME, newName);
        context.getContentResolver().update(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS, newValues,
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)});com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.renameCity(int,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.renameCity(int,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city name stored in the database.
     *
     * @param cityId Open Weather Map city ID
     * @return city name stored in the database (note that the city name provided by OWM may be
     * changed by a user)
     */
    public String findCityName(int cityId) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this,cityId);try{if (context == null) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this);return null;}
        }
        Cursor cursor = context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, CityTable.COLUMN_NAME},
                CityTable.COLUMN_CITY_ID + "=?",
                new String[]{Integer.toString(cityId)}, null);
        if (cursor == null) {
            {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this);return null;}
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this);return null;}
        }
        int columnIndexForCityName = cursor.getColumnIndexOrThrow(CityTable.COLUMN_NAME);
        String cityName = cursor.getString(columnIndexForCityName);
        cursor.close();
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this);return cityName;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.database.SqlOperation.findCityName(int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the last query time for the record to the current time. This is useful when we want a
     * record to appear first in the result set (as results are ordered by the last query time)
     * without requesting weather info from the web; for instance, when the user searches cities
     * already added to the database.
     *
     * @param rowId a unique id of a table record
     */
    public void setLastOverallQueryTimeToCurrentTime(long rowId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime(long)",this,rowId);try{long currentTime = System.currentTimeMillis();
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_ORDERING_VALUE, currentTime);
        Uri rowUri = getRowUri(rowId);
        context.getContentResolver().update(rowUri, newValues, null, null);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime(long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime(long)",this,throwable);throw throwable;}
    }

    /**
     * Sets the last query time to the current time for all the records with the given row IDs.
     *
     * @param rowIds unique ids of the table records
     */
    public void setLastOverallQueryTimeToCurrentTime(long[] rowIds) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime([long)",this,rowIds);try{ContentResolver contentResolver = context.getContentResolver();
        long currentTime = System.currentTimeMillis();
        ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_ORDERING_VALUE, currentTime);
        for (long rowId : rowIds) {
            Uri rowUri = getRowUri(rowId);
            contentResolver.update(rowUri, newValues, null, null);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime([long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.setLastOverallQueryTimeToCurrentTime([long)",this,throwable);throw throwable;}
    }

    /**
     * Updates cities ordering values after city rearranging.
     *
     * @param cityOrderFrom old position of the dragged city
     * @param cityOrderTo   new position of the dragged city
     */
    void dragCity(int cityOrderFrom, int cityOrderTo) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.dragCity(int,int)",this,cityOrderFrom,cityOrderTo);try{if (cityOrderFrom == cityOrderTo) {
            {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.dragCity(int,int)",this);return;}
        }
        if (cityOrderFrom < 0 || cityOrderTo < 0) {
            throw new IllegalArgumentException("Unexpected city orders: " + cityOrderFrom + ", " +
                    cityOrderTo);
        }

        String sortOrder = CityTable.COLUMN_ORDERING_VALUE + " DESC";
        Cursor cursor = context.getContentResolver().query(
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS,
                new String[]{CityTable._ID, CityTable.COLUMN_CITY_ID,
                        CityTable.COLUMN_ORDERING_VALUE},
                null, null, sortOrder);
        if (cursor == null) {
            {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.dragCity(int,int)",this);return;}
        }

        if (cityOrderFrom == 0 || cityOrderTo == 0) {
            /*// if the top city in the table changes, we update shared prefs*/
            cursor.moveToPosition(cityOrderFrom == 0 ? 1 : cityOrderFrom);
            int columnIndexForCityOwmId = cursor.getColumnIndexOrThrow(CityTable.COLUMN_CITY_ID);
            int topCityId = cursor.getInt(columnIndexForCityOwmId);
            SharedPrefsHelper.putCityIdIntoSharedPrefs(context, topCityId, true);
        }

        int columnIndexForOrderingValue = cursor.getColumnIndexOrThrow(CityTable.
                COLUMN_ORDERING_VALUE);
        if (cityOrderFrom < cityOrderTo) {
            /*// city dragged down*/
            cursor.moveToPosition(cityOrderTo);
            long orderingValueForCityOrderTo = cursor.getLong(columnIndexForOrderingValue);
            long newOrderingValueForCityOrderFrom = orderingValueForCityOrderTo - 1;

            long maxOrderingValueForNextCityInTable = newOrderingValueForCityOrderFrom - 1;
            while (cursor.moveToNext()) {
                long currentOrderingValueForNextCityInTable = cursor.getLong(
                        columnIndexForOrderingValue);
                if (currentOrderingValueForNextCityInTable > maxOrderingValueForNextCityInTable) {
                    long newOrderingValueForNextCityInTable = maxOrderingValueForNextCityInTable;
                    updateLastQueryTime(cursor, newOrderingValueForNextCityInTable);
                    maxOrderingValueForNextCityInTable--;
                } else {
                    break;
                }
            }

            cursor.moveToPosition(cityOrderFrom);
            updateLastQueryTime(cursor, newOrderingValueForCityOrderFrom);
        } else {
            /*// city dragged up*/
            cursor.moveToPosition(cityOrderTo);
            long orderingValueForCityOrderTo = cursor.getLong(columnIndexForOrderingValue);
            long newOrderingValueForCityOrderFrom = orderingValueForCityOrderTo + 1;

            long minOrderingValueForPreviousCityInTable = newOrderingValueForCityOrderFrom + 1;
            while (cursor.moveToPrevious()) {
                long currentOrderingValueForPreviousCityInTable = cursor.getLong(
                        columnIndexForOrderingValue);
                if (currentOrderingValueForPreviousCityInTable <
                        minOrderingValueForPreviousCityInTable) {
                    long newOrderingValueForPreviousCityInTable =
                            minOrderingValueForPreviousCityInTable;
                    updateLastQueryTime(cursor, newOrderingValueForPreviousCityInTable);
                    minOrderingValueForPreviousCityInTable++;
                } else {
                    break;
                }
            }

            cursor.moveToPosition(cityOrderFrom);
            updateLastQueryTime(cursor, newOrderingValueForCityOrderFrom);
        }

        cursor.close();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.dragCity(int,int)",this,throwable);throw throwable;}
    }

    private void updateLastQueryTime(Cursor cursor, long lastQueryTime) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.database.SqlOperation.updateLastQueryTime(android.database.Cursor,long)",this,cursor,lastQueryTime);try{ContentValues newValues = new ContentValues();
        newValues.put(CityTable.COLUMN_ORDERING_VALUE, lastQueryTime);
        Uri rowUri = getRowUri(cursor);
        int columnIndexForRowId = cursor.getColumnIndexOrThrow(CityTable._ID);
        int rowId = cursor.getInt(columnIndexForRowId);
        context.getContentResolver().update(rowUri, newValues, CityTable._ID + "=?",
                new String[]{Integer.toString(rowId)});com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.database.SqlOperation.updateLastQueryTime(android.database.Cursor,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.database.SqlOperation.updateLastQueryTime(android.database.Cursor,long)",this,throwable);throw throwable;}
    }

}