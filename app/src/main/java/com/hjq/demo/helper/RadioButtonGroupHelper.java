package com.hjq.demo.helper;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 多个 CompoundButton 选中处理辅助类（用于代替 RadioGroup）
 */
public final class RadioButtonGroupHelper implements CompoundButton.OnCheckedChangeListener {

    /** RadioButton集合 */
    private List<RadioButton> mViewSet;

    /** 多个RadioButton监听对象 */
    private OnCheckedChangeListener mListener;

    public RadioButtonGroupHelper(RadioButton... groups) {
        mViewSet = new ArrayList<>(groups.length - 1);

        for (RadioButton view : groups) {
            // 如果这个RadioButton没有设置id的话
            if (view.getId() == View.NO_ID) {
                throw new IllegalArgumentException("are you ok?");
            }
            view.setOnCheckedChangeListener(this);
            mViewSet.add(view);
        }
    }

    public RadioButtonGroupHelper(View rootView, @IdRes int... ids) {
        mViewSet = new ArrayList<>(ids.length - 1);
        for (@IdRes int id : ids) {
            RadioButton view = rootView.findViewById(id);
            view.setOnCheckedChangeListener(this);
            mViewSet.add(view);
        }
    }

    /** 监听标记，避免重复回调 */
    private boolean mTag;

    /**
     * {@link CompoundButton.OnCheckedChangeListener}
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && !mTag) {
            mTag = true;
            for (CompoundButton view : mViewSet) {
                if (view != buttonView && view.isChecked()) {
                    // 这个 API 会触发监听事件
                    view.setChecked(false);
                }
            }
            if (mListener != null) {
                mListener.onCheckedChanged((RadioButton) buttonView, buttonView.getId());
            }
            mTag = false;
        }
    }

    /**
     * 移除监听，避免内存泄露
     */
    public void removeViews() {
        if (mViewSet == null) {
            return;
        }

        for (CompoundButton view : mViewSet) {
            view.setOnCheckedChangeListener(null);
        }
        mViewSet.clear();
        mViewSet = null;
    }

    /**
     * 取消选中
     */
    public void clearCheck() {
        for (CompoundButton view : mViewSet) {
            if (view.isChecked()) {
                view.setChecked(false);
            }
        }
    }

    /**
     * 设置多个RadioButton的监听
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener)  {
        mListener = listener;
    }

    /**
     * 多个CompoundButton选中监听
     */
    public interface OnCheckedChangeListener {
        /**
         * 被选中的CompoundButton对象
         *
         * @param radioButton            选中的RadioButton
         * @param checkedId             选中的资源id
         */
        void onCheckedChanged(RadioButton radioButton, @IdRes int checkedId);
    }
}