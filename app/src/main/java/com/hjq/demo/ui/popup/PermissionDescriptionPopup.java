package com.hjq.demo.ui.popup;

import android.content.Context;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.hjq.base.BasePopupWindow;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2025/06/01
 *    desc   : 权限说明弹窗
 */
public final class PermissionDescriptionPopup {

    public static final class Builder
            extends BasePopupWindow.Builder<Builder> {

        private final TextView mDescriptionView;

        public Builder(@NonNull Context context) {
            super(context);

            setContentView(R.layout.permission_description_popup);
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            setAnimStyle(android.R.style.Animation_Dialog);
            setBackgroundDimAmount(0.1f);
            setTouchable(true);
            setOutsideTouchable(true);

            mDescriptionView = findViewById(R.id.tv_permission_description_message);
        }

        /**
         * 设置权限说明文案
         */
        public Builder setDescription(CharSequence text) {
            mDescriptionView.setText(text);
            return this;
        }
    }
}