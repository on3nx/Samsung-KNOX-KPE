package com.arumaya.dopo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.container.KnoxContainerManager;

import java.util.List;

public class Utils {

    private TextView textView;
    private String TAG;

    public Utils(TextView view, String className) {
        textView = view;
        TAG = className;
    }

    /** Check Knox API level on device, if it does not meet minimum requirement, end user
     * cannot use the applciation */
    public void checkApiLevel(int apiLevel, final Context context)
    {
        if(EnterpriseDeviceManager.getAPILevel() < apiLevel) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            String msg = context.getResources().getString(R.string.api_level_message, EnterpriseDeviceManager.getAPILevel(),apiLevel);
            builder.setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("CLOSE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                    .show();

        } else {
            return;
        }
    }

    /** Log results to a textView in application UI */
    public void log(String text) {
        textView.append(text);
        textView.append("\n\n");
        textView.invalidate();
        Log.d(TAG,text);
    }

    /** Process the exception */
    public void processException(Exception ex, String TAG) {
        if (ex != null) {
            // present the exception message
            String msg = ex.getClass().getCanonicalName() + ": " + ex.getMessage();
            textView.append(msg);
            textView.append("\n\n");
            textView.invalidate();
            Log.e(TAG, msg);
        }
    }

    public int findMyContainerId() {
        int theId = -1;
        List<Integer> ids = KnoxContainerManager.getContainers();
        if (ids != null && !ids.isEmpty()) {
            theId = ids.get(0);
        }
        Log.d(TAG, "findMyContainerId: " + theId);
        return theId;
    }
}
