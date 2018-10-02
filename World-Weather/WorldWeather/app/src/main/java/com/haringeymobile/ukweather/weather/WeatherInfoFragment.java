package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.WeatherInformation;
import com.haringeymobile.ukweather.weather.WeatherInfoType.IllegalWeatherInfoTypeArgumentException;

import java.util.Date;

/**
 * A fragment displaying a common weather information.
 */
public abstract class WeatherInfoFragment extends Fragment {

    public interface IconCacheRequestListener {

        /**
         * Obtains the memory cache storing weather icon bitmaps.
         */
        LruCache<String, Bitmap> getIconMemoryCache();

    }

    public static final String JSON_STRING = "json string";
    protected static final String CITY_NAME = "city name";

    protected TextView extraInfoTextView;
    protected TextView conditionsTextView;
    protected ImageView conditionsImageView;
    protected TextView temperatureTextView;
    protected TextView pressureTextView;
    protected TextView humidityTextView;
    protected TextView windTextView;

    private IconCacheRequestListener iconCacheRequestListener;
    protected WeatherInformationDisplayer weatherInformationDisplayer;

    /**
     * Creates and sets the required weather information fragment.
     *
     * @param weatherInfoType requested weather information type
     * @param cityName        the name of the city for which the weather information was
     *                        requested and obtained
     * @param jsonString      JSON weather information data in textual form
     * @return a fragment to display the requested weather information
     */
    public static WeatherInfoFragment newInstance(WeatherInfoType weatherInfoType, String cityName,
                                                  String jsonString) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.newInstance(WeatherInfoType,java.lang.String,java.lang.String)",weatherInfoType,cityName,jsonString);try{WeatherInfoFragment weatherInfoFragment = createWeatherInfoFragment(weatherInfoType);
        Bundle args = getArgumentBundle(cityName, jsonString);
        weatherInfoFragment.setArguments(args);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.newInstance(WeatherInfoType,java.lang.String,java.lang.String)");return weatherInfoFragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.newInstance(WeatherInfoType,java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

    /**
     * Creates a fragment, corresponding to the requested weather information type.
     *
     * @param weatherInfoType requested weather information type
     * @return a correct type of weather information fragment
     */
    private static WeatherInfoFragment createWeatherInfoFragment(WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.createWeatherInfoFragment(WeatherInfoType)",weatherInfoType);try{switch (weatherInfoType) {
            case CURRENT_WEATHER:
                {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.createWeatherInfoFragment(WeatherInfoType)");return new WeatherCurrentInfoFragment();}
            case DAILY_WEATHER_FORECAST:
                {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.createWeatherInfoFragment(WeatherInfoType)");return new WeatherDailyWeatherForecastChildFragment();}
            case THREE_HOURLY_WEATHER_FORECAST:
                {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.createWeatherInfoFragment(WeatherInfoType)");return new WeatherThreeHourlyForecastChildSwipeFragment();}
            default:
                throw new IllegalWeatherInfoTypeArgumentException(weatherInfoType);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherInfoFragment com.haringeymobile.ukweather.weather.WeatherInfoFragment.createWeatherInfoFragment(WeatherInfoType)",throwable);throw throwable;}
    }

    /**
     * Obtains a bundle with the arguments, to be used to instantiate a new weather information
     * fragment.
     *
     * @param cityName   the name of the city for which the weather information was requested and
     *                   obtained
     * @param jsonString JSON weather information data in textual form
     * @return an argument bundle
     */
    private static Bundle getArgumentBundle(String cityName, String jsonString) {
        com.mijack.Xlog.logStaticMethodEnter("android.os.Bundle com.haringeymobile.ukweather.weather.WeatherInfoFragment.getArgumentBundle(java.lang.String,java.lang.String)",cityName,jsonString);try{Bundle args = new Bundle();
        args.putString(CITY_NAME, cityName);
        args.putString(JSON_STRING, jsonString);
        {com.mijack.Xlog.logStaticMethodExit("android.os.Bundle com.haringeymobile.ukweather.weather.WeatherInfoFragment.getArgumentBundle(java.lang.String,java.lang.String)");return args;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.os.Bundle com.haringeymobile.ukweather.weather.WeatherInfoFragment.getArgumentBundle(java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

    @Override
    public void onAttach(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onAttach(android.content.Context)",this,context);try{super.onAttach(context);
        iconCacheRequestListener = (IconCacheRequestListener) context;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onAttach(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onAttach(android.content.Context)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        weatherInformationDisplayer = new WeatherInformationDisplayer(getContext(),
                iconCacheRequestListener.getIconMemoryCache());com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{View view = inflater.inflate(R.layout.fragment_common_weather_info, container, false);
        getCommonViews(view);
        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.weather.WeatherInfoFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Obtain the text and image views to be displayed in all types of weather information
     * fragments.
     *
     * @param view the root view for the fragment
     */
    protected void getCommonViews(View view) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.getCommonViews(android.view.View)",this,view);try{extraInfoTextView = (TextView) view.findViewById(R.id.city_extra_info_text_view);
        conditionsTextView = (TextView) view.findViewById(R.id.weather_conditions_text_view);
        conditionsImageView = (ImageView) view.findViewById(R.id.weather_conditions_image_view);
        temperatureTextView = (TextView) view.findViewById(R.id.temperature_text_view);
        pressureTextView = (TextView) view.findViewById(R.id.atmospheric_pressure_text_view);
        humidityTextView = (TextView) view.findViewById(R.id.humidity_text_view);
        windTextView = (TextView) view.findViewById(R.id.wind_text_view);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.getCommonViews(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.getCommonViews(android.view.View)",this,throwable);throw throwable;}
    }

    /**
     * Displays the specified weather information on the screen.
     *
     * @param weatherInformation various parameters describing weather
     */
    public void displayWeather(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeather(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{displayExtraInfo(weatherInformation);
        displayConditions(weatherInformation);
        displayWeatherNumericParametersText(weatherInformation);
        displayWindInfo(weatherInformation);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeather(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeather(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

    /**
     * Displays specific details, depending on the requested weather information type - typically,
     * a city name, and, if applicable, the date and time information.
     *
     * @param weatherInformation various parameters describing weather
     */
    protected abstract void displayExtraInfo(WeatherInformation weatherInformation);

    /**
     * Describes and illustrates the weather.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayConditions(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayConditions(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{weatherInformationDisplayer.displayConditions(weatherInformation, conditionsTextView,
                conditionsImageView);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayConditions(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayConditions(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

    /**
     * Displays weather temperature, pressure, and humidity.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWeatherNumericParametersText(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeatherNumericParametersText(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{weatherInformationDisplayer.displayWeatherNumericParametersText(weatherInformation,
                temperatureTextView, pressureTextView, humidityTextView);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeatherNumericParametersText(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWeatherNumericParametersText(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

    /**
     * Displays wind speed and direction.
     *
     * @param weatherInformation various parameters describing weather
     */
    private void displayWindInfo(WeatherInformation weatherInformation) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWindInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,weatherInformation);try{weatherInformationDisplayer.displayWindInfo(weatherInformation, windTextView);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWindInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoFragment.displayWindInfo(com.haringeymobile.ukweather.data.objects.WeatherInformation)",this,throwable);throw throwable;}
    }

    protected String getDateString(Context context, Date date) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getDateString(android.content.Context,java.util.Date)",this,context,date);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getDateString(android.content.Context,java.util.Date)",this);return DateFormat.getMediumDateFormat(context).format(date);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getDateString(android.content.Context,java.util.Date)",this,throwable);throw throwable;}
    }

    protected String getTimeString(Context context, Date date) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getTimeString(android.content.Context,java.util.Date)",this,context,date);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getTimeString(android.content.Context,java.util.Date)",this);return DateFormat.getTimeFormat(context).format(date);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.weather.WeatherInfoFragment.getTimeString(android.content.Context,java.util.Date)",this,throwable);throw throwable;}
    }

}