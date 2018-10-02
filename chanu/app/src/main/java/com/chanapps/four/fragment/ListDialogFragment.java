package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.ThemeSelector;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class ListDialogFragment extends DialogFragment {

    protected String[] array = {};
    protected ArrayAdapter<String> adapter = null;
    protected ListView items = null;
    private DialogInterface.OnCancelListener cancelListener = null;

    public ListDialogFragment(){}

    public Dialog createListDialog(int titleStringId, int emptyTitleStringId, int emptyStringId, String[] array,
                                   ListView.OnItemClickListener listener) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener)",this,titleStringId,emptyTitleStringId,emptyStringId,array,listener);try{com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener)",this);return createListDialog(titleStringId, emptyTitleStringId, emptyStringId, array, listener, null);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener)",this,throwable);throw throwable;}
    }

    public Dialog createListDialog(int titleStringId, int emptyTitleStringId, int emptyStringId, String[] array,
                                   ListView.OnItemClickListener listener, final DialogInterface.OnCancelListener cancelListener) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener)",this,titleStringId,emptyTitleStringId,emptyStringId,array,listener,cancelListener);try{com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener)",this);return createListDialog(getString(titleStringId), getString(emptyTitleStringId), getString(emptyStringId),
                array, listener, cancelListener, null, null);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(int,int,int,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener)",this,throwable);throw throwable;}
    }

    public Dialog createListDialog(String title, String emptyTitle, String empty, String[] array,
                             ListView.OnItemClickListener listener,
                             final DialogInterface.OnCancelListener cancelListener,
                             String positiveLabel,
                             final DialogInterface.OnClickListener positiveListener) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(java.lang.String,java.lang.String,java.lang.String,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener,java.lang.String,DialogInterface.OnClickListener)",this,title,emptyTitle,empty,array,listener,cancelListener,positiveLabel,positiveListener);try{this.array = array;
        this.cancelListener = cancelListener;
        if (array.length > 0) {
            setStyle(STYLE_NO_TITLE, 0);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.items_dialog_fragment, null);
            TextView titleView = (TextView)layout.findViewById(R.id.title);
            titleView.setText(title);
            items = (ListView)layout.findViewById(R.id.items);
            int itemLayoutId = ThemeSelector.instance(getActivity()).isDark()
                    ? R.layout.items_dialog_item_dark
                    : R.layout.items_dialog_item;
            adapter = new ArrayAdapter(getActivity().getApplicationContext(),
                    itemLayoutId, array);
            items.setAdapter(adapter);
            if (listener != null)
                {items.setOnItemClickListener(listener);}
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layout);
            if (positiveListener != null) {
                builder.setPositiveButton(positiveLabel, positiveListener);
            }
            if (cancelListener != null) {
                builder
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ListDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{cancelListener.onCancel(dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ListDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ListDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        });
            }
            else {
                builder.setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ListDialogFragment$2.onClick(android.content.DialogInterface,int)",this,dialog,which);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ListDialogFragment$2.onClick(android.content.DialogInterface,int)",this);}
                        });
            }
            Dialog d = builder.create();
            {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(java.lang.String,java.lang.String,java.lang.String,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener,java.lang.String,DialogInterface.OnClickListener)",this);return d;}
        }
        else {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
            TextView titleView = (TextView)layout.findViewById(R.id.title);
            TextView message = (TextView)layout.findViewById(R.id.message);
            titleView.setText(emptyTitle);
            message.setText(empty);
            setStyle(STYLE_NO_TITLE, 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layout);
            if (cancelListener != null) {
                builder
                        .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ListDialogFragment$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{cancelListener.onCancel(dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ListDialogFragment$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ListDialogFragment$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        });
            }
            else {
                builder.setNegativeButton(R.string.dismiss,
                        new DialogInterface.OnClickListener() {
                            {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ListDialogFragment$4.onClick(android.content.DialogInterface,int)",this,dialog,which);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ListDialogFragment$4.onClick(android.content.DialogInterface,int)",this);}
                        });
            }
            Dialog d = builder.create();
            {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(java.lang.String,java.lang.String,java.lang.String,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener,java.lang.String,DialogInterface.OnClickListener)",this);return d;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.ListDialogFragment.createListDialog(java.lang.String,java.lang.String,java.lang.String,[java.lang.String,ListView.OnItemClickListener,DialogInterface.OnCancelListener,java.lang.String,DialogInterface.OnClickListener)",this,throwable);throw throwable;}
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ListDialogFragment.onCancel(android.content.DialogInterface)",this,dialogInterface);try{if (cancelListener != null)
            {cancelListener.onCancel(dialogInterface);}com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ListDialogFragment.onCancel(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ListDialogFragment.onCancel(android.content.DialogInterface)",this,throwable);throw throwable;}
    }

}
