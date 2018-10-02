package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.chanapps.four.activity.PostReplyActivity;
import com.chanapps.four.activity.R;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class PostingReplyDialogFragment extends DialogFragment {

    public static final String TAG = PostingReplyDialogFragment.class.getSimpleName();

    protected static final String THREAD_NO = "threadNo";
    protected PostReplyActivity.PostReplyTask task;
    protected long  threadNo;

    public PostingReplyDialogFragment() { /*// when on-create gets called*/
        super();
    }

    public PostingReplyDialogFragment(PostReplyActivity.PostReplyTask task, long threadNo) {
        super();
        this.task = task;
        this.threadNo = threadNo;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.PostingReplyDialogFragment.onCreateDialog(android.os.Bundle)",this,bundle);try{if (bundle != null) { /*// recalled on existing task*/
           this.threadNo = bundle.getLong(THREAD_NO);
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
        TextView title = (TextView)layout.findViewById(R.id.title);
        TextView message = (TextView)layout.findViewById(R.id.message);
        int titleId = threadNo <= 0 ? R.string.new_thread_menu : R.string.post_reply_title;
        title.setText(titleId);
        message.setText(R.string.dialog_posting_reply);
        setStyle(STYLE_NO_TITLE, 0);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.PostingReplyDialogFragment.onCreateDialog(android.os.Bundle)",this);return (new AlertDialog.Builder(getActivity()))
                .setView(layout)
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PostingReplyDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{if (task != null)
                                    {task.cancel(true);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PostingReplyDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PostingReplyDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.PostingReplyDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.PostingReplyDialogFragment.onSaveInstanceState(android.os.Bundle)",this,outState);try{outState.putLong(THREAD_NO, threadNo);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.PostingReplyDialogFragment.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.PostingReplyDialogFragment.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
