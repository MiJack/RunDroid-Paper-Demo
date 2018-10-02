package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.chanapps.four.activity.R;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class GenericDialogFragment extends DialogFragment {
    public static final String TAG = GenericDialogFragment.class.getSimpleName();
    protected String titleString;
    protected String messageString;
    public GenericDialogFragment(){}
    public GenericDialogFragment(String title, String message) {
        super();
        titleString = title;
        messageString = message;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.GenericDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
        TextView title = (TextView)layout.findViewById(R.id.title);
        TextView message = (TextView)layout.findViewById(R.id.message);
        title.setText(titleString);
        message.setText(messageString);
        setStyle(STYLE_NO_TITLE, 0);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.GenericDialogFragment.onCreateDialog(android.os.Bundle)",this);return (new AlertDialog.Builder(getActivity()))
                .setView(layout)
                .setNegativeButton(R.string.dismiss,
                        new DialogInterface.OnClickListener() {
                            {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.GenericDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.GenericDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}
                        })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.GenericDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
