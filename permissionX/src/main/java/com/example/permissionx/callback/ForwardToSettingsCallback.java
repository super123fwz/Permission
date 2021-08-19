package com.example.permissionx.callback;

import android.support.annotation.NonNull;

import com.example.permissionx.request.ForwardScope;

import java.util.List;



public interface ForwardToSettingsCallback {

    /**
     *当您应该告诉用户在设置中允许这些权限时调用。
     *@param范围
     *显示基本原理对话框的范围。
     *@param拒绝列表
     *设置中应允许的权限。
     */
    void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList);
}
