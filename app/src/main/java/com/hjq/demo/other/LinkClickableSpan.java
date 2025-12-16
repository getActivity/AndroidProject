package com.hjq.demo.other;

import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.NonNull;
import com.hjq.demo.ui.activity.common.BrowserActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2023/06/24
 *    desc   : 点击跳转链接的 ClickableSpan
 */
public class LinkClickableSpan extends ClickableSpan {

   private final String mTargetUrl;

   public LinkClickableSpan(@NonNull String url) {
      mTargetUrl = url;
   }

   @Override
   public void onClick(@NonNull View widget) {
      BrowserActivity.start(widget.getContext(), mTargetUrl);
   }
}