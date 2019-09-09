package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.hjq.base.BaseDialog;
import com.hjq.base.BaseRecyclerViewAdapter;
import com.hjq.demo.R;
import com.hjq.demo.common.MyDialogFragment;
import com.hjq.demo.common.MyRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/12
 *    desc   : 省市区选择对话框
 *    doc    : https://baijiahao.baidu.com/s?id=1615894776741007967
 */
public final class AddressDialog {

    public static final class Builder
            extends MyDialogFragment.Builder<Builder>
            implements BaseRecyclerViewAdapter.OnItemClickListener,
            View.OnClickListener, TabLayout.OnTabSelectedListener, Runnable {

        private final TextView mTitleView;
        private final ImageView mCloseView;
        private final TabLayout mTabLayout;
        private final ImageView mHintView;

        private final RecyclerView mProvinceView;
        private final RecyclerView mCityView;
        private final RecyclerView mAreaView;

        private final AddressDialogAdapter mProvinceAdapter;
        private final AddressDialogAdapter mCityAdapter;
        private final AddressDialogAdapter mAreaAdapter;

        private OnListener mListener;

        private String mProvince = null;
        private String mCity = null;
        private String mArea = null;

        private boolean mIgnoreArea;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_address);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getSystemService(WindowManager.class).getDefaultDisplay().getMetrics(displayMetrics);
            setHeight(displayMetrics.heightPixels / 2);

            mTitleView = findViewById(R.id.tv_address_title);
            mCloseView = findViewById(R.id.iv_address_closer);
            mTabLayout = findViewById(R.id.tb_address_tab);
            mHintView = findViewById(R.id.iv_address_hint);

            mProvinceView = findViewById(R.id.rv_address_province);
            mCityView = findViewById(R.id.rv_address_city);
            mAreaView = findViewById(R.id.rv_address_area);

            mProvinceAdapter = new AddressDialogAdapter(getContext());
            mCityAdapter = new AddressDialogAdapter(getContext());
            mAreaAdapter = new AddressDialogAdapter(getContext());

            mCloseView.setOnClickListener(this);

            mProvinceAdapter.setOnItemClickListener(this);
            mCityAdapter.setOnItemClickListener(this);
            mAreaAdapter.setOnItemClickListener(this);

            mProvinceView.setAdapter(mProvinceAdapter);
            mCityView.setAdapter(mCityAdapter);
            mAreaView.setAdapter(mAreaAdapter);

            mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);
            mTabLayout.addOnTabSelectedListener(this);

            // 显示省份列表
            mProvinceAdapter.setData(AddressManager.getProvinceList(getContext()));
        }

        public Builder setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }
        public Builder setTitle(CharSequence text) {
            mTitleView.setText(text);
            return this;
        }

        /**
         * 设置默认省份
         */
        public Builder setProvince(String province) {
            if (province != null && !"".equals(province)) {
                List<AddressBean> data = mProvinceAdapter.getData();
                if (data != null && !data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (province.equals(data.get(i).getName())) {
                            onItemClick(mProvinceView, null, i);
                            break;
                        }
                    }
                }
            }
            return this;
        }

        /**
         * 设置默认城市
         */
        public Builder setCity(String city) {
            if (mIgnoreArea) {
                // 已经忽略了县级区域的选择，不能选定指定的城市
                throw new IllegalStateException("The selection of county-level regions has been ignored. The designated city cannot be selected");
            }
            if (city != null && !"".equals(city)) {
                List<AddressBean> data = mCityAdapter.getData();
                if (data != null && !data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (city.equals(data.get(i).getName())) {
                            onItemClick(mCityView, null, i);
                            break;
                        }
                    }
                }
            }
            return this;
        }

        /**
         * 不选择县级区域
         */
        public Builder setIgnoreArea() {
            List<AddressBean> data = mCityAdapter.getData();
            if (data != null && !data.isEmpty()) {
                // 已经指定了城市，不能再忽略县级区域
                throw new IllegalStateException("Cities have been designated and county-level areas can no longer be ignored");
            }
            mIgnoreArea = true;
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseRecyclerViewAdapter.OnItemClickListener}
         */

        @SuppressWarnings("all")
        @Override
        public synchronized void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (recyclerView == mProvinceView) {

                // 记录当前选择的省份
                mProvince = mProvinceAdapter.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mProvince);
                mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);

                mCityAdapter.setData(AddressManager.getCityList(mProvinceAdapter.getItem(position).getNext()));

                mProvinceView.setVisibility(View.GONE);
                mCityView.setVisibility(View.VISIBLE);

                // 如果当前选择的是直辖市，就直接跳过选择城市，直接选择区域
                if (mCityAdapter.getItemCount() == 1) {
                    onItemClick(mCityView, null, 0);
                }

            } else if (recyclerView == mCityView) {

                // 记录当前选择的城市
                mCity = mCityAdapter.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mCity);

                if (mIgnoreArea) {

                    if (mListener != null) {
                        mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                    }

                    // 延迟关闭
                    postDelayed(this, 300);

                } else {
                    mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);
                    mAreaAdapter.setData(AddressManager.getAreaList(mCityAdapter.getItem(position).getNext()));
                }

                mCityView.setVisibility(View.GONE);

                if (mIgnoreArea) {
                    mHintView.setVisibility(View.VISIBLE);
                } else {
                    mAreaView.setVisibility(View.VISIBLE);
                }

            } else if (recyclerView == mAreaView) {

                // 记录当前选择的区域
                mArea = mAreaAdapter.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mArea);

                mAreaView.setVisibility(View.INVISIBLE);
                mHintView.setVisibility(View.VISIBLE);

                if (mListener != null) {
                    mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                }

                // 延迟关闭
                postDelayed(this, 300);
            }
        }

        /**
         * {@link Runnable}
         */

        @Override
        public void run() {
            if (getDialogFragment() != null &&
                    getDialogFragment().isAdded() &&
                    getDialog() != null &&
                    getDialog().isShowing()) {
                dismiss();
            }
        }

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(View v) {
            if (v == mCloseView) {
                dismiss();
                if (mListener != null) {
                    mListener.onCancel(getDialog());
                }
            }
        }

        /**
         * {@link TabLayout.OnTabSelectedListener}
         */

        /** Tab条目被选中 */
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.setText(getString(R.string.address_hint));
            switch (tab.getPosition()) {
                case 0:
                    mProvince = mCity = mArea = null;
                    if (mTabLayout.getTabAt(2) != null) {
                        mTabLayout.removeTabAt(2);
                    }
                    if (mTabLayout.getTabAt(1) != null) {
                        mTabLayout.removeTabAt(1);
                    }
                    mProvinceView.setVisibility(View.VISIBLE);
                    mCityView.setVisibility(View.GONE);
                    mAreaView.setVisibility(View.GONE);
                    break;
                case 1:
                    mCity = mArea = null;
                    if (mTabLayout.getTabAt(2) != null) {
                        mTabLayout.removeTabAt(2);
                    }
                    mProvinceView.setVisibility(View.GONE);
                    mCityView.setVisibility(View.VISIBLE);
                    mAreaView.setVisibility(View.GONE);
                    break;
                case 2:
                    mArea = null;
                    mProvinceView.setVisibility(View.GONE);
                    mCityView.setVisibility(View.GONE);
                    mAreaView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        /** Tab条目被取消选中 */
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        /** Tab条目被重复点击 */
        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    }

    private static final class AddressDialogAdapter extends MyRecyclerViewAdapter<AddressBean> {

        private AddressDialogAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            TypedValue typedValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(getDrawable(typedValue.resourceId));
                } else {
                    textView.setBackgroundDrawable(getDrawable(typedValue.resourceId));
                }
            }
            textView.setTextColor(0xFF222222);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            return new ViewHolder(textView);
        }

        final class ViewHolder extends MyRecyclerViewAdapter.ViewHolder {

            private final TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) getItemView();
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).getName());
            }
        }
    }

    private static final class AddressBean {

        /** （省\市\区）的名称 */
        private String name;
        /** 下一级的 Json */
        private JSONObject next;

        private AddressBean(String name, JSONObject next) {
            this.name = name;
            this.next = next;
        }

        private String getName() {
            return name;
        }

        private JSONObject getNext() {
            return next;
        }
    }

    /**
     * 省市区数据管理类
     */
    private static final class AddressManager {

        /**
         * 获取省列表
         */
        private static List<AddressBean> getProvinceList(Context context) {
            try {
                // 省市区Json数据文件来源：https://github.com/getActivity/ProvinceJson
                JSONArray jsonArray = getProvinceJson(context);

                if (jsonArray != null) {

                    int length = jsonArray.length();
                    ArrayList<AddressBean> list = new ArrayList<>(length);
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        list.add(new AddressBean(jsonObject.getString("name"), jsonObject));
                    }

                    return list;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 获取城市列表
         *
         * @param jsonObject        城市Json
         */
        private static List<AddressBean> getCityList(JSONObject jsonObject) {
            try {
                JSONArray listCity = jsonObject.getJSONArray("city");
                int length = listCity.length();

                ArrayList<AddressBean> list = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    list.add(new AddressBean(listCity.getJSONObject(i).getString("name"), listCity.getJSONObject(i)));
                }

                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 获取区域列表
         *
         * @param jsonObject        区域 Json
         */
        private static List<AddressBean> getAreaList(JSONObject jsonObject) {
            try {
                JSONArray listArea = jsonObject.getJSONArray("area");
                int length = listArea.length();

                ArrayList<AddressBean> list = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    String string = listArea.getString(i);
                    list.add(new AddressBean(string, null));
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 获取资产目录下面文件的字符串
         */
        private static JSONArray getProvinceJson(Context context) {
            try {
                InputStream inputStream = context.getAssets().open("province.json");
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[512];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, length);
                }
                outStream.close();
                inputStream.close();
                return new JSONArray(outStream.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface OnListener {

        /**
         * 选择完成后回调
         *
         * @param province          省
         * @param city              市
         * @param area              区
         */
        void onSelected(BaseDialog dialog, String province, String city, String area);

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);
    }
}