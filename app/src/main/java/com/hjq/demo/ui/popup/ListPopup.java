package com.hjq.demo.ui.popup;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;
import com.hjq.demo.R;
import com.hjq.demo.common.MyAdapter;
import com.hjq.demo.other.ArrowDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/10/18
 *    desc   : 列表弹窗
 */
public final class ListPopup {

    public static final class Builder
            extends BasePopupWindow.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private final MenuAdapter mAdapter;

        public Builder(Context context) {
            super(context);

            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setContentView(recyclerView);

            mAdapter = new MenuAdapter(getContext());
            mAdapter.setOnItemClickListener(this);
            recyclerView.setAdapter(mAdapter);

            new ArrowDrawable.Builder(context)
                    .setArrowOrientation(Gravity.TOP)
                    .setArrowGravity(Gravity.CENTER)
                    .setShadowSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()))
                    .setBackgroundColor(0xFFFFFFFF)
                    .apply(recyclerView);
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 重新设置动画
                    setAnimStyle(AnimAction.ANIM_SCALE);
                    break;
                default:
                    break;
            }
            return super.setGravity(gravity);
        }

        public Builder setList(int... ids) {
            List<String> data = new ArrayList<>(ids.length);
            for (int id : ids) {
                data.add(getString(id));
            }
            return setList(data);
        }

        public Builder setList(String... data) {
            return setList(Arrays.asList(data));
        }

        @SuppressWarnings("all")
        public Builder setList(List data) {
            mAdapter.setData(data);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressWarnings("all")
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                mListener.onSelected(getPopupWindow(), position, mAdapter.getItem(position));
            }
        }
    }

    private static final class MenuAdapter extends MyAdapter<Object> {

        private MenuAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final TextView mTextView;

            ViewHolder() {
                super(new TextView(getContext()));
                mTextView = (TextView) getItemView();
                mTextView.setTextColor(getColor(R.color.black50));
                mTextView.setTextSize(16);
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).toString());
                mTextView.setPaddingRelative((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, position == 0 ? 12 : 0, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            }
        }
    }

    public interface OnListener<T> {

        /**
         * 选择条目时回调
         */
        void onSelected(BasePopupWindow popupWindow, int position, T t);
    }
}