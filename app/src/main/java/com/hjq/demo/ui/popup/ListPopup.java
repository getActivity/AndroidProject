package com.hjq.demo.ui.popup;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BasePopupWindow;
import com.hjq.core.action.AnimAction;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.other.ArrowDrawable;
import com.hjq.smallest.width.SmallestWidthAdaptation;
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

        @SuppressWarnings("rawtypes")
        private boolean mAutoDismiss = true;

        @NonNull
        private final MenuAdapter mAdapter;

        @Nullable
        private OnListener mListener;

        public Builder(@NonNull Context context) {
            super(context);

            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setContentView(recyclerView);

            mAdapter = new MenuAdapter(context);
            mAdapter.setOnItemClickListener(this);
            recyclerView.setAdapter(mAdapter);

            new ArrowDrawable.Builder(context)
                    .setArrowOrientation(Gravity.TOP)
                    .setArrowGravity(Gravity.CENTER)
                    .setShadowSize((int) SmallestWidthAdaptation.dp2px(context, 10))
                    .setBackgroundColor(getColor(R.color.white))
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

        @SuppressWarnings("rawtypes")
        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressWarnings("all")
        @Override
        public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener == null) {
                return;
            }
            mListener.onSelected(getPopupWindow(), position, mAdapter.getItem(position));
        }
    }

    private static final class MenuAdapter extends AppAdapter<Object> {

        private MenuAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder {

            private final TextView mTextView;

            ViewHolder() {
                super(new TextView(getContext()));
                mTextView = (TextView) getItemView();
                mTextView.setTextColor(getColor(R.color.black50));
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, SmallestWidthAdaptation.sp2px(getContext(), 16));
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).toString());

                mTextView.setPaddingRelative((int) SmallestWidthAdaptation.dp2px(itemView, 12),
                        (position == 0 ? (int) SmallestWidthAdaptation.dp2px(itemView, 12) : 0),
                        (int) SmallestWidthAdaptation.dp2px(itemView, 12),
                        (int) SmallestWidthAdaptation.dp2px(itemView, 10));
            }
        }
    }

    public interface OnListener<T> {

        /**
         * 选择条目时回调
         */
        void onSelected(@NonNull BasePopupWindow popupWindow, int position, T data);
    }
}