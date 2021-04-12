package com.example.futuregram3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class SampleLoginActivity extends AppCompatActivity {

    private SessionCallback sessionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_login);

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    } // onCreate() 메소드

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // onActivityResult() 메소드

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    } // onDestroy() 메소드

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        } // onSessionOpened() 메소드

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception!=null){
                Logger.e(exception);
            }
        }
    } // SessionCallback inner 클래스

    protected void redirectSignupActivity(){
        final Intent intent = new Intent(this,JoinActivity.class);
        startActivity(intent);
        finish();
    } // redirectSignupActivity() 메소드

} // SampleLoginActivity 클래스
