package com.hjq.custom.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import com.hjq.custom.widget.R;
import java.util.regex.Pattern;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/06/29
 *    desc   : 正则输入限制编辑框
 */
public class RegexEditText extends AppCompatEditText implements InputFilter {

    /** 手机号（只能以 1 开头） */
    public static final String REGEX_MOBILE = "[1]\\d{0,10}";
    /** 中文（普通的中文字符） */
    public static final String REGEX_CHINESE = "[\\u4e00-\\u9fa5]*";
    /** 英文（大写和小写的英文） */
    public static final String REGEX_ENGLISH = "[a-zA-Z]*";
    /** 数字（只允许输入纯数字）*/
    public static final String REGEX_NUMBER = "\\d*";
    /** 计数（非 0 开头的数字） */
    public static final String REGEX_COUNT = "[1-9]\\d*";
    /** 用户名（中文、英文、数字） */
    public static final String REGEX_NAME = "[[\\u4e00-\\u9fa5]|[a-zA-Z]|\\d]*";
    /** 非空格的字符（不能输入空格） */
    public static final String REGEX_NONNULL = "\\S+";
    /** 密码（只能输入英文，数字，英文符号） */
    public static final String REGEX_PASSWORD = "[a-zA-Z|\\d|,|\\.|\\|?|!|:|/|@|\"|;|'|~|\\|\\(|\\)|<|>|\\|\\[|\\]|\\{|\\}|\\*|&|\\\\||`|#|\\$|%|\\^|_|\\|\\+|\\-|=]+";

    /** 正则表达式规则 */
    private Pattern mPattern;

    public RegexEditText(@NonNull Context context) {
        this(context, null);
    }

    public RegexEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public RegexEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegexEditText);

        if (array.hasValue(R.styleable.RegexEditText_inputRegex)) {
            setInputRegex(array.getString(R.styleable.RegexEditText_inputRegex));
        } else if (array.hasValue(R.styleable.RegexEditText_regexType)) {
            int regexType = array.getInt(R.styleable.RegexEditText_regexType, 0);
            switch (regexType) {
                case 0x01:
                    setInputRegex(REGEX_MOBILE);
                    break;
                case 0x02:
                    setInputRegex(REGEX_CHINESE);
                    break;
                case 0x03:
                    setInputRegex(REGEX_ENGLISH);
                    break;
                case 0x04:
                    setInputRegex(REGEX_NUMBER);
                    break;
                case 0x05:
                    setInputRegex(REGEX_COUNT);
                    break;
                case 0x06:
                    setInputRegex(REGEX_NAME);
                    break;
                case 0x07:
                    setInputRegex(REGEX_NONNULL);
                    break;
                case 0x08:
                    setInputRegex(REGEX_PASSWORD);
                    break;
                default:
                    break;
            }
        }

        array.recycle();

        // 适配 RTL 特性
        if (getTextAlignment() == TEXT_ALIGNMENT_GRAVITY) {
            setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        }
    }

    /**
     * 是否有这个输入标记
     */
    public boolean hasInputType(int type) {
        return (getInputType() & type) != 0;
    }

    /**
     * 添加一个输入标记
     */
    public void addInputType(int type) {
        setInputType(getInputType() | type);
    }

    /**
     * 移除一个输入标记
     */
    public void removeInputType(int type) {
        setInputType(getInputType() & ~type);
    }

    /**
     * 设置输入正则
     */
    public void setInputRegex(@Nullable String regex) {
        if (TextUtils.isEmpty(regex)) {
            return;
        }
        mPattern = Pattern.compile(regex);
        addFilters(this);
    }

    /**
     * 获取输入正则
     */
    public String getInputRegex() {
        if (mPattern == null) {
            return null;
        }
        return mPattern.pattern();
    }

    /**
     * 添加筛选规则
     */
    public void addFilters(InputFilter filter) {
        if (filter == null) {
            return;
        }

        final InputFilter[] newFilters;
        final InputFilter[] oldFilters = getFilters();
        if (oldFilters != null && oldFilters.length > 0) {
            newFilters = new InputFilter[oldFilters.length + 1];
            // 复制旧数组的元素到新数组中
            System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.length);
            newFilters[oldFilters.length] = filter;
        } else {
            newFilters = new InputFilter[1];
            newFilters[0] = filter;
        }
        super.setFilters(newFilters);
    }

    /**
     * 清空筛选规则
     */
    public void clearFilters() {
        super.setFilters(new InputFilter[0]);
    }

    /**
     * {@link InputFilter}
     *
     * @param source        新输入的字符串
     * @param start         新输入的字符串起始下标
     * @param end           新输入的字符串终点下标
     * @param dest          输入之前文本框内容
     * @param destStart     在原内容上的起始坐标
     * @param destEnd       在原内容上的终点坐标
     * @return              返回字符串将会加入到内容中
     */
    @Override
    public CharSequence filter(@NonNull CharSequence source, int start, int end, @NonNull Spanned dest, int destStart, int destEnd) {
        if (mPattern == null) {
            return source;
        }

        // 拼接出最终的字符串
        String begin = dest.toString().substring(0, destStart);
        String over = dest.toString().substring(destStart + (destEnd - destStart), destStart + (dest.toString().length() - begin.length()));
        String result = begin + source + over;

        // 判断是插入还是删除
        if (destStart > destEnd - 1) {
            // 如果是插入字符
            if (!mPattern.matcher(result).matches()) {
                // 如果不匹配就不让这个字符输入
                return "";
            }
        } else {
            // 如果是删除字符
            if (!mPattern.matcher(result).matches()) {
                // 如果不匹配则不让删除（删空操作除外）
                if (!result.isEmpty()) {
                    return dest.toString().substring(destStart, destEnd);
                }
            }
        }

        // 不做任何修改
        return source;
    }
}