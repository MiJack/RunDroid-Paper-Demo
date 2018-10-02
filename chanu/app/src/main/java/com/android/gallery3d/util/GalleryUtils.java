/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.util;

import android.os.Build;
import com.chanapps.four.component.URLFormatComponent;
import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.app.PackagesMonitor;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.JobContext;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GalleryUtils {
    private static final String TAG = "GalleryUtils";
    private static final String MAPS_PACKAGE_NAME = "com.google.android.apps.maps";
    private static final String MAPS_CLASS_NAME = "com.google.android.maps.MapsActivity";

    private static final String MIME_TYPE_IMAGE = "image/*";
    private static final String MIME_TYPE_VIDEO = "video/*";
    private static final String MIME_TYPE_ALL = "*/*";
    private static final String DIR_TYPE_IMAGE = "vnd.android.cursor.dir/image";
    private static final String DIR_TYPE_VIDEO = "vnd.android.cursor.dir/video";

    private static final String PREFIX_PHOTO_EDITOR_UPDATE = "editor-update-";
    private static final String PREFIX_HAS_PHOTO_EDITOR = "has-editor-";

    private static final String KEY_CAMERA_UPDATE = "camera-update";
    private static final String KEY_HAS_CAMERA = "has-camera";

    private static Context sContext;


    static float sPixelDensity = -1f;

    public static void initialize(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.initialize(com.android.gallery3d.util.ThreadPool.JobContext)",context);try{sContext = context;
        if (sPixelDensity < 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            sPixelDensity = metrics.density;
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.initialize(com.android.gallery3d.util.ThreadPool.JobContext)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.initialize(com.android.gallery3d.util.ThreadPool.JobContext)",throwable);throw throwable;}
    }

    public static float dpToPixel(float dp) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.util.GalleryUtils.dpToPixel(float)",dp);try{com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.util.GalleryUtils.dpToPixel(float)");return sPixelDensity * dp;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.util.GalleryUtils.dpToPixel(float)",throwable);throw throwable;}
    }

    public static int dpToPixel(int dp) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.util.GalleryUtils.dpToPixel(int)",dp);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.dpToPixel(int)");return Math.round(dpToPixel((float) dp));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.util.GalleryUtils.dpToPixel(int)",throwable);throw throwable;}
    }

    public static int meterToPixel(float meter) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.util.GalleryUtils.meterToPixel(float)",meter);try{/*// 1 meter = 39.37 inches, 1 inch = 160 dp.*/
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.meterToPixel(float)");return Math.round(dpToPixel(meter * 39.37f * 160));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.util.GalleryUtils.meterToPixel(float)",throwable);throw throwable;}
    }

    public static byte[] getBytes(String in) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.util.GalleryUtils.getBytes(java.lang.String)",in);try{byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray()) {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.util.GalleryUtils.getBytes(java.lang.String)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.util.GalleryUtils.getBytes(java.lang.String)",throwable);throw throwable;}
    }

    /*// Below are used the detect using database in the render thread. It only*/
    /*// works most of the time, but that's ok because it's for debugging only.*/

    private static volatile Thread sCurrentThread;
    private static volatile boolean sWarned;

    public static void setRenderThread() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.setRenderThread()");try{sCurrentThread = Thread.currentThread();com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.setRenderThread()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.setRenderThread()",throwable);throw throwable;}
    }

    public static void assertNotInRenderThread() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.assertNotInRenderThread()");try{if (!sWarned) {
            if (Thread.currentThread() == sCurrentThread) {
                sWarned = true;
                Log.w(TAG, new Throwable("Should not do this in render thread"));
            }
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.assertNotInRenderThread()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.assertNotInRenderThread()",throwable);throw throwable;}
    }

    private static final double RAD_PER_DEG = Math.PI / 180.0;
    private static final double EARTH_RADIUS_METERS = 6367000.0;

    public static double fastDistanceMeters(double latRad1, double lngRad1,
            double latRad2, double lngRad2) {
       com.mijack.Xlog.logStaticMethodEnter("double com.android.gallery3d.util.GalleryUtils.fastDistanceMeters(double,double,double,double)",latRad1,lngRad1,latRad2,lngRad2);try{if ((Math.abs(latRad1 - latRad2) > RAD_PER_DEG)
             || (Math.abs(lngRad1 - lngRad2) > RAD_PER_DEG)) {
           {com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.util.GalleryUtils.fastDistanceMeters(double,double,double,double)");return accurateDistanceMeters(latRad1, lngRad1, latRad2, lngRad2);}
       }
       /*// Approximate sin(x) = x.*/
       double sineLat = (latRad1 - latRad2);

       /*// Approximate sin(x) = x.*/
       double sineLng = (lngRad1 - lngRad2);

       /*// Approximate cos(lat1) * cos(lat2) using*/
       /*// cos((lat1 + lat2)/2) ^ 2*/
       double cosTerms = Math.cos((latRad1 + latRad2) / 2.0);
       cosTerms = cosTerms * cosTerms;
       double trigTerm = sineLat * sineLat + cosTerms * sineLng * sineLng;
       trigTerm = Math.sqrt(trigTerm);

       /*// Approximate arcsin(x) = x*/
       {com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.util.GalleryUtils.fastDistanceMeters(double,double,double,double)");return EARTH_RADIUS_METERS * trigTerm;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("double com.android.gallery3d.util.GalleryUtils.fastDistanceMeters(double,double,double,double)",throwable);throw throwable;}
    }

    public static double accurateDistanceMeters(double lat1, double lng1,
            double lat2, double lng2) {
        com.mijack.Xlog.logStaticMethodEnter("double com.android.gallery3d.util.GalleryUtils.accurateDistanceMeters(double,double,double,double)",lat1,lng1,lat2,lng2);try{double dlat = Math.sin(0.5 * (lat2 - lat1));
        double dlng = Math.sin(0.5 * (lng2 - lng1));
        double x = dlat * dlat + dlng * dlng * Math.cos(lat1) * Math.cos(lat2);
        {com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.util.GalleryUtils.accurateDistanceMeters(double,double,double,double)");return (2 * Math.atan2(Math.sqrt(x), Math.sqrt(Math.max(0.0,
                1.0 - x)))) * EARTH_RADIUS_METERS;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("double com.android.gallery3d.util.GalleryUtils.accurateDistanceMeters(double,double,double,double)",throwable);throw throwable;}
    }


    public static final double toMile(double meter) {
        com.mijack.Xlog.logStaticMethodEnter("double com.android.gallery3d.util.GalleryUtils.toMile(double)",meter);try{com.mijack.Xlog.logStaticMethodExit("double com.android.gallery3d.util.GalleryUtils.toMile(double)");return meter / 1609;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("double com.android.gallery3d.util.GalleryUtils.toMile(double)",throwable);throw throwable;}
    }

    /*// For debugging, it will block the caller for timeout millis.*/
    public static void fakeBusy(JobContext jc, int timeout) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.fakeBusy(com.android.gallery3d.util.ThreadPool.JobContext,int)",jc,timeout);try{final ConditionVariable cv = new ConditionVariable();
        jc.setCancelListener(new CancelListener() {
            public void onCancel() {
                com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.util.GalleryUtils$1.onCancel()",this);try{cv.open();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.util.GalleryUtils$1.onCancel()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils$1.onCancel()",this,throwable);throw throwable;}
            }
        });
        cv.block(timeout);
        jc.setCancelListener(null);com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.fakeBusy(com.android.gallery3d.util.ThreadPool.JobContext,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.fakeBusy(com.android.gallery3d.util.ThreadPool.JobContext,int)",throwable);throw throwable;}
    }

    public static boolean isEditorAvailable(Context context, String mimeType) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.util.GalleryUtils.isEditorAvailable(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String)",context,mimeType);try{int version = PackagesMonitor.getPackagesVersion(context);

        String updateKey = PREFIX_PHOTO_EDITOR_UPDATE + mimeType;
        String hasKey = PREFIX_HAS_PHOTO_EDITOR + mimeType;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getInt(updateKey, 0) != version) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> infos = packageManager.queryIntentActivities(
                    new Intent(Intent.ACTION_EDIT).setType(mimeType), 0);
            prefs.edit().putInt(updateKey, version)
                        .putBoolean(hasKey, !infos.isEmpty())
                        .commit();
        }

        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.isEditorAvailable(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String)");return prefs.getBoolean(hasKey, true);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.util.GalleryUtils.isEditorAvailable(com.android.gallery3d.util.ThreadPool.JobContext,java.lang.String)",throwable);throw throwable;}
    }

    public static boolean isCameraAvailable(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.util.GalleryUtils.isCameraAvailable(com.android.gallery3d.util.ThreadPool.JobContext)",context);try{int version = PackagesMonitor.getPackagesVersion(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getInt(KEY_CAMERA_UPDATE, 0) != version) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> infos = packageManager.queryIntentActivities(
                    new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA), 0);
            prefs.edit().putInt(KEY_CAMERA_UPDATE, version)
                        .putBoolean(KEY_HAS_CAMERA, !infos.isEmpty())
                        .commit();
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.isCameraAvailable(com.android.gallery3d.util.ThreadPool.JobContext)");return prefs.getBoolean(KEY_HAS_CAMERA, true);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.util.GalleryUtils.isCameraAvailable(com.android.gallery3d.util.ThreadPool.JobContext)",throwable);throw throwable;}
    }

    public static boolean isValidLocation(double latitude, double longitude) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.util.GalleryUtils.isValidLocation(double,double)",latitude,longitude);try{/*// TODO: change || to && after we fix the default location issue*/
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.isValidLocation(double,double)");return (latitude != MediaItem.INVALID_LATLNG || longitude != MediaItem.INVALID_LATLNG);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.util.GalleryUtils.isValidLocation(double,double)",throwable);throw throwable;}
    }

    public static String formatLatitudeLongitude(String format, double latitude,
            double longitude) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.util.GalleryUtils.formatLatitudeLongitude(java.lang.String,double,double)",format,latitude,longitude);try{/*// We need to specify the locale otherwise it may go wrong in some language*/
        /*// (e.g. Locale.FRENCH)*/
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.util.GalleryUtils.formatLatitudeLongitude(java.lang.String,double,double)");return String.format(Locale.ENGLISH, format, latitude, longitude);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.GalleryUtils.formatLatitudeLongitude(java.lang.String,double,double)",throwable);throw throwable;}
    }

    public static void showOnMap(Context context, double latitude, double longitude) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.showOnMap(com.android.gallery3d.util.ThreadPool.JobContext,double,double)",context,latitude,longitude);try{try {
            /*// We don't use "geo:latitude,longitude" because it only centers*/
            /*// the MapView to the specified location, but we need a marker*/
            /*// for further operations (routing to/from).*/
            /*// The q=(lat, lng) syntax is suggested by geo-team.*/
            String uri = formatLatitudeLongitude(
                    URLFormatComponent.getUrl(context, URLFormatComponent.GOOGLE_MAPS_URL_FORMAT),
                    latitude, longitude);
            ComponentName compName = new ComponentName(MAPS_PACKAGE_NAME,
                    MAPS_CLASS_NAME);
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(uri)).setComponent(compName);
            context.startActivity(mapsIntent);
        } catch (ActivityNotFoundException e) {
            /*// Use the "geo intent" if no GMM is installed*/
            Log.e(TAG, "GMM activity not found!", e);
            String url = formatLatitudeLongitude("geo:%f,%f", latitude, longitude);
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(mapsIntent);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.showOnMap(com.android.gallery3d.util.ThreadPool.JobContext,double,double)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.showOnMap(com.android.gallery3d.util.ThreadPool.JobContext,double,double)",throwable);throw throwable;}
    }

    public static void setViewPointMatrix(
            float matrix[], float x, float y, float z) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.setViewPointMatrix(float,float,float,float)",matrix[],x,y,z);try{/*// The matrix is*/
        /*// -z,  0,  x,  0*/
        /*//  0, -z,  y,  0*/
        /*//  0,  0,  1,  0*/
        /*//  0,  0,  1, -z*/
        Arrays.fill(matrix, 0, 16, 0);
        matrix[0] = matrix[5] = matrix[15] = -z;
        matrix[8] = x;
        matrix[9] = y;
        matrix[10] = matrix[11] = 1;com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.setViewPointMatrix(float,float,float,float)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.setViewPointMatrix(float,float,float,float)",throwable);throw throwable;}
    }

    public static int getBucketId(String path) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.util.GalleryUtils.getBucketId(java.lang.String)",path);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.getBucketId(java.lang.String)");return path.toLowerCase().hashCode();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.util.GalleryUtils.getBucketId(java.lang.String)",throwable);throw throwable;}
    }

    /*// Returns a (localized) string for the given duration (in seconds).*/
    public static String formatDuration(final Context context, int duration) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.util.GalleryUtils.formatDuration(com.android.gallery3d.util.ThreadPool.JobContext,int)",context,duration);try{int h = duration / 3600;
        int m = (duration - h * 3600) / 60;
        int s = duration - (h * 3600 + m * 60);
        String durationValue;
        if (h == 0) {
            durationValue = String.format(context.getString(R.string.details_ms), m, s);
        } else {
            durationValue = String.format(context.getString(R.string.details_hms), h, m, s);
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.util.GalleryUtils.formatDuration(com.android.gallery3d.util.ThreadPool.JobContext,int)");return durationValue;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.util.GalleryUtils.formatDuration(com.android.gallery3d.util.ThreadPool.JobContext,int)",throwable);throw throwable;}
    }

    public static void setSpinnerVisibility(final Activity activity,
            final boolean visible) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.setSpinnerVisibility(com.android.gallery3d.app.GalleryActivity,boolean)",activity,visible);try{SpinnerVisibilitySetter.getInstance(activity).setSpinnerVisibility(visible);com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.setSpinnerVisibility(com.android.gallery3d.app.GalleryActivity,boolean)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.setSpinnerVisibility(com.android.gallery3d.app.GalleryActivity,boolean)",throwable);throw throwable;}
    }

    public static int determineTypeBits(Context context, Intent intent) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.util.GalleryUtils.determineTypeBits(com.android.gallery3d.util.ThreadPool.JobContext,android.content.Intent)",context,intent);try{int typeBits = 0;
        String type = intent.resolveType(context);

        if (MIME_TYPE_ALL.equals(type)) {
            typeBits = DataManager.INCLUDE_ALL;
        } else if (MIME_TYPE_IMAGE.equals(type) ||
                DIR_TYPE_IMAGE.equals(type)) {
            typeBits = DataManager.INCLUDE_IMAGE;
        } else if (MIME_TYPE_VIDEO.equals(type) ||
                DIR_TYPE_VIDEO.equals(type)) {
            typeBits = DataManager.INCLUDE_VIDEO;
        } else {
            typeBits = DataManager.INCLUDE_ALL;
        }

        if (intent.getBooleanExtra(Intent.EXTRA_LOCAL_ONLY, false)) {
            typeBits |= DataManager.INCLUDE_LOCAL_ONLY;
        }

        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.determineTypeBits(com.android.gallery3d.util.ThreadPool.JobContext,android.content.Intent)");return typeBits;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.util.GalleryUtils.determineTypeBits(com.android.gallery3d.util.ThreadPool.JobContext,android.content.Intent)",throwable);throw throwable;}
    }

    public static int getSelectionModePrompt(int typeBits) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.util.GalleryUtils.getSelectionModePrompt(int)",typeBits);try{if ((typeBits & DataManager.INCLUDE_VIDEO) != 0) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.getSelectionModePrompt(int)");return (typeBits & DataManager.INCLUDE_IMAGE) == 0
                    ? R.string.select_video
                    : R.string.select_item;}
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.util.GalleryUtils.getSelectionModePrompt(int)");return R.string.select_image;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.util.GalleryUtils.getSelectionModePrompt(int)",throwable);throw throwable;}
    }

    public static boolean hasSpaceForSize(long size) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.util.GalleryUtils.hasSpaceForSize(long)",size);try{String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.hasSpaceForSize(long)");return false;}
        }

        String path = Environment.getExternalStorageDirectory().getPath();
        try {
            StatFs stat = new StatFs(path);
            long availableSize;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {availableSize = deprecatedAvailableSize(stat);}
            else
                {availableSize = availableSize(stat);}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.hasSpaceForSize(long)");return availableSize > size;}
        } catch (Exception e) {
            Log.i(TAG, "Fail to access external storage", e);
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.hasSpaceForSize(long)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.util.GalleryUtils.hasSpaceForSize(long)",throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected static long deprecatedAvailableSize(StatFs stat) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.util.GalleryUtils.deprecatedAvailableSize(android.os.StatFs)",stat);try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.util.GalleryUtils.deprecatedAvailableSize(android.os.StatFs)");return stat.getAvailableBlocks() * (long) stat.getBlockSize();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.util.GalleryUtils.deprecatedAvailableSize(android.os.StatFs)",throwable);throw throwable;}
    }

    protected static long availableSize(StatFs stat) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.util.GalleryUtils.availableSize(android.os.StatFs)",stat);try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.util.GalleryUtils.availableSize(android.os.StatFs)");return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.util.GalleryUtils.availableSize(android.os.StatFs)",throwable);throw throwable;}
    }

    public static void assertInMainThread() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.assertInMainThread()");try{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.assertInMainThread()");if (Thread.currentThread() == sContext.getMainLooper().getThread()) {
            throw new AssertionError();
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.assertInMainThread()",throwable);throw throwable;}
    }

    public static void doubleToRational(double value, long[] output) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long)",value,output);try{/*// error is a magic number to control the tollerance of error*/
        doubleToRational(value, output, 0.00001);com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long)",throwable);throw throwable;}
    }

    private static void doubleToRational(double value, long[] output, double error) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long,double)",value,output,error);try{long number = (long) value;
        value -= number;
        if (value < 0.000001 || error > 1) {
            output[0] = (int) (number + value + 0.5);
            output[1] = 1;
        } else {
            doubleToRational(1.0 / value, output, error / value);
            number = number * output[0] + output[1];
            output[1] = output[0];
            output[0] = number;
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long,double)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.doubleToRational(double,[long,double)",throwable);throw throwable;}
    }

    public static boolean isPanorama(MediaItem item) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.util.GalleryUtils.isPanorama(com.android.gallery3d.data.MediaItem)",item);try{if (item == null) {{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.isPanorama(com.android.gallery3d.data.MediaItem)");return false;}}
        int w = item.getWidth();
        int h = item.getHeight();
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.util.GalleryUtils.isPanorama(com.android.gallery3d.data.MediaItem)");return (h > 0 && w / h >= 2);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.util.GalleryUtils.isPanorama(com.android.gallery3d.data.MediaItem)",throwable);throw throwable;}
    }

	public static void removeActivity(GalleryActivity activity) {
		com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.util.GalleryUtils.removeActivity(com.android.gallery3d.app.GalleryActivity)",activity);try{SpinnerVisibilitySetter.removeInstance(activity);com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.util.GalleryUtils.removeActivity(com.android.gallery3d.app.GalleryActivity)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.util.GalleryUtils.removeActivity(com.android.gallery3d.app.GalleryActivity)",throwable);throw throwable;}
	}
}
