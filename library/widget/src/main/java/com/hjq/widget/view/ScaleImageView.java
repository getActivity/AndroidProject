package com.hjq.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.hjq.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/08/02
 *    desc   : 长按缩放松手恢复的 ImageView
 */
public final class ScaleImageView extends AppCompatImageView {

    private float mScaleSize = 1.2f;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        setScaleSize(array.getFloat(R.styleable.ScaleImageView_scaleRatio, mScaleSize));
        array.recycle();
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        // 判断当前手指是否按下了
        if (pressed) {
            setScaleX(mScaleSize);
            setScaleY(mScaleSize);
        } else {
            setScaleX(1);
            setScaleY(1);
        }
    }

    public void setScaleSize(float size) {
        mScaleSize = size;
    }
}