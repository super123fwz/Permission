package com.example.permissionx.request;


import com.example.permissionx.dialog.RationaleDialog;
import com.example.permissionx.dialog.RationaleDialogFragment;

import java.util.List;



public class ForwardScope {

    private PermissionBuilder pb;

    private ChainTask chainTask;

    public ForwardScope(PermissionBuilder pb,ChainTask chainTask){
        this.chainTask=chainTask;
        this.pb=pb;
    }

    /**
     *
     *显示一个基本原理对话框，向用户解释为什么需要这些权限。
     *@param permissions 要请求的权限。
     *@param message 向用户显示的消息。
     *@param positiveText 参数
     *    正面按钮上的文本。当用户单击时，PermissionX将再次请求权限。
     *@param negativeText 参数
     *    负片按钮上的文本。当用户单击时，PermissionX将完成请求。
     */
    public void showRequestReasonDialog(List<String> permissions, String message, String positiveText, String negativeText){
        pb.showHandlePermissionDialog(chainTask,false,permissions,message,positiveText,negativeText);
    }

    public void showRequestReasonDialog(List<String> permissions,String message,String positiveText){
        showRequestReasonDialog(permissions,message,positiveText,null);
    }

    /**
     *显示一个对话框，告诉用户在设置中允许这些权限。
     *@param rationaleDialog
     *对话框向用户解释为什么需要这些权限。
     */
    public void showRequestReasonDialog(RationaleDialog rationaleDialog){
        pb.showHandlePermissionDialog(chainTask,false,rationaleDialog);
    }
    public void showRequestReasonDialog(RationaleDialogFragment rationaleDialogFragment){
        pb.showHandlePermissionDialog(chainTask,false,rationaleDialogFragment);
    }
}
