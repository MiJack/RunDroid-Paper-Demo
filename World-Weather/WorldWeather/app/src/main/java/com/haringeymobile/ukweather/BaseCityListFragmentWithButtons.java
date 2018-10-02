package com.haringeymobile.ukweather;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.WeatherContentProvider;

/**
 * A fragment containing a list of cities with clickable buttons.
 */
public abstract class BaseCityListFragmentWithButtons extends ListFragment
        implements LoaderCallbacks<Cursor>, OnClickListener {

    /**
     * Columns in the database that will be displayed in a list row.
     */
    protected static final String[] COLUMNS_TO_DISPLAY = new String[]{CityTable.COLUMN_NAME};
    /**
     * Resource IDs of views that will display the data mapped from the
     * database.
     */
    protected static final int[] TO = new int[]{R.id.city_name_in_list_row_text_view};
    /**
     * Loader ID.
     */
    private static final int LOADER_ALL_CITY_RECORDS = 0;

    protected Activity parentActivity;
    protected BaseCityCursorAdapter cursorAdapter;

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        parentActivity = activity;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onActivityCreated(android.os.Bundle)",this,savedInstanceState);try{super.onActivityCreated(savedInstanceState);
        prepareCityList();
        getLoaderManager().initLoader(LOADER_ALL_CITY_RECORDS, null, this);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Prepares the list view to load and display data.
     */
    private void prepareCityList() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.prepareCityList()",this);try{cursorAdapter = getCityCursorAdapter();
        setListAdapter(cursorAdapter);
        setListViewForClicks();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.prepareCityList()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.prepareCityList()",this,throwable);throw throwable;}
    }

    /**
     * Obtains a concrete adapter with specific button set.
     *
     * @return an adapter with specific functionality
     */
    protected abstract BaseCityCursorAdapter getCityCursorAdapter();

    /**
     * Enables the buttons contained by the list items gain focus and react to
     * clicks.
     */
    private void setListViewForClicks() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.setListViewForClicks()",this);try{ListView listView = getListView();
        listView.setItemsCanFocus(true);
        listView.setFocusable(false);
        listView.setFocusableInTouchMode(false);
        listView.setClickable(false);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.setListViewForClicks()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.setListViewForClicks()",this,throwable);throw throwable;}
    }

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onResume()",this);try{super.onResume();
        /*// Starts a new or restarts an existing Loader in this manager*/
        getLoaderManager().restartLoader(0, null, this);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onResume()",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onDetach()",this);try{super.onDetach();
        parentActivity = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onDetach()",this,throwable);throw throwable;}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        com.mijack.Xlog.logMethodEnter("android.support.v4.content.CursorLoader com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onCreateLoader(int,android.os.Bundle)",this,id,args);try{String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = CityTable.COLUMN_ORDERING_VALUE + " DESC";

        CursorLoader cursorLoader = new CursorLoader(parentActivity,
                WeatherContentProvider.CONTENT_URI_CITY_RECORDS, projection,
                selection, selectionArgs, sortOrder);
        {com.mijack.Xlog.logMethodExit("android.support.v4.content.CursorLoader com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onCreateLoader(int,android.os.Bundle)",this);return cursorLoader;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v4.content.CursorLoader com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onCreateLoader(int,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoadFinished(android.support.v4.content.CursorLoader,android.database.Cursor)",this,loader,data);try{cursorAdapter.swapCursor(data);
        if (jumpToTheTopOfList()) {
            ListView listView = getListView();
            if (listView != null) {
                listView.setSelection(0);
            }
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoadFinished(android.support.v4.content.CursorLoader,android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoadFinished(android.support.v4.content.CursorLoader,android.database.Cursor)",this,throwable);throw throwable;}
    }

    protected boolean jumpToTheTopOfList() {
        com.mijack.Xlog.logMethodEnter("boolean com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.jumpToTheTopOfList()",this);try{com.mijack.Xlog.logMethodExit("boolean com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.jumpToTheTopOfList()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.jumpToTheTopOfList()",this,throwable);throw throwable;}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoaderReset(android.support.v4.content.CursorLoader)",this,loader);try{cursorAdapter.swapCursor(null);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoaderReset(android.support.v4.content.CursorLoader)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.BaseCityListFragmentWithButtons.onLoaderReset(android.support.v4.content.CursorLoader)",this,throwable);throw throwable;}
    }

}