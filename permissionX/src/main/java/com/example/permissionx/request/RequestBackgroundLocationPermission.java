package com.example.permissionx.request;

import android.Manifest;
import android.os.Build;

import com.example.permissionx.PermissionX;

import java.util.ArrayList;
import java.util.List;

public class RequestBackgroundLocationPermission extends BaseTask {

    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    public RequestBackgroundLocationPermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void requset() {
        if (pb.shouldRequestBackgroundLocationPermission()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                pb.specialPermissions.remove(ACCESS_BACKGROUND_LOCATION);
                pb.permissionsWontRequest.add(ACCESS_BACKGROUND_LOCATION);
            }
            if (PermissionX.isGranted(pb.fragmentActivity, ACCESS_BACKGROUND_LOCATION)) {
                finish();
                return;
            }
            boolean accessFindLocationGranted = PermissionX.isGranted(pb.fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
            boolean accessCoarseLocationGranted = PermissionX.isGranted(pb.fragmentActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (accessCoarseLocationGranted || accessFindLocationGranted) {
                if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                    List<String> requestList = new ArrayList<>();
                    requestList.add(ACCESS_BACKGROUND_LOCATION);
                    if (pb.explainReasonCallbackWithBeforeParam != null) {
                        pb.explainReasonCallbackWithBeforeParam.onExplainReason(getExplainScope(), requestList, true);
                    } else {
                        pb.explainReasonCallback.onExplainReason(getExplainScope(), requestList);
                    }
                } else {
                    requestAgain(null);
                }
                return;
            }
        }
        //此时不应请求 ACCESS_BACKGROUND_LOCATION，因此我们调用finish（）来完成此任务。
        finish();
    }

    @Override
    public void requestAgain(List<String> permissions) {
        //不管权限参数是什么，总是请求 ACCESS_BACKGROUND_LOCATION
        pb.requestAccessBackgroundLocationNow(this);
    }
}
