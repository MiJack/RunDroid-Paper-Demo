package com.haringeymobile.ukweather.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A decoration that adds vertical margins to the items view (these margins act as a recycler
 * view list divider).
 */
public class ItemDecorationListDivider extends RecyclerView.ItemDecoration {
    private int divider;

    public ItemDecorationListDivider(int divider) {
        this.divider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.utils.ItemDecorationListDivider.getItemOffsets(android.graphics.Rect,android.support.v7.widget.RecyclerView,android.support.v7.widget.RecyclerView,RecyclerView.State)",this,outRect,view,parent,state);try{outRect.bottom = divider;
        if (parent.getChildLayoutPosition(view) == 0)
            {outRect.top = divider;}com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.utils.ItemDecorationListDivider.getItemOffsets(android.graphics.Rect,android.support.v7.widget.RecyclerView,android.support.v7.widget.RecyclerView,RecyclerView.State)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.utils.ItemDecorationListDivider.getItemOffsets(android.graphics.Rect,android.support.v7.widget.RecyclerView,android.support.v7.widget.RecyclerView,RecyclerView.State)",this,throwable);throw throwable;}
    }

}
