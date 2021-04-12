package com.example.futuregram3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.futuregram3.LoginActivity.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }
    /**
     * 메세지를 받았을 경우 그 메세지에 대하여 구현하는 부분입니다.
     * **/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("text").split("¡")[0];
        String chatRoomNo = remoteMessage.getData().get("text").split("¡")[1];
        String selectMemberNo = remoteMessage.getData().get("text").split("¡")[2];
        Log.w("fcm", "sendNotification() 메소드 호출 타이틀"+title+"   메시지 : "+message);
        if(remoteMessage!=null && remoteMessage.getData().size()>0){
            sendNotification(remoteMessage);
        }else{
            sendNotification(remoteMessage);
        }
    }





    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    /**
     * 구글 토큰을 얻는 값입니다.
     * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용됩니다.
     * **/
    @Override
    public void onNewToken(String token) {
        Log.w("fcm", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        // 앱 서버로 토큰을 보낼 때 재정의 해서 사용해라
    }

    public void sendNotification(RemoteMessage remoteMessage){

//        String title = remoteMessage.getNotification().getTitle();
//        String message = remoteMessage.getNotification().getBody().split("¡")[0];
//        String chatRoomNo = remoteMessage.getNotification().getBody().split("¡")[1];
//        String selectMemberNo = remoteMessage.getNotification().getBody().split("¡")[2];

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("text").split("¡")[0];
        String chatRoomNo = remoteMessage.getData().get("text").split("¡")[1];
        String selectMemberNo = remoteMessage.getData().get("text").split("¡")[2];

        MyAppService myAppService = new MyAppService();
        MyAppData myAppData = myAppService.readAllData(getApplicationContext());
        Member selectMember = myAppService.findMemberByMemberNo(myAppData,Integer.parseInt(selectMemberNo));

        Log.w("fcm", "sendNotification() 메소드 호출");
        Log.w("fcm", "sendNotification() 메소드 호출 타이틀"+title+"   메시지 : "+message);

        Intent intent = new Intent(getApplicationContext(),MessagingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("chatRoomNo",Integer.parseInt(chatRoomNo));
        intent.putExtra("selectMember",selectMember);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        /**
         * 오레오 버전부터는 Notification Channel이 없으면 푸시가 생성되지 않는 현상이 있습니다.
         * **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.w("fcm", "조건에 들어있는지");

            String channel = "채널";
            String channel_nm = "채널명";

            NotificationManager notichannel = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("채널에 대한 설명.");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
//            channelMessage.setShowBadge(false);
            channelMessage.setShowBadge(true);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            notichannel.createNotificationChannel(channelMessage);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId(channel)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
//                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(9999, notificationBuilder.build());

        }
    }

} // 서비스 클래스
