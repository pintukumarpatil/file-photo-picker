package com.github.pkp;
/**
 * Helper class for handling permissions.
 * @author Pintu Kumar Patil
 * @version 1.0
 * @since 3/Mar/2023
 */
public abstract class PermissionHandler {
    public abstract void onGranted();
    public abstract void onDeny();
}
