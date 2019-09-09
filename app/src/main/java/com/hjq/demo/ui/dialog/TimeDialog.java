package com.hjq.demo.ui.dialog;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.common.MyDialogFragment;
import com.hjq.demo.widget.LoopView;

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
            extends MyDialogFragment.Builder<Builder>
            implements View.OnClickListener {

        private final TextView mTitleView;
        private final TextView mCancelView;
        private final View mLineView;
        private final TextView mConfirmView;

        private final LoopView mHourView;
        private final LoopView mMinuteView;
        private final LoopView mSecondView;

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        @SuppressWarnings("all")
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_time);
            setAnimStyle(BaseDialog.AnimStyle.IOS);

            mTitleView = findViewById(R.id.tv_time_title);
            mCancelView = findViewById(R.id.tv_time_cancel);
            mLineView = findViewById(R.id.v_time_line);
            mConfirmView = findViewById(R.id.tv_time_confirm);

            mHourView = findViewById(R.id.lv_time_hour);
            mMinuteView = findViewById(R.id.lv_time_minute);
            mSecondView = findViewById(R.id.lv_time_second);

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

            mHourView.setData(hourData);
            mMinuteView.setData(minuteData);
            mSecondView.setData(secondData);

            mCancelView.setOnClickListener(this);
            mConfirmView.setOnClickListener(this);

            Calendar calendar = Calendar.getInstance();
            setHour(calendar.get(Calendar.HOUR_OF_DAY));
            setMinute(calendar.get(Calendar.MINUTE));
            setSecond(calendar.get(Calendar.SECOND));
        }

        public Builder setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }
        public Builder setTitle(CharSequence text) {
            mTitleView.setText(text);
            return this;
        }

        public Builder setConfirm(@StringRes int id) {
            return setConfirm(getString(id));
        }
        public Builder setConfirm(CharSequence text) {
            mConfirmView.setText(text);

            mLineView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            return this;
        }

        public Builder setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }
        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);
            return this;
        }

        public Builder setListener(OnListener listener) {
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

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setTime(String time) {
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
            return setHour(Integer.valueOf(hour));
        }

        public Builder setHour(int hour) {
            int index = hour;
            if (index < 0 || hour == 24) {
                index = 0;
            } else if (index > mHourView.getSize() - 1) {
                index = mHourView.getSize() - 1;
            }
            mHourView.setInitPosition(index);
            return this;
        }

        public Builder setMinute(String minute) {
            return setMinute(Integer.valueOf(minute));
        }

        public Builder setMinute(int minute) {
            int index = minute;
            if (index < 0) {
                index = 0;
            } else if (index > mMinuteView.getSize() - 1) {
                index = mMinuteView.getSize() - 1;
            }
            mMinuteView.setInitPosition(index);
            return this;
        }

        public Builder setSecond(String second) {
            return setSecond(Integer.valueOf(second));
        }

        public Builder setSecond(int second) {
            int index = second;
            if (index < 0) {
                index = 0;
            } else if (index > mSecondView.getSize() - 1) {
                index = mSecondView.getSize() - 1;
            }
            mSecondView.setInitPosition(index);
            return this;
        }

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                if (v == mConfirmView) {
                    mListener.onSelected(getDialog(), mHourView.getSelectedItem(), mMinuteView.getSelectedItem(), mSecondView.getSelectedItem());
                } else if (v == mCancelView) {
                    mListener.onCancel(getDialog());
                }
            }
        }
    }

    public interface OnListener {

        /**
         * 选择完时间后回调
         *
         * @param hour              小时
         * @param minute            分钟
         * @param second            秒钟
         */
        void onSelected(BaseDialog dialog, int hour, int minute, int second);

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);
    }
}