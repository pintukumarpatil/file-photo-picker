package com.github.pkp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * @author Pintu Kumar Patil
 * @version 1.0
 * @updated on  2/March/23
 * @since 24/July/19
 */
public class PermissionsChecker {

    static int mRequestCode;
    private static PermissionHandler mHandler;

    /**
     * @param context     on an application
     * @param permissions that we need to get permissions
     * @return un granted permissions
     */
    private static ArrayList<String> getDenyPermissions(@NonNull Context context, @NonNull String[] permissions) {
        ArrayList<String> denyPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(permission);
            }
        }
        if (denyPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && denyPermissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE) && Environment.isExternalStorageManager()) {
                denyPermissions.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        return denyPermissions;
    }

    /**
     * Method handling the permissions to getting camera and storage access
     *
     * @param context activity context
     * @return boolean variable
     */
    public static boolean isAccessPermissionGranted(Context context, @NonNull String[] permissions) {
        boolean status = true;
        ArrayList<String> denyPermissions = getDenyPermissions(context, permissions);
        if (denyPermissions.size() > 0) {
            status = false;
        }
        return status;
    }

    /**
     * Method handling the permissions to isCameraAccessed
     *
     * @param context activity context
     * @return boolean variable
     */
    public static boolean isCameraAccessed(Context context) {
        boolean status = true;
        ArrayList<String> denyPermissions = getDenyPermissions(context, new String[]{Manifest.permission.CAMERA});
        if (denyPermissions.size() > 0) {
            status = false;
        }
        return status;
    }
    /**
     * Method handling the permissions to isCameraAccessed
     *
     * @param context activity context
     * @return boolean variable
     */
    public static boolean isStorageAccessed(Context context) {
        boolean status = true;
        ArrayList<String> denyPermissions = getDenyPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        if (denyPermissions.size() > 0) {
            status = false;
        }
        return status;
    }
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mRequestCode == requestCode && mHandler != null) {
            if (grantResults.length > 0) {
                boolean allPermissionGranted = true;
                for (int grantResult : grantResults) {
                    allPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
                }
                if (allPermissionGranted) {
                    mHandler.onGranted();
                } else {
                    mHandler.onDeny();
                }
            } else {
                mHandler.onDeny();
            }
        }
    }

    /**
     * @param activity    of current screen
     * @param requestCode of permission
     * @param permissions that we need to granted
     * @param handler     of permission
     */
    public static void check(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull PermissionHandler handler) {
        mRequestCode = requestCode;
        mHandler = handler;
        ArrayList<String> denyPermissions = getDenyPermissions(activity, permissions);
        if (denyPermissions.size() == 0) {
            if (mHandler != null)
                mHandler.onGranted();
        } else {
            ActivityCompat.requestPermissions(activity, denyPermissions.toArray(new String[0]), requestCode);
        }
    }
}
