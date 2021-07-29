package com.example.permissionx.request;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

public class RequestManageExternalStoragePermission extends BaseTask {

    /**
     *定义常量以与低于R的系统兼容。
     */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    public RequestManageExternalStoragePermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void requset() {
        if (pb.shouldRequestManageExternalStoragePermission()&& (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)){
            if (Environment.isExternalStorageManager()){
                //MANAGE_EXTERNAL_STORAGE 已授予权限，现在可以完成此任务。
                finish();
                return;
            }
            if (pb.explainReasonCallback!=null||pb.explainReasonCallbackWithBeforeParam!=null){
                List<String> requestList=new ArrayList<>();
                requestList.add(MANAGE_EXTERNAL_STORAGE);
                if (pb.explainReasonCallbackWithBeforeParam!=null){
                    //回调ExplainReasonCallbackWithBeforeParam在ExplainReasonCallback之前
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(getExplainScope(),requestList,true);
                }else{
                    pb.explainReasonCallback.onExplainReason(getExplainScope(),requestList);
                }
            }else{
                //没有explainReasonCallback的实现，我们不能请求
                //此时MANAGE_EXTERNAL_STORAGE权限，因为用户无法理解原因。
                finish();
            }
            return;
        }
        finish();
    }

    @Override
    public void requestAgain(List<String> permissions) {
        pb.requestManageExternalStoragePermissionNow(this);
    }
}
