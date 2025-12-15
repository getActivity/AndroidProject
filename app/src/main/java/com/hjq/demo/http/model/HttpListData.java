package com.hjq.demo.http.model;

import androidx.annotation.Nullable;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/07
 *    desc   : 统一接口列表数据结构
 */
public class HttpListData<T> extends HttpData<HttpListData.ListBean<T>> {

    public static class ListBean<T> {

        /** 当前页码 */
        private int pageIndex;
        /** 页大小 */
        private int pageSize;
        /** 总数量 */
        private int totalNumber;
        /** 数据 */
        @Nullable
        private List<T> items;

        /**
         * 判断是否是最后一页
         */
        public boolean isLastPage() {
            if (items == null) {
                return true;
            }
            if (pageSize == 0) {
                // 避免出现除零异常
                return true;
            }
            return Math.ceil((float) totalNumber / pageSize) <= pageIndex;
        }

        public int getTotalNumber() {
            return totalNumber;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        @Nullable
        public List<T> getItems() {
            return items;
        }
    }
}