package com.haringeymobile.ukweather;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery;
import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.database.SqlOperation;
import com.haringeymobile.ukweather.settings.SettingsActivity;
import com.haringeymobile.ukweather.utils.GeneralDialogFragment;
import com.haringeymobile.ukweather.utils.MiscMethods;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;
import com.haringeymobile.ukweather.weather.WeatherForecastParentFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoActivity;
import com.haringeymobile.ukweather.weather.WeatherInfoFragment;
import com.haringeymobile.ukweather.weather.WeatherInfoType;
import com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString;

import java.net.URL;

import static com.haringeymobile.ukweather.settings.SettingsActivity.LANGUAGE_DEFAULT;
import static com.haringeymobile.ukweather.settings.SettingsActivity.PREF_APP_LANGUAGE;
import static com.haringeymobile.ukweather.settings.SettingsActivity.PREF_APP_THEME;

/**
 * An activity containing a {@link CityListFragmentWithWeatherButtons}. On
 * screens with larger width it also has tre second pane to embed a
 * {@link WeatherInfoFragment}.
 */
public class MainActivity extends RefreshingActivity implements
        CityListFragmentWithWeatherButtons.OnWeatherInfoButtonClickedListener,
        GetAvailableCitiesTask.OnCitySearchResponseRetrievedListener,
        CitySearchResultsDialog.OnCityNamesListItemClickedListener,
        AddCityFragment.OnNewCityQueryTextListener,
        FindCitiesQueryProcessor.InvalidQueryListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String CITY_ID = "city id";
    public static final String CITY_NAME = "city name";
    public static final String LIST_FRAGMENT_TAG = "list fragment";
    public static final String WORKER_FRAGMENT_TAG = "worker fragment";
    private static final String ADD_CITY_FRAGMENT_TAG = "add city dialog";
    private static final String QUERY_STRING_TOO_SHORT_ALERT_DIALOG_FRAGMENT_TAG =
            "short query fragment";

    private SearchResponseForFindQuery searchResponseForFindQuery;
    private boolean isDualPane;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);

        isDualPane = findViewById(R.id.weather_info_container) != null;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        workerFragment = (WorkerFragmentToRetrieveJsonString) fragmentManager
                .findFragmentByTag(WORKER_FRAGMENT_TAG);
        if (workerFragment == null) {
            workerFragment = new WorkerFragmentToRetrieveJsonString();
            fragmentTransaction.add(workerFragment, WORKER_FRAGMENT_TAG);
        }
        Fragment cityListFragment = fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG);
        if (cityListFragment == null) {
            cityListFragment = new CityListFragmentWithWeatherButtons();
            fragmentTransaction.add(R.id.city_list_container, cityListFragment,
                    LIST_FRAGMENT_TAG);
        }
        fragmentTransaction.commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        if (searchView != null) {
            handleIntent(getIntent());
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Handles the intent that was provided to custom search suggestions, as described in
     * http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#HandlingIntent
     *
     * @param intent the intent that started this activity. Since this activity is searchable and
     *               "single top", a new intent will replace the old one each time the user
     *               performs a search
     */
    private void handleIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.handleIntent(android.content.Intent)",this,intent);try{final SqlOperation sqlOperation = new SqlOperation(this);
        boolean collapseSearchViewAfterHandlingIntent = true;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            /*// Handle the search request*/
            CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
            if (cursorAdapter != null) {
                int cityCount = cursorAdapter.getCount();
                if (cityCount == 0) {
                    collapseSearchViewAfterHandlingIntent = false;
                    showAlertDialog(R.string.dialog_title_no_cities_found);
                } else {
                    final long[] rowIds = new long[cityCount];
                    for (int i = 0; i < cityCount; i++) {
                        rowIds[i] = cursorAdapter.getItemId(i);
                    }

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity$1.run()",this);try{sqlOperation.setLastOverallQueryTimeToCurrentTime(rowIds);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity$1.run()",this,throwable);throw throwable;}
                        }
                    }).start();
                }
            }
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            /*// Handle a suggestions click*/
            final Uri data = intent.getData();
            final long rowId = Long.valueOf(data.getLastPathSegment());

            new Thread(new Runnable() {

                @Override
                public void run() {
                    com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity$2.run()",this);try{sqlOperation.setLastOverallQueryTimeToCurrentTime(rowId);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity$2.run()",this,throwable);throw throwable;}
                }
            }).start();
        }

        if (collapseSearchViewAfterHandlingIntent) {
            if (searchView != null) {
                searchView.onActionViewCollapsed();
            }
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.handleIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.handleIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onNewIntent(android.content.Intent)",this,intent);try{super.onNewIntent(intent);
        /*// important - we set a new intent as a default intent, so the search suggestions can*/
        /*// be handled properly*/
        setIntent(intent);
        handleIntent(getIntent());com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onNewIntent(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onNewIntent(android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onRestoreInstanceState(android.os.Bundle)",this,savedInstanceState);try{super.onRestoreInstanceState(savedInstanceState);
        String jsonString = savedInstanceState.getString(WEATHER_INFO_JSON_STRING);
        if (jsonString != null) {
            searchResponseForFindQuery = new Gson().fromJson(jsonString,
                    SearchResponseForFindQuery.class);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onRestoreInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onRestoreInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onStart()",this);try{super.onStart();
        if (isDualPane) {
            workerFragment.retrieveLastRequestedWeatherInfo();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onStart()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onSaveInstanceState(android.os.Bundle)",this,outState);try{super.onSaveInstanceState(outState);
        if (searchResponseForFindQuery != null) {
            outState.putString(WEATHER_INFO_JSON_STRING,
                    new Gson().toJson(searchResponseForFindQuery));
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onSharedPreferenceChanged(android.content.SharedPreferences,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,sharedPreferences,key);try{if (PREF_APP_THEME.equals(key)) {
            recreate();
        } else if (PREF_APP_LANGUAGE.equals(key)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String appLocaleCode = preferences.getString(PREF_APP_LANGUAGE, LANGUAGE_DEFAULT);

            String newAppLocaleCode;
            if (appLocaleCode.equals(LANGUAGE_DEFAULT)) {
                newAppLocaleCode = WorldWeatherApplication.systemLocaleCode;
            } else {
                newAppLocaleCode = appLocaleCode;
            }
            MiscMethods.updateLocale(newAppLocaleCode, getResources());

            recreate();
            resetActionBarTitle();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onSharedPreferenceChanged(android.content.SharedPreferences,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onSharedPreferenceChanged(android.content.SharedPreferences,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.MainActivity.onCreateOptionsMenu(android.view.Menu)",this,menu);try{getMenuInflater().inflate(R.menu.main, menu);
        setCitySearching(menu);
        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.MainActivity.onCreateOptionsMenu(android.view.Menu)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.MainActivity.onCreateOptionsMenu(android.view.Menu)",this,throwable);throw throwable;}
    }

    /**
     * Locates the search view in the action bar, and prepares it for city searching.
     *
     * @param menu options menu containing the city search view
     */
    private void setCitySearching(Menu menu) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.setCitySearching(android.view.Menu)",this,menu);try{MenuItem searchItem = menu.findItem(R.id.mi_search_cities);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getResources().getString(R.string.city_searchable_hint));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.setCitySearching(android.view.Menu)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.setCitySearching(android.view.Menu)",this,throwable);throw throwable;}
    }

    @Override
    public void showAlertDialog(final int stringResourceId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.showAlertDialog(int)",this,stringResourceId);try{GeneralDialogFragment.newInstance(getResources().getString(stringResourceId), null).
                show(getSupportFragmentManager(), QUERY_STRING_TOO_SHORT_ALERT_DIALOG_FRAGMENT_TAG);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.showAlertDialog(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.showAlertDialog(int)",this,throwable);throw throwable;}
    }

    /**
     * If there is a network connection, and the user query is valid, starts the task to search the
     * cities satisfying the provided query.
     *
     * @param query a location search text provided by the user
     */
    @Override
    public void onQueryTextSubmit(String query) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onQueryTextSubmit(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,query);try{if (MiscMethods.isUserOnline(MainActivity.this)) {
            FindCitiesQueryProcessor findCitiesQueryProcessor =
                    new FindCitiesQueryProcessor(this, query);
            URL url = findCitiesQueryProcessor.getUrlForFindCitiesQuery(this);
            if (url != null) {
                new GetAvailableCitiesTask(MainActivity.this).execute(url);
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.error_message_no_connection,
                    Toast.LENGTH_SHORT).show();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onQueryTextSubmit(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onQueryTextSubmit(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this,item);try{int id = item.getItemId();
        if (id == R.id.mi_add_city) {
            showAddCityDialog();
        } else if (id == R.id.mi_city_management) {
            Intent cityManagementIntent = new Intent(this, CityManagementActivity.class);
            startActivityWithTransitionAnimation(cityManagementIntent);
        } else if (id == R.id.mi_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityWithTransitionAnimation(settingsIntent);
        } else if (id == R.id.mi_rate_application) {
            goToPlayStore();
        } else if (id == R.id.mi_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivityWithTransitionAnimation(aboutIntent);
        }
        {com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this);return super.onOptionsItemSelected(item);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.MainActivity.onOptionsItemSelected(android.view.MenuItem)",this,throwable);throw throwable;}
    }

    /**
     * Displays a dialog allowing user to search new cities.
     */
    private void showAddCityDialog() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.showAddCityDialog()",this);try{FragmentManager fragmentManager = getSupportFragmentManager();
        AddCityFragment addCityFragment = (AddCityFragment) fragmentManager
                .findFragmentByTag(ADD_CITY_FRAGMENT_TAG);
        if (addCityFragment == null) {
            addCityFragment = new AddCityFragment();
            addCityFragment.show(fragmentManager, ADD_CITY_FRAGMENT_TAG);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.showAddCityDialog()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.showAddCityDialog()",this,throwable);throw throwable;}
    }

    /**
     * Attempts to visit the app's page in the Play Store via the Play Store app. If this fails
     * (the Play Store app not installed on the user's device), the second try is to do so via
     * the browser.
     */
    private void goToPlayStore() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.goToPlayStore()",this);try{final String appPackageName = getPackageName();
        try {
            startActivityWithTransitionAnimation(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivityWithTransitionAnimation(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appPackageName)));
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.goToPlayStore()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.goToPlayStore()",this,throwable);throw throwable;}
    }

    @Override
    public void onSearchResponseForFindQueryRetrieved(
            SearchResponseForFindQuery searchResponseForFindQuery) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onSearchResponseForFindQueryRetrieved(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,searchResponseForFindQuery);try{this.searchResponseForFindQuery = searchResponseForFindQuery;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onSearchResponseForFindQueryRetrieved(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onSearchResponseForFindQueryRetrieved(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    @Override
    public void onFoundCityNamesItemClicked(int position) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onFoundCityNamesItemClicked(int)",this,position);try{AddCityFragment addCityFragment = (AddCityFragment) getSupportFragmentManager()
                .findFragmentByTag(ADD_CITY_FRAGMENT_TAG);
        if (addCityFragment != null) {
            addCityFragment.dismiss();
        }

        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }

        if (searchResponseForFindQuery != null) {
            CityCurrentWeather selectedCityWeather = searchResponseForFindQuery
                    .getCities().get(position);
            String currentWeatherJsonString = new Gson().toJson(selectedCityWeather);

            if (isDualPane) {
                displayRetrievedDataInThisActivity(currentWeatherJsonString,
                        WeatherInfoType.CURRENT_WEATHER);
            }

            /*// Since the Open Weather Map search response for the 'find cities' query contains the*/
            /*// current weather information for each found city, we can cache this weather*/
            /*// information for the selected city in the database, just in case the user requests it*/
            /*// shortly (quite likely, given that s/he had just selected the city).*/
            insertNewRecordOrUpdateCity(selectedCityWeather.getCityId(),
                    selectedCityWeather.getCityName(), currentWeatherJsonString);
            saveWeatherInfoRequest(selectedCityWeather.getCityId(),
                    WeatherInfoType.CURRENT_WEATHER);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onFoundCityNamesItemClicked(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onFoundCityNamesItemClicked(int)",this,throwable);throw throwable;}
    }

    /**
     * Updates the current weather record for the city if it already exists in the database,
     * otherwise inserts a new record.
     *
     * @param cityId                   Open Weather Map ID for the city
     * @param cityName                 the name as provided by the Open Weather Map
     * @param currentWeatherJsonString JSON current weather data
     */
    private void insertNewRecordOrUpdateCity(int cityId, String cityName,
                                             String currentWeatherJsonString) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.insertNewRecordOrUpdateCity(int,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,cityId,cityName,currentWeatherJsonString);try{Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_INSERT_OR_UPDATE_CITY_RECORD);
        intent.putExtra(CITY_ID, cityId);
        intent.putExtra(CITY_NAME, cityName);
        intent.putExtra(WEATHER_INFO_JSON_STRING, currentWeatherJsonString);
        startService(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.insertNewRecordOrUpdateCity(int,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.insertNewRecordOrUpdateCity(int,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString)",this,throwable);throw throwable;}
    }

    /**
     * Saves the requested city and weather information type in the SharedPreferences, so they can
     * be retrieved later and a new request formed automatically.
     *
     * @param cityId          Open Weather Map ID for the requested city
     * @param weatherInfoType requested weather information type
     */
    private void saveWeatherInfoRequest(int cityId, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.saveWeatherInfoRequest(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,cityId,weatherInfoType);try{SharedPrefsHelper.putCityIdIntoSharedPrefs(this, cityId, false);
        SharedPrefsHelper.putLastWeatherInfoTypeIntoSharedPrefs(this, weatherInfoType);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.saveWeatherInfoRequest(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.saveWeatherInfoRequest(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    @Override
    public void onCityWeatherInfoRequested(int cityId, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.onCityWeatherInfoRequested(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,cityId,weatherInfoType);try{workerFragment.retrieveWeatherInfoJsonString(cityId, weatherInfoType);
        saveWeatherInfoRequest(cityId, weatherInfoType);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.onCityWeatherInfoRequested(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.onCityWeatherInfoRequested(int,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    @Override
    public void displayRetrievedData(String jsonString, WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.displayRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,jsonString,weatherInfoType);try{if (isDualPane) {
            displayRetrievedDataInThisActivity(jsonString, weatherInfoType);
        } else {
            displayRetrievedDataInNewActivity(jsonString, weatherInfoType);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.displayRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.displayRetrievedData(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    /**
     * Creates and embeds a new fragment of the correct type to display the obtained weather data
     * in the second pane of this activity.
     *
     * @param jsonString      JSON weather information data in textual form
     * @param weatherInfoType a type of the retrieved weather data
     */
    private void displayRetrievedDataInThisActivity(String jsonString,
                                                    WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInThisActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,jsonString,weatherInfoType);try{Fragment fragment;
        if (weatherInfoType == WeatherInfoType.CURRENT_WEATHER) {
            fragment = WeatherInfoFragment.newInstance(weatherInfoType, null, jsonString);
        } else {
            fragment = WeatherForecastParentFragment.newInstance(weatherInfoType, jsonString);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weather_info_container, fragment).commit();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInThisActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInThisActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    /**
     * Starts a new activity to display the obtained weather data.
     *
     * @param jsonString      JSON weather information data in textual form
     * @param weatherInfoType a type of the retrieved weather data
     */
    private void displayRetrievedDataInNewActivity(String jsonString,
                                                   WeatherInfoType weatherInfoType) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInNewActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,jsonString,weatherInfoType);try{Intent intent = new Intent(this, WeatherInfoActivity.class);
        intent.putExtra(WEATHER_INFORMATION_TYPE, (Parcelable) weatherInfoType);
        intent.putExtra(WEATHER_INFO_JSON_STRING, jsonString);
        startActivityWithTransitionAnimation(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInNewActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.displayRetrievedDataInNewActivity(com.haringeymobile.ukweather.weather.WorkerFragmentToRetrieveJsonString,com.haringeymobile.ukweather.weather.WeatherInfoType)",this,throwable);throw throwable;}
    }

    private void startActivityWithTransitionAnimation(Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.MainActivity.startActivityWithTransitionAnimation(android.content.Intent)",this,intent);try{startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.MainActivity.startActivityWithTransitionAnimation(android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.MainActivity.startActivityWithTransitionAnimation(android.content.Intent)",this,throwable);throw throwable;}
    }

}