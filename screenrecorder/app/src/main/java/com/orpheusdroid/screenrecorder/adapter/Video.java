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

package com.orpheusdroid.screenrecorder.adapter;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Date;

/**
 * Created by vijai on 07-11-2016.
 */

public class Video implements Comparable<Video> {
    private String FileName;
    private File file;
    private Bitmap thumbnail;
    private Date lastModified;
    private boolean isSection = false;
    private boolean isSelected = false;

    public Video(boolean isSection, Date lastModified) {
        this.isSection = isSection;
        this.lastModified = lastModified;
    }

    public Video(String fileName, File file, Bitmap thumbnail, Date lastModified) {
        FileName = fileName;
        this.file = file;
        this.thumbnail = thumbnail;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.adapter.Video.getFileName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.adapter.Video.getFileName()",this);return FileName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.adapter.Video.getFileName()",this,throwable);throw throwable;}
    }

    public File getFile() {
        com.mijack.Xlog.logMethodEnter("java.io.File com.orpheusdroid.screenrecorder.adapter.Video.getFile()",this);try{com.mijack.Xlog.logMethodExit("java.io.File com.orpheusdroid.screenrecorder.adapter.Video.getFile()",this);return file;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.io.File com.orpheusdroid.screenrecorder.adapter.Video.getFile()",this,throwable);throw throwable;}
    }

    public Bitmap getThumbnail() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.orpheusdroid.screenrecorder.adapter.Video.getThumbnail()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.orpheusdroid.screenrecorder.adapter.Video.getThumbnail()",this);return thumbnail;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.orpheusdroid.screenrecorder.adapter.Video.getThumbnail()",this,throwable);throw throwable;}
    }

    public Date getLastModified() {
        com.mijack.Xlog.logMethodEnter("java.util.Date com.orpheusdroid.screenrecorder.adapter.Video.getLastModified()",this);try{com.mijack.Xlog.logMethodExit("java.util.Date com.orpheusdroid.screenrecorder.adapter.Video.getLastModified()",this);return lastModified;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Date com.orpheusdroid.screenrecorder.adapter.Video.getLastModified()",this,throwable);throw throwable;}
    }

    public boolean isSection() {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSection()",this);try{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSection()",this);return isSection;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSection()",this,throwable);throw throwable;}
    }

    public boolean isSelected() {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSelected()",this);try{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSelected()",this);return isSelected;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.adapter.Video.isSelected()",this,throwable);throw throwable;}
    }

    public void setSelected(boolean selected) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.Video.setSelected(boolean)",this,selected);try{isSelected = selected;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.Video.setSelected(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.Video.setSelected(boolean)",this,throwable);throw throwable;}
    }

    @Override
    public int compareTo(Video video) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.adapter.Video.compareTo(com.orpheusdroid.screenrecorder.adapter.Video)",this,video);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.adapter.Video.compareTo(com.orpheusdroid.screenrecorder.adapter.Video)",this);return getLastModified().compareTo(video.getLastModified());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.adapter.Video.compareTo(com.orpheusdroid.screenrecorder.adapter.Video)",this,throwable);throw throwable;}
    }
}
