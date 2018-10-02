/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class GalleryActionBar implements ActionBar.OnNavigationListener {
    private static final String TAG = "GalleryActionBar";

    public interface ClusterRunner {
        public void doCluster(int id);
    }

    private static class ActionItem {
        public int action;
        public boolean enabled;
        public boolean visible;
        public int spinnerTitle;
        public int dialogTitle;
        public int clusterBy;

        public ActionItem(int action, boolean applied, boolean enabled, int title,
                int clusterBy) {
            this(action, applied, enabled, title, title, clusterBy);
        }

        public ActionItem(int action, boolean applied, boolean enabled, int spinnerTitle,
                int dialogTitle, int clusterBy) {
            this.action = action;
            this.enabled = enabled;
            this.spinnerTitle = spinnerTitle;
            this.dialogTitle = dialogTitle;
            this.clusterBy = clusterBy;
            this.visible = true;
        }
    }

    private static final ActionItem[] sClusterItems = new ActionItem[] {
        new ActionItem(FilterUtils.CLUSTER_BY_ALBUM, true, false, R.string.albums,
                R.string.group_by_album),
        new ActionItem(FilterUtils.CLUSTER_BY_LOCATION, true, false,
                R.string.locations, R.string.location, R.string.group_by_location),
        new ActionItem(FilterUtils.CLUSTER_BY_TIME, true, false, R.string.times,
                R.string.time, R.string.group_by_time),
        new ActionItem(FilterUtils.CLUSTER_BY_FACE, true, false, R.string.people,
                R.string.group_by_faces),
        new ActionItem(FilterUtils.CLUSTER_BY_TAG, true, false, R.string.tags,
                R.string.group_by_tags)
    };

    private class ClusterAdapter extends BaseAdapter {

        public int getCount() {
            com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getCount()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getCount()",this);return sClusterItems.length;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getCount()",this,throwable);throw throwable;}
        }

        public Object getItem(int position) {
            com.mijack.Xlog.logMethodEnter("java.lang.Object com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItem(int)",this,position);try{com.mijack.Xlog.logMethodExit("java.lang.Object com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItem(int)",this);return sClusterItems[position];}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Object com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItem(int)",this,throwable);throw throwable;}
        }

        public long getItemId(int position) {
            com.mijack.Xlog.logMethodEnter("long com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItemId(int)",this,position);try{com.mijack.Xlog.logMethodExit("long com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItemId(int)",this);return sClusterItems[position].action;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("long com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getItemId(int)",this,throwable);throw throwable;}
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            com.mijack.Xlog.logMethodEnter("android.view.View com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,convertView,parent);try{if (convertView == null) {
                convertView = mInflater.inflate(R.layout.action_bar_text,
                        parent, false);
            }
            TextView view = (TextView) convertView;
            view.setText(sClusterItems[position].spinnerTitle);
            {com.mijack.Xlog.logMethodExit("android.view.View com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return convertView;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.android.gallery3d.app.GalleryActionBar$ClusterAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
        }
    }

    private ClusterRunner mClusterRunner;
    /*//private CharSequence[] mTitles;*/
    /*//private ArrayList<Integer> mActions;*/
    private Context mContext;
    private LayoutInflater mInflater;
    private GalleryActivity mActivity;
    private ActionBar mActionBar;
    private int mCurrentIndex;
    private ClusterAdapter mAdapter = new ClusterAdapter();

    public GalleryActionBar(GalleryActivity activity) {
        mActionBar = ((Activity) activity).getActionBar();
        mContext = activity.getAndroidContext();
        mActivity = activity;
        mInflater = ((Activity) mActivity).getLayoutInflater();
        mCurrentIndex = 0;
    }

    public static int getHeight(Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("int com.android.gallery3d.app.GalleryActionBar.getHeight(android.app.Activity)",activity);try{ActionBar actionBar = activity.getActionBar();
        {com.mijack.Xlog.logStaticMethodExit("int com.android.gallery3d.app.GalleryActionBar.getHeight(android.app.Activity)");return actionBar != null ? actionBar.getHeight() : 0;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("int com.android.gallery3d.app.GalleryActionBar.getHeight(android.app.Activity)",throwable);throw throwable;}
    }

    /*
    private void createDialogData() {
        //ArrayList<CharSequence> titles = new ArrayList<CharSequence>();
        //mActions = new ArrayList<Integer>();
        //for (ActionItem item : sClusterItems) {
        //    if (item.enabled && item.visible) {
        //        titles.add(mContext.getString(item.dialogTitle));
        //        mActions.add(item.action);
        //    }
        //}
        //mTitles = new CharSequence[titles.size()];
        //titles.toArray(mTitles);
    }
    */
    public void setClusterItemEnabled(int id, boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setClusterItemEnabled(int,boolean)",this,id,enabled);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setClusterItemEnabled(int,boolean)",this);for (ActionItem item : sClusterItems) {
            if (item.action == id) {
                item.enabled = enabled;
                return;
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setClusterItemEnabled(int,boolean)",this,throwable);throw throwable;}
    }

    public void setClusterItemVisibility(int id, boolean visible) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setClusterItemVisibility(int,boolean)",this,id,visible);try{com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setClusterItemVisibility(int,boolean)",this);for (ActionItem item : sClusterItems) {
            if (item.action == id) {
                item.visible = visible;
                return;
            }
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setClusterItemVisibility(int,boolean)",this,throwable);throw throwable;}
    }

    public int getClusterTypeAction() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.GalleryActionBar.getClusterTypeAction()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.GalleryActionBar.getClusterTypeAction()",this);return sClusterItems[mCurrentIndex].action;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.GalleryActionBar.getClusterTypeAction()",this,throwable);throw throwable;}
    }

    public static String getClusterByTypeString(Context context, int type) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.android.gallery3d.app.GalleryActionBar.getClusterByTypeString(android.content.Context,int)",context,type);try{for (ActionItem item : sClusterItems) {
            if (item.action == type) {
                {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.app.GalleryActionBar.getClusterByTypeString(android.content.Context,int)");return context.getString(item.clusterBy);}
            }
        }
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.android.gallery3d.app.GalleryActionBar.getClusterByTypeString(android.content.Context,int)");return null;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.GalleryActionBar.getClusterByTypeString(android.content.Context,int)",throwable);throw throwable;}
    }

    public static ShareActionProvider initializeShareActionProvider(Menu menu) {
        com.mijack.Xlog.logStaticMethodEnter("android.widget.ShareActionProvider com.android.gallery3d.app.GalleryActionBar.initializeShareActionProvider(android.view.Menu)",menu);try{MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = null;
        if (item != null)
            {shareActionProvider = (ShareActionProvider) item.getActionProvider();}
        {com.mijack.Xlog.logStaticMethodExit("android.widget.ShareActionProvider com.android.gallery3d.app.GalleryActionBar.initializeShareActionProvider(android.view.Menu)");return shareActionProvider;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("android.widget.ShareActionProvider com.android.gallery3d.app.GalleryActionBar.initializeShareActionProvider(android.view.Menu)",throwable);throw throwable;}
    }

    public void hideClusterMenu() {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.hideClusterMenu()",this);try{/*//mClusterRunner = null;*/
        /*//mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);*/com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.hideClusterMenu()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.hideClusterMenu()",this,throwable);throw throwable;}
    }

    public void setTitle(String title) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setTitle(java.lang.String)",this,title);try{if (mActionBar != null) {mActionBar.setTitle(title);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setTitle(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setTitle(java.lang.String)",this,throwable);throw throwable;}
    }
    
    public String getTitle() {
    	com.mijack.Xlog.logMethodEnter("java.lang.String com.android.gallery3d.app.GalleryActionBar.getTitle()",this);try{if (mActionBar != null && mActionBar.getTitle() != null) {{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.GalleryActionBar.getTitle()",this);return mActionBar.getTitle().toString();}}
    	{com.mijack.Xlog.logMethodExit("java.lang.String com.android.gallery3d.app.GalleryActionBar.getTitle()",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.android.gallery3d.app.GalleryActionBar.getTitle()",this,throwable);throw throwable;}
    }

    public void setTitle(int titleId) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setTitle(int)",this,titleId);try{if (mActionBar != null) {mActionBar.setTitle(titleId);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setTitle(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setTitle(int)",this,throwable);throw throwable;}
    }

    public void setSubtitle(String title) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setSubtitle(java.lang.String)",this,title);try{if (mActionBar != null) {mActionBar.setSubtitle(title);}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setSubtitle(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setSubtitle(java.lang.String)",this,throwable);throw throwable;}
    }

    public int getHeight() {
        com.mijack.Xlog.logMethodEnter("int com.android.gallery3d.app.GalleryActionBar.getHeight()",this);try{com.mijack.Xlog.logMethodExit("int com.android.gallery3d.app.GalleryActionBar.getHeight()",this);return mActionBar == null ? 0 : mActionBar.getHeight();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.android.gallery3d.app.GalleryActionBar.getHeight()",this,throwable);throw throwable;}
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.app.GalleryActionBar.onNavigationItemSelected(int,long)",this,itemPosition,itemId);try{if (itemPosition != mCurrentIndex && mClusterRunner != null) {
            mActivity.getGLRoot().lockRenderThread();
            try {
                mClusterRunner.doCluster(sClusterItems[itemPosition].action);
            } finally {
                mActivity.getGLRoot().unlockRenderThread();
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.app.GalleryActionBar.onNavigationItemSelected(int,long)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.app.GalleryActionBar.onNavigationItemSelected(int,long)",this,throwable);throw throwable;}
    }
    
    public void setDisplayHomeAsUpEnabled(boolean enabled) {
    	com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setDisplayHomeAsUpEnabled(boolean)",this,enabled);try{if (mActionBar != null) {
    		mActionBar.setDisplayHomeAsUpEnabled(enabled);
    	}com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setDisplayHomeAsUpEnabled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setDisplayHomeAsUpEnabled(boolean)",this,throwable);throw throwable;}
    }

    public void setDisplayShowHomeEnabled(boolean enabled) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.app.GalleryActionBar.setDisplayShowHomeEnabled(boolean)",this,enabled);try{if (mActionBar != null) {
            mActionBar.setDisplayShowHomeEnabled(enabled);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.app.GalleryActionBar.setDisplayShowHomeEnabled(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.app.GalleryActionBar.setDisplayShowHomeEnabled(boolean)",this,throwable);throw throwable;}
    }
}
