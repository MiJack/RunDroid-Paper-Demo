/**
 * 
 */
package com.chanapps.four.activity;

import android.app.*;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.chanapps.four.service.ThreadImageDownloadService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author grzegorznittner
 *
 */
public class CancelDownloadActivity extends Activity {
	public static final String TAG = CancelDownloadActivity.class.getSimpleName();
	
	private static final String NOTIFICATION_ID = "NotificationId";
	
	final Context context = this;
	
    public static Intent createIntent(Context context, final int notificationId, final String boardCode, final long threadNo) {
		com.mijack.Xlog.logStaticMethodEnter("android.content.Intent com.chanapps.four.activity.CancelDownloadActivity.createIntent(android.content.Context,int,android.app.String,long)",context,notificationId,boardCode,threadNo);try{Intent intent = new Intent(context, CancelDownloadActivity.class);
		intent.putExtra(NOTIFICATION_ID, notificationId);
		intent.putExtra(ThreadActivity.BOARD_CODE, boardCode);
        intent.putExtra(ThreadActivity.THREAD_NO, threadNo);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		{com.mijack.Xlog.logStaticMethodExit("android.content.Intent com.chanapps.four.activity.CancelDownloadActivity.createIntent(android.content.Context,int,android.app.String,long)");return intent;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.content.Intent com.chanapps.four.activity.CancelDownloadActivity.createIntent(android.content.Context,int,android.app.String,long)",throwable);throw throwable;}
	}
 
	public void onCreate(Bundle savedInstanceState) {
		com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.CancelDownloadActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
		
		final int notificationId = getIntent().getIntExtra(NOTIFICATION_ID, 0);
		if (notificationId == 0) {
			finish();
			{com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.CancelDownloadActivity.onCreate(android.os.Bundle)",this);return;}
		}
		
		setContentView(R.layout.cancel_download_dialog);
        (new CancelDownloadDialogFragment(notificationId)).show(getFragmentManager(), TAG);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.CancelDownloadActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
	}

    protected class CancelDownloadDialogFragment extends DialogFragment {

        private int notificationId;

        public CancelDownloadDialogFragment() {
            super();
        }

        public CancelDownloadDialogFragment(int notificationId) {
            super();
            this.notificationId = notificationId;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            com.mijack.Xlog.logMethodEnter("android.app.Dialog com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
            TextView title = (TextView)layout.findViewById(R.id.title);
            TextView message = (TextView)layout.findViewById(R.id.message);
            title.setText(R.string.cancel_download_title);
            message.setText(R.string.cancel_download_message);
            setStyle(STYLE_NO_TITLE, 0);
            {com.mijack.Xlog.logMethodExit("android.app.Dialog com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment.onCreateDialog(android.os.Bundle)",this);return (new AlertDialog.Builder(getActivity()))
                    .setView(layout)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{if (notificationId != 0) {
                                        ThreadImageDownloadService.cancelDownload(getBaseContext(), notificationId);
                                    }
                                    CancelDownloadActivity.this.finish();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                                }
                            })
                    .setNegativeButton(R.string.dismiss,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    com.mijack.Xlog.logMethodEnter("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{CancelDownloadActivity.this.finish();com.mijack.Xlog.logMethodExit("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                                }
                            })
                    .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.Dialog com.chanapps.four.activity.CancelDownloadActivity$CancelDownloadDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
        }

    }

}
