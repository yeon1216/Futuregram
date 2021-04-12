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

import org.w3c.dom.Text;

public class PasswordUpdateActivity extends AppCompatActivity {

    EditText inputPhoneNumET; // 전화번호 입력창
    Button sendAuthenticationNumBtn; // 인증번호 보내기 버튼
    EditText inputAuthenticationET; // 인증번호 입력창
    TextView waitingTimeTV; // 남은시간 텍스트뷰
    Button authenticationNumOkBtn; // 인증번호 확인 버튼
    EditText passwordInputET; // 비밀번호 입력창
    EditText passwordCheckInputET; // 비밀번호 확인 입력창
    Button passwordUpdateBtn; // 비밀번호 수정 버튼

    MyAppService myAppService; // 앱 서비스
    MyAppData myAppData; // 앱 데이터
    Member loginMember; // 로그인 멤버

    String authenticationNum; // 인증번호
    String inputPhoneNum; // 입력한 전화번호

    PasswordUpdateActivityAsyncTask passwordUpdateActivityAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        myAppService = new MyAppService(); // 앱 서비스
        myAppData = myAppService.readAllData(this); // 앱 데이터
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo); // 로그인멤버

        inputPhoneNumET = findViewById(R.id.inputPhoneNumET); // 전화번호 입력창
        sendAuthenticationNumBtn = findViewById(R.id.sendAuthenticationNumBtn); // 인증번호 보내기 버튼
        // 인증번호 보내기 버튼 클릭시 이벤트
        sendAuthenticationNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loginMember.phone.equals(inputPhoneNumET.getText().toString())){
                    int send_sms_PermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
                    if(send_sms_PermissionCheck == PackageManager.PERMISSION_GRANTED){
                        authenticationNum = myAppService.makeAuthenticationNum(); // 인증번호 생성하기
//                        inputAuthenticationET.setText(authenticationNum); // << 여기서 리시버 생각해보기 >>
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("01029412360",null,"[futuregram]\n인증번호 : " +authenticationNum,null,null);
                        inputPhoneNum = inputPhoneNumET.getText().toString().trim();

                        // 여기에 asyncTask
                        // 이미 생성되어있던 어신크태스크 종료시키기
                        if(passwordUpdateActivityAsyncTask !=null){
                            passwordUpdateActivityAsyncTask.setIsSendAuthenticationnNumBtnClicked(true);
                            passwordUpdateActivityAsyncTask =null;
                        }

                        passwordUpdateActivityAsyncTask = new PasswordUpdateActivityAsyncTask(getApplicationContext(),waitingTimeTV);
                        passwordUpdateActivityAsyncTask.execute(0);

                    }else{
                        Toast.makeText(getApplicationContext(),"SMS 전송 권한 없음",Toast.LENGTH_SHORT).show();
                        if(ActivityCompat.shouldShowRequestPermissionRationale(PasswordUpdateActivity.this,Manifest.permission.SEND_SMS)){
                            Toast.makeText(getApplicationContext(),"SMS 전송 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                        }else{
                            ActivityCompat.requestPermissions(PasswordUpdateActivity.this,new String[]{Manifest.permission.SEND_SMS},1);
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"자신의 전화번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputAuthenticationET = findViewById(R.id.inputAuthenticationET); // 인증번호 입력창
        waitingTimeTV = findViewById(R.id.waitingTimeTV); // 남은시간 텍스트뷰
        authenticationNumOkBtn = findViewById(R.id.authenticationNumOkBtn); // 인증번호 확인 버튼
        // 인증번호 확인 버튼 클릭시 이벤트
        authenticationNumOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputPhoneNum!=null){
                    if(!passwordUpdateActivityAsyncTask.getIsTimeOk()){
                        Toast.makeText(getApplicationContext(),"인증 시간이 초과되었습니다. 전화번호를 다시 인증해주세요",Toast.LENGTH_SHORT).show();
                    }else{
                        if(authenticationNum.equals(inputAuthenticationET.getText().toString().trim())){
                            passwordUpdateActivityAsyncTask.setIsAuthenticationSuccess(true);
                            passwordInputET.setVisibility(View.VISIBLE);
                            passwordCheckInputET.setVisibility(View.VISIBLE);
                            passwordUpdateBtn.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(getApplicationContext(),"인증번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"전화번호를 인증해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
        passwordInputET = findViewById(R.id.passwordInputET); // 비밀번호 입력창
        passwordCheckInputET = findViewById(R.id.passwordCheckInputET); // 비밀번호 확인 입력창
        passwordUpdateBtn = findViewById(R.id.passwordUpdateBtn); // 비밀번호 수정 버튼
        // 비밀번호 수정 버튼 클릭시 이벤트
        passwordUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPassword = passwordInputET.getText().toString().trim();
                String inputPasswordCheck = passwordCheckInputET.getText().toString().trim();
                if(myAppService.checkPassword(inputPassword)){
                    if(myAppService.checkPasswordCheck(inputPassword,inputPasswordCheck)){
                        for(Member member : myAppData.members){
                            if(member.memberNo == loginMember.memberNo){
                                member.password = inputPassword;
                            }
                        }
                        myAppService.writeAllData(myAppData,getApplicationContext());
                        Toast.makeText(getApplicationContext(),"비밀번호가 수정되었습니다",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"비밀번호와 비밀번호 확인이 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"올바른 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });


    } // onCreate() 메소드


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
    } // 사용자가 sms 전송 권한을 승인 했는지 여부 체크하는 메소드

} // 비밀번호 수정 클래스


class PasswordUpdateActivityAsyncTask extends AsyncTask<Integer,Integer,Integer> {

    private Context context;
    private TextView waitingTimeTV;
    private boolean isSendAuthenticationnNumBtnClicked;
    private boolean isAuthenticationSuccess;
    private boolean isDestroyActivity;
    private boolean isTimeOk;

    public PasswordUpdateActivityAsyncTask(Context context, TextView waitingTimeTV) {
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
