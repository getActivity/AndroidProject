package com.hjq.demo.common;

import com.hjq.toast.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的Activity基类
 */
public class CommonApplication extends UIApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化吐司工具类
        ToastUtils.init(getApplicationContext());

        // 友盟统计
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}