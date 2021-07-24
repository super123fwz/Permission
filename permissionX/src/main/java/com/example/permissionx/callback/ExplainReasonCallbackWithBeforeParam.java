package com.example.permissionx.callback;

import android.support.annotation.NonNull;

import com.example.permissionx.request.ExplainScope;

import java.util.List;

public interface ExplainReasonCallbackWithBeforeParam {

    /**
     *当您应该解释为什么需要这些权限时调用。
     *@param范围
     *显示基本原理对话框的范围。
     *@param拒绝列表
     *您应该解释的权限。
     *@param请求前
     *指明是在权限请求之前还是之后。使用{@link PermissionBuilder #explainReasonBeforeRequest()}
     */
    void onExplainReason(@NonNull ExplainScope scope,@NonNull List<String> deniedList, boolean beforeRequest);
}
