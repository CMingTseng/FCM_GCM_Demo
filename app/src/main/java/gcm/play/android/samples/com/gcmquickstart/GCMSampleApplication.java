package gcm.play.android.samples.com.gcmquickstart;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import gcm.android.play.google.quickstart.GCMBaseApplication;
import gcm.android.play.google.quickstart.GCMInstanceIDListenerService;
import gcm.android.play.google.quickstart.GCMMessagingListenerService;

/**
 * Created by Neo on 2018/4/16.
 */

public class GCMSampleApplication extends GCMBaseApplication {
    private static final String TAG = "GCMBaseApplication";
    private static final String GCM_SENDER_ID = "2222-55555"; // Project id from Google Developers Console
    private final BroadcastReceiver mTriggerRegisIntentServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GCMInstanceIDListenerService.GCM_TOKEN_REFRESH)) {
                Log.i(TAG, "Get token refresh broadcast to start get new token & reg ");
                if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                    startService(new Intent(context, RegistrationIntentService.class));
                /* or here get new token   */
//                try {
//                    // [START register_for_gcm]
//                    // Initially this call goes out to the network to retrieve the token, subsequent calls
//                    // are local.
//                    // GCM_SENDER_ID(the Sender ID) is typically derived from google-services.json.
//                    // See https://developers.google.com/cloud-messaging/android/start for details on this file.
//                    // [START get_token]
//                    final InstanceID instanceID = InstanceID.getInstance(context);
//                    final String refreshedToken = instanceID.getToken(GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                    // [END get_token]
//                    Log.i(TAG, "GCM Registration Token: " + refreshedToken);
//                    //            // Saving reg id to shared preferences
//                    getSharedPreferences(GCMInstanceIDListenerService.GCMINFO, Context.MODE_MULTI_PROCESS).edit().putString(GCMInstanceIDListenerService.GCM_TOKEN_REGID, refreshedToken).commit();
//
//                    // [END register_for_gcm]
//                } catch (Exception e) {
//                    Log.d(TAG, "Failed to complete token refresh", e);
//                }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter triggerRegisIntentServicefilter = new IntentFilter();
        triggerRegisIntentServicefilter.addAction(GCMInstanceIDListenerService.GCM_TOKEN_REFRESH);
        registerReceiver(mTriggerRegisIntentServiceReceiver, triggerRegisIntentServicefilter);
    }

    @Override
    protected void handleNotification(Bundle remoteMessage) {
        if (remoteMessage != null) {
            final Intent pushNotification = new Intent(C.PUSH_NOTIFICATION);
            pushNotification.putExtra(C.PUSH_NOTIFICATION_PAYLOAD, remoteMessage.getBundle(GCMMessagingListenerService.PUSH_REMOTEMESSAGE));
            broadcastNotificationMessage(pushNotification);
        }
    }

    @Override
    protected void handleDataMessage(Bundle remoteMessage) {
        if (remoteMessage != null) {
            final Intent pushNotification = new Intent(C.PUSH_NOTIFICATION);
            pushNotification.putExtra(C.PUSH_NOTIFICATION_BODY, remoteMessage.getBundle(GCMMessagingListenerService.PUSH_REMOTEMESSAGE));
            broadcastNotificationMessage(pushNotification);
        }
    }

    private void broadcastNotificationMessage(Intent notify) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(notify);
        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/notification");
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
