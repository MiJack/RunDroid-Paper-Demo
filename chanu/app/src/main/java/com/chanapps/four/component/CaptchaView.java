package com.chanapps.four.component;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CaptchaView extends WebView {

    private static final String BASE_URL = "http://chanu.4chan.org";
    private String captchaResponse;
    
    public class CaptchaCallback {

        @JavascriptInterface
        public void captchaEntered(String response) {
            com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CaptchaView$CaptchaCallback.captchaEntered(java.lang.String)",this,response);try{setCaptchaResponse(response);com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CaptchaView$CaptchaCallback.captchaEntered(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CaptchaView$CaptchaCallback.captchaEntered(java.lang.String)",this,throwable);throw throwable;}
        }
    }

    public CaptchaView(Context context) {
        super(context);
    }

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptchaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initCaptcha() {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CaptchaView.initCaptcha()",this);try{try {
            String body = IOUtils.toString(new BufferedInputStream(getResources().getAssets().open("captcha.html")));
            
            getSettings().setJavaScriptEnabled(true);
            setWebChromeClient(new WebChromeClient());
            addJavascriptInterface(new CaptchaCallback(), "CaptchaCallback");
            loadDataWithBaseURL(BASE_URL, body, "text/html", "UTF-8", null);
            setBackgroundColor(Color.TRANSPARENT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CaptchaView.initCaptcha()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CaptchaView.initCaptcha()",this,throwable);throw throwable;}
    }

    public String getCaptchaResponse() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.component.CaptchaView.getCaptchaResponse()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.component.CaptchaView.getCaptchaResponse()",this);return captchaResponse;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.component.CaptchaView.getCaptchaResponse()",this,throwable);throw throwable;}
    }

    public void setCaptchaResponse(String captchaResponse) {
        com.mijack.Xlog.logMethodEnter("void com.chanapps.four.component.CaptchaView.setCaptchaResponse(java.lang.String)",this,captchaResponse);try{this.captchaResponse = captchaResponse;com.mijack.Xlog.logMethodExit("void com.chanapps.four.component.CaptchaView.setCaptchaResponse(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.chanapps.four.component.CaptchaView.setCaptchaResponse(java.lang.String)",this,throwable);throw throwable;}
    }
}
