package com.example.permissionx;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.example.permissionx.dialog.PermissionMap;
import com.example.permissionx.request.PermissionBuilder;
import com.example.permissionx.request.RequestBackgroundLocationPermission;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class PermissionMediator {
    private FragmentActivity fragmentActivity = null;
    private Fragment fragment = null;

    public PermissionMediator(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public PermissionMediator(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     *您要请求的所有权限。
     *
     *@param permissions 传递权限的vararg param。
     *@return PermissionBuilder本身。
     */
    public PermissionBuilder permissions(List<String> permissions) {
        Set<String> normalPermissionSet = new LinkedHashSet<>();
        Set<String> specialPermissionSet = new LinkedHashSet<>();
        int osVersion = Build.VERSION.SDK_INT;
        int targetSdkVersion = 0;
        if (fragmentActivity != null) {
            targetSdkVersion = fragmentActivity.getApplicationInfo().targetSdkVersion;
        } else {
            if (fragment != null && fragment.getContext() != null) {
                targetSdkVersion = fragment.getContext().getApplicationInfo().targetSdkVersion;
            }
        }
        for (String permission : permissions) {
            if (PermissionMap.allSpecialPermissions().contains(permission)) {
                specialPermissionSet.add(permission);
            } else {
                normalPermissionSet.add(permission);
            }
        }
        if (specialPermissionSet.contains(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)) {
            if (osVersion == Build.VERSION_CODES.Q || (osVersion == Build.VERSION_CODES.R && targetSdkVersion < Build.VERSION_CODES.R)) {

                //如果我们请求访问Q或R上的 ACCESS_BACKGROUND_LOCATION，但目标版本低于R，
                //我们不需要特别请求，只要请求正常的许可即可。
                specialPermissionSet.remove(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
                normalPermissionSet.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        return new PermissionBuilder(fragmentActivity,fragment,normalPermissionSet,specialPermissionSet);
    }


    public PermissionBuilder permissions(String ...permission){
        List<String> cache=new ArrayList<>();
        for(String per:permission){
            cache.add(per);
        }
        return permissions(cache);
    }
}
