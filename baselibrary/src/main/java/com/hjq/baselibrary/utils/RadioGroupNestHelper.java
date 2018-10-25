package com.hjq.baselibrary.utils;

import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个RadioGroup嵌套类处理辅助类
 */
/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   :
 */
public final class RadioGroupNestHelper implements RadioGroup.OnCheckedChangeListener {

    private List<RadioGroup> mViewSet;//RadioGroup集合

    private RadioGroup.OnCheckedChangeListener mListener;

    public RadioGroupNestHelper(RadioGroup... groups) {
        mViewSet = new ArrayList<>(groups.length - 1);

        for (RadioGroup view : groups) {
            view.setOnCheckedChangeListener(this);
            mViewSet.add(view);
        }
    }

    private boolean mTag;//监听标记，避免重复回调

    // RadioGroup.OnCheckedChangeListener

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!mTag) {
            mTag = true;
            for (RadioGroup view : mViewSet) {
                if (view != group) {
                    view.clearCheck();
                }
            }
            if (mListener != null) {
                mListener.onCheckedChanged(group, checkedId);
            }
            mTag = false;
        }
    }

    /**
     * 移除监听，避免内存泄露
     */
    public void removeViews() {
        if (mViewSet == null) return;

        for (RadioGroup view : mViewSet) {
            view.setOnCheckedChangeListener(null);
        }
        mViewSet.clear();
        mViewSet = null;
    }

    /**
     * 设置多个RadioGroup的监听
     */
    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener l) {
        mListener = l;
    }

    /**
     * 取消选中
     */
    public void clearCheck() {
        for (RadioGroup view : mViewSet) {
            view.clearCheck();
        }
    }
}
