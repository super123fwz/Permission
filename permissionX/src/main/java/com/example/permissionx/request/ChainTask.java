package com.example.permissionx.request;

import java.util.List;

/**
 *定义任务接口以请求权限。
 *不能同时请求所有权限。某些权限需要单独请求。
 *所以每个权限请求都需要实现这个接口，并在它们的实现中执行请求逻辑。
 */
public interface ChainTask {

    /**
     *获取用于显示RequestReasonDialog的ExplainScope。
     *@ExplainScope的返回实例。*获取用于显示RequestReasonDialog的ExplainScope。
     *@ExplainScope返回实例。
     */
    ExplainScope getExplainScope();

    /**
     *获取用于显示ForwardToSettings对话框的ForwardScope。
     *@return ForwardScope实例。
     */
    ForwardScope getForwardScope();

    /**
     * 执行请求逻辑。
     */
    void requset();

    /**
     *当用户被拒绝时再次请求权限。
     *@param权限 再次请求的权限。
     */
    void requestAgain(List<String> permissions);

    /**
     *完成此任务并通知请求结果。
     */
    void finish();
}
