package com.example.permissionx.request;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.permissionx.PermissionX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;



public class InvisibleFragment extends Fragment {

    //请求普通权限的代码。
    public static final int REQUEST_NORMAL_PERMISSIONS = 1;

    //请求 ACCESS_BACKGROUND_LOCATION 权限的代码。其他人不能通过Android R请求此权限。
    public static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 2;

    //转发到当前应用的设置页的代码。
    public static final int FORWARD_TO_SETTINGS = 1;

    //请求 SYSTEM_ALERT_WINDOW 权限的代码。
    public static final int ACTION_MANAGE_OVERLAY_PERMISSION = 2;

    //请求写入设置权限的代码。
    public static final int ACTION_WRITE_SETTINGS_PERMISSION = 3;

    //请求管理外部存储权限的代码。
    public static final int ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION = 4;

    private PermissionBuilder pb;

    private ChainTask task;

    /**
     * 通过调用{@link Fragment#requestPermissions（String[]，int）}，立即请求权限，
     * 并在ActivityCompat.OnRequestPermissionsResultCallback中处理请求结果。
     *
     * @param permissionBuilder permissionBuilder的实例。
     * @param permission        您要请求的权限。
     * @param chainTask         当前任务的实例。
     */
    public void requestNow(PermissionBuilder permissionBuilder, Set<String> permission, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        requestPermissions(permission.toArray(new String[0]), REQUEST_NORMAL_PERMISSIONS);
    }

    public void requestAccessBackgroundLocationNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        requestPermissions(new String[]{RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION);
    }

    /**
     * 请求SYSTEM_ALERT_WINDOW设置权限。在Android M及更高版本上，它是由
     * Settings.ACTION_MANAGE_OVERLAY_PERMISSION  设置。
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestSystemAlertWindowPermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        if (!Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION);
        } else {
            onRequestSystemAlertWindowPermissionResult();
        }
    }

    /**
     * 请求WRITE_SETTINGS设置权限。在Android M及更高版本上，它是由
     * Settings.ACTION_MANAGE_WRITE_SETTINGS  设置。
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestWriteSettingsPermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        if (!Settings.System.canWrite(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivityForResult(intent, ACTION_WRITE_SETTINGS_PERMISSION);
        } else {
            onRequestWriteSettingsPermissionResult();
        }
    }

    /**
     * 请求MANAGE_EXTERNAL_STORAGE存储权限。在Android R及更高版本上，它是由
     * Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
     */
    void requestManageExternalStoragePermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageEmulated()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        } else {
            onRequestManageExternalStoragePermissionResult();
        }
    }

    private void onRequestNormalPermissionsResult(String[] permissions, int[] grantResults) {
        //为了安全起见，我们永远不能保留已授予的权限，因为用户可能会在“设置”中关闭某些权限。
        //所以每次请求时，都必须再次请求已授予的权限并刷新已授予的权限集。
        pb.grantedPermissions.clear();
        List<String> showReasonList = new ArrayList<>(); //在请求权限中保留被拒绝的权限。
        List<String> forwardList = new ArrayList<>(); //在请求权限中永久保留被拒绝的权限。
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pb.grantedPermissions.add(permission);
                //从PermissionBuilder中设置的deniedPermissions和permanentDeniedPermissions中删除授予的权限。
                pb.deniedPermissions.remove(permission);
                pb.permanentDeniedPermissions.remove(permission);
            } else {
                boolean shouldShowRationale = shouldShowRequestPermissionRationale(permission);
                //被拒绝的权限可以变成永久被拒绝的权限，但永久被拒绝的权限不能变成被拒绝的权限。
                if (shouldShowRationale) {
                    showReasonList.add(permission);
                    pb.deniedPermissions.add(permission);
                    //所以不需要从permanentdeniedproperties中删除当前权限，因为它不在那里。
                } else {
                    forwardList.add(permission);
                    pb.permanentDeniedPermissions.add(permission);
                    //我们必须从deniedPermissions中删除当前权限，因为它现在是永久拒绝权限。
                    pb.deniedPermissions.remove(permission);
                }
            }
        }
        List<String> deniedPermissions = new ArrayList<>(); ////用于验证deniedPermissions和permanentDeniedPermissions
        deniedPermissions.addAll(pb.deniedPermissions);
        deniedPermissions.addAll(pb.permanentDeniedPermissions);
        //也许用户可以在我们没有请求的设置中打开一些权限，所以再次检查被拒绝的权限以确保安全。
        for (String permission : deniedPermissions) {
            if (PermissionX.isGranted(getContext(), permission)) {
                pb.deniedPermissions.remove(permission);
                pb.grantedPermissions.add(permission);
            }
        }
        boolean allGranted = pb.grantedPermissions.size() == pb.normalPermissions.size();
        if (allGranted) {
            task.finish();
        } else {
            boolean shouldFinishTheTask = true;   //指出我们是否应该完成任务
            //如果explainReasonCallback不为null并且存在被拒绝的权限。尝试ExplainReasonCallback。
            if ((pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) && !showReasonList.isEmpty()) {
                shouldFinishTheTask = false;
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    //回调ExplainReasonCallbackWithBeforeParam在ExplainReasonCallback之前
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(), new ArrayList<String>(pb.deniedPermissions), false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(), new ArrayList<String>(pb.deniedPermissions));
                }
                //存储这些被永久拒绝的权限，否则在再次请求时它们将丢失。
                pb.tempPermanentDeniedPermissions.addAll(forwardList);
            } else if (pb.forwardToSettings != null && (!forwardList.isEmpty() || !pb.tempPermanentDeniedPermissions.isEmpty())) {
                //如果forwardToSettingsCallback不为null并且存在永久拒绝的权限。尝试ForwardToSettingsCallback。
                shouldFinishTheTask = false;  //不应该，因为ForwardToSettingsCallback处理它
                pb.tempPermanentDeniedPermissions.clear();   //一旦ForwardToSettings回调，就不需要再存储它们了。
                pb.forwardToSettings.onForwardToSettings(task.getForwardScope(), new ArrayList<String>(pb.permanentDeniedPermissions));
            }
            //如果未调用showRequestReasonDialog或showForwardToSettingsDialog。我们应该完成任务。
            //有时会调用ExplainReasonCallback或ForwardToSettingsCallback，但开发人员没有调用
            //在回调中显示RequestReasonDialog或showForwardToSettingsDialog。
            //在这种情况下和所有其他情况下，任务都应该完成。
            if (shouldFinishTheTask || !pb.showDialogCalled) {
                task.finish();
            }
            //每次请求后重置此值。如果不这样做，开发人员将在ExplainReasonCallback中调用showRequestReasonDialog
            //但是没有在ForwardToSettingsCallback中调用showForwardToSettingsDialog，请求进程将丢失。因为
            //上一个showDialogCalled会影响下一个请求逻辑。
            pb.showDialogCalled = false;
        }
    }

    private void onRequestBackgroundLocationPermissionResult() {
        if (checkForGC()) {
            if (PermissionX.isGranted(getContext(), RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)) {
                pb.grantedPermissions.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                //从PermissionBuilder中设置的deniedPermissions和permanentDeniedPermissions中删除授予的权限。
                pb.deniedPermissions.remove(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                pb.permanentDeniedPermissions.remove(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                task.finish();
            } else {
                boolean goesToRequestCallback = true;  //指出我们是否应该完成任务
                boolean shouldShowRationale = shouldShowRequestPermissionRationale(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                //如果explainReasonCallback不为空，我们应该显示基本原理。尝试ExplainReasonCallback。
                if ((pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) && shouldShowRationale) {
                    goesToRequestCallback = false;  //不应该，因为ExplainReasonCallback处理它
                    List<String> permissionsToExplain = new ArrayList<>();
                    permissionsToExplain.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                    if (pb.explainReasonCallbackWithBeforeParam != null) {
                        pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(), permissionsToExplain, false);
                    } else {
                        pb.explainReasonCallback.onExplainReason(task.getExplainScope(), permissionsToExplain);
                    }
                } else if (pb.forwardToSettings != null && !shouldShowRationale) {
                    //如果forwardToSettingsCallback不为null，则不应显示基本原理。尝试ForwardToSettingsCallback。

                    goesToRequestCallback = false;
                    List<String> permissionsToForward = new ArrayList<>();
                    permissionsToForward.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                    pb.forwardToSettings.onForwardToSettings(task.getForwardScope(), permissionsToForward);
                }
                //如果未调用showRequestReasonDialog或showForwardToSettingsDialog。我们应该完成任务。
                //有时会调用ExplainReasonCallback或ForwardToSettingsCallback，但开发人员没有调用
                //在回调中显示RequestReasonDialog或showForwardToSettingsDialog。
                //在这种情况下和所有其他情况下，任务都应该完成。
                if (goesToRequestCallback || !pb.showDialogCalled) {
                    task.finish();
                }
            }
        }
    }

    //处理 WRITE_SETTINGS 权限请求的结果。
    private void onRequestSystemAlertWindowPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(getContext())) {
                task.finish();
            } else if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(),
                            Collections.singletonList(Manifest.permission.SYSTEM_ALERT_WINDOW), false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(), Collections.singletonList(Manifest.permission.SYSTEM_ALERT_WINDOW));
                }
            }
        } else {
            task.finish();
        }
    }

    //处理 WRITE_SETTINGS 权限请求的结果。
    private void onRequestWriteSettingsPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getContext())) {
                task.finish();
            } else if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(),
                            Collections.singletonList(Manifest.permission.WRITE_SETTINGS), false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(), Collections
                            .singletonList(Manifest.permission.WRITE_SETTINGS));
                }
            }
        } else {
            task.finish();
        }
    }

    //处理 MANAGE_EXTERNAL_STORAGE 权限请求。
    private void onRequestManageExternalStoragePermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageEmulated()) {
                task.finish();
            } else if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) {
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(),
                            Collections.singletonList(Manifest.permission.MANAGE_EXTERNAL_STORAGE), false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(),
                            Collections.singletonList(Manifest.permission.MANAGE_EXTERNAL_STORAGE));
                }
            }
        } else {
            task.finish();
        }
    }

    private boolean checkForGC() {
        if (pb == null || task == null) {
            Log.w("PermissionX", "PermissionBuilder和ChainTask此时不应为null，因此在这种情况下我们不能执行任何操作。");
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NORMAL_PERMISSIONS) {
            onRequestNormalPermissionsResult(permissions, grantResults);
        } else if (requestCode==REQUEST_BACKGROUND_LOCATION_PERMISSION){
            onRequestBackgroundLocationPermissionResult();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (checkForGC()) {
            switch (requestCode) {
                case FORWARD_TO_SETTINGS:
                    task.requestAgain(new ArrayList<>(pb.forwardPermissions));
                    break;
                case ACTION_MANAGE_OVERLAY_PERMISSION:
                    onRequestSystemAlertWindowPermissionResult();
                    break;
                case ACTION_WRITE_SETTINGS_PERMISSION:
                    onRequestWriteSettingsPermissionResult();
                    break;
                case ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION:
                    onRequestManageExternalStoragePermissionResult();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkForGC()){
            if (pb.currentDialog!=null&&pb.currentDialog.isShowing()){
                pb.currentDialog.dismiss();
            }
        }
    }
}
