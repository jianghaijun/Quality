package com.sx.quality.listener;

import java.util.List;

/**
 * Created by jack on 2017/10/11.
 */
public interface PermissionListener {
    void agree();

    void refuse(List<String> refusePermission);
}
