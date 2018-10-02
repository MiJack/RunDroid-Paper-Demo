package com.haringeymobile.ukweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.database.GeneralDatabaseService;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An activity to manage city deletion and renaming.
 */
public class CityManagementActivity extends ThemedActivity implements
        CityListFragmentWithUtilityButtons.OnUtilityButtonClickedListener,
        CityUtilitiesCursorAdapter.Listener,
        DeleteCityDialog.OnDialogButtonClickedListener {

    public static final String CITY_ID = "city id";
    public static final String CITY_NEW_NAME = "city new name";
    public static final String CITY_ORDER_FROM = "city order x";
    public static final String CITY_ORDER_TO = "city order y";
    static final String CITY_DELETE_DIALOG_FRAGMENT_TAG = "delete city dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onCityNameChangeRequested(final int cityId, final String originalName) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.onCityNameChangeRequested(int,java.lang.String)",this,cityId,originalName);try{AlertDialog.Builder cityNameChangeDialog = new AlertDialog.Builder(this);

        String dialogTitle = getDialogTitle(originalName);
        cityNameChangeDialog.setTitle(dialogTitle);

        final EditText cityNameEditText = getNewCityNameEditText();
        cityNameChangeDialog.setView(cityNameEditText);

        DialogInterface.OnClickListener dialogOnClickListener = getDialogOnClickListener(
                cityId, originalName, cityNameEditText);
        cityNameChangeDialog.setPositiveButton(android.R.string.ok, dialogOnClickListener);

        cityNameChangeDialog.show();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.onCityNameChangeRequested(int,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.onCityNameChangeRequested(int,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Creates the dialog's title.
     *
     * @param originalName current city name
     * @return the dialog title, asking to enter a new name for the city
     */
    private String getDialogTitle(final String originalName) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.CityManagementActivity.getDialogTitle(java.lang.String)",this,originalName);try{Resources res = getResources();
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.CityManagementActivity.getDialogTitle(java.lang.String)",this);return String.format(res.getString(R.string.dialog_title_rename_city), originalName);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.CityManagementActivity.getDialogTitle(java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Obtains an editable view were the new city name should be typed in.
     *
     * @return an editable view for the new city name
     */
    private EditText getNewCityNameEditText() {
        com.mijack.Xlog.logMethodEnter("android.widget.EditText com.haringeymobile.ukweather.CityManagementActivity.getNewCityNameEditText()",this);try{final EditText cityNameEditText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cityNameEditText.setLayoutParams(lp);
        {com.mijack.Xlog.logMethodExit("android.widget.EditText com.haringeymobile.ukweather.CityManagementActivity.getNewCityNameEditText()",this);return cityNameEditText;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.EditText com.haringeymobile.ukweather.CityManagementActivity.getNewCityNameEditText()",this,throwable);throw throwable;}
    }

    /**
     * Obtain a listener for dialog button clicks.
     *
     * @param cityId           OpenWeatherMap city ID
     * @param originalName     current city name
     * @param cityNameEditText an editable view for the new city name
     * @return a dialog button clicks listener
     */
    private DialogInterface.OnClickListener getDialogOnClickListener(
            final int cityId, final String originalName,
            final EditText cityNameEditText) {
        com.mijack.Xlog.logMethodEnter("DialogInterface.OnClickListener com.haringeymobile.ukweather.CityManagementActivity.getDialogOnClickListener(int,java.lang.String,android.widget.EditText)",this,cityId,originalName,cityNameEditText);try{DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity$1.onClick(android.content.DialogInterface,int)",this,dialog,whichButton);try{String newName = cityNameEditText.getText().toString();
                if (newName.length() == 0) {
                    showEmptyNameErrorMessage();
                } else {
                    boolean userNameHasBeenChanged = !newName.equals(originalName);
                    if (userNameHasBeenChanged) {
                        renameCity(cityId, newName);
                    }
                }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        };
        {com.mijack.Xlog.logMethodExit("DialogInterface.OnClickListener com.haringeymobile.ukweather.CityManagementActivity.getDialogOnClickListener(int,java.lang.String,android.widget.EditText)",this);return dialogOnClickListener;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("DialogInterface.OnClickListener com.haringeymobile.ukweather.CityManagementActivity.getDialogOnClickListener(int,java.lang.String,android.widget.EditText)",this,throwable);throw throwable;}
    }

    /**
     * Displays a message informing that the new name cannot be an empty string.
     */
    private void showEmptyNameErrorMessage() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.showEmptyNameErrorMessage()",this);try{Toast.makeText(this, R.string.message_enter_city_name, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.showEmptyNameErrorMessage()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.showEmptyNameErrorMessage()",this,throwable);throw throwable;}
    }

    /**
     * Renames the specified city.
     *
     * @param cityId  OpenWeatherMap city ID
     * @param newName a new name for the city
     */
    private void renameCity(int cityId, String newName) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.renameCity(int,java.lang.String)",this,cityId,newName);try{Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_RENAME_CITY);
        intent.putExtra(CITY_ID, cityId);
        intent.putExtra(CITY_NEW_NAME, newName);
        startService(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.renameCity(int,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.renameCity(int,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onCityRecordDeletionRequested(int cityId, String cityName) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionRequested(int,java.lang.String)",this,cityId,cityName);try{FragmentManager fragmentManager = getSupportFragmentManager();
        DeleteCityDialog dialogFragment = (DeleteCityDialog) fragmentManager
                .findFragmentByTag(CITY_DELETE_DIALOG_FRAGMENT_TAG);
        if (dialogFragment == null) {
            dialogFragment = DeleteCityDialog.newInstance(cityId, cityName);
            dialogFragment.show(fragmentManager, CITY_DELETE_DIALOG_FRAGMENT_TAG);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionRequested(int,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionRequested(int,java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onCityRecordDeletionConfirmed(int cityId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionConfirmed(int)",this,cityId);try{removeCityById(cityId);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionConfirmed(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.onCityRecordDeletionConfirmed(int)",this,throwable);throw throwable;}
    }

    @Override
    public void removeCityById(int cityId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.removeCityById(int)",this,cityId);try{updateLastRequestedCityInfo(cityId);
        Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_DELETE_CITY_RECORDS);
        intent.putExtra(CITY_ID, cityId);
        startService(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.removeCityById(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.removeCityById(int)",this,throwable);throw throwable;}
    }

    /**
     * Makes note in the SharedPreferences if the city to be deleted is also the
     * last city to be queried for weather information (so the next time an
     * automatic weather information request is made, it won't be necessary to
     * go to the database to check if the city still exists).
     *
     * @param cityId OpenWeatherMap city ID
     */
    private void updateLastRequestedCityInfo(int cityId) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.updateLastRequestedCityInfo(int)",this,cityId);try{int lastCityId = SharedPrefsHelper.getCityIdFromSharedPrefs(this);
        if (cityId == lastCityId) {
            SharedPrefsHelper.putCityIdIntoSharedPrefs(this, CityTable.CITY_ID_DOES_NOT_EXIST,
                    false);
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.updateLastRequestedCityInfo(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.updateLastRequestedCityInfo(int)",this,throwable);throw throwable;}
    }

    @Override
    public void dragCity(int cityOrderFrom, int cityOrderTo) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityManagementActivity.dragCity(int,int)",this,cityOrderFrom,cityOrderTo);try{Intent intent = new Intent(this, GeneralDatabaseService.class);
        intent.setAction(GeneralDatabaseService.ACTION_DRAG_CITY);
        intent.putExtra(CITY_ORDER_FROM, cityOrderFrom);
        intent.putExtra(CITY_ORDER_TO, cityOrderTo);
        startService(intent);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityManagementActivity.dragCity(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityManagementActivity.dragCity(int,int)",this,throwable);throw throwable;}
    }

}