/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.screenrecorder.ui;

import android.app.ProgressDialog;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class EditVideoActivity extends AppCompatActivity implements OnTrimVideoListener{
    private ProgressDialog saveprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        if(!getIntent().hasExtra(Const.VIDEO_EDIT_URI_KEY)) {
            Toast.makeText(this, getResources().getString(R.string.video_not_found), Toast.LENGTH_SHORT).show();
            finish();
            {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.onCreate(android.os.Bundle)",this);return;}
        }

        Uri videoUri = Uri.parse(getIntent().getStringExtra(Const.VIDEO_EDIT_URI_KEY));

        if (!new File(videoUri.getPath()).exists()) {
            Toast.makeText(this, getResources().getString(R.string.video_not_found), Toast.LENGTH_SHORT).show();
            finish();
            {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.onCreate(android.os.Bundle)",this);return;}
        }

        K4LVideoTrimmer videoTrimmer = findViewById(R.id.videoTimeLine);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        /*//use one of overloaded setDataSource() functions to set your data source*/
        retriever.setDataSource(this, videoUri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInMins = (((int)Long.parseLong(time)) / 1000)+1000;
        Log.d(Const.TAG, timeInMins+"");

        File video = new File(videoUri.getPath());

        videoTrimmer.setOnTrimVideoListener(this);
        videoTrimmer.setVideoURI(videoUri);
        videoTrimmer.setMaxDuration(timeInMins);
        Log.d(Const.TAG, "Edited file save name: " + video.getAbsolutePath());
        videoTrimmer.setDestinationPath(video.getParent()+"/");}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void getResult(Uri uri) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.getResult(android.net.Uri)",this,uri);try{Log.d(Const.TAG, uri.getPath());
        indexFile(uri.getPath());

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$1.run()",this);try{saveprogress = new ProgressDialog(EditVideoActivity.this);
                saveprogress.setMessage("Please wait while the video is being saved");
                saveprogress.setTitle("Please wait");
                saveprogress.setIndeterminate(true);
                saveprogress.show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$1.run()",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.getResult(android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.getResult(android.net.Uri)",this,throwable);throw throwable;}
    }

    @Override
    public void cancelAction() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.cancelAction()",this);try{finish();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.cancelAction()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.cancelAction()",this,throwable);throw throwable;}
    }

    private void indexFile(String SAVEPATH) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.indexFile(java.lang.String)",this,SAVEPATH);try{/*//Create a new ArrayList and add the newly created video file path to it*/
        ArrayList<String> toBeScanned = new ArrayList<>();
        toBeScanned.add(SAVEPATH);
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);

        /*//Request MediaScannerConnection to scan the new file and index it*/
        MediaScannerConnection.scanFile(this, toBeScannedStr, null, new MediaScannerConnection.OnScanCompletedListener() {

            @Override
            public void onScanCompleted(String path, Uri uri) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$2.onScanCompleted(java.lang.String,android.net.Uri)",this,path,uri);try{Log.i(Const.TAG, "SCAN COMPLETED: " + path);
                saveprogress.cancel();
                setResult(Const.VIDEO_EDIT_RESULT_CODE);
                finish();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$2.onScanCompleted(java.lang.String,android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity$2.onScanCompleted(java.lang.String,android.net.Uri)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.indexFile(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.EditVideoActivity.indexFile(java.lang.String)",this,throwable);throw throwable;}
    }

}
