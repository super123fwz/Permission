package com.example.permissionx.request;

import android.Manifest;
import android.os.Build;
import android.provider.Settings;
import android.text.style.BulletSpan;

import java.util.ArrayList;
import java.util.List;

public class RequestWriteSettingsPermission extends BaseTask {


    public RequestWriteSettingsPermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void requset() {
        if (pb.shouldRequestWriteSettingsPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pb.getTargetSdkVersion() >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(pb.fragmentActivity)) {
                    //已授予写入设置权限，现在可以完成此任务。
                    finish();
                    return;
                }
                if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                    List<String> requestList = new ArrayList<>();
                    requestList.add(Manifest.permission.WRITE_SETTINGS);
                    if (pb.explainReasonCallbackWithBeforeParam != null) {
                        pb.explainReasonCallbackWithBeforeParam.onExplainReason(explainReasonScope, requestList, true);
                    } else {
                        pb.explainReasonCallback.onExplainReason(explainReasonScope, requestList);
                    }
                } else {
                    //没有explainReasonCallback的实现，我们不能请求
                    //此时写入设置权限，因为用户无法理解
                    finish();
                }
            }else {
                //写入设置权限在Android M以下自动授予。
                pb.grantedPermissions.add(Manifest.permission.WRITE_SETTINGS);
                //此时，不应再对写入WRITE_SETTINGS权限进行特殊处理。
                pb.specialPermissions.remove(Manifest.permission.WRITE_SETTINGS);
            }
        } else {
            //此时不应请求WRITE_SETTINGS权限，因此我们调用finish（）来完成此任务。
            finish();
        }
    }

    @Override
    public void requestAgain(List<String> permissions) {
        //不管permissions参数是什么，总是请求 WRITE_SETTINGS 权限。
        pb.requestWriteSettingsPermissionNow(this);
    }
}
