package com.example.permissionx.request;

import com.example.permissionx.PermissionX;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestNormalPermissions extends BaseTask {

    public RequestNormalPermissions(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void requset() {
        List<String> requestList = new ArrayList<>();
        for (String permission : pb.normalPermissions) {
            if (PermissionX.isGranted(pb.fragmentActivity, permission)) {
                pb.grantedPermissions.add(permission);
            } else {
                requestList.add(permission);
            }
        }
        if (requestList.isEmpty()) {
            finish();
            return;
        }
        if (pb.explainReasonBeforeRequest && (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null)) {
            pb.explainReasonBeforeRequest = false;
            pb.deniedPermissions.addAll(requestList);
            if (pb.explainReasonCallbackWithBeforeParam != null) {
                pb.explainReasonCallbackWithBeforeParam.onExplainReason(explainReasonScope, requestList, true);
            } else {
                pb.explainReasonCallback.onExplainReason(explainReasonScope, requestList);
            }
        } else {
            pb.requestNow(pb.normalPermissions, this);
        }

    }

    @Override
    public void requestAgain(List<String> permissions) {
        Set<String> permissionsToRequestAgain=new HashSet<>();
        permissionsToRequestAgain.addAll(permissions);
        pb.requestNow(permissionsToRequestAgain,this);
    }
}
