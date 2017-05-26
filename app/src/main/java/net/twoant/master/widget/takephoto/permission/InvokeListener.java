package net.twoant.master.widget.takephoto.permission;


import net.twoant.master.widget.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
