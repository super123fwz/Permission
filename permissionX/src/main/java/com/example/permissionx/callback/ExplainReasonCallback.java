package com.example.permissionx.callback;

import android.support.annotation.NonNull;

import com.example.permissionx.request.ExplainScope;

import java.util.List;



public interface ExplainReasonCallback {

    /**
     *当您应该解释为什么需要这些权限时调用。
     *@param scope 显示基本原理对话框的范围。
     *@param deniedList 您应该解释的权限。
     */
    void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList);
}
