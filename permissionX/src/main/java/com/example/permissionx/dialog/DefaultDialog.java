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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.example.permissionx.R;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DefaultDialog extends RationaleDialog {

    private Context context;
    private List<String> permissions;
    private String message;
    private String positiveText;
    private String negativeText;
    private int lightColor;
    private int darkColor;
    private View binding;
    private View viewItem;
    private ViewHolder viewHolder;
    private ViewHolderItem viewHolderItem;

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
        binding = LayoutInflater.from(context).inflate(R.layout.permissionx_default_dialog_layout, null, false);
        viewItem = LayoutInflater.from(context).inflate(R.layout.permissionx_permission_item, null, false);
        init();
        setupText();
        buildPermissionsLayout();
        setupWindow();
    }

    private void init() {
        if (binding == null) {
            viewHolder = new ViewHolder();
            viewHolder.positiveBtn = binding.findViewById(R.id.positiveBtn);
            viewHolder.negativeBtn = binding.findViewById(R.id.negativeBtn);
            viewHolder.messageText = binding.findViewById(R.id.messageText);
            viewHolder.permissionsLayout = binding.findViewById(R.id.permissionsLayout);
            viewHolder.negativeLayout = binding.findViewById(R.id.negativeLayout);
        } else {
            viewHolder = (ViewHolder) binding.getTag();
        }
        if (viewItem==null){
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.permission_item=viewItem.findViewById(R.id.permission_item);
            viewHolderItem.permissionText=viewItem.findViewById(R.id.permissionText);
            viewHolderItem.permissionIcon=viewItem.findViewById(R.id.permissionIcon);
        } else {
            viewHolderItem = (ViewHolderItem) viewItem.getTag();
        }
    }

    /**
     * 提供Positive按钮实例以继续请求。
     *
     * @return Positive按钮实例继续请求。
     */
    @NonNull
    @Override
    public View getPositiveButton() {
        return viewHolder.positiveBtn;
    }

    /**
     * 提供Negative按钮实例以中止请求。
     * 这是另一种选择。如果negativeText为null，则返回null，表示所有这些权限都是必需的。
     *
     * @return Negative按钮实例中止请求。如果所有这些权限都是必需的，则为null。
     */
    @NonNull
    @Override
    public View getNegativeButton() {
        if (negativeText != null) {
            return viewHolder.negativeBtn;
        }
        return null;
    }

    /**
     * 提供再次请求的权限。
     *
     * @return 权限以再次请求
     */
    @NonNull
    @Override
    public List<String> getPermissionsToRequest() {
        return permissions;
    }

    /**
     * 检查权限布局是否为空。
     * 如果传入的所有权限都是无效权限例如名为
     * “你好，世界”。我们不会将这些添加到权限布局中。
     *
     * @return
     */
    public boolean isPermissionLayoutEmpty() {
        return viewHolder.permissionsLayout.getChildCount() == 0;
    }

    /**
     * 在对话框上设置文本和文本颜色。
     */
    private void setupText() {
        viewHolder.messageText.setText(message);
        viewHolder.positiveBtn.setText(positiveText);
        if (negativeText != null) {
            viewHolder.negativeLayout.setVisibility(View.VISIBLE);
            viewHolder.negativeBtn.setText(negativeText);
        } else {
            viewHolder.negativeLayout.setVisibility(View.GONE);
        }
        if (isDarkTheme()) {
            if (darkColor != -1) {
                viewHolder.positiveBtn.setTextColor(darkColor);
                viewHolder.negativeBtn.setTextColor(darkColor);
            }
        } else {
            if (lightColor != -1) {
                viewHolder.positiveBtn.setTextColor(lightColor);
                viewHolder.negativeBtn.setTextColor(lightColor);
            }
        }
    }

    /**
     在对话框上设置文本和文本颜色。
     */
    private void buildPermissionsLayout(){
        HashSet<String> tempSet=new HashSet<>();
        int currentVersion= Build.VERSION.SDK_INT;
        String permissionGroup;
        for (String permission: permissions){
            switch (currentVersion){
                case Build.VERSION_CODES.Q:
                    permissionGroup= PermissionMap.permissionMapOnR().get(permission);
                    break;
                case Build.VERSION_CODES.R:
                    permissionGroup=PermissionMap.permissionMapOnR().get(permission);
                    break;
                default:
                    try {
                        PermissionInfo permissionInfo=context.getPackageManager().getPermissionInfo(permission,0);
                        permissionGroup=permissionInfo.group;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        permissionGroup=null;
                    }
                    break;
            }
            if ((PermissionMap.allSpecialPermissions().contains(permission)&&!tempSet.contains(permission))
                    ||(permissionGroup!=null&&!tempSet.contains(permissionGroup))){
                switch (permission){
                    case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                        viewHolderItem.permissionText.setText(context.getString(R.string.permissionx_access_background_location));
                        viewHolderItem.permissionIcon.setImageResource(R.drawable.permissionx_ic_location);
                        break;
                    case Manifest.permission.SYSTEM_ALERT_WINDOW:
                        viewHolderItem.permissionText.setText(context.getString(R.string.permissionx_system_alert_window));
                        viewHolderItem.permissionIcon.setImageResource(R.drawable.permissionx_ic_alert);
                        break;
                    case Manifest.permission.WRITE_SETTINGS:
                        viewHolderItem.permissionText.setText(context.getString(R.string.permissionx_write_settings));
                        viewHolderItem.permissionIcon.setImageResource(R.drawable.permissionx_ic_setting);
                        break;
                    case Manifest.permission.MANAGE_EXTERNAL_STORAGE:
                        viewHolderItem.permissionText.setText(context.getString(R.string.permissionx_manage_external_storage));
                        viewHolderItem.permissionIcon.setImageResource(R.drawable.permissionx_ic_storage);
                        break;
                    default:

                        try {
                            viewHolderItem.permissionText.setText(context.getPackageManager().getPermissionGroupInfo(permissionGroup,0).labelRes);
                            viewHolderItem.permissionIcon.setImageResource(context.getPackageManager().getPermissionGroupInfo(permissionGroup,0).icon);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                if (isDarkTheme()){
                    if (darkColor!=-1){
                        viewHolder.positiveBtn.setTextColor(darkColor);
                        viewHolder.negativeBtn.setTextColor(darkColor);
                    }
                }else{
                    if (lightColor!=-1){
                        viewHolder.positiveBtn.setTextColor(lightColor);
                        viewHolder.negativeBtn.setTextColor(lightColor);
                    }
                }
            }
        }
    }

    /**
     *要显示的设置对话框窗口。在纵向和横向模式下控制不同的窗口大小。
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
     * 目前我们在黑暗主题与否。
     * @return
     */
    private boolean isDarkTheme() {
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }
    class ViewHolder {
        private Button positiveBtn;
        private Button negativeBtn;
        private TextView messageText;
        private LinearLayout permissionsLayout;
        private LinearLayout negativeLayout;
    }
    class ViewHolderItem{
        private TextView permissionText;
        private ImageView permissionIcon;
        private LinearLayout permission_item;
    }
}
