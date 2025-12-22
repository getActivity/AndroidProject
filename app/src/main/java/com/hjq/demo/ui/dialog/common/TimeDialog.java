package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseDialog;
import com.hjq.core.recycler.PickerLayoutManager;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppAdapter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/08/17
 *    desc   : 时间选择对话框
 */
public final class TimeDialog {

    public static final class Builder
            extends StyleDialog.Builder<Builder> {

        @NonNull
        private final RecyclerView mHourView;
        @NonNull
        private final RecyclerView mMinuteView;
        @NonNull
        private final RecyclerView mSecondView;

        @NonNull
        private final PickerLayoutManager mHourManager;
        @NonNull
        private final PickerLayoutManager mMinuteManager;
        @NonNull
        private final PickerLayoutManager mSecondManager;

        @NonNull
        private final PickerAdapter mHourAdapter;
        @NonNull
        private final PickerAdapter mMinuteAdapter;
        @NonNull
        private final PickerAdapter mSecondAdapter;

        @Nullable
        private OnListener mListener;

        @SuppressWarnings("all")
        public Builder(@NonNull Context context) {
            super(context);
            setCustomView(R.layout.time_dialog);
            setTitle(R.string.time_title);

            mHourView = findViewById(R.id.rv_time_hour);
            mMinuteView = findViewById(R.id.rv_time_minute);
            mSecondView = findViewById(R.id.rv_time_second);

            mHourAdapter = new PickerAdapter(context);
            mMinuteAdapter = new PickerAdapter(context);
            mSecondAdapter = new PickerAdapter(context);

            // 生产小时
            ArrayList<String> hourData = new ArrayList<>(24);
            for (int i = 0; i <= 23; i++) {
                hourData.add((i < 10 ? "0" : "") + i + " " + getString(R.string.common_hour));
            }

            // 生产分钟
            ArrayList<String> minuteData = new ArrayList<>(60);
            for (int i = 0; i <= 59; i++) {
                minuteData.add((i < 10 ? "0" : "") + i + " " + getString(R.string.common_minute));
            }

            // 生产秒钟
            ArrayList<String> secondData = new ArrayList<>(60);
            for (int i = 0; i <= 59; i++) {
                secondData.add((i < 10 ? "0" : "") + i + " " + getString(R.string.common_second));
            }

            mHourAdapter.setData(hourData);
            mMinuteAdapter.setData(minuteData);
            mSecondAdapter.setData(secondData);

            mHourManager = new PickerLayoutManager.Builder(context)
                    .build();
            mMinuteManager = new PickerLayoutManager.Builder(context)
                    .build();
            mSecondManager = new PickerLayoutManager.Builder(context)
                    .build();

            mHourView.setLayoutManager(mHourManager);
            mMinuteView.setLayoutManager(mMinuteManager);
            mSecondView.setLayoutManager(mSecondManager);

            mHourView.setAdapter(mHourAdapter);
            mMinuteView.setAdapter(mMinuteAdapter);
            mSecondView.setAdapter(mSecondAdapter);

            Calendar calendar = Calendar.getInstance();
            setHour(calendar.get(Calendar.HOUR_OF_DAY));
            setMinute(calendar.get(Calendar.MINUTE));
            setSecond(calendar.get(Calendar.SECOND));
        }

        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * 不选择秒数
         */
        public Builder setIgnoreSecond() {
            mSecondView.setVisibility(View.GONE);
            return this;
        }

        public Builder setTime(String time) {
            if (time == null) {
                return this;
            }
            // 102030
            if (time.matches("\\d{6}")) {
                setHour(time.substring(0, 2));
                setMinute(time.substring(2, 4));
                setSecond(time.substring(4, 6));
            // 10:20:30
            } else if (time.matches("\\d{2}:\\d{2}:\\d{2}")) {
                setHour(time.substring(0, 2));
                setMinute(time.substring(3, 5));
                setSecond(time.substring(6, 8));
            }
            return this;
        }

        public Builder setHour(String hour) {
            if (hour == null) {
                return this;
            }
            return setHour(Integer.parseInt(hour));
        }

        public Builder setHour(int hour) {
            int index = hour;
            if (index < 0 || hour == 24) {
                index = 0;
            } else if (index > mHourAdapter.getCount() - 1) {
                index = mHourAdapter.getCount() - 1;
            }
            mHourView.scrollToPosition(index);
            return this;
        }

        public Builder setMinute(String minute) {
            if (minute == null) {
                return this;
            }
            return setMinute(Integer.parseInt(minute));
        }

        public Builder setMinute(int minute) {
            int index = minute;
            if (index < 0) {
                index = 0;
            } else if (index > mMinuteAdapter.getCount() - 1) {
                index = mMinuteAdapter.getCount() - 1;
            }
            mMinuteView.scrollToPosition(index);
            return this;
        }

        public Builder setSecond(String second) {
            if (second == null) {
                return this;
            }
            return setSecond(Integer.parseInt(second));
        }

        public Builder setSecond(int second) {
            int index = second;
            if (index < 0) {
                index = 0;
            } else if (index > mSecondAdapter.getCount() - 1) {
                index = mSecondAdapter.getCount() - 1;
            }
            mSecondView.scrollToPosition(index);
            return this;
        }

        @SingleClick
        @Override
        public void onClick(@NonNull View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onSelected(getDialog(), mHourManager.getPickedPosition(), mMinuteManager.getPickedPosition(), mSecondManager.getPickedPosition());
            } else if (viewId == R.id.tv_ui_cancel) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }
    }

    private static final class PickerAdapter extends AppAdapter<String> {

        private PickerAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder {

            private final TextView mPickerView;

            ViewHolder() {
                super(R.layout.picker_item);
                mPickerView = findViewById(R.id.tv_picker_name);
            }

            @Override
            public void onBindView(int position) {
                mPickerView.setText(getItem(position));
            }
        }
    }

    public interface OnListener {

        /**
         * 选择完时间后回调
         *
         * @param hour              时钟
         * @param minute            分钟
         * @param second            秒钟
         */
        void onSelected(@NonNull BaseDialog dialog, int hour, int minute, int second);

        /**
         * 点击取消时回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }
}