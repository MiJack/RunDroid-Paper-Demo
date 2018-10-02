package com.haringeymobile.ukweather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * A fragment containing a list of cities with clickable buttons, requesting utility work, such as
 * renaming a city, or removing it from the database.
 */
public class CityListFragmentWithUtilityButtons extends BaseCityListFragmentWithButtons {

    /**
     * A listener for utility button clicks.
     */
    public interface OnUtilityButtonClickedListener {

        /**
         * Reacts to the request to remove the specified city from the database.
         *
         * @param cityId   OpenWeatherMap city ID
         * @param cityName city name in the database
         */
        void onCityRecordDeletionRequested(int cityId, String cityName);

        /**
         * Reacts to the request to rename the specified city.
         *
         * @param cityId           OpenWeatherMap city ID
         * @param cityOriginalName current city name in the database
         */
        void onCityNameChangeRequested(int cityId, String cityOriginalName);
    }

    private OnUtilityButtonClickedListener onUtilityButtonClickedListener;

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        try {
            onUtilityButtonClickedListener = (OnUtilityButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUtilityButtonClickedListener");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{boolean isRemovalModeButton = SharedPrefsHelper.isRemovalModeButton(parentActivity);

        int layoutResourceId = isRemovalModeButton ?
                R.layout.general_drag_sort_list_remove_disabled :
                R.layout.general_drag_sort_list_remove_enabled;

        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return inflater.inflate(layoutResourceId, container, false);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected BaseCityCursorAdapter getCityCursorAdapter() {
        com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.getCityCursorAdapter()",this);try{com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.getCityCursorAdapter()",this);return new CityUtilitiesCursorAdapter(parentActivity,
                R.layout.row_city_list_with_weather_buttons, null, COLUMNS_TO_DISPLAY, TO, 0, this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.BaseCityCursorAdapter com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.getCityCursorAdapter()",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onDetach()",this);try{super.onDetach();
        onUtilityButtonClickedListener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onDetach()",this,throwable);throw throwable;}
    }

    @Override
    protected boolean jumpToTheTopOfList() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.jumpToTheTopOfList()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.jumpToTheTopOfList()",this);return false;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.jumpToTheTopOfList()",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onClick(android.view.View)",this,view);try{int listItemPosition = getListView().getPositionForView(view);
        int cityId = cursorAdapter.getCityId(listItemPosition);
        String cityName = cursorAdapter.getCityName(listItemPosition);

        int viewId = view.getId();
        switch (viewId) {
            case R.id.city_rename_button:
                onUtilityButtonClickedListener.onCityNameChangeRequested(cityId, cityName);
                break;
            case R.id.city_remove_button:
                onUtilityButtonClickedListener.onCityRecordDeletionRequested(cityId, cityName);
                break;
            default:
                throw new IllegalArgumentException("Not supported view ID: " + viewId);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityListFragmentWithUtilityButtons.onClick(android.view.View)",this,throwable);throw throwable;}
    }

}