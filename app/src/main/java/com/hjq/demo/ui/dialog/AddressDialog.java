package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/12
 *    desc   : 省市区选择对话框
 *    doc    : https://baijiahao.baidu.com/s?id=1615894776741007967
 */
public final class AddressDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements TabLayout.OnTabSelectedListener,
            Runnable, RecyclerViewAdapter.OnSelectListener,
            BaseDialog.OnShowListener, BaseDialog.OnDismissListener {

        private final TextView mTitleView;
        private final ImageView mCloseView;
        private final TabLayout mTabLayout;

        private final ViewPager2 mViewPager;
        private final RecyclerViewAdapter mAdapter;
        private final ViewPager2.OnPageChangeCallback mCallback;

        private OnListener mListener;

        private String mProvince = null;
        private String mCity = null;
        private String mArea = null;

        private boolean mIgnoreArea;

        @SuppressWarnings("all")
        public Builder(Context context) {
            super(context);
            setContentView(R.layout.address_dialog);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getSystemService(WindowManager.class).getDefaultDisplay().getMetrics(displayMetrics);
            setHeight(displayMetrics.heightPixels / 2);

            mViewPager = findViewById(R.id.vp_address_province);
            mAdapter = new RecyclerViewAdapter(context);
            mAdapter.setOnSelectListener(this);
            mViewPager.setAdapter(mAdapter);

            mTitleView = findViewById(R.id.tv_address_title);
            mCloseView = findViewById(R.id.iv_address_closer);
            mTabLayout = findViewById(R.id.tb_address_tab);
            setOnClickListener(mCloseView);

            mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);
            mTabLayout.addOnTabSelectedListener(this);

            mCallback = new ViewPager2.OnPageChangeCallback() {

                private int mPreviousScrollState, mScrollState = SCROLL_STATE_IDLE;

                @Override
                public void onPageScrollStateChanged(int state) {
                    mPreviousScrollState = mScrollState;
                    mScrollState = state;
                    if (state == ViewPager2.SCROLL_STATE_IDLE && mTabLayout.getSelectedTabPosition() != mViewPager.getCurrentItem()) {
                        final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE || (mScrollState == SCROLL_STATE_SETTLING && mPreviousScrollState == SCROLL_STATE_IDLE);
                        mTabLayout.selectTab(mTabLayout.getTabAt(mViewPager.getCurrentItem()), updateIndicator);
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    final boolean updateText = mScrollState != SCROLL_STATE_SETTLING || mPreviousScrollState == SCROLL_STATE_DRAGGING;
                    final boolean updateIndicator = !(mScrollState == SCROLL_STATE_SETTLING && mPreviousScrollState == SCROLL_STATE_IDLE);
                    mTabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
                }
            };

            // 显示省份列表
            mAdapter.addItem(AddressManager.getProvinceList(getContext()));
            addOnShowListener(this);
            addOnDismissListener(this);
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
            if (!TextUtils.isEmpty(province)) {
                List<AddressBean> data = mAdapter.getItem(0);
                if (data != null && !data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (province.equals(data.get(i).getName())) {
                            onSelected(0, i);
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
            if (!TextUtils.isEmpty(city)) {
                List<AddressBean> data = mAdapter.getItem(1);
                if (data != null && !data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (city.equals(data.get(i).getName())) {
                            // 避开直辖市，因为选择省的时候已经自动跳过市区了
                            if (mAdapter.getItem(1).size() > 1) {
                                onSelected(1, i);
                            }
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
            if (mAdapter.getItemCount() == 3) {
                // 已经指定了城市，则不能忽略县级区域
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
         * {@link RecyclerViewAdapter.OnSelectListener}
         */

        @SuppressWarnings("all")
        @Override
        public void onSelected(int recyclerViewPosition, int clickItemPosition) {
            switch (recyclerViewPosition) {
                case 0:
                    // 记录当前选择的省份
                    mProvince = mAdapter.getItem(recyclerViewPosition).get(clickItemPosition).getName();
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mProvince);

                    mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);
                    mAdapter.addItem(AddressManager.getCityList(mAdapter.getItem(recyclerViewPosition).get(clickItemPosition).getNext()));
                    mViewPager.setCurrentItem(recyclerViewPosition + 1);

                    // 如果当前选择的是直辖市，就直接跳过选择城市，直接选择区域
                    if (mAdapter.getItem(recyclerViewPosition + 1).size() == 1) {
                        onSelected(recyclerViewPosition + 1, 0);
                    }
                    break;
                case 1:
                    // 记录当前选择的城市
                    mCity = mAdapter.getItem(recyclerViewPosition).get(clickItemPosition).getName();
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mCity);

                    if (mIgnoreArea) {

                        if (mListener != null) {
                            mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                        }

                        // 延迟关闭
                        postDelayed(this::dismiss, 300);

                    } else {
                        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.address_hint)), true);
                        mAdapter.addItem(AddressManager.getAreaList(mAdapter.getItem(recyclerViewPosition).get(clickItemPosition).getNext()));
                        mViewPager.setCurrentItem(recyclerViewPosition + 1);
                    }

                    break;
                case 2:
                    // 记录当前选择的区域
                    mArea = mAdapter.getItem(recyclerViewPosition).get(clickItemPosition).getName();
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(mArea);

                    if (mListener != null) {
                        mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                    }

                    // 延迟关闭
                    postDelayed(this::dismiss, 300);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void run() {
            if (isShowing()) {
                dismiss();
            }
        }

        @SingleClick
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

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            synchronized (this) {
                if (mViewPager.getCurrentItem() != tab.getPosition()) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                tab.setText(getString(R.string.address_hint));
                switch (tab.getPosition()) {
                    case 0:
                        mProvince = mCity = mArea = null;
                        if (mTabLayout.getTabAt(2) != null) {
                            mTabLayout.removeTabAt(2);
                            mAdapter.removeItem(2);
                        }
                        if (mTabLayout.getTabAt(1) != null) {
                            mTabLayout.removeTabAt(1);
                            mAdapter.removeItem(1);
                        }
                        break;
                    case 1:
                        mCity = mArea = null;
                        if (mTabLayout.getTabAt(2) != null) {
                            mTabLayout.removeTabAt(2);
                            mAdapter.removeItem(2);
                        }
                        break;
                    case 2:
                        mArea = null;
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}

        /**
         * {@link BaseDialog.OnShowListener}
         */

        @Override
        public void onShow(BaseDialog dialog) {
            // 注册 ViewPager 滑动监听
            mViewPager.registerOnPageChangeCallback(mCallback);
        }

        /**
         * {@link BaseDialog.OnDismissListener}
         */

        @Override
        public void onDismiss(BaseDialog dialog) {
            // 反注册 ViewPager 滑动监听
            mViewPager.unregisterOnPageChangeCallback(mCallback);
        }
    }

    private final static class RecyclerViewAdapter extends MyAdapter<List<AddressBean>> {

        private OnSelectListener mListener;

        private RecyclerViewAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends MyAdapter.ViewHolder implements OnItemClickListener {

            private final AddressAdapter mAdapter;

            ViewHolder() {
                super(new RecyclerView(getContext()));
                RecyclerView recyclerView = (RecyclerView) getItemView();
                recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mAdapter = new AddressAdapter(getContext());
                mAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onBindView(int position) {
                mAdapter.setData(getItem(position));
            }

            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (mListener != null) {
                    mListener.onSelected(getViewHolderPosition(), position);
                }
            }
        }

        private void setOnSelectListener(OnSelectListener listener) {
            mListener = listener;
        }

        private interface OnSelectListener {

            void onSelected(int recyclerViewPosition, int clickItemPosition);
        }
    }

    private static final class AddressAdapter extends MyAdapter<AddressBean> {

        private AddressAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundResource(R.drawable.transparent_selector);
            textView.setTextColor(0xFF222222);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            return new ViewHolder(textView);
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final TextView mTextView;

            private ViewHolder(View itemView) {
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
        private final String name;
        /** 下一级的 Json */
        private final JSONObject next;

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
                InputStream inputStream = context.getResources().openRawResource(R.raw.province);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[512];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, length);
                }
                outStream.close();
                inputStream.close();
                return new JSONArray(outStream.toString());
            } catch (IOException | JSONException e) {
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
        default void onCancel(BaseDialog dialog) {}
    }
}