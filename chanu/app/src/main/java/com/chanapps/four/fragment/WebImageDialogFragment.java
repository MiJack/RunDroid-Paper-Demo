package com.chanapps.four.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.chanapps.four.activity.PostReplyActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.ActivityDispatcher;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.loader.ChanImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.net.URI;
import java.util.UUID;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class WebImageDialogFragment extends DialogFragment {

    public static final String TAG = WebImageDialogFragment.class.getSimpleName();

    private String boardCode;
    private long threadNo;

    private ViewGroup frame;
    private EditText urlTextView;
    private ImageView webImage;
    private ProgressBar webProgress;
    private ImageButton webButton;
    private ImageButton webBrowse;

    private Uri fullImageUri;
    private String fullImagePath;

    private boolean downloadSuccess = false;

    public WebImageDialogFragment() {
        super();
    }

    public WebImageDialogFragment(String boardCode, long threadNo) {
        this();
        this.boardCode = boardCode;
        this.threadNo = threadNo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.AlertDialog com.chanapps.four.fragment.WebImageDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.web_image_dialog_fragment, null);
        urlTextView = (EditText)view.findViewById(R.id.text);
        urlTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.fragment.WebImageDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this,v,actionId,event);try{downloadImage();
                {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.WebImageDialogFragment.onCreateDialog(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.fragment.WebImageDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.fragment.WebImageDialogFragment$1.onEditorAction(android.widget.TextView,int,android.view.KeyEvent)",this,throwable);throw throwable;}
            }
        });
        webImage = (ImageView)view.findViewById(R.id.widget_coverflowcard_image);
        webProgress = (ProgressBar)view.findViewById(R.id.progress_bar);
        webButton = (ImageButton)view.findViewById(R.id.button);
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$2.onClick(android.view.View)",this,v);try{downloadImage();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });
        webBrowse = (ImageButton)view.findViewById(R.id.browse);
        webBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$3.onClick(android.view.View)",this,v);try{ActivityDispatcher.launchUrlInBrowser(getActivity(),
                        URLFormatComponent.getUrl(getActivity(), URLFormatComponent.GOOGLE_IMAGE_SEARCH_URL));com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$3.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$3.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });
        frame = (ViewGroup)view.findViewById(R.id.frame);
        {com.mijack.Xlog.logMethodExit("android.app.AlertDialog com.chanapps.four.fragment.WebImageDialogFragment.onCreateDialog(android.os.Bundle)",this);return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.web_image_button_attach, null)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$4.onClick(android.content.DialogInterface,int)",this,dialog,which);try{WebImageDialogFragment.this.dismiss();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$4.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$4.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.AlertDialog com.chanapps.four.fragment.WebImageDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    protected void downloadImage() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.downloadImage()",this);try{String url = urlTextView.getText().toString();
        if ("".equals(url)) {
            Toast.makeText(urlTextView.getContext(), R.string.web_image_input_url_first, Toast.LENGTH_SHORT).show();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.downloadImage()",this);return;}
        }
        String postExt = "_downloaded_" + UUID.randomUUID() + ".jpg"; /*// correct?*/
        fullImageUri = ChanFileStorage.getHiddenLocalImageUri(urlTextView.getContext(), boardCode, threadNo, postExt);
        fullImagePath = (new File(URI.create(fullImageUri.toString()))).getAbsolutePath();
        DisplayImageOptions options = (new DisplayImageOptions.Builder())
                .cacheInMemory()
                .cacheOnDisc()
                .showStubImage(R.drawable.stub_image_background)
                .resetViewBeforeLoading()
                .displayer(new FadeInBitmapDisplayer(100))
                .fullSizeImageLocation(fullImagePath)
                .imageSize(new ImageSize(300, 300))
                .build();
        ChanImageLoader.getInstance(urlTextView.getContext()).displayImage(url, webImage, options, imageListener);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment.downloadImage()",this,throwable);throw throwable;}
    }

    protected ImageLoadingListener imageListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingStarted(android.widget.String,android.view.View)",this,imageUri,view);try{downloadSuccess = false;
            webProgress.setVisibility(View.VISIBLE);
            frame.setVisibility(View.VISIBLE);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingStarted(android.widget.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingStarted(android.widget.String,android.view.View)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingFailed(android.widget.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,imageUri,view,failReason);try{webProgress.setVisibility(View.GONE);
            Toast.makeText(view.getContext(), R.string.thread_couldnt_download_image, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingFailed(android.widget.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingFailed(android.widget.String,android.view.View,com.nostra13.universalimageloader.core.assist.FailReason)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingComplete(android.widget.String,android.view.View,android.graphics.Bitmap)",this,imageUri,view,loadedImage);try{webProgress.setVisibility(View.GONE);
            downloadSuccess = true;com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingComplete(android.widget.String,android.view.View,android.graphics.Bitmap)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingComplete(android.widget.String,android.view.View,android.graphics.Bitmap)",this,throwable);throw throwable;}
        }
        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingCancelled(android.widget.String,android.view.View)",this,imageUri,view);try{webProgress.setVisibility(View.GONE);
            Toast.makeText(view.getContext(), R.string.thread_couldnt_download_image, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingCancelled(android.widget.String,android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$5.onLoadingCancelled(android.widget.String,android.view.View)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onActivityCreated(Bundle bundle) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.onActivityCreated(android.os.Bundle)",this,bundle);try{super.onActivityCreated(bundle);
        /*//getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.onCancel(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.onCancel(android.content.DialogInterface)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.onDismiss(android.content.DialogInterface)",this,dialog);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.onDismiss(android.content.DialogInterface)",this);}

    protected View.OnClickListener attachListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment$6.onClick(android.view.View)",this,view);try{String url = urlTextView.getText().toString();
            if ("".equals(url)) {
                Toast.makeText(view.getContext(), R.string.web_image_input_url_first, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$6.onClick(android.view.View)",this);return;}
            }
            if (fullImageUri == null || !downloadSuccess) {
                Toast.makeText(view.getContext(), R.string.web_image_download_first, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment$6.onClick(android.view.View)",this);return;}
            }
            closeKeyboard();
            ((PostReplyActivity)getActivity()).setImageUri(fullImageUri);
            Toast.makeText(view.getContext(), "Attached image", Toast.LENGTH_SHORT).show();
            dismiss();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment$6.onClick(android.view.View)",this,throwable);throw throwable;}
        }
    };

    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.onResume()",this);try{super.onResume();
        AlertDialog dialog = (AlertDialog)getDialog();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(attachListener);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment.onResume()",this,throwable);throw throwable;}
    }

    private void closeKeyboard() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.WebImageDialogFragment.closeKeyboard()",this);try{IBinder windowToken = getActivity().getCurrentFocus() != null ?
                getActivity().getCurrentFocus().getWindowToken()
                : null;
        if (windowToken != null) { /*// close the keyboard*/
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.WebImageDialogFragment.closeKeyboard()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.WebImageDialogFragment.closeKeyboard()",this,throwable);throw throwable;}
    }

}
