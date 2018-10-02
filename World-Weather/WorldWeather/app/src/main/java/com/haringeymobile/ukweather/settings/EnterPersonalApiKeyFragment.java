package com.haringeymobile.ukweather.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

public class EnterPersonalApiKeyFragment extends DialogFragment {

    interface Listener {

        void onCancelUpdatingPersonalApiKey();

    }

    private Listener listener;

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        listener = (Listener) activity;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onAttach(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{final Activity activity = getActivity();

        final EditText personalKeyEditText = new EditText(activity);
        String currentPersonalApiKey = SharedPrefsHelper.getPersonalApiKeyFromSharedPrefs(activity);
        if (!"".equals(currentPersonalApiKey)) {
            personalKeyEditText.append(currentPersonalApiKey);
        }

        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onCreateDialog(android.os.Bundle)",this);return new AlertDialog.Builder(getActivity())
                .setView(personalKeyEditText)
                .setIcon(R.drawable.ic_edit)
                .setTitle(R.string.dialog_title_enter_personal_key)

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{listener.onCancelUpdatingPersonalApiKey();
                        dismiss();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })

                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{String newKew = personalKeyEditText.getText().toString();
                        SharedPrefsHelper.putPersonalApiKeyIntoSharedPrefs(activity, newKew);
                        dismiss();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })

                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onDetach()",this);try{super.onDetach();
        listener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.settings.EnterPersonalApiKeyFragment.onDetach()",this,throwable);throw throwable;}
    }

}
