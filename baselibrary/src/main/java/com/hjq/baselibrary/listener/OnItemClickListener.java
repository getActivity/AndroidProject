package com.hjq.baselibrary.listener;

import android.view.View;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : RecyclerView条目点击监听类
 */
public interface OnItemClickListener{

    /**
     * 当RecyclerView某个条目被点击时回调
     *
     * @param itemView      被点击的条目对象
     * @param position      被点击的条目位置
     */
    void onItemClick(View itemView, int position);
}
