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

package com.android.gallery3d.photoeditor;

import android.graphics.Bitmap;

/**
 * Photo that holds a GL texture and all its methods must be only accessed from the GL thread.
 */
public class Photo {

    private int texture;
    private int width;
    private int height;

    /**
     * Factory method to ensure every Photo instance holds a valid texture.
     */
    public static Photo create(Bitmap bitmap) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(android.graphics.Bitmap)",bitmap);try{com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(android.graphics.Bitmap)");return (bitmap != null) ? new Photo(
                RendererUtils.createTexture(bitmap), bitmap.getWidth(), bitmap.getHeight()) : null;}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(android.graphics.Bitmap)",throwable);throw throwable;}
    }

    public static Photo create(int width, int height) {
        com.mijack.Xlog.logStaticMethodEnter("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(int,int)",width,height);try{com.mijack.Xlog.logStaticMethodExit("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(int,int)");return new Photo(RendererUtils.createTexture(), width, height);}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.android.gallery3d.photoeditor.Photo com.android.gallery3d.photoeditor.Photo.create(int,int)",throwable);throw throwable;}
    }

    private Photo(int texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public int texture() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.Photo.texture()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.Photo.texture()",this);return texture;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.Photo.texture()",this,throwable);throw throwable;}
    }

    public boolean matchDimension(Photo photo) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.photoeditor.Photo.matchDimension(com.android.gallery3d.photoeditor.Photo)",this,photo);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.photoeditor.Photo.matchDimension(com.android.gallery3d.photoeditor.Photo)",this);return ((photo.width == width) && (photo.height == height));}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.photoeditor.Photo.matchDimension(com.android.gallery3d.photoeditor.Photo)",this,throwable);throw throwable;}
    }

    public void changeDimension(int width, int height) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Photo.changeDimension(int,int)",this,width,height);try{this.width = width;
        this.height = height;
        RendererUtils.clearTexture(texture);
        texture = RendererUtils.createTexture();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Photo.changeDimension(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Photo.changeDimension(int,int)",this,throwable);throw throwable;}
    }

    public int width() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.Photo.width()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.Photo.width()",this);return width;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.Photo.width()",this,throwable);throw throwable;}
    }

    public int height() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.photoeditor.Photo.height()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.photoeditor.Photo.height()",this);return height;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.photoeditor.Photo.height()",this,throwable);throw throwable;}
    }

    public Bitmap save() {
        com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.photoeditor.Photo.save()",this);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.photoeditor.Photo.save()",this);return RendererUtils.saveTexture(texture, width, height);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.photoeditor.Photo.save()",this,throwable);throw throwable;}
    }

    /**
     * Clears the texture; this instance should not be used after its clear() is called.
     */
    public void clear() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.photoeditor.Photo.clear()",this);try{RendererUtils.clearTexture(texture);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.photoeditor.Photo.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.photoeditor.Photo.clear()",this,throwable);throw throwable;}
    }
}
