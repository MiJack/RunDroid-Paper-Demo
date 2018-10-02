package com.haringeymobile.ukweather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.haringeymobile.ukweather.R;

/**
 * A task that shows a circular progress bar and the "loading" message while
 * executing.
 */
public abstract class AsyncTaskWithProgressBar<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private Context context;
    private ProgressDialog progressDialog;

    public AsyncTaskWithProgressBar<Params, Progress, Result> setContext(
            Context context) {
        com.mijack.Xlog.logMethodEnter("AsyncTaskWithProgressBar com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.setContext(android.content.Context)",this,context);try{this.context = context;
        {com.mijack.Xlog.logMethodExit("AsyncTaskWithProgressBar com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.setContext(android.content.Context)",this);return this;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("AsyncTaskWithProgressBar com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.setContext(android.content.Context)",this,throwable);throw throwable;}
    }

    @Override
    protected void onPreExecute() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPreExecute()",this);try{super.onPreExecute();
        if (context != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(
                    R.string.loading_message));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>$1.onCancel(android.content.DialogInterface)",this,arg0);try{progressDialog.dismiss();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>$1.onCancel(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>$1.onCancel(android.content.DialogInterface)",this,throwable);throw throwable;}
                }
            });

            progressDialog.show();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPreExecute()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCancelled() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onCancelled()",this);try{super.onCancelled();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onCancelled()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onCancelled()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostExecute(Result result) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPostExecute(Result)",this,result);try{super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPostExecute(Result)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.AsyncTaskWithProgressBar<Params, Progress, Result>.onPostExecute(Result)",this,throwable);throw throwable;}
    }
}
