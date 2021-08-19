package com.example.permissionx.request;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.example.permissionx.callback.ExplainReasonCallback;
import com.example.permissionx.callback.ExplainReasonCallbackWithBeforeParam;
import com.example.permissionx.callback.ForwardToSettingsCallback;
import com.example.permissionx.callback.RequestCallback;
import com.example.permissionx.dialog.DefaultDialog;
import com.example.permissionx.dialog.RationaleDialog;
import com.example.permissionx.dialog.RationaleDialogFragment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;



public class PermissionBuilder {

    //作为Fragment的替代选择的所有内容的片段实例。
    private Fragment fragment;

    //所有activity的实例。
    public FragmentActivity fragmentActivity;

    //在灯光主题中的DefaultDialog上设置的自定义着色颜色。
    private int lightColor = -1;

    //要在DefaultDialog的深色主题中设置的自定义着色颜色
    private int darkColor = -1;

    /**
     * 向用户显示的当前对话框的实例。
     * 当隐形影被破坏时，我们需要关闭这个对话框。
     */
    public Dialog currentDialog = null;

    //应用程序要请求的正常运行时权限。
    public Set<String> normalPermissions;

    /**
     * 特殊情况下需要处理的特殊权限。
     * 如“系统警报”窗口、写入设置和管理外部存储。
     */
    public Set<String> specialPermissions;

    //指示PermissionX应在请求之前解释请求原因。
    public boolean explainReasonBeforeRequest = false;

    /**
     * 表示[ExplainScope.showRequestReasonDialog]或[ForwardScope.showForwardToSettingsDialog]
     * 在[.onExplainRequestReason]或[.onForwardToSettings]回调中调用。
     * 如果未调用，则PermissionX将自动调用requestCallback。
     */
    public boolean showDialogCalled = false;

    //一些不应该请求的权限将存储在这里。并在请求完成时通知用户。
    public Set<String> permissionsWontRequest = new LinkedHashSet<>();

    //保留已在请求的权限中授予的权限。
    public Set<String> grantedPermissions = new LinkedHashSet<>();

    //保留在请求的权限中被拒绝的权限。
    public Set<String> deniedPermissions = new LinkedHashSet<>();

    /**
     * 持有在请求的权限中被永久拒绝的权限。
     * （否认，不再追问）
     */
    public Set<String> permanentDeniedPermissions = new LinkedHashSet<>();

    /**
     * 当我们请求多个权限时。有的被拒绝，有的被永久拒绝。
     * 拒绝的权限将首先被回调。
     * 永久拒绝的权限将存储在此tempPermanentDeniedPermissions中。
     * 一旦不再存在被拒绝的权限，它们将被回调。
     */
    public Set<String> tempPermanentDeniedPermissions = new LinkedHashSet<>();

    /**
     * 持有应转发到允许设置的权限。
     * 并非所有被永久拒绝的权限都应转发到设置。
     * 只有开发者认为有必要的才应该这样做。
     */
    public Set<String> forwardPermissions = new LinkedHashSet<>();

    //方法的回调。不能为空。
    public RequestCallback requestCallback;
    public ExplainReasonCallback explainReasonCallback;
    public ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam;
    public ForwardToSettingsCallback forwardToSettings;

    /**
     * 当权限需要解释请求原因时调用。
     * 通常每次用户拒绝您的请求时都会调用此方法。
     * 如果链接了[.explainReasonBeforeRequest]，则此方法可能在权限请求之前运行。
     *
     * @param callback 权限被用户拒绝。
     * @return PermissionBuilder本身。
     */
    public PermissionBuilder onExplainRequestReason(ExplainReasonCallback callback) {
        explainReasonCallback = callback;
        return this;
    }

    /**
     * 当权限需要解释请求原因时调用。
     * 通常每次用户拒绝您的请求时都会调用此方法。
     * 如果链接了[.explainReasonBeforeRequest]，则此方法可能在权限请求之前运行。
     * beforeRequest参数将告诉您此方法当前在权限请求之前或之后。
     *
     * @param callback 权限被用户拒绝。
     * @return PermissionBuilder本身。
     */
    public PermissionBuilder onExplainRequestReason(ExplainReasonCallbackWithBeforeParam callback) {
        explainReasonCallbackWithBeforeParam = callback;
        return this;
    }

    /**
     * 当权限需要转发到允许的设置时调用。
     * 通常，用户拒绝您的请求，并且选中“永不再问”将调用此方法。
     * 记住[.onExplainRequestReason]总是在这个方法之前。
     * 如果调用了[.onExplainRequestReason]，则不会在同一请求时间调用此方法。
     *
     * @param callback 用户拒绝权限并选中“永不询问”。
     * @return PermissionBuilder本身。
     */
    public PermissionBuilder onForwardToSettings(ForwardToSettingsCallback callback) {
        forwardToSettings = callback;
        return this;
    }

    /**
     * 如果需要显示请求权限的基本原理，请在请求语法中链接此方法。
     * 在请求权限之前将调用[.onExplainRequestReason]。
     *
     * @return PermissionBuilder本身。
     */
    public PermissionBuilder explainReasonBeforeRequest() {
        explainReasonBeforeRequest = true;
        return this;
    }

    public PermissionBuilder(FragmentActivity fragmentActivity, Fragment fragment, Set<String> normalPermissions, Set<String> specialPermissions) {

        if (fragmentActivity!=null){
            this.fragmentActivity = fragmentActivity;
        }
        if (fragmentActivity==null&&fragment!=null){
            this.fragmentActivity=fragment.getActivity();
        }
        this.fragment = fragment;
        this.normalPermissions = normalPermissions;
        this.specialPermissions = specialPermissions;
    }


    /**
     * 将“淡色”设置为“默认基本原理”对话框。
     *
     * @param lightColor 用于轻主题。格式为0xAARRGGBB的颜色值。不要传递资源ID。
     *                   要从资源ID获取颜色值，请调用getColor。
     * @return PermissionBuilder本身。
     * @param暗颜色 用于黑暗主题。格式为0xAARRGGBB的颜色值。不要传递资源ID。
     * 要从资源ID获取颜色值，请调用getColor。
     */
    public PermissionBuilder setDialogTintColor(int lightColor, int darkColor) {
        this.lightColor = lightColor;
        this.darkColor = darkColor;
        return this;
    }


    /**
     *立即请求权限，并在回调中处理请求结果。
     *@param callback 3个参数。全部批准，授予名单，拒绝名单。
     */
    public void request(RequestCallback callback) {
        requestCallback = callback;

        //构建请求链。
        //RequestNormalPermissions首先运行。
        //然后运行RequestBackgroundLocationPermission。
        RequestChain requestChain=new RequestChain();
        requestChain.addTaskToChain(new RequestNormalPermissions(this));
        requestChain.addTaskToChain(new RequestBackgroundLocationPermission(this));
        requestChain.addTaskToChain(new RequestSystemAlertWindowPermission(this));
        requestChain.addTaskToChain(new RequestWriteSettingsPermission(this));
        requestChain.addTaskToChain(new RequestManageExternalStoragePermission(this));
        requestChain.runTask();
    }

    /**
     * 此方法是内部的，不应由开发人员调用。
     * 向用户显示一个对话框并解释为什么需要这些权限。
     *
     * @param chainTask              当前任务的实例。
     * @param showReasonOrGoSettings 表示应该显示解释原因或转发到设置。
     * @param permissions            再次请求。
     * @param message                向用户解释为什么需要这些权限的消息。
     * @param positiveText           正片文本上的正片按钮再次请求。
     * @param negativeText           负片按钮上的负片文本。如果不应取消此对话框，则可能为空。
     */
    public void showHandlePermissionDialog(ChainTask chainTask, boolean showReasonOrGoSettings, List<String> permissions
            , String message, String positiveText, String negativeText) {
        if (message == null || positiveText == null)
            throw new NullPointerException();
        DefaultDialog defaultDialog = new DefaultDialog(fragmentActivity, permissions, message, positiveText, negativeText, lightColor, darkColor);
        showHandlePermissionDialog(chainTask, showReasonOrGoSettings, defaultDialog);
    }

    /**
     * 此方法是内部的，不应由开发人员调用。
     * 向用户显示一个对话框并解释为什么需要这些权限。
     *
     * @param chainTask              当前任务的实例。
     * @param showReasonOrGoSettings 表示应该显示解释原因或转发到设置。
     * @param dialog                 向用户解释为什么需要这些权限
     */
    public void showHandlePermissionDialog(final ChainTask chainTask, final boolean showReasonOrGoSettings, final RationaleDialog dialog) {
        showDialogCalled = true;
        final List<String> permissions = dialog.getPermissionsToRequest();
        if (permissions.isEmpty()) {
            chainTask.finish();
            return;
        }
        currentDialog = dialog;
        dialog.show();
        boolean a=dialog instanceof DefaultDialog;
        boolean b=((DefaultDialog) dialog).isPermissionLayoutEmpty();
        if (dialog instanceof DefaultDialog && ((DefaultDialog) dialog).isPermissionLayoutEmpty()) {
            //没有在对话框上显示的有效权限。
            //我们称之为解散。
            dialog.dismiss();
            chainTask.finish();
        }
        View positiveButton = dialog.getPositiveButton();
        View negativeButton = dialog.getNegativeButton();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        positiveButton.setClickable(true);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (showReasonOrGoSettings) {
                    chainTask.requestAgain(permissions);
                } else {
                    forwardToSettings(permissions);
                }
            }
        });
        if (negativeButton != null) {
            negativeButton.setClickable(true);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    chainTask.finish();
                }
            });
        }
        if (currentDialog != null) {
            currentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    currentDialog = null;
                }
            });
        }
    }

    /**
     * 此方法是内部的，不应由开发人员调用。
     * 向用户显示DialogFragment并解释为什么需要这些权限。
     *
     * @param chainTask              当前任务的实例。
     * @param showReasonOrGoSettings 表示应该显示解释原因或转发到设置。
     * @param dialogFragment         dialogFragment向用户解释为什么需要这些权限。
     */
    public void showHandlePermissionDialog(final ChainTask chainTask, final boolean showReasonOrGoSettings, final RationaleDialogFragment dialogFragment) {
        showDialogCalled = true;
        final List<String> permissions = dialogFragment.getPermissionsToRequest();
        if (permissions.isEmpty()) {
            chainTask.finish();
            return;
        }
        dialogFragment.show(getFragmentManager(), "PermissionXRationaleDialogFragment");
        View positiveButton = dialogFragment.getPositiveButton();
        View negativeButton = dialogFragment.getNegativeButton();
        dialogFragment.setCancelable(false);
        positiveButton.setClickable(true);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
                if (showReasonOrGoSettings) {
                    chainTask.requestAgain(permissions);
                } else {
                    forwardToSettings(permissions);
                }
            }
        });
        if (negativeButton != null) {
            negativeButton.setClickable(true);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogFragment.dismiss();
                    chainTask.finish();
                }
            });
        }

    }

    /**
     * 在fragment中立即请求权限。
     *
     * @param permissions 您要请求的权限。
     * @param chainTask   当前任务的实例。
     */
    public void requestNow(Set<String> permissions, ChainTask chainTask) {
        getInvisibleFragment().requestNow(this, permissions, chainTask);
    }

    /**
     * 在fragment中立即请求访 ACCESS_BACKGROUND_LOCATION 权限。
     *
     * @param chainTask 当前任务的实例。
     */
    public void requestAccessBackgroundLocationNow(ChainTask chainTask) {
        getInvisibleFragment().requestAccessBackgroundLocationNow(this, chainTask);
    }

    /**
     * 在fragment中立即请求访 SYSTEM_ALERT_WINDOW 权限。
     *
     * @param chainTask 当前任务的实例。
     */
    public void requestSystemAlertWindowPermissionNow(ChainTask chainTask) {
        getInvisibleFragment().requestSystemAlertWindowPermissionNow(this, chainTask);
    }

    /**
     * 在fragment中立即请求访 WRITE_SETTINGS 权限。
     *
     * @param chainTask 当前任务的实例。
     */
    public void requestWriteSettingsPermissionNow(ChainTask chainTask) {
        getInvisibleFragment().requestWriteSettingsPermissionNow(this, chainTask);
    }

    /**
     * 在fragment中立即请求访 MANAGE_EXTERNAL_STORAGE 权限。
     *
     * @param chainTask 当前任务的实例。
     */
    public void requestManageExternalStoragePermissionNow(ChainTask chainTask) {
        getInvisibleFragment().requestManageExternalStoragePermissionNow(this, chainTask);
    }

    /**
     * 我们是否应该请求 ACCESS_BACKGROUND_LOCATION 权限。
     *
     * @return 如果特殊权限包含 ACCESS_BACKGROUND_LOCATION 时返回True，否则返回false。
     */
    public boolean shouldRequestBackgroundLocationPermission() {
        return specialPermissions.contains(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
    }

    /**
     * 我们是否应该请求 SYSTEM_ALERT_WINDOW 权限。
     *
     * @return 如果特殊权限包含 SYSTEM_ALERT_WINDOW 时返回True，否则返回false。
     */
    public boolean shouldRequestSystemAlertWindowPermission() {
        return specialPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    /**
     * 我们是否应该请求 WRITE_SETTINGS 权限。
     *
     * @return 如果特殊权限包含 WRITE_SETTINGS 时返回True，否则返回false。
     */
    public boolean shouldRequestWriteSettingsPermission() {
        return specialPermissions.contains(Manifest.permission.WRITE_SETTINGS);
    }

    /**
     * 我们是否应该请求 MANAGE_EXTERNAL_STORAGE 权限。
     *
     * @return 如果特殊权限包含 MANAGE_EXTERNAL_STORAGE 时返回True，否则返回false。
     */
    public boolean shouldRequestManageExternalStoragePermission() {
        return specialPermissions.contains(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE);
    }

    /**
     * 从当前碎片管理器中删除不可见碎片。
     */
    public void removeInvisibleFragment(){
        Fragment existedFragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (existedFragment !=null){
            getFragmentManager().beginTransaction().remove(existedFragment).commitAllowingStateLoss();
        }
    }
    /**
     * 获取当前应用程序的targetSdkVersion。
     *
     * @return 当前应用的targetSdkVersion。
     */
    public int getTargetSdkVersion() {
        return fragmentActivity.getApplicationInfo().targetSdkVersion;
    }

    /**
     * 获取FragmentManager（如果它在Activity中），或者获取ChildFragmentManager（如果它在fragment中）。
     *
     * @return FragmentManager 操作fragment。
     */
    private FragmentManager getFragmentManager() {
        if (fragment != null) {
//            return (fragment.getChildFragmentManager() != null) ? fragment.getChildFragmentManager() : fragmentActivity.getSupportFragmentManager();
            return fragment.getChildFragmentManager();
        }else{
            return fragmentActivity.getSupportFragmentManager();
        }
    }

    /**
     * 获取Activity中的不可见fragment以获取请求权限。
     * 如果没有不可见的fragment，请将其添加到Activity中。
     * 别担心。这个很轻。
     */
    private InvisibleFragment getInvisibleFragment() {
//        FragmentManager fragmentManager = getFragmentManager();
        Fragment existedFragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (existedFragment != null) {
            return (InvisibleFragment) existedFragment;
        } else {
            InvisibleFragment invisibleFragment = new InvisibleFragment();
            getFragmentManager().beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNowAllowingStateLoss();
            return invisibleFragment;
        }
    }

    /**
     *转到应用程序的“设置”页，让用户启用必要的权限。
     *@param permissions 是必需的。
     */
    private void forwardToSettings(List<String> permissions) {
        forwardPermissions.clear();
        forwardPermissions.addAll(permissions);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", fragmentActivity.getPackageName(), null);
        intent.setData(uri);
        getInvisibleFragment().startActivityForResult(intent, InvisibleFragment.FORWARD_TO_SETTINGS);
    }

    static final String FRAGMENT_TAG = "InvisibleFragment";

}
