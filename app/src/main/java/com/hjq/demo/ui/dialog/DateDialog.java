package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyAdapter;
import com.hjq.demo.other.PickerLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/17
 *    desc   : 日期选择对话框
 */
public final class DateDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder>
            implements PickerLayoutManager.OnPickerListener {

        private final int mStartYear = Calendar.getInstance().get(Calendar.YEAR) - 100;
        private final int mEndYear = Calendar.getInstance().get(Calendar.YEAR);

        private final RecyclerView mYearView;
        private final RecyclerView mMonthView;
        private final RecyclerView mDayView;

        private final PickerLayoutManager mYearManager;
        private final PickerLayoutManager mMonthManager;
        private final PickerLayoutManager mDayManager;

        private final PickerAdapter mYearAdapter;
        private final PickerAdapter mMonthAdapter;
        private final PickerAdapter mDayAdapter;

        private OnListener mListener;

        public Builder(Context context) {
            super(context);

            setCustomView(R.layout.date_dialog);
            setTitle(R.string.time_title);

            mYearView = findViewById(R.id.rv_date_year);
            mMonthView = findViewById(R.id.rv_date_month);
            mDayView = findViewById(R.id.rv_date_day);

            mYearAdapter = new PickerAdapter(context);
            mMonthAdapter = new PickerAdapter(context);
            mDayAdapter = new PickerAdapter(context);

            // 生产年份
            ArrayList<String> yearData = new ArrayList<>(10);
            for (int i = mStartYear; i <= mEndYear; i++) {
                yearData.add(i + " " + getString(R.string.common_year));
            }

            // 生产月份
            ArrayList<String> monthData = new ArrayList<>(12);
            for (int i = 1; i <= 12; i++) {
                monthData.add(i + " " + getString(R.string.common_month));
            }

            Calendar calendar = Calendar.getInstance(Locale.CHINA);

            int day = calendar.getActualMaximum(Calendar.DATE);
            // 生产天数
            ArrayList<String> dayData = new ArrayList<>(day);
            for (int i = 1; i <= day; i++) {
                dayData.add(i + " " + getString(R.string.common_day));
            }

            mYearAdapter.setData(yearData);
            mMonthAdapter.setData(monthData);
            mDayAdapter.setData(dayData);

            mYearManager = new PickerLayoutManager.Builder(context)
                    .build();
            mMonthManager = new PickerLayoutManager.Builder(context)
                    .build();
            mDayManager = new PickerLayoutManager.Builder(context)
                    .build();

            mYearView.setLayoutManager(mYearManager);
            mMonthView.setLayoutManager(mMonthManager);
            mDayView.setLayoutManager(mDayManager);

            mYearView.setAdapter(mYearAdapter);
            mMonthView.setAdapter(mMonthAdapter);
            mDayView.setAdapter(mDayAdapter);
            
            setYear(calendar.get(Calendar.YEAR));
            setMonth(calendar.get(Calendar.MONTH) + 1);
            setDay(calendar.get(Calendar.DAY_OF_MONTH));

            mYearManager.setOnPickerListener(this);
            mMonthManager.setOnPickerListener(this);
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * 不选择天数
         */
        public Builder setIgnoreDay() {
            mDayView.setVisibility(View.GONE);
            return this;
        }

        public Builder setDate(long date) {
            if (date > 0) {
                setDate(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(date)));
            }
            return this;
        }

        public Builder setDate(String date) {
            // 20190519
            if (date.matches("\\d{8}")) {
                setYear(date.substring(0, 4));
                setMonth(date.substring(4, 6));
                setDay(date.substring(6, 8));
            // 2019-05-19
            } else if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                setYear(date.substring(0, 4));
                setMonth(date.substring(5, 7));
                setDay(date.substring(8, 10));
            }
            return this;
        }

        public Builder setYear(String year) {
            return setYear(Integer.parseInt(year));
        }

        public Builder setYear(int year) {
            int index = year - mStartYear;
            if (index < 0) {
                index = 0;
            } else if (index > mYearAdapter.getItemCount() - 1) {
                index = mYearAdapter.getItemCount() - 1;
            }
            mYearView.scrollToPosition(index);
            return this;
        }

        public Builder setMonth(String month) {
            return setMonth(Integer.parseInt(month));
        }

        public Builder setMonth(int month) {
            int index = month - 1;
            if (index < 0) {
                index = 0;
            } else if (index > mMonthAdapter.getItemCount() - 1) {
                index = mMonthAdapter.getItemCount() - 1;
            }
            mMonthView.scrollToPosition(index);
            return this;
        }

        public Builder setDay(String day) {
            return setDay(Integer.parseInt(day));
        }

        public Builder setDay(int day) {
            int index = day - 1;
            if (index < 0) {
                index = 0;
            } else if (index > mDayAdapter.getItemCount() - 1) {
                index = mDayAdapter.getItemCount() - 1;
            }
            mDayView.scrollToPosition(index);
            return this;
        }

        /**
         * {@link PickerLayoutManager.OnPickerListener}
         * 
         * @param recyclerView              RecyclerView 对象
         * @param position                  当前滚动的位置
         */
        @Override
        public void onPicked(RecyclerView recyclerView, int position) {
            // 获取这个月最多有多少天
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            if (recyclerView == mYearView) {
                calendar.set(mStartYear + position, mMonthManager.getPickedPosition(), 1);
            } else if (recyclerView == mMonthView) {
                calendar.set(mStartYear + mYearManager.getPickedPosition(), position, 1);
            }

            int day = calendar.getActualMaximum(Calendar.DATE);
            if (mDayAdapter.getItemCount() != day) {
                ArrayList<String> dayData = new ArrayList<>(day);
                for (int i = 1; i <= day; i++) {
                    dayData.add(i + " " + getString(R.string.common_day));
                }
                mDayAdapter.setData(dayData);
            }
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onSelected(getDialog(), mStartYear + mYearManager.getPickedPosition(), mMonthManager.getPickedPosition() + 1, mDayManager.getPickedPosition() + 1);
                    }
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }

        private static final class PickerAdapter extends MyAdapter<String> {

            private PickerAdapter(Context context) {
                super(context);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder();
            }

            private final class ViewHolder extends MyAdapter.ViewHolder {

                private final TextView mPickerView;

                ViewHolder() {
                    super(R.layout.picker_item);
                    mPickerView = (TextView) findViewById(R.id.tv_picker_name);
                }

                @Override
                public void onBindView(int position) {
                    mPickerView.setText(getItem(position));
                }
            }
        }
    }

    public interface OnListener {

        /**
         * 选择完日期后回调
         *
         * @param year              年
         * @param month             月
         * @param day               日
         */
        void onSelected(BaseDialog dialog, int year, int month, int day);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}