package com.example.futuregram3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TransientWritingService extends Service {
    public TransientWritingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 메소드 실행");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStartCommand() 메소드 실행");

        if(intent==null){
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStartCommand() 메소드 : Service.START_STICKY 리턴");
            return Service.START_STICKY;
        }else{
            Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStartCommand() 메소드 : 임시저장글을 받아 홈액티비티에 전달");
            String transientStorageWriteBoardET = intent.getStringExtra("transientStorageWriteBoardET");
            Intent transientStorageWriteBoardETIntent = new Intent(getApplicationContext(),HomeActivity.class);
            transientStorageWriteBoardETIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            transientStorageWriteBoardETIntent.putExtra("transientStorageWriteBoardET",transientStorageWriteBoardET);
            startActivity(transientStorageWriteBoardETIntent);
            stopSelf(); // 서비스 종료
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onDestroy() 메소드 호출");
    }
}
