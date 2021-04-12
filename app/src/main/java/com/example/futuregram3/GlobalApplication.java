package com.example.futuregram3;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    // volitile : 이 변수를 메인 메모리에 저장하겠다는 의미 (여러 쓰레드가 공유하는 값이므로 volatile 사용)
    private static volatile GlobalApplication globalApplication = null;
//    private static volatile Activity currentActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();

        globalApplication = this;

        KakaoSDK.init(new KakaoSDKAdapter());

//        ...
    } // onCreate() 메소드
//    ...

    public static GlobalApplication getGlobalApplicationContext() {
        return globalApplication;
    }

//    public static void setCurrentActivity(Activity currentActivity){
//        GlobalApplication.currentActivity = currentActivity;
//    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                /** AuthType을 클릭하면 로그인하고 싶은 타입을 볼수있다.**/
                // 출처: https://kwon8999.tistory.com/entry/안드로이드-SNS-로그인KaKao [Kwon's developer]

                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        } // getSessionConfig() 메소드

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        } // getApplicationConfig() 메소드

    } // KakaoSDKAdapter 클래스

} //  GlobalApplication 클래스