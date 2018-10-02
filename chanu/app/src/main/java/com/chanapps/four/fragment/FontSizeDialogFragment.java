package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import com.chanapps.four.activity.R;
import com.chanapps.four.data.FontSize;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class FontSizeDialogFragment extends DialogFragment {

    public interface NotifyFontSizeListener {
        void onFontSizeChanged(FontSize fontSize);
    }

    public static final String TAG = FontSizeDialogFragment.class.getSimpleName();

    private FontSize fontSize;
    private CharSequence[] array;
    private NotifyFontSizeListener notifyFontSizeListener;

    public FontSizeDialogFragment(){}

    public FontSizeDialogFragment(FontSize fontSize) {
        super();
        this.fontSize = fontSize;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.FontSizeDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{array = getResources().getTextArray(R.array.font_sizes);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.font_size_menu)
                .setSingleChoiceItems(array, fontSize.ordinal(), selectFontSizeListener)
        ;
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.FontSizeDialogFragment.onCreateDialog(android.os.Bundle)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.FontSizeDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }


    public FontSizeDialogFragment setNotifySortOrderListener(NotifyFontSizeListener notifySortOrderListener) {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.fragment.FontSizeDialogFragment com.chanapps.four.fragment.FontSizeDialogFragment.setNotifySortOrderListener(NotifyFontSizeListener)",this,notifySortOrderListener);try{this.notifyFontSizeListener = notifySortOrderListener;
        {com.mijack.Xlog.logMethodExit("com.chanapps.four.fragment.FontSizeDialogFragment com.chanapps.four.fragment.FontSizeDialogFragment.setNotifySortOrderListener(NotifyFontSizeListener)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.fragment.FontSizeDialogFragment com.chanapps.four.fragment.FontSizeDialogFragment.setNotifySortOrderListener(NotifyFontSizeListener)",this,throwable);throw throwable;}
    }


    private DialogInterface.OnClickListener selectFontSizeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.FontSizeDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{CharSequence item = array[which];
            fontSize = FontSize.valueOfDisplayString(getActivity(), item.toString());
            if (notifyFontSizeListener != null)
                {notifyFontSizeListener.onFontSizeChanged(fontSize);}
            dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.FontSizeDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.FontSizeDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.FontSizeDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.FontSizeDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.FontSizeDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
