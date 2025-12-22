package com.hjq.core.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/25
 *    desc   : 图片选择列表分割线
 */
public final class GridSpaceDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;

    public GridSpaceDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        // default implementation ignored
    }

    @SuppressWarnings("all")
    @Override
    public void getItemOffsets(@NonNull Rect rect, @NonNull View view, RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        int position = recyclerView.getChildAdapterPosition(view);
        int spanCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();

        if (position < spanCount) {
            // 只有第一行才留出顶部间隙
            rect.top = mSpace;
        }

        if ((position + 1) % spanCount == 1) {
            // 每一行的第一个
            rect.left = mSpace;
            rect.right = mSpace / (spanCount + 1);
        } else if ((position + 1) % spanCount == 0) {
            // 每一行的最后一个
            rect.left = mSpace / (spanCount + 1);
            rect.right = mSpace;
        } else {
            rect.left = Math.round(mSpace * ((spanCount - 1f) / spanCount));
            rect.right = rect.left;
        }

        rect.bottom = mSpace;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        // default implementation ignored
    }
}