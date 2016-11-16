package com.andpack.annotation.statusbar;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定匹配【类型】的View 适应状态栏 Padding (Activity 可以省略 StatusBarTranslucent)
 * Created by SCWANG on 2016/9/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusBarPaddingType {
    Class<? extends View>[] value();
}
