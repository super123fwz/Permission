package com.example.permissionx.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionMap {

    @TargetApi(Build.VERSION_CODES.R)
    public static Set<String> allSpecialPermissions(){
        Set<String> allSpecialPermissions = new HashSet();
        allSpecialPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        allSpecialPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        allSpecialPermissions.add(Manifest.permission.WRITE_SETTINGS);
        allSpecialPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        return allSpecialPermissions;
    }

    @TargetApi(Build.VERSION_CODES.Q)
    public static Map<String,String> permissionMapOnQ() {
        Map<String, String> permissionMapOnQ = new HashMap<>();
        permissionMapOnQ.put(Manifest.permission.READ_CALENDAR,Manifest.permission_group.CALENDAR);
        permissionMapOnQ.put(Manifest.permission.WRITE_CALENDAR, Manifest.permission_group.CALENDAR);
        permissionMapOnQ.put(Manifest.permission.READ_CALL_LOG, Manifest.permission_group.CALL_LOG);
        permissionMapOnQ.put(Manifest.permission.WRITE_CALL_LOG,Manifest.permission_group.CALL_LOG);
        permissionMapOnQ.put("android.permission.PROCESS_OUTGOING_CALLS", Manifest.permission_group.CALL_LOG);
        permissionMapOnQ.put(Manifest.permission.CAMERA , Manifest.permission_group.CAMERA);
        permissionMapOnQ.put(Manifest.permission.READ_CONTACTS, Manifest.permission_group.CONTACTS);
        permissionMapOnQ.put(Manifest.permission.WRITE_CONTACTS,Manifest.permission_group.CONTACTS);
        permissionMapOnQ.put(Manifest.permission.GET_ACCOUNTS , Manifest.permission_group.CONTACTS);
        permissionMapOnQ.put(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission_group.LOCATION);
        permissionMapOnQ.put(Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission_group.LOCATION);
        permissionMapOnQ.put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission_group.LOCATION);
        permissionMapOnQ.put(Manifest.permission.RECORD_AUDIO ,Manifest.permission_group.MICROPHONE);
        permissionMapOnQ.put(Manifest.permission.READ_PHONE_STATE, Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.CALL_PHONE , Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.ANSWER_PHONE_CALLS , Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.ADD_VOICEMAIL , Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.USE_SIP , Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.ACCEPT_HANDOVER , Manifest.permission_group.PHONE);
        permissionMapOnQ.put(Manifest.permission.BODY_SENSORS , Manifest.permission_group.SENSORS);
        permissionMapOnQ.put(Manifest.permission.ACTIVITY_RECOGNITION , Manifest.permission_group.ACTIVITY_RECOGNITION);
        permissionMapOnQ.put(Manifest.permission.SEND_SMS , Manifest.permission_group.SMS);
        permissionMapOnQ.put(Manifest.permission.RECEIVE_SMS , Manifest.permission_group.SMS);
        permissionMapOnQ.put(Manifest.permission.READ_SMS , Manifest.permission_group.SMS);
        permissionMapOnQ.put(Manifest.permission.RECEIVE_WAP_PUSH , Manifest.permission_group.SMS);
        permissionMapOnQ.put(Manifest.permission.RECEIVE_MMS , Manifest.permission_group.SMS);
        permissionMapOnQ.put(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission_group.STORAGE);
        permissionMapOnQ.put(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission_group.STORAGE);
        permissionMapOnQ.put(Manifest.permission.ACCESS_MEDIA_LOCATION , Manifest.permission_group.STORAGE);

        return permissionMapOnQ;
    }

    @TargetApi(Build.VERSION_CODES.R)
    public static Map<String,String> permissionMapOnR(){
        return permissionMapOnQ();
    }
}
