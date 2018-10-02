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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, cheatsheet));
            }
        });

        findViewById(R.id.show_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, changelog));
            }
        });

        findViewById(R.id.source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, source));
            }
        });
    }

}
