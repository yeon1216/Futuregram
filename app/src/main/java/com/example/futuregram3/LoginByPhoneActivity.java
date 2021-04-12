package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginByPhoneActivity extends AppCompatActivity {

    MyAppData myAppData; // 임시 데이터
    MyAppService myAppService; // 내 앱의 시스템

    String authenticationNum; // 인증번호
    String inputPhoneNum;

    EditText inputPhoneNumET; // 전화번호 입력 창
    EditText inputAuthenticationET; // 인증번호 입력 창
    TextView waitingTimeTV; // 인증번호 입력 남은시간 텍스트뷰
    Button sendAuthenticationnNumBtn; // 인증번호 보내기 버튼

    boolean isLoginSuccess; // 로그인성공여부 (기존 로그인 activity finish()를 위해)
//    boolean isDestroyActivity; // 화면이 디스트로이 됬는지 여부
//    boolean isSendAuthenticationnNumBtnClicked; // 인증번호 보내기 버튼 클릭 여부

    LoginByPhoneActivityAsyncTask loginByPhoneActivityAsyncTask; // 인증번호 타이머를 위한 asyncTask

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"SMS 전송 권한 승인함",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"SMS 전송 권한 거부됨",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_phone);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");

        isLoginSuccess=false; // 로그인성공여부 (기존 로그인 activity finish()를 위해)
//        isDestroyActivity=false; // 이 화면이 디스트로이 됬는지 확인하는 메소드
        myAppService = new MyAppService(); // 앱서비스 객체 생성
        myAppData =myAppService.readAllData(this);
        inputPhoneNumET = findViewById(R.id.inputPhoneNumET); // 전화번호 입력 창
        waitingTimeTV = findViewById(R.id.waitingTimeTV);  // 인증번호 입력 남은시간 텍스트뷰

        sendAuthenticationnNumBtn = findViewById(R.id.sendAuthenticationNumBtn); // 인증번호 발송 버튼
        // 인증번호 발송 버튼 클릭 이벤트
        sendAuthenticationnNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member tempMember = myAppService.findMemberByPhoneNum(myAppData,inputPhoneNumET.getText().toString().trim());
                if(tempMember != null){
                    // SMS 전송권한
                    int send_sms_PermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
                    if(send_sms_PermissionCheck == PackageManager.PERMISSION_GRANTED){

//                        Toast.makeText(getApplicationContext(),"SMS 전송 권한 있음",Toast.LENGTH_SHORT).show();

                        authenticationNum = myAppService.makeAuthenticationNum(); // 인증번호 생성하기

                        // 이미 생성되어있던 어신크태스크 종료시키기
                        if(loginByPhoneActivityAsyncTask !=null){
                            loginByPhoneActivityAsyncTask.setIsSendAuthenticationnNumBtnClicked(true);
                            loginByPhoneActivityAsyncTask =null;
                        }

                        loginByPhoneActivityAsyncTask = new LoginByPhoneActivityAsyncTask(LoginByPhoneActivity.this,waitingTimeTV);
                        loginByPhoneActivityAsyncTask.execute(0);


                        // inputAuthenticationET.setText(authenticationNum); // << 여기서 리시버 생각해보기 >>
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("01029412360",null,"[futuregram]\n인증번호 : "+authenticationNum,null,null);
                        inputPhoneNum = inputPhoneNumET.getText().toString().trim();


                    }else{
                        Toast.makeText(getApplicationContext(),"SMS 전송 권한 없음",Toast.LENGTH_SHORT).show();
                        if(ActivityCompat.shouldShowRequestPermissionRationale(LoginByPhoneActivity.this,Manifest.permission.SEND_SMS)){
                            Toast.makeText(getApplicationContext(),"SMS 전송 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                        }else{
                            ActivityCompat.requestPermissions(LoginByPhoneActivity.this,new String[]{Manifest.permission.SEND_SMS},1);
                        }
                    }


                }else{
                    Toast.makeText(LoginByPhoneActivity.this,"등록된 번호가 없습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        inputAuthenticationET = findViewById(R.id.inputAuthenticationET); // 인증번호 입력 창

        Button okBtn = findViewById(R.id.okBtn); // 확인 버튼
        // 확인 버튼 클릭시 이벤트
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputPhoneNum!=null){
//                    if(Integer.parseInt(authenticationNum)==-1){
                    Log.w("ttt  mAT.getIsTimeOk()", loginByPhoneActivityAsyncTask.getIsTimeOk()+"");
                    if(!loginByPhoneActivityAsyncTask.getIsTimeOk()){
                        Toast.makeText(getApplicationContext(),"인증 시간이 초과되었습니다. 전화번호를 다시 인증해주세요",Toast.LENGTH_SHORT).show();
                    }else{
                        if(authenticationNum.equals(inputAuthenticationET.getText().toString().trim())){
                            int loginMemberNo = myAppService.loginByPhone(myAppData,inputPhoneNumET.getText().toString().trim()); // 휴대전화로 로그인하기 메소드 실행
                            isLoginSuccess=true;
                            loginByPhoneActivityAsyncTask.setIsLoginSuccess(true);
                            myAppData.loginMemberNo = loginMemberNo; // 로그인 멤버 번호 myAppData에 저장
                            Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            myAppService.writeAllData(myAppData,LoginByPhoneActivity.this);
                            startActivity(intent);

                            finish(); // 로그인을 성공했으니 휴대폰 로그인 액티비티 피니시
                        }else{
                            Toast.makeText(getApplicationContext(),"인증번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"전화번호를 인증해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView loginTV = findViewById(R.id.loginTV); // 로그인 텍스트뷰
        // 로그인 텍스트뷰 클릭 이벤트
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

    } // onCreate 메소드

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStart() 호출");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onResume() 호출");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
        if(isLoginSuccess){
            LoginActivity loginActivity = LoginActivity.loginActivity;
            loginActivity.finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStop() 호출");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onDestroy() 호출");
        if(loginByPhoneActivityAsyncTask!=null){
            loginByPhoneActivityAsyncTask.setIsDestroyActivity(true);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onRestart() 호출");
    }

} // 클래스


class LoginByPhoneActivityAsyncTask extends AsyncTask<Integer,Integer,Integer>{

    private Context context;
    private TextView waitingTimeTV;
    private boolean isSendAuthenticationnNumBtnClicked;
    private boolean isLoginSuccess;
    private boolean isDestroyActivity;
    private boolean isTimeOk;

    public LoginByPhoneActivityAsyncTask(Context context, TextView waitingTimeTV) {
        super();
        this.context = context;
        this.waitingTimeTV = waitingTimeTV;
        this.isSendAuthenticationnNumBtnClicked = false;
        this.isLoginSuccess = false;
        this.isDestroyActivity = false;
        this.isTimeOk = true;
    }

    public void setIsSendAuthenticationnNumBtnClicked(boolean isSendAuthenticationnNumBtnClicked){
        this.isSendAuthenticationnNumBtnClicked = isSendAuthenticationnNumBtnClicked;
    }
    public void setIsLoginSuccess(boolean isLoginSuccess){
        this.isLoginSuccess = isLoginSuccess;
    }
    public void setIsDestroyActivity(boolean isDestroyActivity){
        this.isDestroyActivity = isDestroyActivity;
    }
    public boolean getIsTimeOk(){
        return this.isTimeOk;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        int waitTime = 15;
        for(int i=0;i<15;i++){
            try{Thread.sleep(1000);}catch (InterruptedException e){}
            if(isLoginSuccess || isDestroyActivity){
                publishProgress(-1);
                return 0;
            }
            if(isSendAuthenticationnNumBtnClicked){
                return -1;
            }
            waitTime--;
            publishProgress(waitTime);
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values[0]==0){
            waitingTimeTV.setVisibility(View.INVISIBLE);
            Toast.makeText(context.getApplicationContext(),"인증번호 입력시간이 종료되었습니다.",Toast.LENGTH_SHORT).show();
        }else if(values[0]==-1){
            waitingTimeTV.setVisibility(View.INVISIBLE);
        }else{
            waitingTimeTV.setVisibility(View.VISIBLE);
            waitingTimeTV.setText("남은시간 : "+values[0]+"초");
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        this.isTimeOk =false;
        Log.w("ttt","asyncTask 종료됨?? 죽은건 아닌거같어");
    }

}