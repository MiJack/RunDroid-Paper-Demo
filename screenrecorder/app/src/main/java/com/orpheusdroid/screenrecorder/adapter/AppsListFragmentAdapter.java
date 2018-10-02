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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orpheusdroid.screenrecorder.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vijai on 08-12-2017.
 */

public class AppsListFragmentAdapter extends RecyclerView.Adapter<AppsListFragmentAdapter.SimpleViewHolder> {
    private ArrayList<Apps> apps;
    private OnItemClicked onClick;

    public AppsListFragmentAdapter(ArrayList<Apps> apps) {
        this.apps = apps;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        com.mijack.Xlog.logMethodEnter("com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,parent,viewType);try{View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_apps_list_preference, parent, false);
        {com.mijack.Xlog.logMethodExit("com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this);return new SimpleViewHolder(view);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onBindViewHolder(com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder,int)",this,holder,position);try{Apps app = apps.get(holder.getAdapterPosition());
        holder.textView.setText("" + app.getAppName());
        holder.appIcon.setImageDrawable(app.getAppIcon());

        /*// Show a visible tick mark for the selected app*/
        if (app.isSelectedApp())
            {holder.selectedApp.setVisibility(View.VISIBLE);}
        else
            {holder.selectedApp.setVisibility(View.INVISIBLE);}
        holder.app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$1.onClick(android.view.View)",this,view);try{onClick.onItemClick(holder.getAdapterPosition());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onBindViewHolder(com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.onBindViewHolder(com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter$SimpleViewHolder,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.getItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.getItemCount()",this);return apps.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.getItemCount()",this,throwable);throw throwable;}
    }

    public void setOnClick(OnItemClicked onClick) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.setOnClick(OnItemClicked)",this,onClick);try{this.onClick = onClick;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.setOnClick(OnItemClicked)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter.setOnClick(OnItemClicked)",this,throwable);throw throwable;}
    }

    /*// Interface to handle recycler view item click*/
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    /*// A static view holder class to hold the view items used by the recycler view*/
    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView appIcon;
        ImageView selectedApp;
        RelativeLayout app;

        SimpleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            selectedApp = itemView.findViewById(R.id.appChecked);
            app = itemView.findViewById(R.id.app);
        }
    }
}
