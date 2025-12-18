package com.hjq.custom.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/09/12
 *    desc   : 长按半透明松手恢复的 TextView
 */
public final class PressAlphaTextView extends AppCompatTextView {

    public PressAlphaTextView(@NonNull Context context) {
        super(context);
    }

    public PressAlphaTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PressAlphaTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        // 判断当前手指是否按下了
        if (pressed) {
            setAlpha(0.5f);
        } else {
            setAlpha(1f);
        }
    }
}