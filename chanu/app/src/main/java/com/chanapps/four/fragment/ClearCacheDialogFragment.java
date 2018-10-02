package com.chanapps.four.fragment;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.chanapps.four.activity.R;
import com.chanapps.four.activity.SettingsActivity;
import com.chanapps.four.component.NotificationComponent;
import com.chanapps.four.data.ChanFileStorage;

/**
* Created with IntelliJ IDEA.
* User: arley
* Date: 12/14/12
* Time: 12:44 PM
* To change this template use File | Settings | File Templates.
*/
public class ClearCacheDialogFragment extends DialogFragment {

    public static final String TAG = ClearCacheDialogFragment.class.getSimpleName();

    private static final boolean DEBUG = false;

    private SettingsFragment fragment;

    private static ClearCacheAsyncTask clearCacheAsyncTask;

    public ClearCacheDialogFragment() {}

    public ClearCacheDialogFragment(SettingsFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.app.Dialog com.chanapps.four.fragment.ClearCacheDialogFragment.onCreateDialog(android.os.Bundle)",this,savedInstanceState);try{LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.message_dialog_fragment, null);
        TextView title = (TextView)layout.findViewById(R.id.title);
        TextView message = (TextView)layout.findViewById(R.id.message);
        title.setText(R.string.pref_cache_category);
        message.setText(R.string.dialog_clear_cache_confirm);
        setStyle(STYLE_NO_TITLE, 0);
        {com.mijack.Xlog.logMethodExit("android.app.Dialog com.chanapps.four.fragment.ClearCacheDialogFragment.onCreateDialog(android.os.Bundle)",this);return (new AlertDialog.Builder(getActivity()))
                .setView(layout)
                .setPositiveButton(R.string.dialog_clear,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ClearCacheDialogFragment$1.onClick(android.content.DialogInterface,int)",this,dialog,which);try{(new ClearCacheAsyncTask(getActivity())).execute();com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ClearCacheDialogFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ClearCacheDialogFragment$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{/*// ignore*/com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ClearCacheDialogFragment$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        })
                .create();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.Dialog com.chanapps.four.fragment.ClearCacheDialogFragment.onCreateDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private static class ClearCacheAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private static boolean runningDelete = false;

        public ClearCacheAsyncTask() {}

        public ClearCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        public void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPreExecute()",this);try{if (runningDelete) {
                Toast.makeText(context, R.string.pref_clear_cache_already_running, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPreExecute()",this);return;}
            }
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true)) {
                Toast.makeText(context, R.string.pref_clear_cache_pre_execute_no_notify, Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPreExecute()",this);return;}
            }
            Toast.makeText(context, R.string.pref_clear_cache_pre_execute, Toast.LENGTH_SHORT).show();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        public String doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("android.app.String com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.doInBackground([android.app.Void)",this,params);try{if (runningDelete)
                {{com.mijack.Xlog.logMethodExit("android.app.String com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.doInBackground([android.app.Void)",this);return null;}}
            String contentText;
            if (ChanFileStorage.deleteCacheDirectory(context)) {
                if (DEBUG) {Log.i(TAG, "Successfully cleared cache");}
                contentText = context.getString(R.string.pref_clear_cache_success);
            }
            else {
                Log.e(TAG, "Failed to run clear cache command");
                contentText = context.getString(R.string.pref_clear_cache_error);
            }
            {com.mijack.Xlog.logMethodExit("android.app.String com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.doInBackground([android.app.Void)",this);return contentText;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.String com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.doInBackground([android.app.Void)",this,throwable);throw throwable;}
        }

        @Override
        public void onCancelled() {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onCancelled()",this);try{runningDelete = false;
            if (DEBUG) {Log.i(TAG, "Cancelled clear cache");}
            NotificationComponent.notifyClearCacheCancelled(context);com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onCancelled()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onCancelled()",this,throwable);throw throwable;}
        }

        @Override
        public void onPostExecute(String result) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPostExecute(android.app.String)",this,result);try{runningDelete = false;
            if (result == null)
                {{com.mijack.Xlog.logMethodExit("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPostExecute(android.app.String)",this);return;}}
            if (DEBUG) {Log.i(TAG, "Post execute with clear cache result=" + result);}
            NotificationComponent.notifyClearCacheResult(context, result);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.fragment.ClearCacheDialogFragment$ClearCacheAsyncTask.onPostExecute(android.app.String)",this,throwable);throw throwable;}
        }

    }

}
