package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    EditText inputPhoneNumET; // 전화번호 입력 창
    EditText inputAuthenticationET; // 인증번호 입력 창
    TextView waitingTimeTV; // 인증번호 입력 남은시간 텍스트뷰

    String authenticationNum; // 인증번호

    MyAppService myAppService; // 내 앱의 시스템
    MyAppData myAppData; // 내 앱의 데이터

    String inputPhoneNum; // 입력한 폰 번호
    boolean isDestroyActivity; // 화면이 디스트로이 됬는지 여부

    JoinActivityAsyncTask joinActivityAsyncTask; // 회원가입화면 asyncTask




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");

        isDestroyActivity=false; // 화면이 디스트로이 됬는지 여부

        myAppService = new MyAppService(); // 앱시스템 객체 생성

        Intent intent = getIntent();
        myAppData = myAppService.readAllData(this);

        inputPhoneNumET = findViewById(R.id.inputPhoneNumET); // 전화번호 입력 창
        waitingTimeTV = findViewById(R.id.waitingTimeTV);  // 인증번호 입력 남은시간 텍스트뷰

        Button sendAuthenticationNumBtn = (Button)findViewById(R.id.sendAuthenticationNumBtn); // 인증번호 보내기 버튼
        // 인증번호 보내기 버튼 클릭시 이벤트
        sendAuthenticationNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전화번호가 올바른지 확인하고 이미 계정이 있는지 확인하는 변수와 메소드 (0: 회원가입 가능, 1: 이미 계정이 있음, 2: 전화번호가 이상함)
                int phoneNumCheck = myAppService.checkThisPhoneNum(myAppData, inputPhoneNumET.getText().toString().trim());
                if(phoneNumCheck==0){
                    int send_sms_PermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
                    if(send_sms_PermissionCheck == PackageManager.PERMISSION_GRANTED){
                        authenticationNum = myAppService.makeAuthenticationNum(); // 인증번호 생성하기
//                        inputAuthenticationET.setText(authenticationNum); // << 여기서 리시버 생각해보기 >>
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("01029412360",null,"[futuregram]\n인증번호 : " +authenticationNum,null,null);
                        inputPhoneNum = inputPhoneNumET.getText().toString().trim();

                        // 여기에 asyncTask
                        // 이미 생성되어있던 어신크태스크 종료시키기
                        if(joinActivityAsyncTask !=null){
                            joinActivityAsyncTask.setIsSendAuthenticationnNumBtnClicked(true);
                            joinActivityAsyncTask =null;
                        }

                        joinActivityAsyncTask = new JoinActivityAsyncTask(JoinActivity.this,waitingTimeTV);
                        joinActivityAsyncTask.execute(0);

                    }else{
                        Toast.makeText(getApplicationContext(),"SMS 전송 권한 없음",Toast.LENGTH_SHORT).show();
                        if(ActivityCompat.shouldShowRequestPermissionRationale(JoinActivity.this,Manifest.permission.SEND_SMS)){
                            Toast.makeText(getApplicationContext(),"SMS 전송 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                        }else{
                            ActivityCompat.requestPermissions(JoinActivity.this,new String[]{Manifest.permission.SEND_SMS},1);
                        }
                    }

                }else if(phoneNumCheck==1){
                    Toast.makeText(getApplicationContext(),"해당 번호의 계정이 이미 있습니다.",Toast.LENGTH_SHORT).show();
                }else if(phoneNumCheck==2){
                    Toast.makeText(getApplicationContext(),"올바른 전화번호를 입력해주세요",Toast.LENGTH_SHORT).show();
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
                    if(!joinActivityAsyncTask.getIsTimeOk()){
                        Toast.makeText(getApplicationContext(),"인증 시간이 초과되었습니다. 전화번호를 다시 인증해주세요",Toast.LENGTH_SHORT).show();
                    }else{
                        if(authenticationNum.equals(inputAuthenticationET.getText().toString().trim())){
                            joinActivityAsyncTask.setIsAuthenticationSuccess(true);
                            Intent intent = new Intent(getApplicationContext(),JoinSecondActivity.class);
                            intent.putExtra("inputPhoneNum",inputPhoneNum); // 인텐트에 입력한 전화번호 넣기
                            startActivity(intent);
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
        myAppService.readAllData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");
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
        isDestroyActivity=true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onRestart() 호출");
    }

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

} // JoinActivity 클래스


class JoinActivityAsyncTask extends AsyncTask<Integer,Integer,Integer>{

    private Context context;
    private TextView waitingTimeTV;
    private boolean isSendAuthenticationnNumBtnClicked;
    private boolean isAuthenticationSuccess;
    private boolean isDestroyActivity;
    private boolean isTimeOk;

    public JoinActivityAsyncTask(Context context, TextView waitingTimeTV) {
        super();
        this.context = context;
        this.waitingTimeTV = waitingTimeTV;
        this.isSendAuthenticationnNumBtnClicked = false;
        this.isAuthenticationSuccess = false;
        this.isDestroyActivity = false;
        this.isTimeOk = true;
    }

    public void setIsSendAuthenticationnNumBtnClicked(boolean isSendAuthenticationnNumBtnClicked){
        this.isSendAuthenticationnNumBtnClicked = isSendAuthenticationnNumBtnClicked;
    }
    public void setIsAuthenticationSuccess(boolean isAuthenticationSuccess){
        this.isAuthenticationSuccess = isAuthenticationSuccess;
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
            if(isAuthenticationSuccess || isDestroyActivity){
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