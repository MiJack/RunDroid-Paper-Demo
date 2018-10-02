/*
 * Copyright (c) 2016-2017. Vijai Chandra Prasad R.
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

package com.orpheusdroid.screenrecorder.encoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.orpheusdroid.screenrecorder.Const;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vijai on 31-08-2017.
 */

public class Mp4toGIFConverter {
    private Uri videoUri;
    private Context context;
    private long maxDur = 5000;
    private MediaMetadataRetriever mediaMetadataRetriever;

    public Mp4toGIFConverter(Context context) {
        this();
        this.context = context;
    }

    private Mp4toGIFConverter() {
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public void setVideoUri(Uri videoUri) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.setVideoUri(android.net.Uri)",this,videoUri);try{this.videoUri = videoUri;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.setVideoUri(android.net.Uri)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.setVideoUri(android.net.Uri)",this,throwable);throw throwable;}
    }

    public void convertToGif(){
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.convertToGif()",this);try{/*//MediaMetadataRetriever tRetriever = new MediaMetadataRetriever();*/

        try{
            mediaMetadataRetriever.setDataSource(context, videoUri);

            /*//extract duration in millisecond*/
            String DURATION = mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION);
            maxDur = (long)(Double.parseDouble(DURATION));

            Log.d(Const.TAG, "max dur is" + maxDur);

            TaskSaveGIF myTaskSaveGIF = new TaskSaveGIF();
            myTaskSaveGIF.execute();
        }catch(RuntimeException e){
            e.printStackTrace();
            Toast.makeText(context,
                    "Something Wrong!",
                    Toast.LENGTH_LONG).show();
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.convertToGif()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter.convertToGif()",this,throwable);throw throwable;}
    }

    public class TaskSaveGIF extends AsyncTask<Void, Integer, String> {
        ProgressDialog dialog = new ProgressDialog(context);

        private String getGifFIleName(){
            com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.getGifFIleName()",this);try{String Filename = videoUri.getLastPathSegment();
            {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.getGifFIleName()",this);return Filename.replace("mp4", "gif");}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.getGifFIleName()",this,throwable);throw throwable;}
        }



        @Override
        protected String doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.doInBackground([java.lang.Void)",this,params);try{String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File outFile = new File(extStorageDirectory + File.separator + Const.APPDIR, getGifFIleName());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
                bos.write(genGIF());
                bos.flush();
                bos.close();


                {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.doInBackground([java.lang.Void)",this);return(outFile.getAbsolutePath() + " Saved");}
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.doInBackground([java.lang.Void)",this);return e.getMessage();}
            } catch (IOException e) {
                e.printStackTrace();
                {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.doInBackground([java.lang.Void)",this);return e.getMessage();}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPreExecute()",this);try{dialog.setTitle("Please wait. Saving GIF");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(String result) {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPostExecute(java.lang.String)",this,result);try{Toast.makeText(context,
                    result,
                    Toast.LENGTH_LONG).show();
            dialog.cancel();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPostExecute(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onPostExecute(java.lang.String)",this,throwable);throw throwable;}
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onProgressUpdate([java.lang.Integer)",this,values);try{/*//bar.setProgress(values[0]);*/
            /*//updateFrame();*/
            dialog.setProgress(values[0]);

            Log.d(Const.TAG, "Gif save progress: " + values[0]);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onProgressUpdate([java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.onProgressUpdate([java.lang.Integer)",this,throwable);throw throwable;}
        }

        private byte[] genGIF(){
            com.mijack.Xlog.logMethodEnter("[byte com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.genGIF()",this);try{ByteArrayOutputStream bos = new ByteArrayOutputStream();

            GifEncoder animatedGifEncoder = new GifEncoder();
            animatedGifEncoder.setDelay(1000);
            animatedGifEncoder.setRepeat(0);
            animatedGifEncoder.setQuality(15);
            /*//animatedGifEncoder.setSize(0,0);*/
            animatedGifEncoder.setFrameRate(20.0f);

            Bitmap bmFrame;
            animatedGifEncoder.start(bos);
            for(int i=0; i<100; i+=10){
                long frameTime = maxDur * i/100;
                Log.d(Const.TAG, "GIF GETTING FRAME AT: " + frameTime + "ms");
                bmFrame = mediaMetadataRetriever.getFrameAtTime(frameTime);
                animatedGifEncoder.addFrame(bmFrame);
                publishProgress(i);
            }

            /*//last from at end*/
            bmFrame = mediaMetadataRetriever.getFrameAtTime(maxDur);
            animatedGifEncoder.addFrame(bmFrame);
            publishProgress(100);

            animatedGifEncoder.finish();
            {com.mijack.Xlog.logMethodExit("[byte com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.genGIF()",this);return bos.toByteArray();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.orpheusdroid.screenrecorder.encoder.Mp4toGIFConverter$TaskSaveGIF.genGIF()",this,throwable);throw throwable;}
        }
    }
}
