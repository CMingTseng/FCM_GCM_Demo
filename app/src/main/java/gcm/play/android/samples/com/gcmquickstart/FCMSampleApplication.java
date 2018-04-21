package gcm.play.android.samples.com.gcmquickstart;

import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import fcm.android.play.google.quickstart.BuildConfig;
import fcm.android.play.google.quickstart.FirebaseBaseApplication;
import fcm.android.play.google.quickstart.FirebaseInstanceIDBaseService;

/**
 * Created by Neo on 2018/4/16.
 */

public class FCMSampleApplication extends FirebaseBaseApplication {
    private static final String TAG = "FCMSampleApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, FirebaseInstanceIDBaseService.class));
    }

    @Override
    protected void handleNotification(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            final String message = remoteMessage.getNotification().getBody();
            if (!TextUtils.isEmpty(message)) {
                Log.e(TAG, "push String : " + message);
                final Intent pushNotification = new Intent(C.PUSH_NOTIFICATION);
                pushNotification.putExtra(C.PUSH_NOTIFICATION_PAYLOAD, message);
                broadcastNotificationMessage(pushNotification);
            }
        }
    }

    @Override
    protected void handleDataMessage(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            final String jsonstring = remoteMessage.getData().toString();
            Log.e(TAG, "push Message : " + jsonstring);
            if (!TextUtils.isEmpty(jsonstring)) {
                final Intent pushNotification = new Intent(C.PUSH_NOTIFICATION);
                pushNotification.putExtra(C.PUSH_NOTIFICATION_BODY, jsonstring);
                broadcastNotificationMessage(pushNotification);
            }
        }
    }

    @Override
    protected void handleFCMTokenRefresh(String token) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "TODO something FCM Token Refresh : " + token);
        }
        //TODO this is Demo for notify Activity
        final Intent notifytokenrefresh = new Intent(FirebaseInstanceIDBaseService.FIREBASE_TOKEN_REFRESH);
        notifytokenrefresh.putExtra(FirebaseInstanceIDBaseService.FIREBASE_TOKEN, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(notifytokenrefresh);
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
