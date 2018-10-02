package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.component.URLFormatComponent;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class WebImageSearchDialogFragment extends DialogFragment {

    public static final String TAG = WebImageSearchDialogFragment.class.getSimpleName();

    private EditText searchTextView;

    public WebImageSearchDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.WebImageSearchDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.web_search_image_dialog_fragment, null);
        searchTextView = (EditText)view.findViewById(R.id.text);
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.WebImageSearchDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this,v,actionId,event);try{startWebSearch();
                {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.WebImageSearchDialogFragment.onCreateDialog(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.WebImageSearchDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.WebImageSearchDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this,throwable);throw throwable;}
            }
        });
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.WebImageSearchDialogFragment.onCreateDialog(android.os.Bundle)",this);return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.search_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{startWebSearch();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{WebImageSearchDialogFragment.this.dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.WebImageSearchDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void startWebSearch() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment.startWebSearch()",this);try{String query = searchTextView != null && searchTextView.getText() != null
                ? searchTextView.getText().toString()
                : null;
        if (query != null && !query.isEmpty()) {
            String url = String.format(
                    URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GOOGLE_QUERY_IMAGE_URL_FORMAT), query);
            ActivityDispatcher.launchUrlInBrowser(getActivity(), url);
        }
        else {
            ActivityDispatcher.launchUrlInBrowser(getActivity(),
                    URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GOOGLE_IMAGE_SEARCH_URL));
        }
        dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment.startWebSearch()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment.startWebSearch()",this,throwable);throw throwable;}
    }

    @Override
    public void onStart() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onStart()",this);try{super.onStart();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment$4.run()",this);try{searchTextView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                searchTextView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment$4.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment$4.run()",this,throwable);throw throwable;}

            }
        }, 200);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        /*//getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onCancel(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onCancel(android.content.DialogInterface)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onDismiss(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageSearchDialogFragment.onDismiss(android.content.DialogInterface)",this);}

}
