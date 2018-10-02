package com.chanapps.four.data;

import android.content.Context;
import android.util.Log;
import com.chanapps.four.activity.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: arley
 * Date: 10/30/12
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanPostResponse {

    private static final String TAG = ChanPostResponse.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Context ctx = null;
    private String response = null;
    private boolean isPosted = false;
    private long threadNo = 0;
    private long postNo = 0;
    private String error = null;

    private static final Pattern SUCCESS_REG = Pattern.compile("(<title.*)(Post successful)");
    private static final Pattern POST_REG = Pattern.compile("thread:([0-9]*),no:([0-9]*)"); /*// <!-- thread:44593688,no:44595010 -->*/
    private static final Pattern BAN_REG = Pattern.compile("<h2>([^<]*)<span class=\"banType\">([^<]*)</span>([^<\\[]*)</h2>");
    private static final Pattern ERROR_REG = Pattern.compile("(id=\"errmsg\"[^>]*>)([^<\\[]*)");

    public ChanPostResponse(Context ctx, String response) {
        this.ctx = ctx;
        this.response = response;
    }

    public void processResponse() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.ChanPostResponse.processResponse()",this);try{isPosted = false;
        try {
            error = ctx.getString(R.string.post_reply_response_error);
            if (DEBUG) {Log.i(TAG, "Received response: " + response);}
            Matcher successMatch = SUCCESS_REG.matcher(response);
            Matcher banMatch = BAN_REG.matcher(response);
            Matcher errorMatch = ERROR_REG.matcher(response);
            if (successMatch.find()) {
                isPosted = true;
                error = null;
                try {
                    Matcher threadMatch = POST_REG.matcher(response);
                    if (threadMatch.find()) {
                        threadNo = Long.valueOf(threadMatch.group(1));
                        postNo = Long.valueOf(threadMatch.group(2));
                        if (threadNo == 0) { /*// API strangely uses postNo instead of threadNo when you post a new thread*/
                            threadNo = postNo;
                            postNo = 0;
                        }
                        if (DEBUG) {Log.i(TAG, "Found threadNo:" + threadNo + " postNo:" + postNo);}
                    }
                }
                catch (Exception e) {
                    threadNo = 0;
                    postNo = 0;
                }
            }
            else if (banMatch.find()) {
                    error = banMatch.group(1) + " " + banMatch.group(2) + " " + banMatch.group(3);
            }
            else if (errorMatch.find()) {
                    error = errorMatch.group(2).replaceFirst("Error: ", "");
            }
        }
        catch (Exception e) {
            error = e.getLocalizedMessage();
            isPosted = false;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.ChanPostResponse.processResponse()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.ChanPostResponse.processResponse()",this,throwable);throw throwable;}
    }

    public String getResponse() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.ChanPostResponse.getResponse()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.ChanPostResponse.getResponse()",this);return response;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanPostResponse.getResponse()",this,throwable);throw throwable;}
    }

    public boolean isPosted() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.ChanPostResponse.isPosted()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.ChanPostResponse.isPosted()",this);return isPosted;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.ChanPostResponse.isPosted()",this,throwable);throw throwable;}
    }

    public String getError(Context ctx) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.ChanPostResponse.getError(android.content.Context)",this,ctx);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.ChanPostResponse.getError(android.content.Context)",this);return error;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.ChanPostResponse.getError(android.content.Context)",this,throwable);throw throwable;}
    }

    public long getThreadNo() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.data.ChanPostResponse.getThreadNo()",this);try{com.mijack.Xlog.logMethodExit("long com.chanapps.four.data.ChanPostResponse.getThreadNo()",this);return threadNo;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.data.ChanPostResponse.getThreadNo()",this,throwable);throw throwable;}
    }

    public long getPostNo() {
        com.mijack.Xlog.logMethodEnter("long com.chanapps.four.data.ChanPostResponse.getPostNo()",this);try{com.mijack.Xlog.logMethodExit("long com.chanapps.four.data.ChanPostResponse.getPostNo()",this);return postNo;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.chanapps.four.data.ChanPostResponse.getPostNo()",this,throwable);throw throwable;}
    }
}
