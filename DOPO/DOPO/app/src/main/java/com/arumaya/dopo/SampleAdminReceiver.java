package com.arumaya.dopo;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SampleAdminReceiver extends DeviceAdminReceiver {

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Device admin enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Device admin disabled");
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        showToast(context, "Profile Provisioning Complete");
        DevicePolicyManager myDevicePolicyMgr = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdmin = new ComponentName(context.getApplicationContext(), SampleAdminReceiver.class);
        myDevicePolicyMgr.setProfileName(mDeviceAdmin, "My New Work Profile");
        myDevicePolicyMgr.setProfileEnabled(mDeviceAdmin);
        myDevicePolicyMgr.enableSystemApp(mDeviceAdmin,"com.sec.android.app.camera");
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), SampleAdminReceiver.class);
    }

}
