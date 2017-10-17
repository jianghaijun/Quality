package com.sx.quality.activity;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.sx.quality.listener.PermissionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */

public class BaseActivity extends AppCompatActivity {
    private PermissionListener perListener;

    /**
     * 申请权限
     * @param permissions
     * @param perListener
     */
    public void requestAuthority(String[] permissions, PermissionListener perListener) {
        this.perListener = perListener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            perListener.agree();
        }
    }

    /**
     * 权限申请回调函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> refusePermissionList = new ArrayList<>();

                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            refusePermissionList.add(permissions[i]);
                        }
                    }

                    if (!refusePermissionList.isEmpty()) {
                        perListener.refuse(refusePermissionList);
                    } else {
                        perListener.agree();
                    }
                }
                break;
        }
    }
}
