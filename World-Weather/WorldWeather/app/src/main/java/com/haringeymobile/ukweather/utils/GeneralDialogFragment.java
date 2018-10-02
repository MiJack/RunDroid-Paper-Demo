package com.haringeymobile.ukweather.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * A dialog that displays a title and an (optional) message, and can be dismissed by pressing
 * a button.
 */
public class GeneralDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    /**
     * Creates a new DialogFragment, and sets the provided title and message arguments.
     *
     * @param title   dialog title
     * @param message dialog message; may be null, in which case the message is not displayed
     * @return a new dialog
     */
    public static GeneralDialogFragment newInstance(String title, String message) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.utils.GeneralDialogFragment com.haringeymobile.ukweather.utils.GeneralDialogFragment.newInstance(java.lang.String,java.lang.String)",title,message);try{GeneralDialogFragment fragment = new GeneralDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.utils.GeneralDialogFragment com.haringeymobile.ukweather.utils.GeneralDialogFragment.newInstance(java.lang.String,java.lang.String)");return fragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.utils.GeneralDialogFragment com.haringeymobile.ukweather.utils.GeneralDialogFragment.newInstance(java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.haringeymobile.ukweather.utils.GeneralDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{Bundle args = getArguments();
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener onClickListener = getDialogOnClickListener();
        builder.setTitle(title);
        if (message != null) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.haringeymobile.ukweather.utils.GeneralDialogFragment.onCreateDialog(android.os.Bundle)",this);return builder.create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.haringeymobile.ukweather.utils.GeneralDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private DialogInterface.OnClickListener getDialogOnClickListener() {
        com.mijack.Xlog.logMethodEnter("DialogInterface.OnClickListener com.haringeymobile.ukweather.utils.GeneralDialogFragment.getDialogOnClickListener()",this);try{com.mijack.Xlog.logMethodExit("DialogInterface.OnClickListener com.haringeymobile.ukweather.utils.GeneralDialogFragment.getDialogOnClickListener()",this);return new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.GeneralDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,id);try{dismiss();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.GeneralDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.GeneralDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }

        };}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("DialogInterface.OnClickListener com.haringeymobile.ukweather.utils.GeneralDialogFragment.getDialogOnClickListener()",this,throwable);throw throwable;}
    }

}
