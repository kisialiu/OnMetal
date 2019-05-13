package com.onmetal.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int bottom = 0;
    private int left = 0;
    private int right = 0;
    private int top = 0;

    public ItemOffsetDecoration(int bottom) {
        this.bottom = bottom;
    }

    public ItemOffsetDecoration(int left, int top, int right, int bottom) {
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(left, top, right, bottom);
    }

}
