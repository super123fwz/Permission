package com.example.permissionx.request;

import android.Manifest;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

public class RequestSystemAlertWindowPermission extends BaseTask {

    public RequestSystemAlertWindowPermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void requset() {
        if (pb.shouldRequestSystemAlertWindowPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pb.getTargetSdkVersion() >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(pb.fragmentActivity)) {
                    //SYSTEM_ALERT_WINDOW 已授予权限，我们现在可以完成此任务。
                    finish();
                    return;
                }
                if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                    List<String> requestList = new ArrayList<>();
                    requestList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
                    if (pb.explainReasonCallbackWithBeforeParam != null) {
                        //回调ExplainReasonCallbackWithBeforeParam在ExplainReasonCallback之前
                        pb.explainReasonCallbackWithBeforeParam.onExplainReason(explainReasonScope, requestList, true);
                    } else {
                        pb.explainReasonCallback.onExplainReason(explainReasonScope, requestList);
                    }
                } else {
                    //没有explainReasonCallback的实现，我们不能请求
                    //此时SYSTEM_ALERT_WINDOW 权限，因为用户无法理解原因。
                    finish();
                }

            } else {
                //SYSTEM_ALERT_WINDOW 权限在Android M下自动授予。
                pb.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
                //此时，不应再对系统警报窗口权限进行特殊处理。
                pb.specialPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
                finish();
            }
        }else{
            //此时不应请求SYSTEM_ALERT_WINDOW 权限，因此我们调用finish（）来完成此任务。
            finish();
        }
    }

    @Override
    public void requestAgain(List<String> permissions) {
        //不管permissions参数是什么，总是请求 SYSTEM_ALERT_WINDOW 权限。
        pb.requestSystemAlertWindowPermissionNow(this);
    }
}
