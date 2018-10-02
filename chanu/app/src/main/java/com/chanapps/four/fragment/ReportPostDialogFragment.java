package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chanapps.four.activity.ChanIdentifiedActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.CaptchaView;
import com.chanapps.four.component.CaptchaView.CaptchaCallback;
import com.chanapps.four.task.ReportPostTask;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class ReportPostDialogFragment extends DialogFragment {

    public static final String TAG = ReportPostDialogFragment.class.getSimpleName();

    private String boardCode;
    private long threadNo = 0;
    private long[] postNos = {};
    private Spinner reportTypeSpinner;
    private CaptchaView recaptchaView;
    private TextView reportPostBugWarning;

    public ReportPostDialogFragment(){}

    public ReportPostDialogFragment(String boardCode, long threadNo, long[] postNos) {
        super();
        this.boardCode = boardCode;
        this.threadNo = threadNo;
        this.postNos = postNos;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.ReportPostDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.report_post_dialog_fragment, null);
        builder
            .setView(view)
            .setPositiveButton(R.string.report_post, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{ReportPostDialogFragment.this.dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ReportPostDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                }
            });
        reportTypeSpinner = (Spinner)view.findViewById(R.id.report_post_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.report_post_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(adapter);
        recaptchaView = (CaptchaView) view.findViewById(R.id.report_recaptcha_webview);
        reportPostBugWarning = (TextView)view.findViewById(R.id.report_post_bug_warning);
        if (postNos.length > 1) {
            String s = String.format(getString(R.string.report_post_bug_warning), postNos[0]);
            reportPostBugWarning.setText(s);
            reportPostBugWarning.setVisibility(View.VISIBLE);
        }
        else {
            reportPostBugWarning.setVisibility(View.GONE);
        }
        recaptchaView.initCaptcha();
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.report_post), (DialogInterface.OnClickListener)null);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.ReportPostDialogFragment.onCreateDialog(android.os.Bundle)",this);return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.ReportPostDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ReportPostDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment.onCancel(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.onCancel(android.content.DialogInterface)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment.onDismiss(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.onDismiss(android.content.DialogInterface)",this);}

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment.onResume()",this);try{super.onResume();
        AlertDialog dialog = (AlertDialog)getDialog();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment$2.onClick(android.view.View)",this,v);try{String reportType = reportTypeSpinner.getSelectedItem().toString();
                if ("".equals(reportType)) {
                    Toast.makeText(getActivity(), R.string.report_post_select_type, Toast.LENGTH_SHORT).show();
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.onResume()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment$2.onClick(android.view.View)",this);return;}}
                }
                int reportTypeIndex = reportTypeSpinner.getSelectedItemPosition();
                String recaptchaResponse = recaptchaView.getCaptchaResponse();
                if ("".equals(recaptchaResponse)) {
                    Toast.makeText(getActivity(), R.string.post_reply_enter_captcha, Toast.LENGTH_SHORT).show();
                    {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.onResume()",this);{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment$2.onClick(android.view.View)",this);return;}}
                }

                closeKeyboard();
                ReportPostTask reportPostTask = new ReportPostTask(
                        (ChanIdentifiedActivity)getActivity(), boardCode, threadNo, postNos,
                        reportType, reportTypeIndex, recaptchaResponse);
                ReportingPostDialogFragment dialogFragment = new ReportingPostDialogFragment(reportPostTask);
                dialogFragment.show(getActivity().getSupportFragmentManager(), ReportingPostDialogFragment.TAG);
                if (!reportPostTask.isCancelled())
                    {reportPostTask.execute(dialogFragment);}
                dismiss();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ReportPostDialogFragment$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ReportPostDialogFragment.onResume()",this,throwable);throw throwable;}
    }

    private void closeKeyboard() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ReportPostDialogFragment.closeKeyboard()",this);try{IBinder windowToken = getActivity().getCurrentFocus() != null ?
                getActivity().getCurrentFocus().getWindowToken()
                : null;
        if (windowToken != null) { /*// close the keyboard*/
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ReportPostDialogFragment.closeKeyboard()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ReportPostDialogFragment.closeKeyboard()",this,throwable);throw throwable;}
    }
}
