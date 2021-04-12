package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileUpdateActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM=1;
    Uri profileImageUri; // 프로필 이미지 Uri
    ImageView profileImageIV; // 프로필 이미지 이미지뷰
    EditText profileNickNameET; // 프로필 닉네임입력 창
    TextView applyDefaultProfileImageTV; // 기본프로필적용 텍스트뷰

    MyAppData myAppData; // 앱 데이터
    MyAppService myAppService; // 앱 서비스
    Member loginMember; // 현재 로그인중인 멤버

    boolean isUpdateOk=false; // 수정해도 괜찮은지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");
        myAppService = new MyAppService();
    } // onCreate() 메소드



    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStart() 호출");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onResume() 호출");

        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData,myAppData.loginMemberNo);

        profileNickNameET = findViewById(R.id.profileNickNameET); // 프로필 닉네임 입력 창
        profileNickNameET.setText(loginMember.nickName); // 닉네임 적용

        profileImageIV = findViewById(R.id.profileImageIV); // 프로필 이미지 이미지뷰
        if(loginMember.profileImage.substring(0,1).equals("c")){
            profileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            profileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage)); // 프로필 사진 적용
        }

        // 프로필 이미지 이미지뷰 클릭시 이벤트
        profileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리 접근 권한
                int read_external_storage_PermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(read_external_storage_PermissionCheck == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 있음",Toast.LENGTH_SHORT).show();
                    // 갤러리 접근
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent,PICK_FROM_ALBUM);
                }else{
                    Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 없음",Toast.LENGTH_SHORT).show();
                    if(ActivityCompat.shouldShowRequestPermissionRationale(ProfileUpdateActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                    }else{
                        ActivityCompat.requestPermissions(ProfileUpdateActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                }
            }
        });

        applyDefaultProfileImageTV = findViewById(R.id.applyDefaultProfileImageTV); // 기본 프로필 적용 텍스트뷰
        applyDefaultProfileImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImageIV.setImageResource(R.drawable.default_profile);
                loginMember.profileImage = String.valueOf(R.drawable.default_profile);
            }
        });

        Button okBtn = findViewById(R.id.okBtn); // 확인 버튼
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myAppService.checkNickNameLength(profileNickNameET.getText().toString().trim())){
                    if(myAppService.checkNickNameInputOnlyNumberAndAlphabet(profileNickNameET.getText().toString().trim())){
                        if(!myAppService.checkDuplicateNickName(myAppData,profileNickNameET.getText().toString().trim())){
                            isUpdateOk=true;
                        }else{
                            if(loginMember.nickName.equals(profileNickNameET.getText().toString().trim())){
                                // 자신의 닉네임인 경우
                                isUpdateOk=true;
                            }else{
                                Toast.makeText(getApplicationContext(),"이미 사용중인 닉네임입니다",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"닉네임은 영소문자와 숫자만 가능합니다",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"닉네임은 3 ~ 8자로 입력해주세요",Toast.LENGTH_SHORT).show();
                }

                if(isUpdateOk){
                    loginMember.nickName = profileNickNameET.getText().toString().trim();
                    Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onPause() 호출");

        myAppService.writeAllData(myAppData,this);
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onRestart() 호출");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onActivityResult() 호출");
        if (requestCode == PICK_FROM_ALBUM) {
            if(data!=null){
                profileImageUri = data.getData(); // 이미지 Uri를 가지고 온다
                Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onActivityResult() : profileImageUri : "+profileImageUri);
                Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onActivityResult() : profileImageUri.toString() : "+profileImageUri.toString());
                profileImageIV.setImageURI(profileImageUri);
                loginMember.profileImage = profileImageUri.toString();
                myAppService.writeMemberData(loginMember,this);
            }
        }
    } // onActivityResult 메소드

    // 권한 허용 응답 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 승인함",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 거부됨",Toast.LENGTH_SHORT).show();
            }
        }
    }

} // ProfileUpdateActivity 클래스
