package com.example.permissionx.request;

import android.Manifest;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.example.permissionx.PermissionX;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseTask implements ChainTask {

    /**
     *指向下一个任务。此任务完成时将运行下一个任务。如果没有下一个任务，请求过程将结束。
     */
    protected ChainTask next;

    /**
     *PermissionBuilder的实例。
     */
    protected PermissionBuilder pb;

    /**
     *为要调用的特定函数的explainReasonCallback提供特定的作用域。
     */
    ExplainScope explainReasonScope;

    /**
     *为forwardToSettingsCallback提供特定作用域，以便调用特定函数。
     */
    ForwardScope forwardToSettingsScope;

    public BaseTask(PermissionBuilder pb) {
        this.pb = pb;
        explainReasonScope = new ExplainScope(pb, this);
        forwardToSettingsScope = new ForwardScope(pb, this);
    }

    @Override
    public ExplainScope getExplainScope() {
        return explainReasonScope;
    }

    @Override
    public ForwardScope getForwardScope() {
        return forwardToSettingsScope;
    }


    @Override
    public void finish() {
        if (next != null) {
            next.requset();
        } else {
            //如果没有下一个任务，请完成请求过程并通知结果
            List<String> deniedList = new ArrayList<>();
            deniedList.addAll(pb.deniedPermissions);
            deniedList.addAll(pb.permanentDeniedPermissions);
            deniedList.addAll(pb.permissionsWontRequest);
            if (pb.shouldRequestBackgroundLocationPermission()) {
                if (PermissionX.isGranted(pb.fragmentActivity, RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)) {
                    pb.grantedPermissions.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                } else {
                    deniedList.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                }
            }
            if (pb.shouldRequestSystemAlertWindowPermission()
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && pb.getTargetSdkVersion() >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(pb.fragmentActivity)) {
                        pb.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
                    } else {
                        deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
                    }

            }
            if (pb.shouldRequestWriteSettingsPermission()
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && pb.getTargetSdkVersion() >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(pb.fragmentActivity)) {
                    pb.grantedPermissions.add(Manifest.permission.WRITE_SETTINGS);
                } else {
                    deniedList.add(Manifest.permission.WRITE_SETTINGS);
                }
            }
            if (pb.shouldRequestManageExternalStoragePermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                    pb.grantedPermissions.add(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE);
                } else {
                    deniedList.add(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE);
                }
            }
            if (pb.requestCallback != null) {
                pb.requestCallback.onResult(deniedList.isEmpty(), new ArrayList<String>(pb.grantedPermissions), deniedList);
            }
            pb.removeInvisibleFragment();
        }
    }
}
