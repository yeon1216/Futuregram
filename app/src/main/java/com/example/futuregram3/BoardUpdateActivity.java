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

public class BoardUpdateActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM=1;

    MyAppService myAppService; // 앱 서비스
    MyAppData myAppData; // 앱 데이터
    int tempBoardNo; // 현재 수정 할 게시글 번호
    Board tempBoard; // 현재 수정 할 게시글

    ImageView registerImageIV; // 등록한 이미지 이미지뷰
    EditText boardContentET; // 게시글 내용 수정 창
    Button boardUpdateBtn; // 게시글 수정하기 버튼
    Button cancelBtn; // 취소 버튼

    Uri registerImageUri; // 갤러리에서 가지고온 이미지 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_update);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);

        Intent intent = getIntent();
        tempBoardNo = intent.getIntExtra("tempBoardNo",-1);
        tempBoard = myAppService.findBoardByBoardNo(myAppData,tempBoardNo);
        final boolean isBoardSingleActivity = intent.getBooleanExtra("isBoardSingleActivity",false); // 싱글 게시글 화면으로부터의 수정인지 확인

        registerImageIV = findViewById(R.id.registerImageIV); // 등록한 이미지 이미지뷰
        if(tempBoard.boardImage.substring(0,1).equals("c")){
            registerImageIV.setImageURI(Uri.parse(tempBoard.boardImage));
        }else{
            registerImageIV.setImageResource(Integer.parseInt(tempBoard.boardImage));
        }
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
                    if(ActivityCompat.shouldShowRequestPermissionRationale(BoardUpdateActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(),"외부 저장소 읽기 권한 설명이 필요함",Toast.LENGTH_SHORT).show();
                    }else{
                        ActivityCompat.requestPermissions(BoardUpdateActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                }
            }
        });
        boardContentET = findViewById(R.id.boardContentET); // 게시글 내용 수정 창
        boardContentET.setText(tempBoard.boardContent);

        boardUpdateBtn = findViewById(R.id.boardUpdateBtn); // 게시글 수정하기 버튼
        // 게시글 수정하기 버튼 클릭시 이벤트
        boardUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerImageUri!=null){
                    tempBoard.boardImage = registerImageUri.toString();
                }
                tempBoard.boardContent = boardContentET.getText().toString();
                myAppService.updateBoardData(tempBoard,BoardUpdateActivity.this);
                if(isBoardSingleActivity){
                    Intent intent1 = new Intent(getApplicationContext(),BoardSingleActivity.class);
                    intent1.putExtra("tempBoard",tempBoard);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                }else{
                    finish(); // activity 종료
                }
            }
        });

        cancelBtn = findViewById(R.id.cancelBtn); // 취소 버튼
        // 취소 버튼 클릭시 이벤트
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            registerImageUri = data.getData(); // 이미지 Uri를 가지고 온다
            registerImageIV.setImageURI(registerImageUri);

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


} // 클래스
