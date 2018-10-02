package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.BoardSortType;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class BoardSortOrderDialogFragment extends DialogFragment {

    public interface NotifySortOrderListener {
        void onSortOrderChanged(BoardSortType boardSortType);
    }

    public static final String TAG = BoardSortOrderDialogFragment.class.getSimpleName();

    private BoardSortType sortType;
    private CharSequence[] array;
    private NotifySortOrderListener notifySortOrderListener;

    public BoardSortOrderDialogFragment(){}

    public BoardSortOrderDialogFragment(BoardSortType sortType) {
        super();
        this.sortType = sortType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.BoardSortOrderDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{array = getResources().getTextArray(R.array.sort_order_types);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.sort_order_menu)
                .setSingleChoiceItems(array, sortType.ordinal(), selectSortOrderListener)
        ;
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.BoardSortOrderDialogFragment.onCreateDialog(android.os.Bundle)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.BoardSortOrderDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }


    public BoardSortOrderDialogFragment setNotifySortOrderListener(NotifySortOrderListener notifySortOrderListener) {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.fragment.BoardSortOrderDialogFragment com.chanapps.four.fragment.BoardSortOrderDialogFragment.setNotifySortOrderListener(NotifySortOrderListener)",this,notifySortOrderListener);try{this.notifySortOrderListener = notifySortOrderListener;
        {com.mijack.Xlog.logMethodExit("com.chanapps.four.fragment.BoardSortOrderDialogFragment com.chanapps.four.fragment.BoardSortOrderDialogFragment.setNotifySortOrderListener(NotifySortOrderListener)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.fragment.BoardSortOrderDialogFragment com.chanapps.four.fragment.BoardSortOrderDialogFragment.setNotifySortOrderListener(NotifySortOrderListener)",this,throwable);throw throwable;}
    }


    private DialogInterface.OnClickListener selectSortOrderListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.BoardSortOrderDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{CharSequence item = array[which];
            sortType = BoardSortType.valueOfDisplayString(getActivity(), item.toString());
            if (notifySortOrderListener != null)
                {notifySortOrderListener.onSortOrderChanged(sortType);}
            dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.BoardSortOrderDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.BoardSortOrderDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.BoardSortOrderDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.BoardSortOrderDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.BoardSortOrderDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
