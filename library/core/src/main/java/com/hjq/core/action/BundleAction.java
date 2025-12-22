package com.hjq.core.action;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/10/23
 *    desc   : 参数意图
 */
public interface BundleAction {

    @Nullable
    Bundle getBundle();

    default int getInt(String name) {
        return getInt(name, 0);
    }

    default int getInt(String name, int defaultValue) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getInt(name, defaultValue);
    }

    default long getLong(String name) {
        return getLong(name, 0);
    }

    default long getLong(String name, long defaultValue) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getLong(name, defaultValue);
    }

    default float getFloat(String name) {
        return getFloat(name, 0);
    }

    default float getFloat(String name, float defaultValue) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getFloat(name, defaultValue);
    }

    default double getDouble(String name) {
        return getDouble(name, 0);
    }

    default double getDouble(String name, double defaultValue) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getDouble(name, defaultValue);
    }

    default boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    default boolean getBoolean(String name, boolean defaultValue) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getBoolean(name, defaultValue);
    }

    default String getString(String name) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return null;
        }
        return bundle.getString(name);
    }

    default <P extends Parcelable> P getParcelable(String name) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelable(name);
    }

    @SuppressWarnings("unchecked")
    default <S extends Serializable> S getSerializable(String name) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return null;
        }
        return (S) (bundle.getSerializable(name));
    }

    default ArrayList<String> getStringArrayList(String name) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return null;
        }
        return bundle.getStringArrayList(name);
    }

    default ArrayList<Integer> getIntegerArrayList(String name) {
        Bundle bundle = getBundle();
        if (bundle == null) {
            return null;
        }
        return bundle.getIntegerArrayList(name);
    }
}