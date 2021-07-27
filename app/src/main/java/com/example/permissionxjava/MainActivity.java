package com.example.permissionxjava;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.permissionx.PermissionX;
import com.example.permissionx.callback.ExplainReasonCallback;
import com.example.permissionx.callback.ExplainReasonCallbackWithBeforeParam;
import com.example.permissionx.callback.ForwardToSettingsCallback;
import com.example.permissionx.callback.RequestCallback;
import com.example.permissionx.request.ExplainScope;
import com.example.permissionx.request.ForwardScope;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private Button permissBt;
    private FragmentActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        permissBt=findViewById(R.id.permissBt);
        permissBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionX.init(activity)
                        .permissions(Manifest.permission.CAMERA)
                        .explainReasonBeforeRequest()
                        .onExplainRequestReason(new ExplainReasonCallback() {
                            @Override
                            public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                                scope.showRequestReasonDialog(deniedList,"要请求这些权限","我知道了","取消");
                            }
                        })

                        .onForwardToSettings(new ForwardToSettingsCallback() {
                            @Override
                            public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                                scope.showRequestReasonDialog(deniedList,"你需要手动开启一些权限","我知道了","不行");
                            }
                        })
                        .request(new RequestCallback() {
                            @Override
                            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                                Log.e(TAG,"run");
                                if (allGranted) {
                                    Log.e(TAG, "授予");
                                } else {
                                    Log.e(TAG, "未授予");
                                }
                            }
                        });


            }
        });

    }
}