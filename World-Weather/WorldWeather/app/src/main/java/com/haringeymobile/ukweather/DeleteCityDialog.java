package com.haringeymobile.ukweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A dialog asking for the city deletion confirmation.
 */
public class DeleteCityDialog extends DialogFragment {

    /**
     * A listener for dialog's button clicks.
     */
    public interface OnDialogButtonClickedListener {

        /**
         * Reacts to the confirmation that the specified city should be deleted.
         *
         * @param cityId OpenWeatherMap ID for the city to be deleted
         */
        void onCityRecordDeletionConfirmed(int cityId);
    }

    private static final String CITY_NAME = "city name";

    private Activity parentActivity;
    private OnDialogButtonClickedListener onDialogButtonClickedListener;

    /**
     * Creates a new dialog asking for the city deletion confirmation.
     *
     * @param cityId   OpenWeatherMap ID for the city to be deleted
     * @param cityName city name in the database
     * @return a new dialog fragment with the specified arguments
     */
    public static DeleteCityDialog newInstance(int cityId, String cityName) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.DeleteCityDialog com.haringeymobile.ukweather.DeleteCityDialog.newInstance(int,java.lang.String)",cityId,cityName);try{DeleteCityDialog dialogFragment = new DeleteCityDialog();
        Bundle b = new Bundle();
        b.putInt(CityManagementActivity.CITY_ID, cityId);
        b.putString(CITY_NAME, cityName);
        dialogFragment.setArguments(b);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.DeleteCityDialog com.haringeymobile.ukweather.DeleteCityDialog.newInstance(int,java.lang.String)");return dialogFragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.DeleteCityDialog com.haringeymobile.ukweather.DeleteCityDialog.newInstance(int,java.lang.String)",throwable);throw throwable;}
    }

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.DeleteCityDialog.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        parentActivity = getActivity();
        try {
            onDialogButtonClickedListener = (OnDialogButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogButtonClickedListener");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.DeleteCityDialog.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.DeleteCityDialog.onDetach()",this);try{super.onDetach();
        parentActivity = null;
        onDialogButtonClickedListener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.DeleteCityDialog.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.DeleteCityDialog.onDetach()",this,throwable);throw throwable;}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.haringeymobile.ukweather.DeleteCityDialog.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{String title = getDialogTitle();
        OnClickListener dialogOnClickListener = getDialogOnClickListener();
        CharSequence positiveButtonText = parentActivity.getResources()
                .getString(android.R.string.ok);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.haringeymobile.ukweather.DeleteCityDialog.onCreateDialog(android.os.Bundle)",this);return new AlertDialog.Builder(parentActivity)
                .setIcon(R.drawable.ic_delete)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, dialogOnClickListener)
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.haringeymobile.ukweather.DeleteCityDialog.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Obtains the city deletion dialog title.
     *
     * @return text asking for the city deletion confirmation
     */
    private String getDialogTitle() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.DeleteCityDialog.getDialogTitle()",this);try{Resources res = parentActivity.getResources();
        final String cityName = getArguments().getString(CITY_NAME);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.DeleteCityDialog.getDialogTitle()",this);return String.format(res.getString(R.string.dialog_title_delete_city), cityName);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.DeleteCityDialog.getDialogTitle()",this,throwable);throw throwable;}
    }

    /**
     * Obtains a listener for dialog's button clicks.
     *
     * @return a listener to handle button clicks
     */
    private OnClickListener getDialogOnClickListener() {
        com.mijack.Xlog.logMethodEnter("android.content.DialogInterface.OnClickListener com.haringeymobile.ukweather.DeleteCityDialog.getDialogOnClickListener()",this);try{OnClickListener dialogOnClickListener = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.DeleteCityDialog$1.onClick(android.content.DialogInterface,int)",this,dialog,whichButton);try{onDialogButtonClickedListener
                        .onCityRecordDeletionConfirmed(getArguments().getInt(
                                CityManagementActivity.CITY_ID));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.DeleteCityDialog$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.DeleteCityDialog$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        };
        {com.mijack.Xlog.logMethodExit("android.content.DialogInterface.OnClickListener com.haringeymobile.ukweather.DeleteCityDialog.getDialogOnClickListener()",this);return dialogOnClickListener;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.content.DialogInterface.OnClickListener com.haringeymobile.ukweather.DeleteCityDialog.getDialogOnClickListener()",this,throwable);throw throwable;}
    }
}
