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

package com.orpheusdroid.screenrecorder.folderpicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import java.io.File;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vijai on 01-12-2016.
 */

class DirectoryRecyclerAdapter extends RecyclerView.Adapter<DirectoryRecyclerAdapter.ItemViewHolder> {
    private static OnDirectoryClickedListerner onDirectoryClickedListerner;
    private Context context;
    private ArrayList<File> directories;

    DirectoryRecyclerAdapter(Context context, OnDirectoryClickedListerner listerner, ArrayList<File> directories){
        this.context = context;
        onDirectoryClickedListerner = listerner;
        this.directories = directories;
    }

    @Override
    public DirectoryRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        com.mijack.Xlog.logMethodEnter("DirectoryRecyclerAdapter.ItemViewHolder com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,parent,viewType);try{View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_directory_chooser, parent, false);
        {com.mijack.Xlog.logMethodExit("DirectoryRecyclerAdapter.ItemViewHolder com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this);return new ItemViewHolder(view);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("DirectoryRecyclerAdapter.ItemViewHolder com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,throwable);throw throwable;}
    }

    @Override
    public void onBindViewHolder(final DirectoryRecyclerAdapter.ItemViewHolder holder, int position) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onBindViewHolder(DirectoryRecyclerAdapter.ItemViewHolder,int)",this,holder,position);try{holder.dir.setText(directories.get(position).getName());
        holder.dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter$1.onClick(android.view.View)",this,view);try{Log.d(Const.TAG, "Item clicked: " + directories.get(holder.getAdapterPosition()));
                onDirectoryClickedListerner.OnDirectoryClicked(directories.get(holder.getAdapterPosition()));com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter$1.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter$1.onClick(android.view.View)",this,throwable);throw throwable;}
            }
        });com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onBindViewHolder(DirectoryRecyclerAdapter.ItemViewHolder,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.onBindViewHolder(DirectoryRecyclerAdapter.ItemViewHolder,int)",this,throwable);throw throwable;}
    }

    @Override
    public int getItemCount() {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.getItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.getItemCount()",this);return directories.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.folderpicker.DirectoryRecyclerAdapter.getItemCount()",this,throwable);throw throwable;}
    }

    interface OnDirectoryClickedListerner {
        void OnDirectoryClicked(File directory);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView dir;
        LinearLayout dir_view;
        public ItemViewHolder(View itemView) {
            super(itemView);
            dir = itemView.findViewById(R.id.directory);
            dir_view = itemView.findViewById(R.id.directory_view);
        }
    }
}
