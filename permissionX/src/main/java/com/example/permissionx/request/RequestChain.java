package com.example.permissionx.request;

public class RequestChain {

    /**
     *保存请求过程的第一个任务。权限请求从这里开始。
     */
    private BaseTask headTask;

    /**
     *保存请求过程的最后一个任务。权限请求在这里结束。
     */
    private BaseTask tailTask;

    /**
     *将任务添加到任务链中。
     *@param baseTask 要添加的任务。
     */
    public void addTaskToChain(BaseTask baseTask) {
        if (headTask == null) {
            headTask = tailTask;
        }
        if (tailTask != null) {
            tailTask.next = baseTask;
        }
        tailTask = baseTask;
    }

    /**
     *从第一个任务开始运行此任务链。
     */
    public void runTask() {
        headTask.requset();
    }
}
