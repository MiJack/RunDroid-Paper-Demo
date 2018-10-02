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

package com.orpheusdroid.screenrecorder.adapter;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Created by vijai on 08-12-2017.
 */

public class Apps implements Comparable<Apps> {
    private String appName;
    private String packageName;
    private Drawable appIcon;
    private boolean isSelectedApp;

    public Apps(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    String getAppName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getAppName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getAppName()",this);return appName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getAppName()",this,throwable);throw throwable;}
    }

    public void setAppName(String appName) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppName(java.lang.String)",this,appName);try{this.appName = appName;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppName(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppName(java.lang.String)",this,throwable);throw throwable;}
    }

    public String getPackageName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getPackageName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getPackageName()",this);return packageName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.adapter.Apps.getPackageName()",this,throwable);throw throwable;}
    }

    public void setPackageName(String packageName) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.Apps.setPackageName(java.lang.String)",this,packageName);try{this.packageName = packageName;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.Apps.setPackageName(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.Apps.setPackageName(java.lang.String)",this,throwable);throw throwable;}
    }

    Drawable getAppIcon() {
        com.mijack.Xlog.logMethodEnter("android.graphics.drawable.Drawable com.orpheusdroid.screenrecorder.adapter.Apps.getAppIcon()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.drawable.Drawable com.orpheusdroid.screenrecorder.adapter.Apps.getAppIcon()",this);return appIcon;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.drawable.Drawable com.orpheusdroid.screenrecorder.adapter.Apps.getAppIcon()",this,throwable);throw throwable;}
    }

    public void setAppIcon(Drawable appIcon) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppIcon(android.graphics.drawable.Drawable)",this,appIcon);try{this.appIcon = appIcon;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppIcon(android.graphics.drawable.Drawable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.Apps.setAppIcon(android.graphics.drawable.Drawable)",this,throwable);throw throwable;}
    }

    boolean isSelectedApp() {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.adapter.Apps.isSelectedApp()",this);try{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.adapter.Apps.isSelectedApp()",this);return isSelectedApp;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.adapter.Apps.isSelectedApp()",this,throwable);throw throwable;}
    }

    public void setSelectedApp(boolean selectedApp) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.Apps.setSelectedApp(boolean)",this,selectedApp);try{isSelectedApp = selectedApp;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.Apps.setSelectedApp(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.Apps.setSelectedApp(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public int compareTo(@NonNull Apps apps) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.adapter.Apps.compareTo(@NonNull Apps)",this,apps);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.adapter.Apps.compareTo(@NonNull Apps)",this);return appName.compareTo(apps.appName);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.adapter.Apps.compareTo(@NonNull Apps)",this,throwable);throw throwable;}
    }
}
