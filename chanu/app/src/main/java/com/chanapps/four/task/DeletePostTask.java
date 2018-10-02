package com.chanapps.four.task;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.chanapps.four.activity.ChanIdentifiedActivity;
import com.chanapps.four.activity.R;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.data.*;
import com.chanapps.four.fragment.DeletingPostDialogFragment;
import com.chanapps.four.multipartmime.*;
import com.chanapps.four.multipartmime.PartBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 11/8/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeletePostTask extends AsyncTask<DeletingPostDialogFragment, Void, Integer> {

    public static final String TAG = DeletePostTask.class.getSimpleName();
    public static final boolean DEBUG = false;

    private ChanIdentifiedActivity activity = null;
    private String boardCode = null;
    private long threadNo = 0;
    private long[] postNos = {};
    private String password = null;
    private boolean imageOnly = false;
    private Context context = null;
    private DeletingPostDialogFragment dialogFragment = null;

    public DeletePostTask(ChanIdentifiedActivity activity,
                          String boardCode, long threadNo, long[] postNos, String password, boolean imageOnly) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.boardCode = boardCode;
        this.threadNo = threadNo;
        this.postNos = postNos;
        this.password = password;
        this.imageOnly = imageOnly;
    }

    @Override
    protected Integer doInBackground(DeletingPostDialogFragment... params) { com.mijack.Xlog.logMethodEnter("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this,params);try{/*// dialog is for callback*/
        dialogFragment = params[0];
        try {
            MultipartEntity entity = buildMultipartEntity();
            if (entity == null) {
                Log.e(TAG, "Null entity returned building post delete");
                {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this);return R.string.delete_post_error;}
            }

            String response = executeDeletePost(entity);
            if (response == null || response.isEmpty()) {
                Log.e(TAG, "Null response posting delete");
                {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this);return R.string.delete_post_error;}
            }

            DeletePostResponse deletePostResponse = new DeletePostResponse(context, response);
            deletePostResponse.processResponse();

            if (!postSuccessful(deletePostResponse))
                {{com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this);return R.string.delete_post_error;}}

            {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this);return updateLastFetched();}
        }
        catch (Exception e) {
            Log.e(TAG, "Error posting", e);
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this);return R.string.delete_post_error;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.multipartmime.Integer com.chanapps.four.task.DeletePostTask.doInBackground([com.chanapps.four.fragment.DeletingPostDialogFragment)",this,throwable);throw throwable;}
    }

    protected MultipartEntity buildMultipartEntity() {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.multipartmime.MultipartEntity com.chanapps.four.task.DeletePostTask.buildMultipartEntity()",this);try{List<Part> partsList = new ArrayList<Part>();
        partsList.add(new StringPart("mode", "usrdel", PartBase.ASCII_CHARSET));
        partsList.add(new StringPart("res", Long.toString(threadNo), PartBase.ASCII_CHARSET));
        partsList.add(new StringPart("pwd", password, PartBase.ASCII_CHARSET));
        for (long postNo : postNos)
            {partsList.add(new StringPart(Long.toString(postNo), "delete", PartBase.ASCII_CHARSET));}
        if (imageOnly)
            {partsList.add(new StringPart("onlyimgdel", "on", PartBase.ASCII_CHARSET));}
        Part[] parts = partsList.toArray(new Part[partsList.size()]);
        if (DEBUG)
            {dumpPartsList(partsList);}
        MultipartEntity entity = new MultipartEntity(parts);
        {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.MultipartEntity com.chanapps.four.task.DeletePostTask.buildMultipartEntity()",this);return entity;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.multipartmime.MultipartEntity com.chanapps.four.task.DeletePostTask.buildMultipartEntity()",this,throwable);throw throwable;}
    }

    protected void dumpPartsList(List<Part> partsList) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.task.DeletePostTask.dumpPartsList(java.util.ArrayList)",this,partsList);try{if (DEBUG) {Log.i(TAG, "Dumping mime parts list:");}
        for (Part p : partsList) {
            if (!(p instanceof StringPart))
                {continue;}
            StringPart s = (StringPart)p;
            String line = s.getName() + ": " + s.getValue() + ", ";
            if (DEBUG) {Log.i(TAG, line);}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.task.DeletePostTask.dumpPartsList(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.task.DeletePostTask.dumpPartsList(java.util.ArrayList)",this,throwable);throw throwable;}
    }

    protected String executeDeletePost(MultipartEntity entity) {
        com.mijack.Xlog.logMethodEnter("com.chanapps.four.multipartmime.String com.chanapps.four.task.DeletePostTask.executeDeletePost(com.chanapps.four.multipartmime.MultipartEntity)",this,entity);try{/*// success: 	<meta http-equiv="refresh" content="0;URL=https ://boards.4chan.org/a/res/79766271#p79766271">*/
        String url = String.format(URLFormatComponent.getUrl(context, URLFormatComponent.CHAN_POST_URL_DELETE_FORMAT), boardCode);
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        try {
            HttpPost request = new HttpPost(url);
            entity.setContentEncoding(PartBase.ASCII_CHARSET);
            request.setEntity(entity);
            if (DEBUG)
                {dumpRequestContent(request.getEntity().getContent());}
            if (DEBUG) {Log.i(TAG, "Calling URL: " + request.getURI());}
            HttpResponse httpResponse = client.execute(request);
            if (DEBUG) {Log.i(TAG, "Response: " + (httpResponse == null ? "null" : "length: " + httpResponse.toString().length()));}
            if (httpResponse == null) {
                Log.e(TAG, context.getString(R.string.delete_post_no_response));
                {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.String com.chanapps.four.task.DeletePostTask.executeDeletePost(com.chanapps.four.multipartmime.MultipartEntity)",this);return null;}
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuilder s = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                if (DEBUG) {Log.i(TAG, "Response Line:" + line);}
                s.append(line);
            }
            String response = s.toString();
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.String com.chanapps.four.task.DeletePostTask.executeDeletePost(com.chanapps.four.multipartmime.MultipartEntity)",this);return response;}
        }
        catch (Exception e) {
            Log.e(TAG, "Exception while posting to url=" + url, e);
            {com.mijack.Xlog.logMethodExit("com.chanapps.four.multipartmime.String com.chanapps.four.task.DeletePostTask.executeDeletePost(com.chanapps.four.multipartmime.MultipartEntity)",this);return null;}
        }
        finally {
            if (client != null) {
                client.close();
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.chanapps.four.multipartmime.String com.chanapps.four.task.DeletePostTask.executeDeletePost(com.chanapps.four.multipartmime.MultipartEntity)",this,throwable);throw throwable;}
    }

    protected void dumpRequestContent(InputStream is) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.task.DeletePostTask.dumpRequestContent(com.chanapps.four.multipartmime.InputStream)",this,is);try{if (DEBUG) {Log.i(TAG, "Request Message Body:");}
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String l;
            while ((l = r.readLine()) != null)
                {if (DEBUG) {Log.i(TAG, l);}}
        }
        catch (IOException e) {
            if (DEBUG) {Log.i(TAG, "Exception reading message for logging", e);}
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.task.DeletePostTask.dumpRequestContent(com.chanapps.four.multipartmime.InputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.task.DeletePostTask.dumpRequestContent(com.chanapps.four.multipartmime.InputStream)",this,throwable);throw throwable;}
    }

    protected String errorMessage = null;

    protected boolean postSuccessful(DeletePostResponse deletePostResponse) {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.task.DeletePostTask.postSuccessful(com.chanapps.four.multipartmime.DeletePostResponse)",this,deletePostResponse);try{errorMessage = deletePostResponse.getError(context);
        if (errorMessage != null && !errorMessage.isEmpty()) {
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.task.DeletePostTask.postSuccessful(com.chanapps.four.multipartmime.DeletePostResponse)",this);return false;}
        }

        if (DEBUG) {Log.i(TAG, "isPosted:" + deletePostResponse.isPosted());}
        if (!deletePostResponse.isPosted()) {
            Log.e(TAG, "Unable to post response=" + deletePostResponse.getResponse());
            {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.task.DeletePostTask.postSuccessful(com.chanapps.four.multipartmime.DeletePostResponse)",this);return false;}
        }

        {com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.task.DeletePostTask.postSuccessful(com.chanapps.four.multipartmime.DeletePostResponse)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.task.DeletePostTask.postSuccessful(com.chanapps.four.multipartmime.DeletePostResponse)",this,throwable);throw throwable;}
    }

    protected int updateLastFetched() {
        com.mijack.Xlog.logMethodEnter("int com.chanapps.four.task.DeletePostTask.updateLastFetched()",this);try{/*// forcing thread/board refresh*/
        ChanFileStorage.deletePosts(context, boardCode, threadNo, postNos, imageOnly);
        /*
        ChanActivityId refreshableActivityId = NetworkProfileManager.instance().getActivityId();
        if (refreshableActivityId != null) {
            if (refreshableActivityId.activity == LastActivity.THREAD_ACTIVITY) {
                ChanFileStorage.resetLastFetched(boardCode, threadNo);
                FetchChanDataService.scheduleThreadFetchWithPriority(context, boardCode, threadNo);
            }
        }
        */
        {com.mijack.Xlog.logMethodExit("int com.chanapps.four.task.DeletePostTask.updateLastFetched()",this);return 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.chanapps.four.task.DeletePostTask.updateLastFetched()",this,throwable);throw throwable;}
    }

    @Override
    protected void onCancelled() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.task.DeletePostTask.onCancelled()",this);try{Log.e(TAG, "Post cancelled");
        Toast.makeText(context, R.string.delete_post_cancelled, Toast.LENGTH_SHORT).show();com.mijack.Xlog.logMethodExit("void com.chanapps.four.task.DeletePostTask.onCancelled()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.task.DeletePostTask.onCancelled()",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostExecute(Integer result) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.task.DeletePostTask.onPostExecute(com.chanapps.four.multipartmime.Integer)",this,result);try{if (result != 0) {
            String error = context.getString(result) + (errorMessage == null ? "" : ": " + errorMessage);
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            dialogFragment.dismiss();
            {com.mijack.Xlog.logMethodExit("void com.chanapps.four.task.DeletePostTask.onPostExecute(com.chanapps.four.multipartmime.Integer)",this);return;}
        }
        int msgId = imageOnly ? R.string.delete_post_successful_image : R.string.delete_post_successful;
        Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
        activity.refresh();
        dialogFragment.dismiss();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.task.DeletePostTask.onPostExecute(com.chanapps.four.multipartmime.Integer)",this,throwable);throw throwable;}
    }

}
