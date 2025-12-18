package com.hjq.smallest.width;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2025/12/18
 *    desc   : 最小宽度适配
 */
public final class SmallestWidthAdaptation {

    public static float dp2px(@Nullable View view, int value) {
        if (view == null) {
            return 0;
        }
        return dp2px(view.getContext(), value);
    }

    public static float dp2px(@Nullable Fragment fragment, int value) {
        if (fragment == null) {
            return 0;
        }
        return dp2px(fragment.getContext(), value);
    }

    public static float dp2px(@Nullable Context context, int value) {
        if (context == null) {
            return 0;
        }
        return dp2px(context.getResources(), value);
    }

    public static float dp2px(@Nullable Resources resources, int value) {
        if (resources == null) {
            return 0;
        }
        return resources.getDimension(R.dimen.dp_1) * value;
    }

    public static float px2dp(@Nullable View view, float value) {
        if (view == null) {
            return 0;
        }
        return px2dp(view.getContext(), value);
    }

    public static float px2dp(@Nullable Fragment fragment, float value) {
        if (fragment == null) {
            return 0;
        }
        return px2dp(fragment.getContext(), value);
    }

    public static float px2dp(@Nullable Context context, float value) {
        if (context == null) {
            return 0;
        }
        return px2dp(context.getResources(), value);
    }

    public static float px2dp(@Nullable Resources resources, float value) {
        if (resources == null) {
            return 0;
        }
        return value / resources.getDimension(R.dimen.dp_1);
    }

    public static float sp2px(@Nullable View view, int value) {
        if (view == null) {
            return 0;
        }
        return sp2px(view.getContext(), value);
    }

    public static float sp2px(@Nullable Fragment fragment, int value) {
        if (fragment == null) {
            return 0;
        }
        return sp2px(fragment.getContext(), value);
    }

    public static float sp2px(@Nullable Context context, int value) {
        if (context == null) {
            return 0;
        }
        return sp2px(context.getResources(), value);
    }

    public static float sp2px(@Nullable Resources resources, int value) {
        if (resources == null) {
            return 0;
        }
        return resources.getDimension(R.dimen.sp_10) / 10f * value;
    }

    public static float px2sp(@Nullable View view, int value) {
        if (view == null) {
            return 0;
        }
        return px2sp(view.getContext(), value);
    }

    public static float px2sp(@Nullable Fragment fragment, int value) {
        if (fragment == null) {
            return 0;
        }
        return px2sp(fragment.getContext(), value);
    }

    public static float px2sp(@Nullable Context context, int value) {
        if (context == null) {
            return 0;
        }
        return px2sp(context.getResources(), value);
    }

    public static float px2sp(@Nullable Resources resources, int value) {
        if (resources == null) {
            return 0;
        }
        return (value * 10f) / resources.getDimension(R.dimen.sp_10);
    }
}