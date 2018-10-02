package com.haringeymobile.ukweather.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.haringeymobile.ukweather.R;

/**
 * ListPreference with the customized list item view.
 */
public class CustomListPreference extends ListPreference {

    public CustomListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.CustomListPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this,builder);try{ListAdapter listAdapter = new ArrayAdapter(getContext(),
                R.layout.row_custom_preference_list, getEntries());

        builder.setAdapter(listAdapter, this);
        super.onPrepareDialogBuilder(builder);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.CustomListPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.CustomListPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this,throwable);throw throwable;}
    }

}
