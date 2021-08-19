package com.example.permissionx;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

/**
 * 一个开源的Android库，使运行时权限的处理变得非常简单。
 * <p>
 * 以下代码段显示了简单用法：
 * <pre>
 *   PermissionX.init(activity)
 *      .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA)
 *      .request { allGranted, grantedList, deniedList ->
 *          // handling the logic
 *      }
 * </pre>
 */

public class PermissionX {

    /**
     *初始化PermissionX使一切准备就绪。
     *@param fragmentActivity fragment的实例
     *@return PermissionCollection实例。
     */
    public static PermissionMediator init(FragmentActivity fragmentActivity) {
        return new PermissionMediator(fragmentActivity);
    }

    /**
     *初始化PermissionX使一切准备就绪。
     *@param fragment fragment的实例
     *@return PermissionCollection实例。
     */
    public static PermissionMediator init(Fragment fragment) {
        return new PermissionMediator(fragment);
    }

    /**
     * 用于检查权限是否被授予的帮助函数。
     *
     * @param context    任何上下文，都不会被保留。
     * @param permission 要检查的特定权限名称。e、 g.[android.Manifest.permission.CAMERA]。
     * @return True如果授予此权限，否则返回False。
     */
    public static boolean isGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
