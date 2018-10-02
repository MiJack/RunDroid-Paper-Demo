package com.chanapps.four.data;

import android.content.Context;
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
public class DeletePostResponse {

    private Context ctx = null;
    private String response = null;
    private boolean isPosted = false;
    private String error = null;

    private static final Pattern BAN_REG = Pattern.compile("<h2>([^<]*)<span class=\"banType\">([^<]*)</span>([^<]*)</h2>");
    private static final Pattern ERROR_REG = Pattern.compile("(id=\"errmsg\"[^>]*>)([^<]*)");

    public DeletePostResponse(Context ctx, String response) {
        this.ctx = ctx;
        this.response = response;
    }

    public void processResponse() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.data.DeletePostResponse.processResponse()",this);try{isPosted = false;
        try {
            Matcher banMatch = BAN_REG.matcher(response);
            Matcher errorMatch = ERROR_REG.matcher(response);
            if ("".equals(response))
                {error = ctx.getString(R.string.delete_post_response_error);}
            else if (banMatch.find())
                {error = banMatch.group(1) + " " + banMatch.group(2) + " " + banMatch.group(3);}
            else if (errorMatch.find())
                {error = errorMatch.group(2).replaceFirst("Error: ", "");}
            else
                {isPosted = true;}
        }
        catch (Exception e) {
            error = e.getLocalizedMessage();
            isPosted = false;
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.data.DeletePostResponse.processResponse()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.data.DeletePostResponse.processResponse()",this,throwable);throw throwable;}
    }

    public String getResponse() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.DeletePostResponse.getResponse()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.DeletePostResponse.getResponse()",this);return response;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.DeletePostResponse.getResponse()",this,throwable);throw throwable;}
    }

    public boolean isPosted() {
        com.mijack.Xlog.logMethodEnter("boolean com.chanapps.four.data.DeletePostResponse.isPosted()",this);try{com.mijack.Xlog.logMethodExit("boolean com.chanapps.four.data.DeletePostResponse.isPosted()",this);return isPosted;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.chanapps.four.data.DeletePostResponse.isPosted()",this,throwable);throw throwable;}
    }

    public String getError(Context ctx) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.DeletePostResponse.getError(android.content.Context)",this,ctx);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.DeletePostResponse.getError(android.content.Context)",this);return error;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.DeletePostResponse.getError(android.content.Context)",this,throwable);throw throwable;}
    }

}
