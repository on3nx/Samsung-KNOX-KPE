package com.arumaya.dopo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.knox.EnterpriseKnoxManager;
import com.samsung.android.knox.container.KnoxContainerManager;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.restriction.RestrictionPolicy;

import static android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE;
import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME;
import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //...called when the activity is starting. This is where most initialization should go.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView LogView = findViewById(R.id.logview_id);
        LogView.setMovementMethod(new ScrollingMovementMethod());
        mUtils = new Utils(LogView, TAG);

        // Check if device supports Knox SDK
        mUtils.checkApiLevel(24, this);

        Button CreateAndroidProfile = findViewById(R.id.CreateAndroidProfilebtn);
        CreateAndroidProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                createAndroidProfile();
            }
        });
        Button ActivateLicencebtn = findViewById(R.id.ActivateLicencebtn);
        ActivateLicencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateLicence();
            }
        });
        Button DeActivateLicencebtn = findViewById(R.id.DeActivateLicencebtn);
        DeActivateLicencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivateLicence();
            }
        });
        Button ToggleCamerabtn = findViewById(R.id.ToggleCamerabtn);
        ToggleCamerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                toggleCameraState();
            }
        });

        // Check if the application is a Profile Owner
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean isProfileOwner = devicePolicyManager.isProfileOwnerApp(getPackageName());
        if(isProfileOwner){
            ToggleCamerabtn.setEnabled(true);
            CreateAndroidProfile.setEnabled(false);
        } else {
            ActivateLicencebtn.setEnabled(false);
            DeActivateLicencebtn.setEnabled(false);
            ToggleCamerabtn.setEnabled(false);
        }
    }

    private void createAndroidProfile() {
        Activity provisioningActivity = this;
        // Set up the provisioning intent
        Intent provisioningIntent = new Intent(ACTION_PROVISION_MANAGED_PROFILE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            provisioningIntent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, SampleAdminReceiver.getComponentName(provisioningActivity));
        } else {
            provisioningIntent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, getPackageName());
        }

        if (provisioningIntent.resolveActivity(provisioningActivity.getPackageManager()) != null) {
            startActivityForResult(provisioningIntent, 1);
            provisioningActivity.finish();
        } else {
            Toast.makeText(provisioningActivity, R.string.provisioning_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Note that embedding your license key in code is unsafe and is done here for
     * demonstration purposes only.
     * Please visit https://seap.samsung.com/license-keys/about. for more details about license
     * keys.
     */
    private void activateLicence() {
        mUtils.log("Activating: " + Constants.LICENSE_KEY);

        // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager KPEManager = KnoxEnterpriseLicenseManager.getInstance(this);

        try {
            // KPE License Activation TODO Add KPE license key to Constants.java
            KPEManager.activateLicense(Constants.LICENSE_KEY);
            mUtils.log(getResources().getString(R.string.license_progress));
        } catch (Exception e) {
            mUtils.processException(e, TAG);
        }
    }

    private void deActivateLicence() {
        mUtils.log("De activating: " + Constants.LICENSE_KEY);

        // Instantiate the EnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager licenseManager = KnoxEnterpriseLicenseManager.getInstance(this);

        try {
            // License Activation TODO Add license key to Constants.java
            licenseManager.deActivateLicense(Constants.LICENSE_KEY);
            mUtils.log(getResources().getString(R.string.deactivate_license_progress));
        } catch (Exception e) {
            mUtils.processException(e,TAG);
        }
    }

    private void toggleCameraState() {
        EnterpriseKnoxManager ekm = EnterpriseKnoxManager.getInstance(this);
        KnoxContainerManager kcm = ekm.getKnoxContainerManager(mUtils.findMyContainerId());
        RestrictionPolicy restrictionPolicy = kcm.getRestrictionPolicy();
        boolean cameraEnabled = restrictionPolicy.isCameraEnabled(false);
        try { // Disable camera. Other applications that use the camera cannot use it.
            boolean result = restrictionPolicy.setCameraState(!cameraEnabled);
            if (result) { mUtils.log(getResources().getString(R.string.camera_state, !cameraEnabled)); }
        } catch (Exception e) {
            mUtils.processException(e,TAG);
        }
    }
}
