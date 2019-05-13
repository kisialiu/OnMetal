package com.onmetal.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int bottom;
    private int left;
    private int right;
    private int top;

    public GridItemOffsetDecoration(int left, int top, int right, int bottom) {
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if(position % 2 != 0) {
            outRect.set(20, top, 10, bottom);
        } else {
            outRect.set(20, top, right, bottom);
        }
    }

}
