package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class WriteBoardActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM=1;

    Uri registerImageUri; // 등록할 이미지 Uri

    MyAppData myAppData;
    MyAppService myAppService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");
        myAppService = new MyAppService();

        Intent intent = getIntent();
//        myAppData = (MyAppData)intent.getSerializableExtra("myAppData");
        myAppData = myAppService.readAllData(this);

        ImageView registerImageIV = findViewById(R.id.registerImageIV); // 이미지 등록 아이콘 이미지뷰
        // 이미지 등록 아이콘 이미지뷰 클릭 이벤트
        registerImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리 접근 권한
                int read_external_storage_PermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(read_external_storage_PermissionCheck == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 있음",Toast.LENGTH_SHORT).show();

                    // 권한이 있으면 갤러리 접근
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent,PICK_FROM_ALBUM);

                }else{
                    Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 없음",Toast.LENGTH_SHORT).show();
                    if(ActivityCompat.shouldShowRequestPermissionRationale(WriteBoardActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                    }else{
                        ActivityCompat.requestPermissions(WriteBoardActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                }

            }
        });

        Button nextBtn = findViewById(R.id.nextBtn); // 다음 버튼
        // 다음 버튼 클릭 이벤트
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerImageUri!=null){
                    Intent intent = new Intent(getApplicationContext(),WriteBoardSecondActivity.class);
                    intent.putExtra("registerImageUri",registerImageUri); // 등록할 이미지 URI 인텐트로 전달
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"사진을 등록해주세요",Toast.LENGTH_SHORT).show();
                }
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
    public void onBackPressed() {
        super.onBackPressed();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBackPressed() 호출");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if(data!=null){
                registerImageUri = data.getData(); // 이미지 Uri를 가지고 온다
                ImageView registerImageIV = findViewById(R.id.registerImageIV);
                registerImageIV.setImageURI(registerImageUri);
            }
        }
    }


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

} // WriteBoardActivity 클래스
