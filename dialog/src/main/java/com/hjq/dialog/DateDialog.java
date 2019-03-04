package com.hjq.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;

import com.hjq.dialog.widget.LoopView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/17
 *    desc   : 日期选择对话框
 */
public final class DateDialog extends AbsLooperDialog {

    private static final int START_YEAR = 2012;
    private static final int EDN_YEAR = 2022;

    public final static class Builder
            extends AbsLooperDialog.Builder<Builder>
            implements LoopView.LoopScrollListener {

        private LoopView mYearView;
        private LoopView mMonthView;
        private LoopView mDayView;

        private OnListener mListener;

        public Builder(FragmentActivity activity) {
            super(activity);

            // 生产年份
            ArrayList<String> yearList = new ArrayList<>(10);
            for (int i = START_YEAR; i <= EDN_YEAR; i++) {
                yearList.add(i + " " + getString(R.string.dialog_date_year));
            }

            // 生产月份
            ArrayList<String> monthList = new ArrayList<>(12);
            for (int i = 1; i <= 12; i++) {
                monthList.add(i + " " + getString(R.string.dialog_date_month));
            }

            mYearView = createLoopView();
            mMonthView = createLoopView();
            mDayView = createLoopView();

            mYearView.setData(yearList);
            mMonthView.setData(monthList);

            mYearView.setLoopListener(this);
            mMonthView.setLoopListener(this);

            Calendar calendar = Calendar.getInstance();
            mYearView.setInitPosition(calendar.get(Calendar.YEAR) - START_YEAR);
            mMonthView.setInitPosition(calendar.get(Calendar.MONTH));
            mDayView.setInitPosition(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }

        @Override
        public void onItemSelect(LoopView loopView, int position) {
            // 获取这个月最多有多少天
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            if (loopView == mYearView) {
                calendar.set(START_YEAR + mYearView.getSelectedItem(), mMonthView.getSelectedItem(), 1);
            }else if (loopView == mMonthView) {
                calendar.set(START_YEAR + mYearView.getSelectedItem(), mMonthView.getSelectedItem(), 1);
            }

            int day = calendar.getActualMaximum(Calendar.DATE);

            ArrayList<String> dayList = new ArrayList<>(day);
            for (int i = 1; i <= day; i++) {
                dayList.add(i + " " + getString(R.string.dialog_date_day));
            }

            mDayView.setData(dayList);
        }

        public Builder setListener(OnListener l) {
            mListener = l;
            return this;
        }

        @Override
        protected void onConfirm() {
            if (mListener != null) {
                mListener.onSelected(getDialog(), START_YEAR + mYearView.getSelectedItem(),
                        mMonthView.getSelectedItem() + 1, mDayView.getSelectedItem() + 1);
            }
            dismiss();
        }

        protected void onCancel() {
            if (mListener != null) {
                mListener.onCancel(getDialog());
            }
            dismiss();
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
        void onSelected(Dialog dialog, int year, int month, int day);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}
