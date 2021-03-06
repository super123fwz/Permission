package com.example.permissionx.dialog;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.permissionx.R;

import java.util.HashSet;
import java.util.List;




public class DefaultDialog extends RationaleDialog {

    private Context context;
    private List<String> permissions;
    private String message;
    private String positiveText;
    private String negativeText;
    private int lightColor;
    private int darkColor;

    private Button positiveBtn;
    private Button negativeBtn;
    private TextView messageText;
    private LinearLayout permissionsLayout;
    private LinearLayout negativeLayout;

    public DefaultDialog(Context context, List<String> permissions, String message, String positiveText, String negativeText, int lightColor, int darkColor) {
        super(context, R.style.PermissionXDefaultDialog);
        this.context = context;
        this.permissions = permissions;
        this.message = message;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissionx_default_dialog_layout);
        init();
        setupText();
        buildPermissionsLayout();
        setupWindow();
    }

    private void init() {
        positiveBtn = findViewById(R.id.positiveBtn);
        negativeBtn = findViewById(R.id.negativeBtn);
        messageText = findViewById(R.id.messageText);
        permissionsLayout = findViewById(R.id.permissionsLayout);
        negativeLayout = findViewById(R.id.negativeLayout);


    }

    /**
     * ??????Positive??????????????????????????????
     *
     * @return Positive???????????????????????????
     */
    @NonNull
    @Override
    public View getPositiveButton() {
        return positiveBtn;
    }

    /**
     * ??????Negative??????????????????????????????
     * ??????????????????????????????negativeText???null????????????null?????????????????????????????????????????????
     *
     * @return Negative???????????????????????????????????????????????????????????????????????????null???
     */
    @NonNull
    @Override
    public View getNegativeButton() {
        if (negativeText != null) {
            return negativeBtn;
        }
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @return ?????????????????????
     */
    @NonNull
    @Override
    public List<String> getPermissionsToRequest() {
        return permissions;
    }

    /**
     * ?????????????????????????????????
     * ?????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    public boolean isPermissionLayoutEmpty() {
        return permissionsLayout.getChildCount() == 0;
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void setupText() {
        messageText.setText(message);
        positiveBtn.setText(positiveText);
        if (negativeText != null) {
            negativeLayout.setVisibility(View.VISIBLE);
            negativeBtn.setText(negativeText);
        } else {
            negativeLayout.setVisibility(View.GONE);
        }
        if (isDarkTheme()) {
            if (darkColor != -1) {
                positiveBtn.setTextColor(darkColor);
                negativeBtn.setTextColor(darkColor);
            }
        } else {
            if (lightColor != -1) {
                positiveBtn.setTextColor(lightColor);
                negativeBtn.setTextColor(lightColor);
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void buildPermissionsLayout() {
        HashSet<String> tempSet = new HashSet<>();
        int currentVersion = Build.VERSION.SDK_INT;
        String permissionGroup;
        ViewGroup parent;
        for (String permission : permissions) {
            switch (currentVersion) {
                case Build.VERSION_CODES.Q:
                    permissionGroup = PermissionMap.permissionMapOnR().get(permission);
                    break;
                case Build.VERSION_CODES.R:
                    permissionGroup = PermissionMap.permissionMapOnR().get(permission);
                    break;
                default:
                    try {
                        PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(permission, 0);
                        permissionGroup = permissionInfo.group;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        permissionGroup = null;
                    }
                    break;
            }
            if ((PermissionMap.allSpecialPermissions().contains(permission) && !tempSet.contains(permission))
                    || (permissionGroup != null && !tempSet.contains(permissionGroup))) {

                View viewItem = LayoutInflater.from(context).inflate(R.layout.permissionx_permission_item, null, false);
                TextView permissionText = viewItem.findViewById(R.id.permissionText);
                ImageView permissionIcon = viewItem.findViewById(R.id.permissionIcon);

                switch (permission) {
                    case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                        permissionText.setText(context.getString(R.string.permissionx_access_background_location));
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_location);
                        break;
                    case Manifest.permission.SYSTEM_ALERT_WINDOW:
                        permissionText.setText(context.getString(R.string.permissionx_system_alert_window));
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_alert);
                        break;
                    case Manifest.permission.WRITE_SETTINGS:
                        permissionText.setText(context.getString(R.string.permissionx_write_settings));
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_setting);
                        break;
                    case Manifest.permission.MANAGE_EXTERNAL_STORAGE:
                        permissionText.setText(context.getString(R.string.permissionx_manage_external_storage));
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_storage);
                        break;
                    default:
                        try {
                            permissionText.setText(context.getPackageManager().getPermissionGroupInfo(permissionGroup, 0).labelRes);
                            permissionIcon.setImageResource(context.getPackageManager().getPermissionGroupInfo(permissionGroup, 0).icon);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                if (isDarkTheme()) {
                    if (darkColor != -1) {
                        positiveBtn.setTextColor(darkColor);
                        negativeBtn.setTextColor(darkColor);
                    }
                } else {
                    if (lightColor != -1) {
                        positiveBtn.setTextColor(lightColor);
                        negativeBtn.setTextColor(lightColor);
                    }
                }
                permissionsLayout.addView(viewItem);
                tempSet.add(permissionGroup != null ? permissionGroup : permission);
            }
        }

    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void setupWindow() {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        Window window = getWindow();

        if (width < height) {
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                window.setGravity(Gravity.CENTER);
                attributes.width = (int) (width * 0.86);
                window.setAttributes(attributes);
            }
        } else {
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                window.setGravity(Gravity.CENTER);
                attributes.width = (int) (width * 0.6);
                window.setAttributes(attributes);
            }
        }


    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    private boolean isDarkTheme() {
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }

}
