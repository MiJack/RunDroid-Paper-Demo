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
import com.chanapps.four.task.DeletePostTask;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class DeletingPostDialogFragment extends DialogFragment {
    public static final String TAG = DeletingPostDialogFragment.class.getSimpleName();
    DeletePostTask task;
    boolean onlyImages;
    public DeletingPostDialogFragment(){}
    public DeletingPostDialogFragment(DeletePostTask task, boolean onlyImages) {
        super();
        this.task = task;
        this.onlyImages = onlyImages;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.DeletingPostDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
        TextView title = (TextView)layout.findViewById(R.id.title);
        TextView message = (TextView)layout.findViewById(R.id.message);
        title.setText(R.string.delete_post_title);
        int messageId = onlyImages ? R.string.delete_post_deleting_image : R.string.delete_post_deleting;
        message.setText(messageId);
        setStyle(STYLE_NO_TITLE, 0);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.DeletingPostDialogFragment.onCreateDialog(android.os.Bundle)",this);return (new AlertDialog.Builder(getActivity()))
                .setView(layout)
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.DeletingPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{task.cancel(true);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.DeletingPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.DeletingPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.DeletingPostDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }
}
