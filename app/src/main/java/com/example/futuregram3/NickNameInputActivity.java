package com.example.futuregram3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class NickNameInputActivity extends AppCompatActivity {

    EditText inputNickNameET;

    MyAppData myAppData; // 앱의 데이터
    MyAppService myAppService; // 내 앱의 시스템
    Member loginMember; // 현재 로그인 멤버

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name_input);

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);
        int loginMemberNo = myAppData.loginMemberNo;
        loginMember = myAppService.findMemberByMemberNo(myAppData,loginMemberNo);

        inputNickNameET = findViewById(R.id.inputNickNameET);
        Button okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAppService.checkNickNameLength(inputNickNameET.getText().toString().trim())){
                    if(myAppService.checkNickNameInputOnlyNumberAndAlphabet(inputNickNameET.getText().toString().trim())){
                        if(!myAppService.checkDuplicateNickName(myAppData,inputNickNameET.getText().toString().trim())){
                            Log.w("kakao",loginMember.nickName);
                            loginMember.nickName = inputNickNameET.getText().toString().trim();
                            myAppService.writeAllData(myAppData,NickNameInputActivity.this);
                            Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"이미 사용중인 닉네임입니다",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"닉네임은 영소문자와 숫자만 가능합니다",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"닉네임은 3 ~ 8자로 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button makeRandomNickNameBtn = findViewById(R.id.makeRandomNickNameBtn);
        makeRandomNickNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempNickName="";

                while (true){
                    Random r = new Random();
                    tempNickName = r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10)+""+r.nextInt(10)+"_k";
                    if(!myAppService.checkDuplicateNickName(myAppData,tempNickName)){
                        break;
                    }
                }

                loginMember.nickName = tempNickName;
                myAppService.writeAllData(myAppData,NickNameInputActivity.this);

                Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

    } // onCreate() 메소드
} // 클래스
