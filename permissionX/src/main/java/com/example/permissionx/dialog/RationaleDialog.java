package com.example.permissionx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

/**
 *要继承的基本对话框类，用于显示基本原理对话框并向用户显示为什么需要所请求的权限。
 *您的对话框必须有一个正按钮才能继续请求，并且有一个可选的否定按钮来取消请求。
 */
public abstract class RationaleDialog extends Dialog {
    public RationaleDialog(Context context) {
        super(context);
    }

    public RationaleDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RationaleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     *要继承的基本对话框类，用于显示基本原理对话框并向用户显示为什么需要所请求的权限。
     *您的对话框必须有一个积极的按钮继续请求和一个可选的消极按钮取消请求。
     * @return 对话框上正按钮的实例。
     */
    abstract public @NonNull
    View getPositiveButton();

    /**
     *返回对话框上负按钮的实例。
     *如果您请求的权限是必需的，则对话框中不能有否定按钮。
     *在这种情况下，您可以简单地返回null。
     *@return 对话框中正按钮的实例，如果对话框中没有负按钮，则返回null。
     */
    abstract public @NonNull
    View getNegativeButton();

    /**
     *提供请求权限。这些权限应该是显示在“基本原理”对话框上的权限。
     * @return 要请求的权限列表。
     */
    abstract public @NonNull
    List<String> getPermissionsToRequest();
}
