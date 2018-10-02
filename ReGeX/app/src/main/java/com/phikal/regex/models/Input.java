package com.phikal.regex.models;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class Input implements TextWatcher {

    private StatusCallback statusCallback;

    @Override
    public abstract void afterTextChanged(Editable text);

    public void setStatusCallback(StatusCallback sc) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Input.setStatusCallback(StatusCallback)",this,sc);try{this.statusCallback = sc;com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Input.setStatusCallback(StatusCallback)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.models.Input.setStatusCallback(StatusCallback)",this,throwable);throw throwable;}
    }

    protected void updateStatus(Response resp, String msg) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Input.updateStatus(Response,java.lang.String)",this,resp,msg);try{if (statusCallback != null)
            {statusCallback.status(resp, msg);}com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Input.updateStatus(Response,java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.models.Input.updateStatus(Response,java.lang.String)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Input.beforeTextChanged(java.lang.CharSequence,int,int,int)",this,charSequence,i,i1,i2);com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Input.beforeTextChanged(java.lang.CharSequence,int,int,int)",this);}

    {com.mijack.Xlog.logMethodEnter("void com.phikal.regex.models.Input.onTextChanged(java.lang.CharSequence,int,int,int)",this,charSequence,i,i1,i2);com.mijack.Xlog.logMethodExit("void com.phikal.regex.models.Input.onTextChanged(java.lang.CharSequence,int,int,int)",this);}

    public enum Response {OK, ERROR}

    public interface StatusCallback {
        void status(Response resp, String msg);
    }
}
