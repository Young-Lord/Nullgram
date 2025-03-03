package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.RawRes;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.camera.CameraController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;

import top.qwq2333.nullgram.utils.PermissionUtils;

public class BasePermissionsActivity extends Activity {
    public final static int REQUEST_CODE_GEOLOCATION = 2,
            REQUEST_CODE_EXTERNAL_STORAGE = 4,
            REQUEST_CODE_ATTACH_CONTACT = 5,
            REQUEST_CODE_CALLS = 7,
            REQUEST_CODE_OPEN_CAMERA = 20,
            REQUEST_CODE_VIDEO_MESSAGE = 150,
            REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR = 151,
            REQUEST_CODE_SIGN_IN_WITH_GOOGLE = 200,
            REQUEST_CODE_PAYMENT_FORM = 210,
            REQUEST_CODE_MEDIA_GEO = 211;

    protected int currentAccount = -1;

    protected boolean checkPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults == null) {
            grantResults = new int[0];
        }
        if (permissions == null) {
            permissions = new String[0];
        }

        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (requestCode == 104) {
            if (granted) {
                if (GroupCallActivity.groupCallInstance != null) {
                    GroupCallActivity.groupCallInstance.enableCamera();
                }
            } else {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("VoipNeedCameraPermission", R.string.VoipNeedCameraPermission));
            }
        } else if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE || requestCode == REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_folder, requestCode == REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR ? LocaleController.getString("PermissionNoStorageAvatar", R.string.PermissionNoStorageAvatar) :
                        LocaleController.getString("PermissionStorageWithHint", R.string.PermissionStorageWithHint));
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (requestCode == REQUEST_CODE_ATTACH_CONTACT) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_contacts, LocaleController.getString("PermissionNoContactsSharing", R.string.PermissionNoContactsSharing));
                return false;
            } else {
                ContactsController.getInstance(currentAccount).forceImportContacts();
            }
        } else if (requestCode == 3 || requestCode == REQUEST_CODE_VIDEO_MESSAGE) {
            boolean audioGranted = PermissionUtils.isRecordAudioPermissionGranted();
            boolean cameraGranted = PermissionUtils.isCameraPermissionGranted();
            if (requestCode == REQUEST_CODE_VIDEO_MESSAGE && (!audioGranted || !cameraGranted)) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraMicVideo", R.string.PermissionNoCameraMicVideo));
            } else if (!audioGranted) {
                showPermissionErrorAlert(R.raw.permission_request_microphone, LocaleController.getString("PermissionNoAudioWithHint", R.string.PermissionNoAudioWithHint));
            } else if (!cameraGranted) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraWithHint", R.string.PermissionNoCameraWithHint));
            } else {
                if (SharedConfig.inappCamera) {
                    CameraController.getInstance().initCamera(null);
                }
                return false;
            }
        } else if (requestCode == 18 || requestCode == 19 || requestCode == REQUEST_CODE_OPEN_CAMERA || requestCode == 22) {
            if (!granted) {
                showPermissionErrorAlert(R.raw.permission_request_camera, LocaleController.getString("PermissionNoCameraWithHint", R.string.PermissionNoCameraWithHint));
            }
        } else if (requestCode == REQUEST_CODE_GEOLOCATION) {
            NotificationCenter.getGlobalInstance().postNotificationName(granted ? NotificationCenter.locationPermissionGranted : NotificationCenter.locationPermissionDenied);
        } else if (requestCode == REQUEST_CODE_MEDIA_GEO) {
            NotificationCenter.getGlobalInstance().postNotificationName(granted ? NotificationCenter.locationPermissionGranted : NotificationCenter.locationPermissionDenied, 1);
        }
        return true;
    }

    protected AlertDialog createPermissionErrorAlert(@RawRes int animationId, String message) {
        return new AlertDialog.Builder(this)
                .setTopAnimation(animationId, AlertsCreator.PERMISSIONS_REQUEST_TOP_ICON_SIZE, false, Theme.getColor(Theme.key_dialogTopBackground))
                .setMessage(AndroidUtilities.replaceTags(message))
                .setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), (dialogInterface, i) -> {
                    try {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                        startActivity(intent);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                })
                .setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null)
                .create();
    }

    private void showPermissionErrorAlert(@RawRes int animationId, String message) {
        createPermissionErrorAlert(animationId, message).show();
    }
}
