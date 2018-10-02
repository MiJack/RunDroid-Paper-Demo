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

import android.graphics.drawable.Drawable;
import android.os.Build;
import com.chanapps.four.gallery3d.R;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import java.util.ArrayList;

public class CustomMenu implements OnMenuItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "FilterMenu";

    public static class DropDownMenu {
        private Button mButton;
        private PopupMenu mPopupMenu;
        private Menu mMenu;

        public DropDownMenu(Context context, Button button, int menuId,
                OnMenuItemClickListener listener) {
            mButton = button;
            Drawable d = context.getResources().getDrawable(R.drawable.dropdown_normal_holo_dark);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {deprecatedSetBackground(mButton, d);}
            else
                {mButton.setBackground(d);}
            mPopupMenu = new PopupMenu(context, mButton);
            mMenu = mPopupMenu.getMenu();
            mPopupMenu.getMenuInflater().inflate(menuId, mMenu);
            mPopupMenu.setOnMenuItemClickListener(listener);
            mButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu$DropDownMenu$1.onClick(android.view.View)",this,v);try{mPopupMenu.show();com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu$DropDownMenu$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu$DropDownMenu$1.onClick(android.view.View)",this,throwable);throw throwable;}
                }
            });
        }

        @SuppressWarnings("deprecation")
        protected void deprecatedSetBackground(Button b, Drawable d) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.deprecatedSetBackground(android.widget.Button,android.graphics.drawable.Drawable)",this,b,d);try{b.setBackgroundDrawable(d);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.deprecatedSetBackground(android.widget.Button,android.graphics.drawable.Drawable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.deprecatedSetBackground(android.widget.Button,android.graphics.drawable.Drawable)",this,throwable);throw throwable;}
        }

        public MenuItem findItem(int id) {
            com.mijack.Xlog.logMethodEnter("android.view.MenuItem com.android.gallery3d.ui.CustomMenu$DropDownMenu.findItem(int)",this,id);try{com.mijack.Xlog.logMethodExit("android.view.MenuItem com.android.gallery3d.ui.CustomMenu$DropDownMenu.findItem(int)",this);return mMenu.findItem(id);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.MenuItem com.android.gallery3d.ui.CustomMenu$DropDownMenu.findItem(int)",this,throwable);throw throwable;}
        }

        public void setTitle(CharSequence title) {
            com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.setTitle(java.lang.CharSequence)",this,title);try{mButton.setText(title);com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.setTitle(java.lang.CharSequence)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu$DropDownMenu.setTitle(java.lang.CharSequence)",this,throwable);throw throwable;}
        }
    }



    private Context mContext;
    private ArrayList<DropDownMenu> mMenus;
    private OnMenuItemClickListener mListener;

    public CustomMenu(Context context) {
        mContext = context;
        mMenus = new ArrayList<DropDownMenu>();
    }

    public DropDownMenu addDropDownMenu(Button button, int menuId) {
        com.mijack.Xlog.logMethodEnter("com.android.gallery3d.ui.CustomMenu$DropDownMenu com.android.gallery3d.ui.CustomMenu.addDropDownMenu(android.widget.Button,int)",this,button,menuId);try{DropDownMenu menu = new DropDownMenu(mContext, button, menuId, this);
        mMenus.add(menu);
        {com.mijack.Xlog.logMethodExit("com.android.gallery3d.ui.CustomMenu$DropDownMenu com.android.gallery3d.ui.CustomMenu.addDropDownMenu(android.widget.Button,int)",this);return menu;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.android.gallery3d.ui.CustomMenu$DropDownMenu com.android.gallery3d.ui.CustomMenu.addDropDownMenu(android.widget.Button,int)",this,throwable);throw throwable;}
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu.setOnMenuItemClickListener(android.widget.PopupMenu.OnMenuItemClickListener)",this,listener);try{mListener = listener;com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu.setOnMenuItemClickListener(android.widget.PopupMenu.OnMenuItemClickListener)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu.setOnMenuItemClickListener(android.widget.PopupMenu.OnMenuItemClickListener)",this,throwable);throw throwable;}
    }

    public MenuItem findMenuItem(int id) {
        com.mijack.Xlog.logMethodEnter("android.view.MenuItem com.android.gallery3d.ui.CustomMenu.findMenuItem(int)",this,id);try{MenuItem item = null;
        for (DropDownMenu menu : mMenus) {
            item = menu.findItem(id);
            if (item != null) {{com.mijack.Xlog.logMethodExit("android.view.MenuItem com.android.gallery3d.ui.CustomMenu.findMenuItem(int)",this);return item;}}
        }
        {com.mijack.Xlog.logMethodExit("android.view.MenuItem com.android.gallery3d.ui.CustomMenu.findMenuItem(int)",this);return item;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.MenuItem com.android.gallery3d.ui.CustomMenu.findMenuItem(int)",this,throwable);throw throwable;}
    }

    public void setMenuItemAppliedEnabled(int id, boolean applied, boolean enabled,
            boolean updateTitle) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu.setMenuItemAppliedEnabled(int,boolean,boolean,boolean)",this,id,applied,enabled,updateTitle);try{MenuItem item = null;
        for (DropDownMenu menu : mMenus) {
            item = menu.findItem(id);
            if (item != null) {
                item.setCheckable(true);
                item.setChecked(applied);
                item.setEnabled(enabled);
                if (updateTitle) {
                    menu.setTitle(item.getTitle());
                }
            }
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu.setMenuItemAppliedEnabled(int,boolean,boolean,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu.setMenuItemAppliedEnabled(int,boolean,boolean,boolean)",this,throwable);throw throwable;}
    }

    public void setMenuItemVisibility(int id, boolean visibility) {
        com.mijack.Xlog.logMethodEnter("void com.android.gallery3d.ui.CustomMenu.setMenuItemVisibility(int,boolean)",this,id,visibility);try{MenuItem item = findMenuItem(id);
        if (item != null) {
            item.setVisible(visibility);
        }com.mijack.Xlog.logMethodExit("void com.android.gallery3d.ui.CustomMenu.setMenuItemVisibility(int,boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.android.gallery3d.ui.CustomMenu.setMenuItemVisibility(int,boolean)",this,throwable);throw throwable;}
    }

    public boolean onMenuItemClick(MenuItem item) {
        com.mijack.Xlog.logMethodEnter("boolean com.android.gallery3d.ui.CustomMenu.onMenuItemClick(android.view.MenuItem)",this,item);try{if (mListener != null) {
            {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.CustomMenu.onMenuItemClick(android.view.MenuItem)",this);return mListener.onMenuItemClick(item);}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.android.gallery3d.ui.CustomMenu.onMenuItemClick(android.view.MenuItem)",this);return false;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.android.gallery3d.ui.CustomMenu.onMenuItemClick(android.view.MenuItem)",this,throwable);throw throwable;}
    }
}
