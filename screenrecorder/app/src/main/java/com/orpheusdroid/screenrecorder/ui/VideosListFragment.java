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

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.Video;
import com.orpheusdroid.screenrecorder.adapter.VideoRecyclerAdapter;
import com.orpheusdroid.screenrecorder.interfaces.PermissionResultListener;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * <p>
 *     This fragment lists the videos recorded
 * </p>
 *
 * @author Vijai Chandra Prasad .R
 * @see VideoRecyclerAdapter
 * @see PermissionResultListener
 * @see GetVideosAsync
 */
public class VideosListFragment extends Fragment implements PermissionResultListener, SwipeRefreshLayout.OnRefreshListener {
    /**
     * RecyclerView
     */
    private RecyclerView videoRV;

    /**
     * TextView to display empty videos or permission denied message
     */
    private TextView message;

    /**
     * SharedPreference object
     */
    private SharedPreferences prefs;

    /**
     * SwipeRefreshLayout object to refresh list
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * ArrayList to store all videos from the save location
     */
    private ArrayList<Video> videosList = new ArrayList<>();

    private boolean loadInOnCreate = false;

    public VideosListFragment() {

    }

    /**
     * Method to check if the file's meme type is video
     *
     * @param path String - path to the file
     * @return boolean
     */
    private static boolean isVideoFile(String path) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment.isVideoFile(java.lang.String)",path);try{String mimeType = URLConnection.guessContentTypeFromName(path);
        {com.mijack.Xlog.logStaticMethodExit("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment.isVideoFile(java.lang.String)");return mimeType != null && mimeType.startsWith("video");}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment.isVideoFile(java.lang.String)",throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{View view = inflater.inflate(R.layout.fragment_videos, container, false);
        message = view.findViewById(R.id.message_tv);
        videoRV = view.findViewById(R.id.videos_rv);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (loadInOnCreate)
            {checkPermission();}

        {com.mijack.Xlog.logMethodExit("android.view.View com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /*//Load videos from the directory only when the fragment is visible to the screen*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setUserVisibleHint(boolean)",this,isVisibleToUser);try{super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity() != null) {
            Log.d(Const.TAG, "Videos fragment is visible load the videos");
            checkPermission();
        } else if (isVisibleToUser && getActivity() == null)
            {loadInOnCreate = true;}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setUserVisibleHint(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setUserVisibleHint(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this,menu,inflater);try{super.onCreateOptionsMenu(menu, inflater);
        MenuItem refresh = menu.add("Refresh");
        refresh.setIcon(R.drawable.ic_refresh_white_24dp);
        refresh.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$1.onMenuItemClick(android.view.MenuItem)",this,menuItem);try{/*// Prevent repeated refresh requests*/
                if (swipeRefreshLayout.isRefreshing())
                        {{com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$1.onMenuItemClick(android.view.MenuItem)",this);return false;}}}
                videosList.clear();
                checkPermission();
                Log.d(Const.TAG, "Refreshing");
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this);{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$1.onMenuItemClick(android.view.MenuItem)",this);return false;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$1.onMenuItemClick(android.view.MenuItem)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)",this,throwable);throw throwable;}
    }

    /**
     * Check if we have permission to read the external storage and load the videos into ArrayList<Video>
     */
    private void checkPermission() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.checkPermission()",this);try{if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setPermissionResultListener(this);
                ((MainActivity) getActivity()).requestPermissionStorage();
            }
        } else {
            /*//We have required permission now and lets populate the video from the selected*/
            /*// directory if the arraylist holding videos is empty*/
            if (videosList.isEmpty()) {
                File directory = new File(prefs.getString(getString(R.string.savelocation_key),
                        Environment.getExternalStorageDirectory()
                                + File.separator + "screenrecorder"));
                /*//Remove directory pointers and other files from the list*/
                if (!directory.exists()){
                    MainActivity.createDir();
                    Log.d(Const.TAG, "Directory missing! Creating dir");
                }
                ArrayList<File> filesList = new ArrayList<File>();
                if (directory.isDirectory() && directory.exists()) {
                    filesList.addAll(Arrays.asList(getVideos(directory.listFiles())));
                }
                /*//Read the videos and extract details from it in async.*/
                /*// This is essential if the directory contains huge number of videos*/

                new GetVideosAsync().execute(filesList.toArray(new File[filesList.size()]));
            }
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.checkPermission()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.checkPermission()",this,throwable);throw throwable;}
    }

    /**
     * Filter all video files from array of files
     *
     * @param files File[] containing files from a directory
     * @return File[] containing only video files
     */
    private File[] getVideos(File[] files) {
        com.mijack.Xlog.logMethodEnter("[java.io.File com.orpheusdroid.screenrecorder.ui.VideosListFragment.getVideos([java.io.File)",this,files);try{List<File> newFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && isVideoFile(file.getPath()))
                {newFiles.add(file);}
        }
        {com.mijack.Xlog.logMethodExit("[java.io.File com.orpheusdroid.screenrecorder.ui.VideosListFragment.getVideos([java.io.File)",this);return newFiles.toArray(new File[newFiles.size()]);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.io.File com.orpheusdroid.screenrecorder.ui.VideosListFragment.getVideos([java.io.File)",this,throwable);throw throwable;}
    }

    /**
     * Init recyclerview once the videos list is ready
     *
     * @param videos ArrayList<Video>
     */
    private void setRecyclerView(ArrayList<Video> videos) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setRecyclerView(java.util.ArrayList)",this,videos);try{videoRV.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        videoRV.setLayoutManager(layoutManager);
        final VideoRecyclerAdapter adapter = new VideoRecyclerAdapter(getActivity(), videos, this);
        videoRV.setAdapter(adapter);
        /*//Set the span to 1 (width to match the screen) if the view type is section*/
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.ui.VideosListFragment$2.getSpanSize(int)",this,position);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.ui.VideosListFragment$2.getSpanSize(int)",this);{com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setRecyclerView(java.util.ArrayList)",this);return adapter.isSection(position) ? layoutManager.getSpanCount() : 1;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.ui.VideosListFragment$2.getSpanSize(int)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.setRecyclerView(java.util.ArrayList)",this,throwable);throw throwable;}
    }

    /*//Permission result callback method*/
    @Override
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onPermissionResult(int,[java.lang.String,[int)",this,requestCode,permissions,grantResults);try{switch (requestCode) {
            case Const.EXTDIR_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "Storage permission granted.");
                    /*//Performing storage task immediately after granting permission sometimes causes*/
                    /*//permission not taking effect.*/
                    checkPermission();
                } else {
                    Log.d(Const.TAG, "Storage permission denied.");
                    videoRV.setVisibility(View.GONE);
                    message.setText(R.string.video_list_permission_denied_message);
                }
                break;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onPermissionResult(int,[java.lang.String,[int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onPermissionResult(int,[java.lang.String,[int)",this,throwable);throw throwable;}
    }

    /**
     * Clear the videos ArrayList once the save directory is changed which forces reloading of videos from new directory
     */
    public void removeVideosList() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.removeVideosList()",this);try{videosList.clear();
        Log.d(Const.TAG, "Reached video fragment");com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.removeVideosList()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.removeVideosList()",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{super.onActivityResult(requestCode, resultCode, data);
        Log.d(Const.TAG, "Refresh data after edit!");
        removeVideosList();
        checkPermission();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    @Override
    public void onRefresh() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onRefresh()",this);try{videosList.clear();
        checkPermission();
        Log.d(Const.TAG, "Refreshing");com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onRefresh()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment.onRefresh()",this,throwable);throw throwable;}
    }

    /**
     * Class to retrieve videos from the directory asynchronously
     *
     * @author Vijai Chandra Prasad .R
     */
    class GetVideosAsync extends AsyncTask<File[], Integer, ArrayList<Video>> {
        /*//ProgressDialog progress;*/
        File[] files;
        ContentResolver resolver;

        GetVideosAsync() {
            resolver = getActivity().getApplicationContext().getContentResolver();
        }

        @Override
        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPreExecute()",this);try{super.onPreExecute();
            /*//Set refreshing to true*/
            swipeRefreshLayout.setRefreshing(true);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPostExecute(java.util.ArrayList)",this,videos);try{/*//If the directory has no videos, remove recyclerview from rootview and show empty message.*/
            /*// Else set recyclerview and remove message textview*/
            if (videos.isEmpty()) {
                videoRV.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            } else {
                /*//Sort the videos in a descending order*/
                Collections.sort(videos, Collections.<Video>reverseOrder());
                setRecyclerView(addSections(videos));
                videoRV.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
            }
            /*//Finish refreshing*/
            swipeRefreshLayout.setRefreshing(false);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPostExecute(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onPostExecute(java.util.ArrayList)",this,throwable);throw throwable;}
        }

        /*//Add sections depending on the date the video is recorded to array list*/
        private ArrayList<Video> addSections(ArrayList<Video> videos) {
            com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addSections(java.util.ArrayList)",this,videos);try{ArrayList<Video> videosWithSections = new ArrayList<>();
            Date currentSection = new Date();
            Log.d(Const.TAG, "Original Length: " + videos.size());
            for (int i = 0; i < videos.size(); i++) {
                Video video = videos.get(i);
                /*//Add the first section arbitrarily*/
                if (i==0){
                    videosWithSections.add(new Video(true, video.getLastModified()));
                    videosWithSections.add(video);
                    currentSection = video.getLastModified();
                    continue;
                }
                if (addNewSection(currentSection, video.getLastModified())){
                    videosWithSections.add(new Video(true, video.getLastModified()));
                    currentSection = video.getLastModified();
                }
                videosWithSections.add(video);
            }
            Log.d(Const.TAG, "Length with sections: " + videosWithSections.size());
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addSections(java.util.ArrayList)",this);return videosWithSections;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addSections(java.util.ArrayList)",this,throwable);throw throwable;}
        }

        /**
         * Method to add date sections to videos list
         * <p>
         * <p></p>Check if a new Section is to be added by comparing the difference of the section date
         * and the video's last modified date</p>
         *
         * @param current Date of current video
         * @param next    Date of next video
         * @return boolean if a new section must be added
         */
        private boolean addNewSection(Date current, Date next)
        {
            com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addNewSection(java.util.Date,java.util.Date)",this,current,next);try{Calendar currentSectionDate = toCalendar(current.getTime());
            Calendar nextVideoDate = toCalendar(next.getTime());

            /*// Get the represented date in milliseconds*/
            long milis1 = currentSectionDate.getTimeInMillis();
            long milis2 = nextVideoDate.getTimeInMillis();

            /*// Calculate difference in milliseconds*/
            int dayDiff = (int)Math.abs((milis2 - milis1) / (24 * 60 * 60 * 1000));
            Log.d(Const.TAG, "Date diff is: " + (dayDiff));
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addNewSection(java.util.Date,java.util.Date)",this);return dayDiff > 0;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.addNewSection(java.util.Date,java.util.Date)",this,throwable);throw throwable;}
        }

        /**
         * Method to return a Calander object from the timestamp
         *
         * @param timestamp long timestamp
         * @return Calendar
         */
        private Calendar toCalendar(long timestamp)
        {
            com.mijack.Xlog.logMethodEnter("java.util.Calendar com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.toCalendar(long)",this,timestamp);try{Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            {com.mijack.Xlog.logMethodExit("java.util.Calendar com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.toCalendar(long)",this);return calendar;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Calendar com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.toCalendar(long)",this,throwable);throw throwable;}
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onProgressUpdate([java.lang.Integer)",this,values);try{super.onProgressUpdate(values);

            Log.d(Const.TAG, "Progress is :" + values[0]);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onProgressUpdate([java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.onProgressUpdate([java.lang.Integer)",this,throwable);throw throwable;}
        }

        @Override
        protected ArrayList<Video> doInBackground(File[]... arg) {
            com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.doInBackground([File[])",this,arg);try{/*//Get video file name, Uri and video thumbnail from mediastore*/
            files = arg[0];
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.isDirectory() && isVideoFile(file.getPath())) {
                    videosList.add(new Video(file.getName(),
                            file,
                            getBitmap(file),
                            new Date(file.lastModified())));
                    /*//Update progress dialog*/
                    publishProgress(i);
                }
            }
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.doInBackground([File[])",this);return videosList;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.doInBackground([File[])",this,throwable);throw throwable;}
        }

        /**
         * Method to get thumbnail from mediastore for video file
         *
         * @param file File object of the video
         * @return Bitmap
         */
        Bitmap getBitmap(File file) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.getBitmap(java.io.File)",this,file);try{String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
            Cursor cursor = resolver.query(MediaStore.Video.Media.getContentUri("external"),
                    projection,
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{file.getPath()}, null);

            if (cursor != null && cursor.moveToNext()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int id = cursor.getInt(idColumn);
                Bitmap thumbNail = MediaStore.Video.Thumbnails.getThumbnail(resolver, id,
                        MediaStore.Video.Thumbnails.MINI_KIND, null);
                Log.d(Const.TAG, "Retrieved thumbnail for file: " + file.getName());
                cursor.close();
                {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.getBitmap(java.io.File)",this);return thumbNail;}
            }
            {com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.getBitmap(java.io.File)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.orpheusdroid.screenrecorder.ui.VideosListFragment$GetVideosAsync.getBitmap(java.io.File)",this,throwable);throw throwable;}
        }
    }
}
