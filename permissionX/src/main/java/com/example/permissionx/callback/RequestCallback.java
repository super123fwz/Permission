package com.example.permissionx.callback;


import android.support.annotation.NonNull;

import java.util.List;

public interface RequestCallback {

    /**
     *
     *请求结果的回调。
     *@param已批准 指示是否已授予所有权限。
     *@param grantedList 用户授予的所有权限。
     *@param deniedList 用户拒绝的所有权限。
     */
    void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList);
}
