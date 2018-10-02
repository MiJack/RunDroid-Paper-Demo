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

package com.android.gallery3d.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import java.io.Closeable;
import java.io.InterruptedIOException;
import java.util.Random;

public class Utils {
    private static final String TAG = "Utils";
    private static final String DEBUG_TAG = "GalleryDebug";

    private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

    private static long[] sCrcTable = new long[256];

    private static final boolean IS_DEBUG_BUILD =
            Build.TYPE.equals("eng") || Build.TYPE.equals("userdebug");

    private static final String MASK_STRING = "********************************";

    /*// Throws AssertionError if the input is false.*/
    public static void assertTrue(boolean cond) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.assertTrue(boolean)",cond);try{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.assertTrue(boolean)");if (!cond) {
            throw new AssertionError();
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.assertTrue(boolean)",throwable);throw throwable;}
    }

    /*// Throws AssertionError if the input is false.*/
    public static void assertTrue(boolean cond, String message, Object ... args) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.assertTrue(boolean,java.lang.String,[Object )",cond,message,args);try{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.assertTrue(boolean,java.lang.String,[Object )");if (!cond) {
            throw new AssertionError(
                    args.length == 0 ? message : String.format(message, args));
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.assertTrue(boolean,java.lang.String,[Object )",throwable);throw throwable;}
    }

    /*// Throws NullPointerException if the input is null.*/
    public static <T> T checkNotNull(T object) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.Object com.android.gallery3d.common.Utils.checkNotNull(java.lang.Object)",object);try{if (object == null) {throw new NullPointerException();}
        {com.mijack.Xlog.logStaticMethodExit("java.lang.Object com.android.gallery3d.common.Utils.checkNotNull(java.lang.Object)");return object;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.Object com.android.gallery3d.common.Utils.checkNotNull(java.lang.Object)",throwable);throw throwable;}
    }

    /*// Returns true if two input Object are both null or equal*/
    /*// to each other.*/
    public static boolean equals(Object a, Object b) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.equals(java.lang.Object,java.lang.Object)",a,b);try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.equals(java.lang.Object,java.lang.Object)");return (a == b) || (a == null ? false : a.equals(b));}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.equals(java.lang.Object,java.lang.Object)",throwable);throw throwable;}
    }

    /*// Returns true if the input is power of 2.*/
    /*// Throws IllegalArgumentException if the input is <= 0.*/
    public static boolean isPowerOf2(int n) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.isPowerOf2(int)",n);try{if (n <= 0) {throw new IllegalArgumentException();}
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.isPowerOf2(int)");return (n & -n) == n;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.isPowerOf2(int)",throwable);throw throwable;}
    }

    /*// Returns the next power of two.*/
    /*// Returns the input if it is already power of 2.*/
    /*// Throws IllegalArgumentException if the input is <= 0 or*/
    /*// the answer overflows.*/
    public static int nextPowerOf2(int n) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.nextPowerOf2(int)",n);try{if (n <= 0 || n > (1 << 30)) {throw new IllegalArgumentException();}
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.nextPowerOf2(int)");return n + 1;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.nextPowerOf2(int)",throwable);throw throwable;}
    }

    /*// Returns the previous power of two.*/
    /*// Returns the input if it is already power of 2.*/
    /*// Throws IllegalArgumentException if the input is <= 0*/
    public static int prevPowerOf2(int n) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.prevPowerOf2(int)",n);try{if (n <= 0) {throw new IllegalArgumentException();}
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.prevPowerOf2(int)");return Integer.highestOneBit(n);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.prevPowerOf2(int)",throwable);throw throwable;}
    }

    /*// Returns the euclidean distance between (x, y) and (sx, sy).*/
    public static float distance(float x, float y, float sx, float sy) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.common.Utils.distance(float,float,float,float)",x,y,sx,sy);try{float dx = x - sx;
        float dy = y - sy;
        {com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.distance(float,float,float,float)");return (float) Math.hypot(dx, dy);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.common.Utils.distance(float,float,float,float)",throwable);throw throwable;}
    }

    /*// Returns the input value x clamped to the range [min, max].*/
    public static int clamp(int x, int min, int max) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.clamp(int,int,int)",x,min,max);try{if (x > max) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.clamp(int,int,int)");return max;}}
        if (x < min) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.clamp(int,int,int)");return min;}}
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.clamp(int,int,int)");return x;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.clamp(int,int,int)",throwable);throw throwable;}
    }

    /*// Returns the input value x clamped to the range [min, max].*/
    public static float clamp(float x, float min, float max) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.common.Utils.clamp(float,float,float)",x,min,max);try{if (x > max) {{com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.clamp(float,float,float)");return max;}}
        if (x < min) {{com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.clamp(float,float,float)");return min;}}
        {com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.clamp(float,float,float)");return x;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.common.Utils.clamp(float,float,float)",throwable);throw throwable;}
    }

    /*// Returns the input value x clamped to the range [min, max].*/
    public static long clamp(long x, long min, long max) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.common.Utils.clamp(long,long,long)",x,min,max);try{if (x > max) {{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.clamp(long,long,long)");return max;}}
        if (x < min) {{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.clamp(long,long,long)");return min;}}
        {com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.clamp(long,long,long)");return x;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.common.Utils.clamp(long,long,long)",throwable);throw throwable;}
    }

    public static boolean isOpaque(int color) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.isOpaque(int)",color);try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.isOpaque(int)");return color >>> 24 == 0xFF;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.isOpaque(int)",throwable);throw throwable;}
    }

    public static <T> void swap(T[] array, int i, int j) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.swap([java.lang.Object,int,int)",array,i,j);try{T temp = array[i];
        array[i] = array[j];
        array[j] = temp;com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.swap([java.lang.Object,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.swap([java.lang.Object,int,int)",throwable);throw throwable;}
    }

    public static void swap(int[] array, int i, int j) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.swap([int,int,int)",array,i,j);try{int temp = array[i];
        array[i] = array[j];
        array[j] = temp;com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.swap([int,int,int)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.swap([int,int,int)",throwable);throw throwable;}
    }

    /**
     * A function thats returns a 64-bit crc for string
     *
     * @param in input string
     * @return a 64-bit crc value
     */
    public static final long crc64Long(String in) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.common.Utils.crc64Long(java.lang.String)",in);try{if (in == null || in.length() == 0) {
            {com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.crc64Long(java.lang.String)");return 0;}
        }
        {com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.crc64Long(java.lang.String)");return crc64Long(getBytes(in));}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.common.Utils.crc64Long(java.lang.String)",throwable);throw throwable;}
    }

    static {
        long part;
        for (int i = 0; i < 256; i++) {
            part = i;
            for (int j = 0; j < 8; j++) {
                long x = ((int) part & 1) != 0 ? POLY64REV : 0;
                part = (part >> 1) ^ x;
            }
            sCrcTable[i] = part;
        }
    }

    public static final long crc64Long(byte[] buffer) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.common.Utils.crc64Long([byte)",buffer);try{long crc = INITIALCRC;
        for (int k = 0, n = buffer.length; k < n; ++k) {
            crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
        }
        {com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.crc64Long([byte)");return crc;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.common.Utils.crc64Long([byte)",throwable);throw throwable;}
    }

    public static byte[] getBytes(String in) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.common.Utils.getBytes(java.lang.String)",in);try{byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray()) {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.common.Utils.getBytes(java.lang.String)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.common.Utils.getBytes(java.lang.String)",throwable);throw throwable;}
    }

    public static void closeSilently(Closeable c) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.closeSilently(java.io.Closeable)",c);try{if (c == null) {{com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.closeSilently(java.io.Closeable)");return;}}
        try {
            c.close();
        } catch (Throwable t) {
            Log.w(TAG, "close fail", t);
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.closeSilently(java.io.Closeable)",throwable);throw throwable;}
    }

    public static int compare(long a, long b) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.compare(long,long)",a,b);try{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.compare(long,long)");return a < b ? -1 : a == b ? 0 : 1;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.compare(long,long)",throwable);throw throwable;}
    }

    public static int ceilLog2(float value) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.ceilLog2(float)",value);try{int i;
        for (i = 0; i < 31; i++) {
            if ((1 << i) >= value) {break;}
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.ceilLog2(float)");return i;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.ceilLog2(float)",throwable);throw throwable;}
    }

    public static int floorLog2(float value) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.floorLog2(float)",value);try{int i;
        for (i = 0; i < 31; i++) {
            if ((1 << i) > value) {break;}
        }
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.floorLog2(float)");return i - 1;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.floorLog2(float)",throwable);throw throwable;}
    }

    public static void closeSilently(ParcelFileDescriptor fd) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.closeSilently(android.os.ParcelFileDescriptor)",fd);try{try {
            if (fd != null) {fd.close();}
        } catch (Throwable t) {
            Log.w(TAG, "fail to close", t);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.closeSilently(android.os.ParcelFileDescriptor)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.closeSilently(android.os.ParcelFileDescriptor)",throwable);throw throwable;}
    }

    public static void closeSilently(Cursor cursor) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.closeSilently(android.database.Cursor)",cursor);try{try {
            if (cursor != null) {cursor.close();}
        } catch (Throwable t) {
            Log.w(TAG, "fail to close", t);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.closeSilently(android.database.Cursor)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.closeSilently(android.database.Cursor)",throwable);throw throwable;}
    }

    public static float interpolateAngle(
            float source, float target, float progress) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.common.Utils.interpolateAngle(float,float,float)",source,target,progress);try{/*// interpolate the angle from source to target*/
        /*// We make the difference in the range of [-179, 180], this is the*/
        /*// shortest path to change source to target.*/
        float diff = target - source;
        if (diff < 0) {diff += 360f;}
        if (diff > 180) {diff -= 360f;}

        float result = source + diff * progress;
        {com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.interpolateAngle(float,float,float)");return result < 0 ? result + 360f : result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.common.Utils.interpolateAngle(float,float,float)",throwable);throw throwable;}
    }

    public static float interpolateScale(
            float source, float target, float progress) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.common.Utils.interpolateScale(float,float,float)",source,target,progress);try{com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.interpolateScale(float,float,float)");return source + progress * (target - source);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.common.Utils.interpolateScale(float,float,float)",throwable);throw throwable;}
    }

    public static String ensureNotNull(String value) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.common.Utils.ensureNotNull(java.lang.String)",value);try{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.common.Utils.ensureNotNull(java.lang.String)");return value == null ? "" : value;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.common.Utils.ensureNotNull(java.lang.String)",throwable);throw throwable;}
    }

    /*// Used for debugging. Should be removed before submitting.*/
    public static void debug(String format, Object ... args) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.debug(java.lang.String,[Object )",format,args);try{if (args.length == 0) {
            Log.d(DEBUG_TAG, format);
        } else {
            Log.d(DEBUG_TAG, String.format(format, args));
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.debug(java.lang.String,[Object )");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.debug(java.lang.String,[Object )",throwable);throw throwable;}
    }

    public static float parseFloatSafely(String content, float defaultValue) {
        com.mijack.Xlog.logStaticMethodEnter("float com.android.gallery3d.common.Utils.parseFloatSafely(java.lang.String,float)",content,defaultValue);try{if (content == null) {{com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.parseFloatSafely(java.lang.String,float)");return defaultValue;}}
        try {
            {com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.parseFloatSafely(java.lang.String,float)");return Float.parseFloat(content);}
        } catch (NumberFormatException e) {
            {com.mijack.Xlog.logStaticMethodExit("float com.android.gallery3d.common.Utils.parseFloatSafely(java.lang.String,float)");return defaultValue;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("float com.android.gallery3d.common.Utils.parseFloatSafely(java.lang.String,float)",throwable);throw throwable;}
    }

    public static int parseIntSafely(String content, int defaultValue) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.common.Utils.parseIntSafely(java.lang.String,int)",content,defaultValue);try{if (content == null) {{com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.parseIntSafely(java.lang.String,int)");return defaultValue;}}
        try {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.parseIntSafely(java.lang.String,int)");return Integer.parseInt(content);}
        } catch (NumberFormatException e) {
            {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.common.Utils.parseIntSafely(java.lang.String,int)");return defaultValue;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.common.Utils.parseIntSafely(java.lang.String,int)",throwable);throw throwable;}
    }

    public static boolean isNullOrEmpty(String exifMake) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.isNullOrEmpty(java.lang.String)",exifMake);try{com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.isNullOrEmpty(java.lang.String)");return TextUtils.isEmpty(exifMake);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.isNullOrEmpty(java.lang.String)",throwable);throw throwable;}
    }

    public static boolean hasSpaceForSize(long size) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.hasSpaceForSize(long)",size);try{String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.hasSpaceForSize(long)");return false;}
        }

        String path = Environment.getExternalStorageDirectory().getPath();
        try {
            StatFs stat = new StatFs(path);
            long availableSize;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {availableSize = deprecatedAvailableSize(stat);}
            else
                {availableSize = availableSize(stat);}
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.hasSpaceForSize(long)");return availableSize > size;}
        } catch (Exception e) {
            Log.i(TAG, "Fail to access external storage", e);
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.hasSpaceForSize(long)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.hasSpaceForSize(long)",throwable);throw throwable;}
    }

    @SuppressWarnings("deprecation")
    protected static long deprecatedAvailableSize(StatFs stat) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.common.Utils.deprecatedAvailableSize(android.os.StatFs)",stat);try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.deprecatedAvailableSize(android.os.StatFs)");return stat.getAvailableBlocks() * (long) stat.getBlockSize();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.common.Utils.deprecatedAvailableSize(android.os.StatFs)",throwable);throw throwable;}
    }

    protected static long availableSize(StatFs stat) {
        com.mijack.Xlog.logStaticMethodEnter("long com.android.gallery3d.common.Utils.availableSize(android.os.StatFs)",stat);try{com.mijack.Xlog.logStaticMethodExit("long com.android.gallery3d.common.Utils.availableSize(android.os.StatFs)");return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("long com.android.gallery3d.common.Utils.availableSize(android.os.StatFs)",throwable);throw throwable;}
    }

    public static void waitWithoutInterrupt(Object object) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.waitWithoutInterrupt(java.lang.Object)",object);try{try {
            object.wait();
        } catch (InterruptedException e) {
            Log.w(TAG, "unexpected interrupt: " + object);
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.waitWithoutInterrupt(java.lang.Object)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.waitWithoutInterrupt(java.lang.Object)",throwable);throw throwable;}
    }

    public static void shuffle(int array[], Random random) {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.common.Utils.shuffle(int,java.util.Random)",array[],random);try{for (int i = array.length; i > 0; --i) {
            int t = random.nextInt(i);
            if (t == i - 1) {continue;}
            int tmp = array[i - 1];
            array[i - 1] = array[t];
            array[t] = tmp;
        }com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.common.Utils.shuffle(int,java.util.Random)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.common.Utils.shuffle(int,java.util.Random)",throwable);throw throwable;}
    }

    public static boolean handleInterrruptedException(Throwable e) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.android.gallery3d.common.Utils.handleInterrruptedException(java.lang.Throwable)",e);try{/*// A helper to deal with the interrupt exception*/
        /*// If an interrupt detected, we will setup the bit again.*/
        if (e instanceof InterruptedIOException
                || e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.handleInterrruptedException(java.lang.Throwable)");return true;}
        }
        {com.mijack.Xlog.logStaticMethodExit("boolean com.android.gallery3d.common.Utils.handleInterrruptedException(java.lang.Throwable)");return false;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.android.gallery3d.common.Utils.handleInterrruptedException(java.lang.Throwable)",throwable);throw throwable;}
    }

    /**
     * @return String with special XML characters escaped.
     */
    public static String escapeXml(String s) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.common.Utils.escapeXml(java.lang.String)",s);try{StringBuilder sb = new StringBuilder();
        for (int i = 0, len = s.length(); i < len; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '\"': sb.append("&quot;"); break;
                case '\'': sb.append("&#039;"); break;
                case '&':  sb.append("&amp;"); break;
                default: sb.append(c);
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.common.Utils.escapeXml(java.lang.String)");return sb.toString();}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.common.Utils.escapeXml(java.lang.String)",throwable);throw throwable;}
    }

    public static String getUserAgent(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.common.Utils.getUserAgent(android.content.Context)",context);try{PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            throw new IllegalStateException("getPackageInfo failed");
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.common.Utils.getUserAgent(android.content.Context)");return String.format("%s/%s; %s/%s/%s/%s; %d/%s/%s",
                packageInfo.packageName,
                packageInfo.versionName,
                Build.BRAND,
                Build.DEVICE,
                Build.MODEL,
                Build.ID,
                Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE,
                Build.VERSION.INCREMENTAL);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.common.Utils.getUserAgent(android.content.Context)",throwable);throw throwable;}
    }

    public static String[] copyOf(String[] source, int newSize) {
        com.mijack.Xlog.logStaticMethodEnter("[java.lang.String com.android.gallery3d.common.Utils.copyOf([java.lang.String,int)",source,newSize);try{String[] result = new String[newSize];
        newSize = Math.min(source.length, newSize);
        System.arraycopy(source, 0, result, 0, newSize);
        {com.mijack.Xlog.logStaticMethodExit("[java.lang.String com.android.gallery3d.common.Utils.copyOf([java.lang.String,int)");return result;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[java.lang.String com.android.gallery3d.common.Utils.copyOf([java.lang.String,int)",throwable);throw throwable;}
    }

    public static PendingIntent deserializePendingIntent(byte[] rawPendingIntent) {
        com.mijack.Xlog.logStaticMethodEnter("android.app.PendingIntent com.android.gallery3d.common.Utils.deserializePendingIntent([byte)",rawPendingIntent);try{Parcel parcel = null;
        try {
            if (rawPendingIntent != null) {
                parcel = Parcel.obtain();
                parcel.unmarshall(rawPendingIntent, 0, rawPendingIntent.length);
                {com.mijack.Xlog.logStaticMethodExit("android.app.PendingIntent com.android.gallery3d.common.Utils.deserializePendingIntent([byte)");return PendingIntent.readPendingIntentOrNullFromParcel(parcel);}
            } else {
                {com.mijack.Xlog.logStaticMethodExit("android.app.PendingIntent com.android.gallery3d.common.Utils.deserializePendingIntent([byte)");return null;}
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("error parsing PendingIntent");
        } finally {
            if (parcel != null) {parcel.recycle();}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.app.PendingIntent com.android.gallery3d.common.Utils.deserializePendingIntent([byte)",throwable);throw throwable;}
    }

    public static byte[] serializePendingIntent(PendingIntent pendingIntent) {
        com.mijack.Xlog.logStaticMethodEnter("[byte com.android.gallery3d.common.Utils.serializePendingIntent(android.app.PendingIntent)",pendingIntent);try{Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            PendingIntent.writePendingIntentOrNullToParcel(pendingIntent, parcel);
            {com.mijack.Xlog.logStaticMethodExit("[byte com.android.gallery3d.common.Utils.serializePendingIntent(android.app.PendingIntent)");return parcel.marshall();}
        } finally {
            if (parcel != null) {parcel.recycle();}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("[byte com.android.gallery3d.common.Utils.serializePendingIntent(android.app.PendingIntent)",throwable);throw throwable;}
    }

    /*// Mask information for debugging only. It returns <code>info.toString()</code> directly*/
    /*// for debugging build (i.e., 'eng' and 'userdebug') and returns a mask ("****")*/
    /*// in release build to protect the information (e.g. for privacy issue).*/
    public static String maskDebugInfo(Object info) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.common.Utils.maskDebugInfo(java.lang.Object)",info);try{if (info == null) {{com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.common.Utils.maskDebugInfo(java.lang.Object)");return null;}}
        String s = info.toString();
        int length = Math.min(s.length(), MASK_STRING.length());
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.common.Utils.maskDebugInfo(java.lang.Object)");return IS_DEBUG_BUILD ? s : MASK_STRING.substring(0, length);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.common.Utils.maskDebugInfo(java.lang.Object)",throwable);throw throwable;}
    }
}
