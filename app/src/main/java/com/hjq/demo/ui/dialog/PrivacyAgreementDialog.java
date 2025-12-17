package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.hjq.demo.R;
import com.hjq.demo.other.LinkClickableSpan;
import com.hjq.demo.ui.dialog.common.MessageDialog;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2023/06/24
 *    desc   : 用户协议与隐私政策弹窗
 */
public final class PrivacyAgreementDialog {

    public static final class Builder
            extends MessageDialog.Builder {

        public Builder(@NonNull Context context) {
            super(context);

            setCancelable(false);
            setCanceledOnTouchOutside(false);

            setTitle(getString(R.string.privacy_agreement_title));
            setConfirm(getString(R.string.privacy_agreement_agree));
            setCancel(getString(R.string.privacy_agreement_disagree));

            String privacyAgreementContent = getString(R.string.privacy_agreement_content);
            SpannableStringBuilder spannable = new SpannableStringBuilder(privacyAgreementContent);

            String userAgreement = getString(R.string.privacy_agreement_user_agreement_text);
            int userAgreementTextStart = privacyAgreementContent.indexOf(userAgreement);
            int userAgreementTextEnd = userAgreementTextStart + userAgreement.length();
            if (userAgreementTextStart != -1 && userAgreementTextEnd < privacyAgreementContent.length()) {
                spannable.setSpan(new LinkClickableSpan(getString(R.string.privacy_agreement_user_agreement_link)),
                    userAgreementTextStart, userAgreementTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            String privacyPolicy = getString(R.string.privacy_agreement_privacy_policy_text);
            int privacyPolicyTextStart = privacyAgreementContent.indexOf(privacyPolicy);
            int privacyPolicyTextEnd = privacyPolicyTextStart + privacyPolicy.length();
            if (privacyPolicyTextStart != -1 && privacyPolicyTextEnd < privacyAgreementContent.length()) {
                spannable.setSpan(new LinkClickableSpan(getString(R.string.privacy_agreement_privacy_policy_link)),
                    privacyPolicyTextStart, privacyPolicyTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            TextView messageView = getMessageView();
            messageView.setGravity(Gravity.START);
            messageView.setMovementMethod(LinkMovementMethod.getInstance());

            setMessage(spannable);
        }
    }
}