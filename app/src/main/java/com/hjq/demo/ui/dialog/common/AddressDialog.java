package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.ui.adapter.common.TabAdapter;
import com.hjq.smallest.width.SmallestWidthAdaptation;
import com.tencent.bugly.library.Bugly;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/12
 *    desc   : 省市区选择对话框
 *    doc    : https://baijiahao.baidu.com/s?id=1615894776741007967
 */
public final class AddressDialog {

    private static final int TYPE_PROVINCE = 0;
    private static final int TYPE_CITY = 1;
    private static final int TYPE_AREA = 2;

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements TabAdapter.OnTabListener,
            Runnable, RecyclerViewAdapter.OnSelectListener,
            BaseDialog.OnShowListener, BaseDialog.OnDismissListener {

        @NonNull
        private final TextView mTitleView;
        @NonNull
        private final ImageView mCloseView;
        @NonNull
        private final RecyclerView mTabView;

        @NonNull
        private final ViewPager2 mViewPager2;

        @NonNull
        private final TabAdapter mTabAdapter;
        @NonNull
        private final RecyclerViewAdapter mAdapter;

        @NonNull
        private final ViewPager2.OnPageChangeCallback mCallback;

        @Nullable
        private OnListener mListener;

        @NonNull
        private String mProvince = "";
        @NonNull
        private String mCity = "";
        @NonNull
        private String mArea = "";

        private boolean mIgnoreArea;

        @SuppressWarnings("all")
        public Builder(@NonNull Context context) {
            super(context);
            setContentView(R.layout.address_dialog);
            setHeight(getResources().getDisplayMetrics().heightPixels / 2);

            mViewPager2 = findViewById(R.id.vp_address_pager);
            mAdapter = new RecyclerViewAdapter(context);
            mAdapter.setOnSelectListener(this);
            mViewPager2.setAdapter(mAdapter);

            mTitleView = findViewById(R.id.tv_address_title);
            mCloseView = findViewById(R.id.iv_address_close);
            mTabView = findViewById(R.id.rv_address_tab);
            setOnClickListener(mCloseView);

            mTabAdapter = new TabAdapter(context, TabAdapter.TAB_MODE_SLIDING, false);
            mTabAdapter.addItem(getString(R.string.address_hint));
            mTabAdapter.setOnTabListener(this);
            mTabView.setAdapter(mTabAdapter);

            mCallback = new ViewPager2.OnPageChangeCallback() {

                private int mPreviousScrollState, mScrollState = ViewPager2.SCROLL_STATE_IDLE;

                @Override
                public void onPageScrollStateChanged(int state) {
                    mPreviousScrollState = mScrollState;
                    mScrollState = state;
                    if (state == ViewPager2.SCROLL_STATE_IDLE && mTabAdapter.getSelectedPosition() != mViewPager2.getCurrentItem()) {
                        onTabSelected(mTabView, mViewPager2.getCurrentItem());
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // default implementation ignored
                }
            };

            // 显示省份列表
            mAdapter.addItem(AddressManager.getProvinceList(getContext()));
            addOnShowListener(this);
            addOnDismissListener(this);

            reduceDragSensitivity(mViewPager2);
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
            if (TextUtils.isEmpty(province)) {
                return this;
            }

            List<AddressBean> data = mAdapter.getItem(0);
            if (data == null || data.isEmpty()) {
                return this;
            }

            for (int i = 0; i < data.size(); i++) {
                if (!province.equals(data.get(i).getName())) {
                    continue;
                }
                selectedAddress(TYPE_PROVINCE, i, false);
                break;
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
            if (TextUtils.isEmpty(city)) {
                return this;
            }

            List<AddressBean> data = mAdapter.getItem(1);
            if (data == null || data.isEmpty()) {
                return this;
            }

            for (int i = 0; i < data.size(); i++) {
                if (!city.equals(data.get(i).getName())) {
                    continue;
                }
                // 避开直辖市，因为选择省的时候已经自动跳过市区了
                if (mAdapter.getItem(1).size() > 1) {
                    selectedAddress(TYPE_CITY, i, false);
                }
                break;
            }
            return this;
        }

        /**
         * 不选择县级区域
         */
        public Builder setIgnoreArea() {
            if (mAdapter.getCount() == 3) {
                // 已经指定了城市，则不能忽略县级区域
                throw new IllegalStateException("Cities have been designated and county-level areas can no longer be ignored");
            }
            mIgnoreArea = true;
            return this;
        }

        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link RecyclerViewAdapter.OnSelectListener}
         */
        @Override
        public void onSelected(int recyclerViewPosition, int clickItemPosition) {
            selectedAddress(recyclerViewPosition, clickItemPosition, true);
        }

        /**
         * 选择地区
         *
         * @param type              类型（省、市、区）
         * @param position          点击的位置
         * @param smoothScroll      是否需要平滑滚动
         */
        @SuppressWarnings("all")
        private void selectedAddress(int type, int position, boolean smoothScroll) {
            switch (type) {
                case TYPE_PROVINCE:
                    // 记录当前选择的省份
                    mProvince = mAdapter.getItem(type).get(position).getName();
                    mTabAdapter.setItem(type, mProvince);

                    mTabAdapter.addItem(getString(R.string.address_hint));
                    mTabAdapter.setSelectedPosition(TYPE_CITY);
                    mAdapter.addItem(AddressManager.getCityList(mAdapter.getItem(type).get(position).getNext()));
                    mViewPager2.setCurrentItem(TYPE_CITY, smoothScroll);

                    // 如果当前选择的是直辖市，就直接跳过选择城市，直接选择区域
                    if (mAdapter.getItem(TYPE_CITY).size() == 1) {
                        selectedAddress(TYPE_CITY, 0, false);
                    }
                    break;
                case TYPE_CITY:
                    // 记录当前选择的城市
                    mCity = mAdapter.getItem(type).get(position).getName();
                    mTabAdapter.setItem(type, mCity);

                    if (mIgnoreArea) {

                        if (mListener != null) {
                            mListener.onSelected(getDialog(), mProvince, mCity, mArea);
                        }

                        // 延迟关闭
                        postDelayed(this::dismiss, 300);

                    } else {
                        mTabAdapter.addItem(getString(R.string.address_hint));
                        mTabAdapter.setSelectedPosition(TYPE_AREA);
                        mAdapter.addItem(AddressManager.getAreaList(mAdapter.getItem(type).get(position).getNext()));
                        mViewPager2.setCurrentItem(TYPE_AREA, smoothScroll);
                    }

                    break;
                case TYPE_AREA:
                    // 记录当前选择的区域
                    mArea = mAdapter.getItem(type).get(position).getName();
                    mTabAdapter.setItem(type, mArea);

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
        public void onClick(@NonNull View view) {
            if (view == mCloseView) {
                dismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         * {@link TabAdapter.OnTabListener}
         */

        @Override
        public boolean onTabSelected(@NonNull RecyclerView recyclerView, int position) {
            synchronized (this) {
                if (mViewPager2.getCurrentItem() != position) {
                    mViewPager2.setCurrentItem(position);
                }

                mTabAdapter.setItem(position, getString(R.string.address_hint));
                switch (position) {
                    case 0:
                        mProvince = mCity = mArea = "";
                        if (mTabAdapter.getCount() > 2) {
                            mTabAdapter.removeItem(2);
                            mAdapter.removeItem(2);
                        }

                        if (mTabAdapter.getCount() > 1) {
                            mTabAdapter.removeItem(1);
                            mAdapter.removeItem(1);
                        }
                        break;
                    case 1:
                        mCity = mArea = "";
                        if (mTabAdapter.getCount() > 2) {
                            mTabAdapter.removeItem(2);
                            mAdapter.removeItem(2);
                        }
                        break;
                    case 2:
                        mArea = "";
                        break;
                    default:
                        break;
                }
            }
            return true;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */

        @Override
        public void onShow(@NonNull BaseDialog dialog) {
            // 注册 ViewPager 滑动监听
            mViewPager2.registerOnPageChangeCallback(mCallback);
        }

        /**
         * {@link BaseDialog.OnDismissListener}
         */

        @Override
        public void onDismiss(@NonNull BaseDialog dialog) {
            // 反注册 ViewPager 滑动监听
            mViewPager2.unregisterOnPageChangeCallback(mCallback);
        }

        /**
         * 降低 ViewPager2 滑动的灵敏度
         *
         * https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
         */
        @SuppressWarnings("ConstantConditions")
        private static void reduceDragSensitivity(ViewPager2 viewPager2) {
            try {
                Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
                recyclerViewField.setAccessible(true);

                RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(viewPager2);

                Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
                touchSlopField.setAccessible(true);

                int touchSlop = (int) touchSlopField.get(recyclerView);
                touchSlopField.set(recyclerView, touchSlop * 3);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private final static class RecyclerViewAdapter extends AppAdapter<List<AddressBean>> {

        @Nullable
        private OnSelectListener mListener;

        private RecyclerViewAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder implements OnItemClickListener {

            private final AddressAdapter mAdapter;

            ViewHolder() {
                super(new RecyclerView(getContext()));
                RecyclerView recyclerView = (RecyclerView) getItemView();
                recyclerView.setNestedScrollingEnabled(true);
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
            public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
                if (mListener == null) {
                    return;
                }
                mListener.onSelected(getViewHolderPosition(), position);
            }
        }

        private void setOnSelectListener(@Nullable OnSelectListener listener) {
            mListener = listener;
        }

        private interface OnSelectListener {

            void onSelected(int recyclerViewPosition, int clickItemPosition);
        }
    }

    private static final class AddressAdapter extends AppAdapter<AddressBean> {

        private AddressAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundResource(R.drawable.transparent_selector);
            textView.setTextColor(Color.parseColor("#222222"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, SmallestWidthAdaptation.sp2px(parent, 14));
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding((int) SmallestWidthAdaptation.dp2px(getContext(), 20),
                    (int) SmallestWidthAdaptation.dp2px(getContext(), 10),
                    (int) SmallestWidthAdaptation.dp2px(getContext(), 20),
                    (int) SmallestWidthAdaptation.dp2px(getContext(), 10));
            // 适配 RTL 特性
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            return new ViewHolder(textView);
        }

        private final class ViewHolder extends AppViewHolder {

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
        private static List<AddressBean> getProvinceList(@NonNull Context context) {
            try {
                // 省市区Json数据文件来源：https://github.com/getActivity/ProvinceJson
                JSONArray jsonArray = getProvinceJson(context);

                if (jsonArray == null) {
                    return null;
                }

                int length = jsonArray.length();
                ArrayList<AddressBean> list = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(new AddressBean(jsonObject.getString("name"), jsonObject));
                }

                return list;

            } catch (JSONException e) {
                e.printStackTrace();
                Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
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
                Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
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
                Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
                return null;
            }
        }

        /**
         * 获取资产目录下面文件的字符串
         */
        private static JSONArray getProvinceJson(@NonNull Context context) {
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
                Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
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
        void onSelected(@NonNull BaseDialog dialog, @NonNull String province, @NonNull String city, @NonNull String area);

        /**
         * 点击取消时回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }
}