package com.haringeymobile.ukweather.weather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityDailyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.CityInfo;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.data.objects.SearchResponseForDailyForecastQuery;
import com.haringeymobile.ukweather.data.objects.SearchResponseForThreeHourlyForecastQuery;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A fragment with a sliding tab and a view pager, used display a number of nested fragments,
 * containing some kind of weather forecast information.
 */
public class WeatherForecastParentFragment extends Fragment {

    private static final String WEATHER_INFORMATION_TYPE = "weather info type";
    private static final String WEATHER_INFO_JSON_STRING = "forecast json";
    private static final String CITY_NAME_NOT_KNOWN = "??";

    /**
     * For the purposes of displaying the three-hourly forecasts divided into separate daily lists,
     * we consider a "morning" to be the time between 5-7 am. So depending on the data provided by
     * the OWM, the morning hour can be 5, 6, or 7 am.
     */
    private static final int EARLIEST_MORNING_HOUR = 5;

    private FragmentActivity parentActivity;
    private WeatherInfoType weatherInfoType;
    /**
     * The name of the city for which the weather forecast has been obtained.
     */
    private String cityName;
    /**
     * A list of JSON strings to be used to instantiate child fragments.
     */
    private List<String> jsonStringsForChildFragments;
    /**
     * A list of JSON string lists to be used when the three-hourly forecast should be displayed
     * as several separate lists.
     */
    private List<ArrayList<String>> jsonStringListsForChildListFragments;

    /**
     * Obtains a new fragment to display weather forecast.
     *
     * @param weatherInfoType a type of weather forecast
     * @param jsonString      textual representation of JSON weather forecast data
     * @return a fragment to display the weather forecast in a view pager
     */
    public static WeatherForecastParentFragment newInstance(WeatherInfoType weatherInfoType,
                                                            String jsonString) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.WeatherForecastParentFragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.newInstance(WeatherInfoType,java.lang.String)",weatherInfoType,jsonString);try{WeatherForecastParentFragment fragment = new WeatherForecastParentFragment();
        Bundle args = new Bundle();
        args.putParcelable(WEATHER_INFORMATION_TYPE, weatherInfoType);
        args.putString(WEATHER_INFO_JSON_STRING, jsonString);
        fragment.setArguments(args);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherForecastParentFragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.newInstance(WeatherInfoType,java.lang.String)");return fragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherForecastParentFragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.newInstance(WeatherInfoType,java.lang.String)",throwable);throw throwable;}
    }

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        parentActivity = (FragmentActivity) activity;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        weatherInfoType = getArguments().getParcelable(WEATHER_INFORMATION_TYPE);
        jsonStringsForChildFragments = new ArrayList<>();
        extractJsonDataForChildFragments();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Converts the JSON string (passed as an argument) to the correct weather information object,
     * and extracts the data required to instantiate nested fragments.
     */
    private void extractJsonDataForChildFragments() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractJsonDataForChildFragments()",this);try{String jsonString = getArguments().getString(WEATHER_INFO_JSON_STRING);
        Gson gson = new Gson();

        if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
            extractDailyForecastJsonData(jsonString, gson);
        } else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
            extractThreeHourlyForecastJsonData(jsonString, gson);
            if (jsonStringsForChildFragments.size() == 0) {
                {com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractJsonDataForChildFragments()",this);return;}
            }
            if (SharedPrefsHelper.getForecastDisplayMode(getContext()) ==
                    ThreeHourlyForecastDisplayMode.LIST) {
                splitThreeHourlyForecastsIntoDailyLists();
            }
        } else {
            throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(weatherInfoType);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractJsonDataForChildFragments()",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city name, and fills the JSON string list with the extracted daily forecast
     * JSON strings.
     *
     * @param jsonString textual representation of JSON 16 day daily weather forecast data
     * @param gson       a converter between JSON strings and Java objects
     */
    private void extractDailyForecastJsonData(String jsonString, Gson gson) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractDailyForecastJsonData(java.lang.String,com.google.gson.Gson)",this,jsonString,gson);try{SearchResponseForDailyForecastQuery searchResponseForDailyForecastQuery = gson.fromJson(
                jsonString, SearchResponseForDailyForecastQuery.class);
        CityInfo cityInfo = searchResponseForDailyForecastQuery.getCityInfo();
        getCityName(cityInfo);

        List<CityDailyWeatherForecast> dailyForecasts = searchResponseForDailyForecastQuery
                .getDailyWeatherForecasts();
        for (CityDailyWeatherForecast forecast : dailyForecasts) {
            jsonStringsForChildFragments.add(gson.toJson(forecast));
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractDailyForecastJsonData(java.lang.String,com.google.gson.Gson)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractDailyForecastJsonData(java.lang.String,com.google.gson.Gson)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city name.
     *
     * @param cityInfo information about the queried city
     */
    private void getCityName(CityInfo cityInfo) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getCityName(com.haringeymobile.ukweather.data.objects.CityInfo)",this,cityInfo);try{/*// TODO The city can be renamed by a user, so we should query the database for the name*/
        /*// It appears that for some cities the query returns with city information missing, in*/
        /*// which case cityInfo will be null*/
        cityName = cityInfo == null ? CITY_NAME_NOT_KNOWN : cityInfo.getCityName();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getCityName(com.haringeymobile.ukweather.data.objects.CityInfo)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getCityName(com.haringeymobile.ukweather.data.objects.CityInfo)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city name, and fills the JSON string list with the extracted three hourly
     * forecast JSON strings.
     *
     * @param jsonString textual representation of JSON 5 days three hourly weather forecast data
     * @param gson       a converter between JSON strings and Java objects
     */
    private void extractThreeHourlyForecastJsonData(String jsonString, Gson gson) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractThreeHourlyForecastJsonData(java.lang.String,com.google.gson.Gson)",this,jsonString,gson);try{SearchResponseForThreeHourlyForecastQuery searchResponseForThreeHourlyForecastQuery = gson
                .fromJson(jsonString, SearchResponseForThreeHourlyForecastQuery.class);
        CityInfo cityInfo = searchResponseForThreeHourlyForecastQuery.getCityInfo();
        /*// It appears that for some cities the query returns with city information missing, in*/
        /*// which case cityInfo will be null*/
        getCityName(cityInfo);
        List<CityThreeHourlyWeatherForecast> threeHourlyForecasts =
                searchResponseForThreeHourlyForecastQuery.getThreeHourlyWeatherForecasts();
        for (CityThreeHourlyWeatherForecast forecast : threeHourlyForecasts) {
            jsonStringsForChildFragments.add(gson.toJson(forecast));
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractThreeHourlyForecastJsonData(java.lang.String,com.google.gson.Gson)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.extractThreeHourlyForecastJsonData(java.lang.String,com.google.gson.Gson)",this,throwable);throw throwable;}
    }

    /**
     * Divides all the three-hourly forecasts into separate lists, to be displayed as view pager
     * pages. These lists correspond to days. Since there are eight three-hourly forecasts in a 24
     * hour day, the lists should have eight forecasts each. However, as the time of the first
     * forecast can be any time of the day, and we would like (for user convenience) to start each
     * list with the morning forecast (around 5-7 in the morning), the first day ("today") will
     * possibly have less or more than eight forecasts. Also, the last day will usually have less
     * than eight three-hourly forecasts.
     */
    private void splitThreeHourlyForecastsIntoDailyLists() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.splitThreeHourlyForecastsIntoDailyLists()",this);try{int firstForecastHour = getFirstThreeHourlyForecastHour();
        int morningStartHour = findMorningStartHour(firstForecastHour);
        int unallocatedThreeHourlyForecastCount = jsonStringsForChildFragments.size();

        jsonStringListsForChildListFragments = new ArrayList<>();
        int forecastHour = firstForecastHour;
        for (int i = 0; i < unallocatedThreeHourlyForecastCount; i++) {
            boolean shouldStartNewDailyList = shouldStartNewDailyList(morningStartHour,
                    forecastHour);
            if (shouldStartNewDailyList) {
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                ArrayList<String> threeHourlyForecastsForOneDay = new ArrayList<>();
                jsonStringListsForChildListFragments.add(threeHourlyForecastsForOneDay);
            }

            getLatestDailyThreeHourlyForecastList().add(jsonStringsForChildFragments.get(i));

            forecastHour += 3;
            forecastHour %= 24;
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.splitThreeHourlyForecastsIntoDailyLists()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.splitThreeHourlyForecastsIntoDailyLists()",this,throwable);throw throwable;}
    }

    /**
     * Obtains the hour of the very first three-hourly forecast provided by OWM.
     *
     * @return the hour in range [0..23]
     */
    private int getFirstThreeHourlyForecastHour() {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getFirstThreeHourlyForecastHour()",this);try{long firstForecastTime = 1000 * new Gson().fromJson(jsonStringsForChildFragments.get(0),
                CityThreeHourlyWeatherForecast.class).getDate();
        Date date = new Date(firstForecastTime);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getFirstThreeHourlyForecastHour()",this);return calendar.get(Calendar.HOUR_OF_DAY);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getFirstThreeHourlyForecastHour()",this,throwable);throw throwable;}
    }

    /**
     * Finds the morning hour that the forecasts for each day should start. This will be 5, 6, or 7.
     *
     * @param firstForecastHour hour of the first three-hourly forecast, provided by OWM
     * @return an hour in range [5..7]
     */
    private int findMorningStartHour(int firstForecastHour) {
        com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.findMorningStartHour(int)",this,firstForecastHour);try{int remainder = (firstForecastHour - EARLIEST_MORNING_HOUR) % 3;
        if (remainder < 0) {
            remainder += 3;
        }
        if (remainder == 0) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.findMorningStartHour(int)",this);return EARLIEST_MORNING_HOUR;}
        } else if (remainder == 1) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.findMorningStartHour(int)",this);return EARLIEST_MORNING_HOUR + 1;}
        } else if (remainder == 2) {
            {com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.findMorningStartHour(int)",this);return EARLIEST_MORNING_HOUR + 2;}
        } else {
            throw new IllegalStateException("Unexpected remainder: " + remainder);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.findMorningStartHour(int)",this,throwable);throw throwable;}
    }

    /**
     * Finds whether the new daily three hourly list should be created. This will be the case if:
     * (a) currently there are no daily lists at all, or
     * (b) the time of the three-hourly forecast under consideration is a morning hour, unless the
     * current daily list is the only daily list so far, and it only contains forecasts at hours
     * in range [0..4]. That is, if the very first three-hourly forecast time is between 0 and 4 am,
     * then the first daily list will contain all the three hourly forecast until the next day's
     * morning.
     *
     * @param morningStartHour hour at which the day starts (around 5-7 am, depending on the data
     *                         OWM provides
     * @param forecastHour     hour of the three-hourly forecast, provided by the OWM
     * @return true if a new daily list should be created and added to the
     * jsonStringListsForChildListFragments
     */
    private boolean shouldStartNewDailyList(int morningStartHour, int forecastHour) {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.shouldStartNewDailyList(int,int)",this,morningStartHour,forecastHour);try{if (jsonStringListsForChildListFragments.size() == 0) {
            {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.shouldStartNewDailyList(int,int)",this);return true;}
        }
        if (forecastHour == morningStartHour) {
            int threeHourlyForecastCountInCurrentDailyList =
                    getLatestDailyThreeHourlyForecastList().size();
            {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.shouldStartNewDailyList(int,int)",this);return forecastHour - 3 * threeHourlyForecastCountInCurrentDailyList < 0;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.shouldStartNewDailyList(int,int)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.shouldStartNewDailyList(int,int)",this,throwable);throw throwable;}
    }

    private ArrayList<String> getLatestDailyThreeHourlyForecastList() {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getLatestDailyThreeHourlyForecastList()",this);try{int currentDailyListCount = jsonStringListsForChildListFragments.size();
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getLatestDailyThreeHourlyForecastList()",this);return jsonStringListsForChildListFragments.get(currentDailyListCount - 1);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.getLatestDailyThreeHourlyForecastList()",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{if (jsonStringsForChildFragments.size() == 0) {
            TextView textView = new TextView(parentActivity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText(R.string.error_message_no_data);
            textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_large));
            {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return textView;}
        }
        View view = inflater.inflate(R.layout.sliding_tab_host, container, false);
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(
                R.id.tabs);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        WeatherForecastPagerAdapter pagerAdapter = new WeatherForecastPagerAdapter(
                parentActivity.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);

        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onDetach()",this);try{super.onDetach();
        parentActivity = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherForecastParentFragment.onDetach()",this,throwable);throw throwable;}
    }

    /**
     * An adapter to populate view pager with weather forecast fragments.
     */
    private class WeatherForecastPagerAdapter extends FragmentStatePagerAdapter {

        private static final String DAY_TEMPLATE = "E  MMM dd";
        private static final String TIME_TEMPLATE = "E  HH:mm";

        public WeatherForecastPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            com.mijack.Xlog.logMethodEnter("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitle(int)",this,position);try{if (weatherInfoType == WeatherInfoType.DAILY_WEATHER_FORECAST) {
                {com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitle(int)",this);return getPageTitleForDailyWeatherForecast(position);}
            } else if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
                {com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitle(int)",this);return getPageTitleForThreeHourlyWeatherForecast(position);}
            } else {
                throw new WeatherInfoType.IllegalWeatherInfoTypeArgumentException(weatherInfoType);
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitle(int)",this,throwable);throw throwable;}
        }

        /**
         * Obtains the page title for the single day weather forecast.
         *
         * @param position position in a view pager for the requested title
         * @return a formatted date string
         */
        private CharSequence getPageTitleForDailyWeatherForecast(int position) {
            com.mijack.Xlog.logMethodEnter("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForDailyWeatherForecast(int)",this,position);try{long time = 1000 * new Gson().fromJson(jsonStringsForChildFragments.get(position),
                    CityDailyWeatherForecast.class).getDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyLocalizedPattern(DAY_TEMPLATE);
            {com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForDailyWeatherForecast(int)",this);return simpleDateFormat.format(new Date(time));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForDailyWeatherForecast(int)",this,throwable);throw throwable;}
        }

        /**
         * Obtains the page title for the three hourly weather forecast.
         *
         * @param position position in a view pager for the requested title
         * @return a formatted time or date string
         */
        private CharSequence getPageTitleForThreeHourlyWeatherForecast(int position) {
            com.mijack.Xlog.logMethodEnter("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForThreeHourlyWeatherForecast(int)",this,position);try{String template = isRequestedThreeHourlyForecastInListForm() ?
                    DAY_TEMPLATE : TIME_TEMPLATE;
            String jsonString = isRequestedThreeHourlyForecastInListForm() ?
                    jsonStringListsForChildListFragments.get(position).get(0) :
                    jsonStringsForChildFragments.get(position);

            long time = 1000 * new Gson().fromJson(jsonString,
                    CityThreeHourlyWeatherForecast.class).getDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

            simpleDateFormat.applyLocalizedPattern(template);
            {com.mijack.Xlog.logMethodExit("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForThreeHourlyWeatherForecast(int)",this);return simpleDateFormat.format(new Date(time));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.CharSequence com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getPageTitleForThreeHourlyWeatherForecast(int)",this,throwable);throw throwable;}
        }

        @Override
        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getCount()",this);return isRequestedThreeHourlyForecastInListForm() ?
                    jsonStringListsForChildListFragments.size() :
                    jsonStringsForChildFragments.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getCount()",this,throwable);throw throwable;}
        }

        /**
         * Determines whether the requested forecast is a three-hourly forecast that should be
         * displayed as several daily lists.
         */
        private boolean isRequestedThreeHourlyForecastInListForm() {
            com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.isRequestedThreeHourlyForecastInListForm()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.isRequestedThreeHourlyForecastInListForm()",this);return jsonStringListsForChildListFragments != null;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.isRequestedThreeHourlyForecastInListForm()",this,throwable);throw throwable;}
        }

        @Override
        public Fragment getItem(int position) {
            com.mijack.Xlog.logMethodEnter("android.support.v4.app.Fragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getItem(int)",this,position);try{com.mijack.Xlog.logMethodExit("android.support.v4.app.Fragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getItem(int)",this);return isRequestedThreeHourlyForecastInListForm() ?
                    WeatherThreeHourlyForecastChildListFragment.newInstance(
                            jsonStringListsForChildListFragments.get(position)) :
                    WeatherInfoFragment.newInstance(weatherInfoType, cityName,
                            jsonStringsForChildFragments.get(position));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.app.Fragment com.haringeymobile.ukweather.weather.WeatherForecastParentFragment$WeatherForecastPagerAdapter.getItem(int)",this,throwable);throw throwable;}
        }
    }

}