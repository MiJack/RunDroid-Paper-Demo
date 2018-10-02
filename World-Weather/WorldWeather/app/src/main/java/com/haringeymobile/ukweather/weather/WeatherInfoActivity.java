package com.haringeymobile.ukweather.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.haringeymobile.ukweather.MainActivity;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.RefreshingActivity;
import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.SqlOperation;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An activity displaying some kind of weather information.
 */
public class WeatherInfoActivity extends RefreshingActivity {

    /**
     * A tag in the string resources, indicating that the MainActivity currently has a second pane
     * to contain a WeatherInfoFragment, so this activity is not necessary and should finish.
     */
    public static final String DUAL_PANE = "dual_pane";
    /**
     * A string to separate the default toolbar title text, and the queried city name.
     */
    private static final String TOOLBAR_TITLE_AND_CITY_NAME_SEPARATOR = "  |  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        boolean isDualPane = DUAL_PANE.equals(getResources().getString(
                R.string.weather_info_frame_layout_pane_number_tag));
        if (isDualPane) {
            finish();
        } else {
            displayContent();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Sets the action bar and adds the required fragment to the layout.
     */
    private void displayContent() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayContent()",this);try{setContentView(R.layout.activity_weather_info);

        Intent intent = getIntent();
        WeatherInfoType weatherInfoType = intent.getParcelableExtra(
                RefreshingActivity.WEATHER_INFORMATION_TYPE);
        String jsonString = intent.getStringExtra(RefreshingActivity.WEATHER_INFO_JSON_STRING);
        addRequiredFragment(weatherInfoType, jsonString);

        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setToolbarTitle(weatherInfoType, toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayContent()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayContent()",this,throwable);throw throwable;}
    }

    /**
     * Creates and adds the correct type of fragment to the activity.
     *
     * @param weatherInfoType a kind of weather information to be displayed on the screen
     * @param jsonString      a string representing the JSON weather data
     */
    private void addRequiredFragment(WeatherInfoType weatherInfoType, String jsonString) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.addRequiredFragment(WeatherInfoType,java.lang.String)",this,weatherInfoType,jsonString);try{FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = weatherInfoType == WeatherInfoType.CURRENT_WEATHER ?
                WeatherInfoFragment.newInstance(weatherInfoType, null, jsonString)
                : WeatherForecastParentFragment.newInstance(weatherInfoType, jsonString);
        fragmentTransaction.replace(R.id.weather_info_container, fragment);

        workerFragment = (WorkerFragmentToRetrieveJsonString) fragmentManager
                .findFragmentByTag(MainActivity.WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, MainActivity.WORKER_FRAGMENT_TAG);
        }

        fragmentTransaction.commit();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.addRequiredFragment(WeatherInfoType,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.addRequiredFragment(WeatherInfoType,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Determines and sets the toolbar text.
     *
     * @param weatherInfoType a kind of weather information to be displayed on the screen
     * @param toolbar         toolbar for this activity
     */
    private void setToolbarTitle(WeatherInfoType weatherInfoType, Toolbar toolbar) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.setToolbarTitle(WeatherInfoType,android.support.v7.widget.Toolbar)",this,weatherInfoType,toolbar);try{String title = getResources().getString(weatherInfoType.getLabelResourceId());
        toolbar.setTitle(title);
        if (weatherInfoType == WeatherInfoType.THREE_HOURLY_WEATHER_FORECAST) {
            updateTitleWithCityNameIfNecessary(toolbar, title);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.setToolbarTitle(WeatherInfoType,android.support.v7.widget.Toolbar)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.setToolbarTitle(WeatherInfoType,android.support.v7.widget.Toolbar)",this,throwable);throw throwable;}
    }

    /**
     * If the three-hourly forecast should be displayed as a set of daily forecast lists,
     * the toolbar is updated with the queried city name.
     *
     * @param toolbar      toolbar for this activity
     * @param defaultTitle regular title without the city name
     */
    private void updateTitleWithCityNameIfNecessary(final Toolbar toolbar,
                                                    final String defaultTitle) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.updateTitleWithCityNameIfNecessary(android.support.v7.widget.Toolbar,java.lang.String)",this,toolbar,defaultTitle);try{final Context context = this;
        if (SharedPrefsHelper.getForecastDisplayMode(context) ==
                ThreeHourlyForecastDisplayMode.LIST) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity$1.run()",this);try{int lastQueriedCityId = SharedPrefsHelper.getCityIdFromSharedPrefs(context);
                    if (lastQueriedCityId != CityTable.CITY_ID_DOES_NOT_EXIST) {
                        String queriedCityName = new SqlOperation(context)
                                .findCityName(lastQueriedCityId);
                        String updatedTitle = defaultTitle + TOOLBAR_TITLE_AND_CITY_NAME_SEPARATOR +
                                queriedCityName;
                        toolbar.setTitle(updatedTitle);
                    }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity$1.run()",this,throwable);throw throwable;}

                }
            }).start();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.updateTitleWithCityNameIfNecessary(android.support.v7.widget.Toolbar,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.updateTitleWithCityNameIfNecessary(android.support.v7.widget.Toolbar,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestart() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onRestart()",this);try{super.onRestart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        WorkerFragmentToRetrieveJsonString workerFragment =
                (WorkerFragmentToRetrieveJsonString) fragmentManager.findFragmentByTag(
                        MainActivity.WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, MainActivity.WORKER_FRAGMENT_TAG);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        workerFragment.retrieveLastRequestedWeatherInfo();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onRestart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.onRestart()",this,throwable);throw throwable;}
    }

    @Override
    protected void displayRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayRetrievedData(java.lang.String,WeatherInfoType)",this,jsonString,weatherInfoType);try{addRequiredFragment(weatherInfoType, jsonString);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayRetrievedData(java.lang.String,WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherInfoActivity.displayRetrievedData(java.lang.String,WeatherInfoType)",this,throwable);throw throwable;}
    }

}