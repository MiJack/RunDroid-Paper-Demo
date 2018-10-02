package com.phikal.regex.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.phikal.regex.R;


public class HelloActivity extends Activity {

    Uri cheatsheet = Uri.parse("https://zge.us.to/proj/ReGeX/cheetsheet.html"),
            changelog = Uri.parse("https://github.com/phikal/ReGeX/releases"),
            source = Uri.parse("http://github.com/phikal/regex");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.HelloActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.HelloActivity$1.onClick(android.view.View)",this,v);try{onBackPressed();com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.HelloActivity$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.HelloActivity$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });

        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.HelloActivity$2.onClick(android.view.View)",this,v);try{startActivity(new Intent(Intent.ACTION_VIEW, cheatsheet));com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.HelloActivity$2.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.HelloActivity$2.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });

        findViewById(R.id.show_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.HelloActivity$3.onClick(android.view.View)",this,v);try{startActivity(new Intent(Intent.ACTION_VIEW, changelog));com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.HelloActivity$3.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.HelloActivity$3.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });

        findViewById(R.id.source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.mijack.Xlog.logMethodEnter("void com.phikal.regex.activities.HelloActivity$4.onClick(android.view.View)",this,v);try{startActivity(new Intent(Intent.ACTION_VIEW, source));com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.HelloActivity$4.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.HelloActivity$4.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.phikal.regex.activities.HelloActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.phikal.regex.activities.HelloActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

}
