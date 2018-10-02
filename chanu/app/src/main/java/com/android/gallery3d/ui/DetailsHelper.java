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
package com.android.gallery3d.ui;

import android.content.DialogInterface;
import com.chanapps.four.gallery3d.R;
import com.android.gallery3d.app.GalleryActivity;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener;

import android.content.Context;
import android.view.View.MeasureSpec;

public class DetailsHelper {
    private static DetailsAddressResolver sAddressResolver;
    private DetailsViewContainer mContainer;

    public interface DetailsSource {
        public int size();
        public int getIndex();
        public int findIndex(int indexHint);
        public MediaDetails getDetails();
    }

    public interface CloseListener {
        public void onClose();
    }

    public interface DetailsViewContainer {
        public void reloadDetails(int indexHint);
        public void setCloseListener(CloseListener listener);
        public void setClickListener(int stringId, DialogInterface.OnClickListener listener);
        public void show();
        public void hide();
    }

    public DetailsHelper(GalleryActivity activity, GLView rootPane, DetailsSource source) {
        mContainer = new DialogDetailsView(activity, source);
    }

    public void layout(int left, int top, int right, int bottom) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.layout(int,int,int,int)",this,left,top,right,bottom);try{if (mContainer instanceof GLView) {
            GLView view = (GLView) mContainer;
            view.measure(MeasureSpec.UNSPECIFIED,
                    MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.AT_MOST));
            view.layout(0, top, view.getMeasuredWidth(), top + view.getMeasuredHeight());
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.layout(int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.layout(int,int,int,int)",this,throwable);throw throwable;}
    }

    public void reloadDetails(int indexHint) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.reloadDetails(int)",this,indexHint);try{mContainer.reloadDetails(indexHint);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.reloadDetails(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.reloadDetails(int)",this,throwable);throw throwable;}
    }

    public void setClickListener(int stringId, DialogInterface.OnClickListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.setClickListener(int,DialogInterface.OnClickListener)",this,stringId,listener);try{mContainer.setClickListener(stringId, listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.setClickListener(int,DialogInterface.OnClickListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.setClickListener(int,DialogInterface.OnClickListener)",this,throwable);throw throwable;}
    }

    public void setCloseListener(CloseListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.setCloseListener(CloseListener)",this,listener);try{mContainer.setCloseListener(listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.setCloseListener(CloseListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.setCloseListener(CloseListener)",this,throwable);throw throwable;}
    }

    public static String resolveAddress(GalleryActivity activity, double[] latlng,
            AddressResolvingListener listener) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.ui.DetailsHelper.resolveAddress(com.android.gallery3d.app.GalleryActivity,[double,com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener)",activity,latlng,listener);try{if (sAddressResolver == null) {
            sAddressResolver = new DetailsAddressResolver(activity);
        } else {
            sAddressResolver.cancel();
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.resolveAddress(com.android.gallery3d.app.GalleryActivity,[double,com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener)");return sAddressResolver.resolveAddress(latlng, listener);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.DetailsHelper.resolveAddress(com.android.gallery3d.app.GalleryActivity,[double,com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener)",throwable);throw throwable;}
    }

    public static void pause() {
        com.mijack.Xlog.logStaticMethodEnter("void com.android.gallery3d.ui.DetailsHelper.pause()");try{if (sAddressResolver != null) {sAddressResolver.cancel();}com.mijack.Xlog.logStaticMethodExit("void com.android.gallery3d.ui.DetailsHelper.pause()");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.pause()",throwable);throw throwable;}
    }

    public void show() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.show()",this);try{mContainer.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.show()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.show()",this,throwable);throw throwable;}
    }

    public void hide() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.DetailsHelper.hide()",this);try{mContainer.hide();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.DetailsHelper.hide()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.DetailsHelper.hide()",this,throwable);throw throwable;}
    }

    public static String getDetailsName(Context context, int key) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)",context,key);try{switch (key) {
            case MediaDetails.INDEX_TITLE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.title);}
            case MediaDetails.INDEX_DESCRIPTION:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.description);}
            case MediaDetails.INDEX_DATETIME:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.time);}
            case MediaDetails.INDEX_LOCATION:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.location);}
            case MediaDetails.INDEX_PATH:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.path);}
            case MediaDetails.INDEX_WIDTH:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.width);}
            case MediaDetails.INDEX_HEIGHT:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.height);}
            case MediaDetails.INDEX_ORIENTATION:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.orientation);}
            case MediaDetails.INDEX_DURATION:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.duration);}
            case MediaDetails.INDEX_MIMETYPE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.mimetype);}
            case MediaDetails.INDEX_SIZE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.file_size);}
            case MediaDetails.INDEX_MAKE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.maker);}
            case MediaDetails.INDEX_MODEL:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.model);}
            case MediaDetails.INDEX_FLASH:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.flash);}
            case MediaDetails.INDEX_APERTURE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.aperture);}
            case MediaDetails.INDEX_FOCAL_LENGTH:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.focal_length);}
            case MediaDetails.INDEX_WHITE_BALANCE:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.white_balance);}
            case MediaDetails.INDEX_EXPOSURE_TIME:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.exposure_time);}
            case MediaDetails.INDEX_ISO:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return context.getString(R.string.iso);}
            default:
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)");return "Unknown key" + key;}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.ui.DetailsHelper.getDetailsName(android.content.Context,int)",throwable);throw throwable;}
    }
}


