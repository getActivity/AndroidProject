package com.hjq.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.base.BaseRecyclerViewAdapter;

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
            extends BaseDialogFragment.Builder<WaitDialog.Builder>
            implements BaseRecyclerViewAdapter.OnItemClickListener,
            View.OnClickListener, TabLayout.BaseOnTabSelectedListener, Runnable {

        private static final Handler HANDLER = new Handler(Looper.getMainLooper());

        private TextView mTitleView;
        private ImageView mCloseView;
        private TabLayout mTabLayout;

        private RecyclerView mRecyclerView1;
        private RecyclerView mRecyclerView2;
        private RecyclerView mRecyclerView3;
        private ImageView mHintView;

        private AddressDialogAdapter mAdapter1;
        private AddressDialogAdapter mAdapter2;
        private AddressDialogAdapter mAdapter3;

        private OnListener mListener;

        private String mProvince = "";
        private String mCity = "";
        private String mArea = "";

        private boolean mIgnoreArea;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_address);
            setGravity(Gravity.BOTTOM);
            setAnimStyle(BaseDialog.AnimStyle.LEFT);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(displayMetrics.heightPixels * 3 / 7);

            mTitleView = findViewById(R.id.tv_dialog_address_title);
            mCloseView = findViewById(R.id.iv_dialog_address_closer);
            mTabLayout = findViewById(R.id.tb_dialog_address_tab);

            mRecyclerView1 = findViewById(R.id.rv_dialog_address_list1);
            mRecyclerView2 = findViewById(R.id.rv_dialog_address_list2);
            mRecyclerView3 = findViewById(R.id.rv_dialog_address_list3);
            mHintView = findViewById(R.id.iv_dialog_address_hint);

            mAdapter1 = new AddressDialogAdapter(getContext());
            mAdapter2 = new AddressDialogAdapter(getContext());
            mAdapter3 = new AddressDialogAdapter(getContext());

            mCloseView.setOnClickListener(this);

            mAdapter1.setOnItemClickListener(this);
            mAdapter2.setOnItemClickListener(this);
            mAdapter3.setOnItemClickListener(this);

            mRecyclerView1.setAdapter(mAdapter1);
            mRecyclerView2.setAdapter(mAdapter2);
            mRecyclerView3.setAdapter(mAdapter3);

            mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.dialog_select_hint)), true);
            mTabLayout.addOnTabSelectedListener(this);

            // 显示省份列表
            mAdapter1.setData(ProvinceUtils.getProvinceList(getContext()));
        }


        public Builder setTitle(int resId) {
            return setTitle(getString(resId));
        }
        public Builder setTitle(CharSequence text) {
            mTitleView.setText(text);
            return this;
        }

        /**
         * 不选择县级区域
         */
        public Builder setIgnoreArea() {
            mIgnoreArea = true;
            return this;
        }

        public Builder setListener(OnListener l) {
            mListener = l;
            return this;
        }

        /**
         * {@link BaseRecyclerViewAdapter.OnItemClickListener}
         */

        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (recyclerView == mRecyclerView1) {

                // 记录当前选择的省份
                mProvince = mAdapter1.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mProvince);
                mTabLayout.addTab(mTabLayout.newTab().setText(getContext().getResources().getString(R.string.dialog_select_hint)), true);

                mAdapter2.setData(ProvinceUtils.getCityList(mAdapter1.getItem(position).getNext()));

                mRecyclerView1.setVisibility(View.GONE);
                mRecyclerView2.setVisibility(View.VISIBLE);

                // 如果当前选择的是直辖市，就直接跳过选择城市，直接选择区域
                if (mAdapter2.getItemCount() == 1) {
                    onItemClick(mRecyclerView2, null, 0);
                }

            }else if (recyclerView == mRecyclerView2) {

                // 记录当前选择的城市
                mCity = mAdapter2.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mCity);

                if (mIgnoreArea) {

                    if (mListener != null) {
                        mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                    }

                    // 延迟关闭
                    HANDLER.postDelayed(this, 300);

                } else {
                    mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.dialog_select_hint)), true);
                    mAdapter3.setData(ProvinceUtils.getAreaList(mAdapter2.getItem(position).getNext()));
                }

                mRecyclerView2.setVisibility(View.GONE);

                if (mIgnoreArea) {
                    mHintView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerView3.setVisibility(View.VISIBLE);
                }

            }else if (recyclerView == mRecyclerView3) {

                // 记录当前选择的区域
                mArea = mAdapter3.getItem(position).getName();

                mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mArea);

                mRecyclerView3.setVisibility(View.GONE);
                mHintView.setVisibility(View.VISIBLE);

                if (mListener != null) {
                    mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                }

                // 延迟关闭
                HANDLER.postDelayed(this, 300);
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

        // Tab条目被选中
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.setText(getString(R.string.dialog_select_hint));
            switch (tab.getPosition()) {
                case 0:
                    mProvince = "";
                    while (mTabLayout.getTabAt(1) != null) {
                        mTabLayout.removeTabAt(1);
                    }
                    mRecyclerView1.setVisibility(View.VISIBLE);
                    mRecyclerView2.setVisibility(View.GONE);
                    mRecyclerView3.setVisibility(View.GONE);
                    break;
                case 1:
                    mCity = "";
                    while (mTabLayout.getTabAt(2) != null) {
                        mTabLayout.removeTabAt(2);
                    }
                    mRecyclerView1.setVisibility(View.GONE);
                    mRecyclerView2.setVisibility(View.VISIBLE);
                    mRecyclerView3.setVisibility(View.GONE);
                    break;
                case 2:
                    mArea = "";
                    mRecyclerView1.setVisibility(View.GONE);
                    mRecyclerView2.setVisibility(View.GONE);
                    mRecyclerView3.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        // Tab条目被取消选中
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        // Tab条目被重复点击
        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    }

    private static final class AddressDialogAdapter extends BaseRecyclerViewAdapter<AddressBean, BaseRecyclerViewAdapter.ViewHolder> {

        AddressDialogAdapter(Context context) {
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

        @Override
        public void onBindViewHolder(@NonNull BaseRecyclerViewAdapter.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(getItem(position).getName());
        }
    }

    private static final class AddressBean {

        private String name; // 省、市、区的名称
        private JSONObject next; // 下一级的 Json

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
     * 省市区读取工具类
     */
    private static final class ProvinceUtils {

        /**
         * 获取省列表
         */
        private static List<AddressBean> getProvinceList(Context context) {
            try {
                // 省市区Json数据文件来源：https://github.com/getActivity/ProvinceJson
                JSONArray jsonArray = new JSONArray(getAssetsString(context, "province.json"));

                int length = jsonArray.length();

                ArrayList<AddressBean> list = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(new AddressBean(jsonObject.getString("name"), jsonObject));
                }

                return list;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
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
        private static String getAssetsString(Context context, String file) {
            try {
                InputStream inputStream = context.getAssets().open(file);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[512];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, length);
                }
                outStream.close();
                inputStream.close();
                return outStream.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
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
        void onSelected(Dialog dialog, String province, String city, String area);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}