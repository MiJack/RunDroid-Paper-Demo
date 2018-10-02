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

package com.android.gallery3d.gadget;

import com.chanapps.four.gallery3d.R;
import com.chanapps.four.widget.WidgetDatabaseHelper;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @SuppressWarnings("unused")
    private static final String TAG = "GalleryAppWidgetService";

    public static final String EXTRA_WIDGET_TYPE = "widget-type";
    public static final String EXTRA_ALBUM_PATH = "album-path";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        com.mijack.Xlog.logMethodEnter("RemoteViewsFactory com.android.gallery3d.gadget.WidgetService.onGetViewFactory(android.content.Intent)",this,intent);try{int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        int type = intent.getIntExtra(EXTRA_WIDGET_TYPE, 0);
        String albumPath = intent.getStringExtra(EXTRA_ALBUM_PATH);

        {com.mijack.Xlog.logMethodExit("RemoteViewsFactory com.android.gallery3d.gadget.WidgetService.onGetViewFactory(android.content.Intent)",this);return new PhotoRVFactory((GalleryApp) getApplicationContext(), id, type, albumPath);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("RemoteViewsFactory com.android.gallery3d.gadget.WidgetService.onGetViewFactory(android.content.Intent)",this,throwable);throw throwable;}
    }

    private static class EmptySource implements WidgetSource {

        @Override
        public int size() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.WidgetService$EmptySource.size()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.WidgetService$EmptySource.size()",this);return 0;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.WidgetService$EmptySource.size()",this,throwable);throw throwable;}
        }

        @Override
        public Bitmap getImage(int index) {
            com.mijack.Xlog.logMethodEnter("android.graphics.Bitmap com.android.gallery3d.gadget.WidgetService$EmptySource.getImage(int)",this,index);try{com.mijack.Xlog.logMethodExit("android.graphics.Bitmap com.android.gallery3d.gadget.WidgetService$EmptySource.getImage(int)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.graphics.Bitmap com.android.gallery3d.gadget.WidgetService$EmptySource.getImage(int)",this,throwable);throw throwable;}
        }

        @Override
        public Uri getContentUri(int index) {
            com.mijack.Xlog.logMethodEnter("android.net.Uri com.android.gallery3d.gadget.WidgetService$EmptySource.getContentUri(int)",this,index);try{com.mijack.Xlog.logMethodExit("android.net.Uri com.android.gallery3d.gadget.WidgetService$EmptySource.getContentUri(int)",this);throw new UnsupportedOperationException();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.net.Uri com.android.gallery3d.gadget.WidgetService$EmptySource.getContentUri(int)",this,throwable);throw throwable;}
        }

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$EmptySource.setContentListener(com.android.gallery3d.data.ContentListener)",this,listener);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$EmptySource.setContentListener(com.android.gallery3d.data.ContentListener)",this);}

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$EmptySource.reload()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$EmptySource.reload()",this);}

        {com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$EmptySource.close()",this);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$EmptySource.close()",this);}
    }

    private static class PhotoRVFactory implements
            RemoteViewsService.RemoteViewsFactory, ContentListener {

        private final int mAppWidgetId;
        private final int mType;
        private final String mAlbumPath;
        private final GalleryApp mApp;

        private WidgetSource mSource;

        public PhotoRVFactory(GalleryApp app, int id, int type, String albumPath) {
            mApp = app;
            mAppWidgetId = id;
            mType = type;
            mAlbumPath = albumPath;
        }

        @Override
        public void onCreate() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onCreate()",this);try{if (mType == WidgetDatabaseHelper.TYPE_ALBUM) {
                Path path = Path.fromString(mAlbumPath);
                DataManager manager = mApp.getDataManager();
                MediaSet mediaSet = (MediaSet) manager.getMediaObject(path);
                mSource = mediaSet == null
                        ? new EmptySource()
                        : new MediaSetSource(mediaSet);
            } else {
                mSource = new LocalPhotoSource(mApp.getAndroidContext());
            }
            mSource.setContentListener(this);
            AppWidgetManager.getInstance(mApp.getAndroidContext())
                    .notifyAppWidgetViewDataChanged(
                            mAppWidgetId, R.id.appwidget_stack_view);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onCreate()",this,throwable);throw throwable;}
        }

        @Override
        public void onDestroy() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDestroy()",this);try{mSource.close();
            mSource = null;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDestroy()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDestroy()",this,throwable);throw throwable;}
        }

        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getCount()",this);return mSource.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getCount()",this,throwable);throw throwable;}
        }

        public long getItemId(int position) {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getItemId(int)",this);return position;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getItemId(int)",this,throwable);throw throwable;}
        }

        public int getViewTypeCount() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewTypeCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewTypeCount()",this);return 1;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewTypeCount()",this,throwable);throw throwable;}
        }

        public boolean hasStableIds() {
            com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.hasStableIds()",this);try{com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.hasStableIds()",this);return true;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.hasStableIds()",this,throwable);throw throwable;}
        }

        public RemoteViews getLoadingView() {
            com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getLoadingView()",this);try{RemoteViews rv = new RemoteViews(
                    mApp.getAndroidContext().getPackageName(),
                    R.layout.appwidget_loading_item);
            rv.setProgressBar(R.id.appwidget_loading_item, 0, 0, true);
            {com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getLoadingView()",this);return rv;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getLoadingView()",this,throwable);throw throwable;}
        }

        public RemoteViews getViewAt(int position) {
            com.mijack.Xlog.logMethodEnter("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewAt(int)",this,position);try{Bitmap bitmap = mSource.getImage(position);
            if (bitmap == null) {{com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewAt(int)",this);return getLoadingView();}}
            RemoteViews views = new RemoteViews(
                    mApp.getAndroidContext().getPackageName(),
                    R.layout.appwidget_photo_item);
            views.setImageViewBitmap(R.id.appwidget_photo_item, bitmap);
            views.setOnClickFillInIntent(R.id.appwidget_photo_item, new Intent()
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setData(mSource.getContentUri(position)));
            {com.mijack.Xlog.logMethodExit("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewAt(int)",this);return views;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.widget.RemoteViews com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.getViewAt(int)",this,throwable);throw throwable;}
        }

        @Override
        public void onDataSetChanged() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDataSetChanged()",this);try{mSource.reload();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDataSetChanged()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onDataSetChanged()",this,throwable);throw throwable;}
        }

        @Override
        public void onContentDirty() {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onContentDirty()",this);try{AppWidgetManager.getInstance(mApp.getAndroidContext())
                    .notifyAppWidgetViewDataChanged(
                            mAppWidgetId, R.id.appwidget_stack_view);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onContentDirty()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.gadget.WidgetService$PhotoRVFactory.onContentDirty()",this,throwable);throw throwable;}
        }
    }
}
