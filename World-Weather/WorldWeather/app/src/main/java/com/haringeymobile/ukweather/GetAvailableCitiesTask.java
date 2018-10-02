package com.haringeymobile.ukweather;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.data.JsonFetcher;
import com.haringeymobile.ukweather.data.objects.CityCurrentWeather;
import com.haringeymobile.ukweather.data.objects.Coordinates;
import com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery;
import com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar;
import com.haringeymobile.ukweather.utils.GeneralDialogFragment;
import com.haringeymobile.ukweather.utils.MiscMethods;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A task to process the city search URL and deal with the obtained result.
 */
class GetAvailableCitiesTask extends
        AsyncTaskWithProgressBar<URL, Void, SearchResponseForFindQuery> {

    /**
     * A listener for search response retrieval completion.
     */
    public interface OnCitySearchResponseRetrievedListener {

        /**
         * Reacts to the obtained city search result.
         *
         * @param searchResponseForFindQuery an object corresponding to the JSON string provided by
         *                                   the Open Weather Map 'find cities' query
         */
        void onSearchResponseForFindQueryRetrieved(SearchResponseForFindQuery
                                                           searchResponseForFindQuery);

    }

    private static final String CITY_SEARCH_RESULTS_FRAGMENT_TAG = "ic_action_search results";
    private static final String NO_CITIES_FOUND_DIALOG_FRAGMENT_TAG = "no cities fragment";

    private final FragmentActivity activity;

    /**
     * @param activity an activity from which this task is started
     */
    GetAvailableCitiesTask(FragmentActivity activity) {
        this.activity = activity;
        setContext(activity);
    }

    @Override
    protected SearchResponseForFindQuery doInBackground(URL... params) {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery com.haringeymobile.ukweather.GetAvailableCitiesTask.doInBackground([java.net.URL)",this,params);try{String jsonString;
        try {
            jsonString = new JsonFetcher().getJsonString(params[0]);
        } catch (IOException e) {
            MiscMethods.log("IOException in SearchResponseForFindQuery doInBackground()");
            {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery com.haringeymobile.ukweather.GetAvailableCitiesTask.doInBackground([java.net.URL)",this);return null;}
        }
        {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery com.haringeymobile.ukweather.GetAvailableCitiesTask.doInBackground([java.net.URL)",this);return jsonString == null ? null : new Gson().fromJson(jsonString,
                SearchResponseForFindQuery.class);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery com.haringeymobile.ukweather.GetAvailableCitiesTask.doInBackground([java.net.URL)",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostExecute(SearchResponseForFindQuery result) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.onPostExecute(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,result);try{super.onPostExecute(result);
        if (result == null || result.getCode() != JsonFetcher.HTTP_STATUS_CODE_OK) {
            displayErrorMessage();
        } else if (result.getCount() < 1) {
            showNoCitiesFoundAlertDialog();
        } else {
            dealWithSearchResponseForFindCitiesQuery(result);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.onPostExecute(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.onPostExecute(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    /**
     * Displays the network connection error message.
     */
    private void displayErrorMessage() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.displayErrorMessage()",this);try{if (activity != null) {
            Toast.makeText(activity, R.string.error_message_no_connection, Toast.LENGTH_SHORT)
                    .show();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.displayErrorMessage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.displayErrorMessage()",this,throwable);throw throwable;}
    }

    /**
     * Shows an alert dialog informing that no cities were found for the query.
     */
    private void showNoCitiesFoundAlertDialog() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showNoCitiesFoundAlertDialog()",this);try{String dialogTitle = activity.getResources().getString(
                R.string.dialog_title_no_cities_found);
        String dialogMessage = MiscMethods.getNoCitiesFoundDialogMessage(activity.getResources());
        DialogFragment dialogFragment = GeneralDialogFragment.newInstance(dialogTitle,
                dialogMessage);
        dialogFragment.show(activity.getSupportFragmentManager(),
                NO_CITIES_FOUND_DIALOG_FRAGMENT_TAG);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showNoCitiesFoundAlertDialog()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showNoCitiesFoundAlertDialog()",this,throwable);throw throwable;}
    }

    /**
     * Handles the city search response.
     *
     * @param result a city search response, containing found cities and related data
     */
    private void dealWithSearchResponseForFindCitiesQuery(SearchResponseForFindQuery result) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.dealWithSearchResponseForFindCitiesQuery(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,result);try{informActivityAboutObtainedSearchResponse(result);
        showDialogWithSearchResults(result);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.dealWithSearchResponseForFindCitiesQuery(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.dealWithSearchResponseForFindCitiesQuery(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    /**
     * Passes the city search response to the activity that started this task for further
     * processing.
     *
     * @param result a city search response, containing found cities and related data
     */
    private void informActivityAboutObtainedSearchResponse(SearchResponseForFindQuery result) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.informActivityAboutObtainedSearchResponse(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,result);try{com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.informActivityAboutObtainedSearchResponse(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);try {
            OnCitySearchResponseRetrievedListener listener =
                    (OnCitySearchResponseRetrievedListener) activity;
            listener.onSearchResponseForFindQueryRetrieved(result);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnCitySearchResponseRetrievedListener");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.informActivityAboutObtainedSearchResponse(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    /**
     * Creates and shows a dialog with the list of found city names (so the user can choose one of
     * them).
     *
     * @param result a city search response, containing found cities and related data
     */
    private void showDialogWithSearchResults(SearchResponseForFindQuery result) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showDialogWithSearchResults(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,result);try{ArrayList<String> foundCityNames = getFoundCityNames(result);
        CitySearchResultsDialog citySearchResultsDialog = CitySearchResultsDialog
                .newInstance(foundCityNames);
        citySearchResultsDialog.show(activity.getSupportFragmentManager(),
                CITY_SEARCH_RESULTS_FRAGMENT_TAG);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showDialogWithSearchResults(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.GetAvailableCitiesTask.showDialogWithSearchResults(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    /**
     * Obtains a list of city names satisfying the user's search query.
     *
     * @param result a city search response, containing found cities and related data
     * @return a list of city names (with coordinates)
     */
    private ArrayList<String> getFoundCityNames(SearchResponseForFindQuery result) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.haringeymobile.ukweather.GetAvailableCitiesTask.getFoundCityNames(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,result);try{ArrayList<String> foundCityNames = new ArrayList<>();
        List<CityCurrentWeather> cities = result.getCities();
        for (CityCurrentWeather city : cities) {
            String cityName = getCityName(city);
            foundCityNames.add(cityName);
        }
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.haringeymobile.ukweather.GetAvailableCitiesTask.getFoundCityNames(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this);return foundCityNames;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.haringeymobile.ukweather.GetAvailableCitiesTask.getFoundCityNames(com.haringeymobile.ukweather.data.objects.SearchResponseForFindQuery)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city name to be displayed in the found city list.
     *
     * @param cityCurrentWeather weather and other information about the city
     * @return a city name (with latitude and longitude)
     */
    private String getCityName(CityCurrentWeather cityCurrentWeather) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.GetAvailableCitiesTask.getCityName(com.haringeymobile.ukweather.data.objects.CityCurrentWeather)",this,cityCurrentWeather);try{Coordinates cityCoordinates = cityCurrentWeather.getCoordinates();
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.GetAvailableCitiesTask.getCityName(com.haringeymobile.ukweather.data.objects.CityCurrentWeather)",this);return cityCurrentWeather.getCityName() + ", "
                + cityCurrentWeather.getSystemParameters().getCountry() + "\n("
                + MiscMethods.formatDoubleValue(cityCoordinates.getLatitude(), 2)
                + ", "
                + MiscMethods.formatDoubleValue(cityCoordinates.getLongitude(), 2)
                + ")";}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.GetAvailableCitiesTask.getCityName(com.haringeymobile.ukweather.data.objects.CityCurrentWeather)",this,throwable);throw throwable;}
    }

}