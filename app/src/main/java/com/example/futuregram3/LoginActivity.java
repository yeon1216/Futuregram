package com.example.futuregram3;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.Signature;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.InstanceIdResult;
        import com.kakao.auth.ISessionCallback;
        import com.kakao.auth.Session;
        import com.kakao.network.ErrorResult;
        import com.kakao.usermgmt.LoginButton;
        import com.kakao.usermgmt.UserManagement;
        import com.kakao.usermgmt.callback.MeV2ResponseCallback;
        import com.kakao.usermgmt.response.MeV2Response;
        import com.kakao.util.exception.KakaoException;
        import com.kakao.util.helper.log.Logger;

        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.ArrayList;
        import java.util.List;

        import static com.kakao.util.helper.Utility.getPackageInfo;

public class LoginActivity extends AppCompatActivity {



    static String TAG="yeon1216  ";
    static LoginActivity loginActivity;

    EditText idInputET; // 아이디 입력 창
    EditText passInputET; // 비밀번호 입력 창
    LoginButton com_kakao_login; // 카카오 로그인 버튼

    MyAppData myAppData; // 내 앱의 데이터
    MyAppService myAppService; // 내 앱의 시스템

    private SessionCallback sessionCallback;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginActivity=LoginActivity.this;

        // fcm 토큰 받는 거
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//                        Log.w(TAG, "token : " + token);
//                        // Log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
////                        Log.d(TAG, msg);
////                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

        myAppService = new MyAppService(); // 내 앱의 서비스 객체 생성


//        myAppService.initData(this);




        myAppData = myAppService.readAllData(this); // 앱 강제로 초기화 하고 싶으면 이 코드 주석하고 실행하기

        if(myAppData.memberCount==-1){
            myAppData = myAppService.initData(this);
        }



//        int loginMemberNo = myAppService.autoLoginCheck(this);
        // 자동 로그인
        if(myAppData.loginMemberNo!=-1){
            // 로딩하고 실행하기
            Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

            // 로그인 화면 피니시는 꼭 해주기
            finish();
        }






        idInputET = findViewById(R.id.idInputET); // 아이디 입력 창
        passInputET = findViewById(R.id.passInputET);  // 비밀번호 입력 창

        Button loginBtn = findViewById(R.id.loginBtn); // 로그인 버튼
        // 로그인 버튼 클릭시 이벤트
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPhoneNum = idInputET.getText().toString().trim(); // 입력한 전화번호
                String password = passInputET.getText().toString().trim(); // 입력한 비밀번호
                int loginMemberNo = myAppService.login(myAppData,inputPhoneNum,password); // 로그인메소드 실행
                if(loginMemberNo==-1){
                    Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                }else{
                    myAppData.loginMemberNo= loginMemberNo;
                    Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish(); // 로그인 activity finish;
                }
            }
        });


        TextView joinTV = findViewById(R.id.joinTV); // 회원가입 텍스트뷰
        // 회원가입 텍스트뷰 클릭시 이벤트
        joinTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),JoinActivity.class);
//                intent.putExtra("myAppData",myAppData);
                startActivity(intent);
            }
        });

        TextView loginByPhoneTV = findViewById(R.id.loginByPhoneTV); // 휴대전화로 로그인하기 텍스트뷰
        // 휴대전화로 로그인하기 텍스트뷰 클릭시 이벤트
        loginByPhoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginByPhoneActivity.class);
//                intent.putExtra("myAppData",myAppData);
                startActivity(intent);
            }
        });

        ImageView loginByKakaoIV = findViewById(R.id.loginByKakaoIV); // 카카오로 로그인 텍스트뷰
        // 카카오로 로그인 텍스트뷰 클릭시 이벤트
        loginByKakaoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_kakao_login.performClick();
//                Toast.makeText(getApplicationContext(),"카카오로 로그인 (구현 예정)",Toast.LENGTH_SHORT).show();

            }
        });

        com_kakao_login = findViewById(R.id.com_kakao_login);

        // 카카오 로그인을 위해
        sessionCallback = new LoginActivity.SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

    } // onCreate 메소드

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.w("kakao","onActivityResult() 호출");
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // onActivityResult() 메소드

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG+this.getClass().getSimpleName(),"onStart() 호출");

//        DatabaseReference memberCountDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("memberCount");
//        memberCountDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                memberCount = Integer.parseInt(dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG+this.getClass().getSimpleName(),"onResume() 호출");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG+this.getClass().getSimpleName(),"onPause() 호출");
        myAppService.writeAllData(myAppData,this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG+this.getClass().getSimpleName(),"onDestroy() 호출");
        Session.getCurrentSession().removeCallback(sessionCallback);
    }


    // SessionCallback inner 클래스 : 세션 체크시 상태 변경에 따른 콜백. 세션이 오픈되었을 때, 세션이 닫혔을 때 세션 콜백을 넘기게 된다.
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.w("kakao","onSessionOpened() 호출");
            Log.w("kakao","onSessionOpened() 카카오 로그인 성공");
//            redirectSignupActivity();
            requestMe();
        } // onSessionOpened() 메소드 : accesstoken을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.w("kakao","onSessionOpenFailed() 호출");
            Log.w("kakao","onSessionOpenFailed()   "+exception.toString());
            if(exception!=null){
                Logger.e(exception);
            }
        } // onSessionOpenFailed() 메소드 : memory와 cache에 session 정보가 전혀 없는 상태. 일반적으로 로그인 버튼이 보이고 사용자가 클릭시 동의를 받아 access token 요청을 시도한다.

    } // SessionCallback inner 클래스

    private void requestMe(){
        ArrayList<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                super.onFailure(errorResult);
                Log.w("kakao", "requestMe onFailure message : " + errorResult.getErrorMessage());
            }

            @Override
            public void onFailureForUiThread(ErrorResult errorResult) {
                super.onFailureForUiThread(errorResult);
                Log.w("kakao", "requestMe onFailureForUiThread message : " + errorResult.getErrorMessage());
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.w("kakao", "requestMe onSessionClosed message : " + errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(MeV2Response result) {
                Log.w("kakao", "requestMe onSuccess message : " + result.getId() + " " + result.getNickname());
                boolean isMember = false;
                for(Member member : myAppData.members){
                    if(member.phone.equals(String.valueOf(result.getId()))){
                        isMember=true;
                    }
                }
                int loginMemberNo=-1;
                if(isMember){
                    Log.w("kakao", "로그인 하려는 유저가가 멤버인 경우");
                    for(Member member : myAppData.members){
                        if(member.phone.equals(String.valueOf(result.getId()))){
                            loginMemberNo = member.memberNo;
                        }
                    }
                    myAppData.loginMemberNo= loginMemberNo;
                    Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish(); // 로그인 activity finish;
                }else{
                    Log.w("kakao", "로그인 하려는 유저가가 멤버가 아닌 경우");
//                    Member joinMember = new Member(memberCount,String.valueOf(result.getId()),"카kaO123!@#","00000000000");

                    myAppService.join(myAppData,String.valueOf(result.getId()),"카kaO123!@#","00000000000",myAppData.memberCount,getApplicationContext());
//                    myAppData.memberCount++;
//                    myAppData.members.add(joinMember);
//                    myAppService.writeAllData(myAppData,LoginActivity.this);
//                    loginMemberNo = joinMember.memberNo;
                    myAppData.loginMemberNo= myAppData.memberCount-1;
                    Intent intent = new Intent(getApplicationContext(),NickNameInputActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish(); // 로그인 activity finish;
                }
            } // onSuccess() 메소드

        });

    } // requestMe() 메소드

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("kakao", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

} // LoginActivity 클래스

