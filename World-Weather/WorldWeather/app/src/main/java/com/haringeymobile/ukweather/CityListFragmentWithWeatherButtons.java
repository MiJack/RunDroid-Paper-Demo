package com.haringeymobile.ukweather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haringeymobile.ukweather.weather.WeatherInfoType;

/**
 * A fragment containing a list of cities with clickable buttons, requesting various weather
 * information.
 */
public class CityListFragmentWithWeatherButtons extends
        BaseCityListFragmentWithButtons {

    /**
     * A listener for weather information button clicks.
     */
    public interface OnWeatherInfoButtonClickedListener {

        /**
         * Reacts to the request to obtain weather information.
         *
         * @param cityId          OpenWeatherMap city ID
         * @param weatherInfoType a kind of weather information requested
         */
        void onCityWeatherInfoRequested(int cityId, WeatherInfoType weatherInfoType);

    }

    private OnWeatherInfoButtonClickedListener onWeatherInfoButtonClickedListener;

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        onWeatherInfoButtonClickedListener = (OnWeatherInfoButtonClickedListener) activity;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return inflater.inflate(R.layout.fragment_city_list, container, false);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected BaseCityCursorAdapter getCityCursorAdapter() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getCityCursorAdapter()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getCityCursorAdapter()",this);return new CityWeatherCursorAdapter(parentActivity,
                R.layout.row_city_list_with_weather_buttons, null,
                COLUMNS_TO_DISPLAY, TO, 0, this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getCityCursorAdapter()",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onDetach()",this);try{super.onDetach();
        onWeatherInfoButtonClickedListener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onDetach()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onClick(android.view.View)",this,view);try{int listItemPosition = getListView().getPositionForView(view);
        int cityId = cursorAdapter.getCityId(listItemPosition);
        WeatherInfoType weatherInfoType = getRequestedWeatherInfoType(view);
        onWeatherInfoButtonClickedListener.onCityWeatherInfoRequested(cityId, weatherInfoType);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the kind of weather information associated with the view.
     *
     * @param view the clicked view (button)
     * @return requested weather information type
     */
    private WeatherInfoType getRequestedWeatherInfoType(View view) {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getRequestedWeatherInfoType(android.view.View)",this,view);try{int viewId = view.getId();
        switch (viewId) {
            case R.id.city_current_weather_button:
                {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getRequestedWeatherInfoType(android.view.View)",this);return WeatherInfoType.CURRENT_WEATHER;}
            case R.id.city_daily_weather_forecast_button:
                {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getRequestedWeatherInfoType(android.view.View)",this);return WeatherInfoType.DAILY_WEATHER_FORECAST;}
            case R.id.city_three_hourly_weather_forecast_button:
                {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getRequestedWeatherInfoType(android.view.View)",this);return WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST;}
            default:
                throw new IllegalArgumentException("Not supported view ID: " + viewId);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherInfoType com.haringeymobile.ukweather.CityListFragmentWithWeatherButtons.getRequestedWeatherInfoType(android.view.View)",this,throwable);throw throwable;}
    }

}