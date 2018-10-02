/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.app;

import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.util.CacheManager;
import com.android.gallery3d.util.GalleryUtils;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MoviePlayer implements
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        ControllerOverlay.Listener {
    @SuppressWarnings("unused")
    private static final String TAG = "MoviePlayer";

    private static final String KEY_VIDEO_POSITION = "video-position";
    private static final String KEY_RESUMEABLE_TIME = "resumeable-timeout";

    /*// Copied from MediaPlaybackService in the Music Player app.*/
    private static final String SERVICECMD = "com.android.music.musicservicecommand";
    private static final String CMDNAME = "command";
    private static final String CMDPAUSE = "pause";

    /*// If we resume the acitivty with in RESUMEABLE_TIMEOUT, we will keep playing.*/
    /*// Otherwise, we pause the player.*/
    private static final long RESUMEABLE_TIMEOUT = 3 * 60 * 1000; /*// 3 mins*/

    private Context mContext;
    private final VideoView mVideoView;
    private final Bookmarker mBookmarker;
    private final Uri mUri;
    private final Handler mHandler = new Handler();
    private final AudioBecomingNoisyReceiver mAudioBecomingNoisyReceiver;
    private final ActionBar mActionBar;
    private final ControllerOverlay mController;

    private long mResumeableTime = Long.MAX_VALUE;
    private int mVideoPosition = 0;
    private boolean mHasPaused = false;

    /*// If the time bar is being dragged.*/
    private boolean mDragging;

    /*// If the time bar is visible.*/
    private boolean mShowing;

    private final Runnable mPlayingChecker = new Runnable() {
        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$1.run()",this);try{if (mVideoView.isPlaying()) {
                mController.showPlaying();
            } else {
                mHandler.postDelayed(mPlayingChecker, 250);
            }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$1.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$1.run()",this,throwable);throw throwable;}
        }
    };

    private final Runnable mProgressChecker = new Runnable() {
        @Override
        public void run() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$2.run()",this);try{int pos = setProgress();
            mHandler.postDelayed(mProgressChecker, 1000 - (pos % 1000));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$2.run()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$2.run()",this,throwable);throw throwable;}
        }
    };

    public MoviePlayer(View rootView, final MovieActivity movieActivity, Uri videoUri,
            Bundle savedInstance, boolean canReplay) {
        mContext = movieActivity.getApplicationContext();
        mVideoView = (VideoView) rootView.findViewById(R.id.surface_view);
        mBookmarker = new Bookmarker(movieActivity);
        mActionBar = movieActivity.getActionBar();
        mUri = videoUri;

        mController = new MovieControllerOverlay(mContext);
        ((ViewGroup)rootView).addView(mController.getView());
        mController.setListener(this);
        mController.setCanReplay(canReplay);

        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setVideoURI(mUri);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.MoviePlayer$3.onTouch(android.view.View,android.view.MotionEvent)",this,v,event);try{mController.show();
                {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.MoviePlayer$3.onTouch(android.view.View,android.view.MotionEvent)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.MoviePlayer$3.onTouch(android.view.View,android.view.MotionEvent)",this,throwable);throw throwable;}
            }
        });

        /*// When the user touches the screen or uses some hard key, the framework*/
        /*// will change system ui visibility from invisible to visible. We show*/
        /*// the media control at this point.*/
        mVideoView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int visibility) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$4.onSystemUiVisibilityChange(int)",this,visibility);try{if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                    mController.show();
                }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$4.onSystemUiVisibilityChange(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$4.onSystemUiVisibilityChange(int)",this,throwable);throw throwable;}
            }
        });

        mAudioBecomingNoisyReceiver = new AudioBecomingNoisyReceiver();
        mAudioBecomingNoisyReceiver.register();

        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPAUSE);
        movieActivity.sendBroadcast(i);

        if (savedInstance != null) { /*// this is a resumed activity*/
            mVideoPosition = savedInstance.getInt(KEY_VIDEO_POSITION, 0);
            mResumeableTime = savedInstance.getLong(KEY_RESUMEABLE_TIME, Long.MAX_VALUE);
            mVideoView.start();
            mVideoView.suspend();
            mHasPaused = true;
        } else {
            final Integer bookmark = mBookmarker.getBookmark(mUri);
            if (bookmark != null) {
                showResumeDialog(movieActivity, bookmark);
            } else {
                startVideo();
            }
        }
    }

    private void showSystemUi(boolean visible) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.showSystemUi(boolean)",this,visible);try{int flag = visible ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        mVideoView.setSystemUiVisibility(flag);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.showSystemUi(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.showSystemUi(boolean)",this,throwable);throw throwable;}
    }

    public void onSaveInstanceState(Bundle outState) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onSaveInstanceState(android.os.Bundle)",this,outState);try{outState.putInt(KEY_VIDEO_POSITION, mVideoPosition);
        outState.putLong(KEY_RESUMEABLE_TIME, mResumeableTime);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onSaveInstanceState(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onSaveInstanceState(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void showResumeDialog(Context context, final int bookmark) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.showResumeDialog(android.content.Context,int)",this,context,bookmark);try{AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.resume_playing_title);
        builder.setMessage(String.format(
                context.getString(R.string.resume_playing_message),
                GalleryUtils.formatDuration(context, bookmark / 1000)));
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$5.onCancel(android.content.DialogInterface)",this,dialog);try{onCompletion();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$5.onCancel(android.content.DialogInterface)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$5.onCancel(android.content.DialogInterface)",this,throwable);throw throwable;}
            }
        });
        builder.setPositiveButton(
                R.string.resume_playing_resume, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$6.onClick(android.content.DialogInterface,int)",this,dialog,which);try{mVideoView.seekTo(bookmark);
                startVideo();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$6.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$6.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        });
        builder.setNegativeButton(
                R.string.resume_playing_restart, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$7.onClick(android.content.DialogInterface,int)",this,dialog,which);try{startVideo();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$7.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$7.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
            }
        });
        builder.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.showResumeDialog(android.content.Context,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.showResumeDialog(android.content.Context,int)",this,throwable);throw throwable;}
    }

    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onPause()",this);try{mHasPaused = true;
        mHandler.removeCallbacksAndMessages(null);
        mVideoPosition = mVideoView.getCurrentPosition();
        mBookmarker.setBookmark(mUri, mVideoPosition, mVideoView.getDuration());
        mVideoView.suspend();
        mResumeableTime = System.currentTimeMillis() + RESUMEABLE_TIMEOUT;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onPause()",this,throwable);throw throwable;}
    }

    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onResume()",this);try{if (mHasPaused) {
            mVideoView.seekTo(mVideoPosition);
            mVideoView.resume();

            /*// If we have slept for too long, pause the play*/
            if (System.currentTimeMillis() > mResumeableTime) {
                pauseVideo();
            }
        }
        mHandler.post(mProgressChecker);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onResume()",this,throwable);throw throwable;}
    }

    public void onDestroy() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onDestroy()",this);try{mVideoView.stopPlayback();
        mAudioBecomingNoisyReceiver.unregister();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onDestroy()",this,throwable);throw throwable;}
    }

    /*// This updates the time bar display (if necessary). It is called every*/
    /*// second by mProgressChecker and also from places where the time bar needs*/
    /*// to be updated immediately.*/
    private int setProgress() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.MoviePlayer.setProgress()",this);try{if (mDragging || !mShowing) {
            {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.MoviePlayer.setProgress()",this);return 0;}
        }
        int position = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        mController.setTimes(position, duration);
        {com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.MoviePlayer.setProgress()",this);return position;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.MoviePlayer.setProgress()",this,throwable);throw throwable;}
    }

    private void startVideo() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.startVideo()",this);try{/*// For streams that we expect to be slow to start up, show a*/
        /*// progress spinner until playback starts.*/
        String scheme = mUri.getScheme();
        if ("http".equalsIgnoreCase(scheme) || "rtsp".equalsIgnoreCase(scheme)) {
            mController.showLoading();
            mHandler.removeCallbacks(mPlayingChecker);
            mHandler.postDelayed(mPlayingChecker, 250);
        } else {
            mController.showPlaying();
        }

        mVideoView.start();
        setProgress();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.startVideo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.startVideo()",this,throwable);throw throwable;}
    }

    private void playVideo() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.playVideo()",this);try{mVideoView.start();
        mController.showPlaying();
        setProgress();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.playVideo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.playVideo()",this,throwable);throw throwable;}
    }

    private void pauseVideo() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.pauseVideo()",this);try{mVideoView.pause();
        mController.showPaused();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.pauseVideo()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.pauseVideo()",this,throwable);throw throwable;}
    }

    /*// Below are notifications from VideoView*/
    @Override
    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.MoviePlayer.onError(android.media.MediaPlayer,int,int)",this,player,arg1,arg2);try{mHandler.removeCallbacksAndMessages(null);
        /*// VideoView will show an error dialog if we return false, so no need*/
        /*// to show more message.*/
        mController.showErrorMessage("");
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.MoviePlayer.onError(android.media.MediaPlayer,int,int)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.MoviePlayer.onError(android.media.MediaPlayer,int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onCompletion(android.media.MediaPlayer)",this,mp);try{mController.showEnded();
        onCompletion();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onCompletion(android.media.MediaPlayer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onCompletion(android.media.MediaPlayer)",this,throwable);throw throwable;}
    }

    {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onCompletion()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onCompletion()",this);}

    /*// Below are notifications from ControllerOverlay*/
    @Override
    public void onPlayPause() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onPlayPause()",this);try{if (mVideoView.isPlaying()) {
            pauseVideo();
        } else {
            playVideo();
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onPlayPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onPlayPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onSeekStart() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onSeekStart()",this);try{mDragging = true;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onSeekStart()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onSeekStart()",this,throwable);throw throwable;}
    }

    @Override
    public void onSeekMove(int time) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onSeekMove(int)",this,time);try{mVideoView.seekTo(time);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onSeekMove(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onSeekMove(int)",this,throwable);throw throwable;}
    }

    @Override
    public void onSeekEnd(int time) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onSeekEnd(int)",this,time);try{mDragging = false;
        mVideoView.seekTo(time);
        setProgress();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onSeekEnd(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onSeekEnd(int)",this,throwable);throw throwable;}
    }

    @Override
    public void onShown() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onShown()",this);try{mShowing = true;
        mActionBar.show();
        showSystemUi(true);
        setProgress();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onShown()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onShown()",this,throwable);throw throwable;}
    }

    @Override
    public void onHidden() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onHidden()",this);try{mShowing = false;
        mActionBar.hide();
        showSystemUi(false);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onHidden()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onHidden()",this,throwable);throw throwable;}
    }

    @Override
    public void onReplay() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer.onReplay()",this);try{startVideo();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer.onReplay()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer.onReplay()",this,throwable);throw throwable;}
    }

    /*// We want to pause when the headset is unplugged.*/
    private class AudioBecomingNoisyReceiver extends BroadcastReceiver {

        public void register() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.register()",this);try{mContext.registerReceiver(this,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.register()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.register()",this,throwable);throw throwable;}
        }

        public void unregister() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.unregister()",this);try{mContext.unregisterReceiver(this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.unregister()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.unregister()",this,throwable);throw throwable;}
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.onReceive(android.content.Context,android.content.Intent)",this,context,intent);try{if (mVideoView.isPlaying()) {
                mVideoView.pause();
          }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.onReceive(android.content.Context,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.MoviePlayer$AudioBecomingNoisyReceiver.onReceive(android.content.Context,android.content.Intent)",this,throwable);throw throwable;}
        }
    }
}

class Bookmarker {
    private static final String TAG = "Bookmarker";

    private static final String BOOKMARK_CACHE_FILE = "bookmark";
    private static final int BOOKMARK_CACHE_MAX_ENTRIES = 100;
    private static final int BOOKMARK_CACHE_MAX_BYTES = 10 * 1024;
    private static final int BOOKMARK_CACHE_VERSION = 1;

    private static final int HALF_MINUTE = 30 * 1000;
    private static final int TWO_MINUTES = 4 * HALF_MINUTE;

    private final Context mContext;

    public Bookmarker(Context context) {
        mContext = context;
    }

    public void setBookmark(Uri uri, int bookmark, int duration) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.Bookmarker.setBookmark(android.net.Uri,int,int)",this,uri,bookmark,duration);try{try {
            BlobCache cache = CacheManager.getCache(mContext,
                    BOOKMARK_CACHE_FILE, BOOKMARK_CACHE_MAX_ENTRIES,
                    BOOKMARK_CACHE_MAX_BYTES, BOOKMARK_CACHE_VERSION);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(uri.toString());
            dos.writeInt(bookmark);
            dos.writeInt(duration);
            dos.flush();
            cache.insert(uri.hashCode(), bos.toByteArray());
        } catch (Throwable t) {
            Log.w(TAG, "setBookmark failed", t);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.Bookmarker.setBookmark(android.net.Uri,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.Bookmarker.setBookmark(android.net.Uri,int,int)",this,throwable);throw throwable;}
    }

    public Integer getBookmark(Uri uri) {
        com.mijack.Xlog.logMethodEnter("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this,uri);try{try {
            BlobCache cache = CacheManager.getCache(mContext,
                    BOOKMARK_CACHE_FILE, BOOKMARK_CACHE_MAX_ENTRIES,
                    BOOKMARK_CACHE_MAX_BYTES, BOOKMARK_CACHE_VERSION);

            byte[] data = cache.lookup(uri.hashCode());
            if (data == null) {{com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this);return null;}}

            DataInputStream dis = new DataInputStream(
                    new ByteArrayInputStream(data));

            String uriString = dis.readUTF(dis);
            int bookmark = dis.readInt();
            int duration = dis.readInt();

            if (!uriString.equals(uri.toString())) {
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this);return null;}
            }

            if ((bookmark < HALF_MINUTE) || (duration < TWO_MINUTES)
                    || (bookmark > (duration - HALF_MINUTE))) {
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this);return null;}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this);return Integer.valueOf(bookmark);}
        } catch (Throwable t) {
            Log.w(TAG, "getBookmark failed", t);
        }
        {com.mijack.Xlog.logMethodExit("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.android.gallery3d.app.Bookmarker.getBookmark(android.net.Uri)",this,throwable);throw throwable;}
    }
}
