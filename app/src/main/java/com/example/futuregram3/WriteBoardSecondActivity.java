package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WriteBoardSecondActivity extends AppCompatActivity {

    MyAppService myAppService; // 앱 서비스
    MyAppData myAppData; // 앱 데이터
    Member loginMember; // 로그인 멤버
    Uri registerImageUri; // 이미지 uri
    String boardContent; // 작성 글
    String searchLocation; // 검색장소


    EditText boardContentET; // 글작성 창
    TextView registerLocationTV; // 장소 등록 텍스트뷰
    TextView searchLocationTV; // 검색장소 텍스트뷰


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board_second);
        Log.w("map2","onCreate() 호출");

        Intent intent = getIntent();
        registerImageUri = intent.getParcelableExtra("registerImageUri");
        boardContent = intent.getStringExtra("boardContent");
        searchLocation = intent.getStringExtra("searchLocation");
//        Log.w("map2","searchLocation : "+searchLocation);

        myAppService = new MyAppService(); // 앱 서비스 객체 생성
        myAppData = myAppService.readAllData(this);
        int loginMemberNo = myAppData.loginMemberNo;
        loginMember = myAppService.findMemberByMemberNo(myAppData,loginMemberNo);

    } // onCreate 메소드

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w("map2","onNewIntent() 호출");
        registerImageUri = intent.getParcelableExtra("registerImageUri");
        boardContent = intent.getStringExtra("boardContent");
        searchLocation = intent.getStringExtra("searchLocation");
//        Log.w("map2","searchLocation : "+searchLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("map2","onResume() 호출");
        ImageView registerImageIV = findViewById(R.id.registerImageIV);
        registerImageIV.setImageURI(registerImageUri);

        boardContentET = findViewById(R.id.boardContentET);
        if(boardContent!=null){
            boardContentET.setText(boardContent);
        }

        Button boardWriteBtn = findViewById(R.id.boardWriteBtn); // 다음 버튼
        // 글작성 버튼 클릭시 이벤트
        boardWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String boardContent=boardContentET.getText().toString();
                if(searchLocation!=null){
                    boardContent = boardContent+"¡"+searchLocation;
                }
                String boardImage = registerImageUri.toString();
                Board tempBoard = new Board(myAppData.boardCount,boardContent,boardImage,loginMember.memberNo);
                myAppData.boardCount++;
                myAppData.boards.add(0,tempBoard);
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        registerLocationTV = findViewById(R.id.registerLocationTV); // 장소등록 텍스트뷰
        registerLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WriteBoardThirdActivity.class);
                intent.putExtra("registerImageUri",registerImageUri);
                intent.putExtra("boardContent",boardContentET.getText().toString());
                startActivity(intent);
            }
        });

        if(searchLocation!=null){
        Log.w("map2","searchLocation : "+searchLocation);

            String []splitStr = searchLocation.split(",");
            String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
            Log.w("map2","address : "+address);

            String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
            String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
            Log.w("map2","latitude : "+latitude);
            Log.w("map2","longitude : "+longitude);
            searchLocationTV = findViewById(R.id.searchLocationTV);
            searchLocationTV.setText("등록 장소 : "+address);
        }

    } // onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        myAppService.writeAllData(myAppData,this);
        Log.w("map2","onPause() 호출");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStop() 호출");
        Log.w("map2","onStop() 호출");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onBackPressed() 호출");
    }

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


} // WriteBoardSecondActivity 클래스
