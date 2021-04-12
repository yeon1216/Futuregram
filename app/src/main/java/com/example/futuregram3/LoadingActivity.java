package com.example.futuregram3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoadingActivity extends AppCompatActivity {

    TextView appNameTV;
    private Handler handler;
    ImageView loadImage1IV;
    ImageView loadImage2IV;
    ImageView loadImage3IV;
    ImageView loadImage4IV;
    ImageView loadImageEgg1IV;
    ImageView loadImageEgg2IV;
    ImageView loadImageEgg3IV;
    ImageView loadImageEgg4IV;


    MyAppData myAppData; // 앱의 데이터
    MyAppService myAppService; // 내 앱의 시스템
    Member loginMember; // 현재 로그인 멤버

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);
        int loginMemberNo = myAppData.loginMemberNo;
        loginMember = myAppService.findMemberByMemberNo(myAppData,loginMemberNo);

        if(loginMember.memberNo>=0 && loginMember.memberNo<7){
            String token = FirebaseInstanceId.getInstance().getToken();
            Map<String,Object> map = new HashMap<>();
            map.put("pushToken",token);
            FirebaseDatabase.getInstance().getReference("myAppData").child("members").child(String.valueOf(loginMember.memberNo)).updateChildren(map);
        }


    } // onCreate() 메소드



    @Override
    protected void onResume() {
        super.onResume();
        appNameTV = findViewById(R.id.appNameTV);

        loadImage1IV = findViewById(R.id.loadImage1IV);
        loadImage1IV.setVisibility(LinearLayout.INVISIBLE);
        loadImage2IV = findViewById(R.id.loadImage2IV);
        loadImage2IV.setVisibility(LinearLayout.INVISIBLE);
        loadImage3IV = findViewById(R.id.loadImage3IV);
        loadImage3IV.setVisibility(LinearLayout.INVISIBLE);
        loadImage4IV = findViewById(R.id.loadImage4IV);
        loadImage4IV.setVisibility(LinearLayout.INVISIBLE);

        loadImageEgg1IV = findViewById(R.id.loadImageEgg1IV);
        loadImageEgg1IV.setVisibility(LinearLayout.INVISIBLE);
        loadImageEgg2IV = findViewById(R.id.loadImageEgg2IV);
        loadImageEgg2IV.setVisibility(LinearLayout.INVISIBLE);
        loadImageEgg3IV = findViewById(R.id.loadImageEgg3IV);
        loadImageEgg3IV.setVisibility(LinearLayout.INVISIBLE);
        loadImageEgg4IV = findViewById(R.id.loadImageEgg4IV);
        loadImageEgg4IV.setVisibility(LinearLayout.INVISIBLE);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
              if(msg.what==0){
                  loadImage1IV.setVisibility(LinearLayout.VISIBLE);
                  loadImage2IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage3IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage4IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImageEgg1IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg2IV.setVisibility(ImageView.INVISIBLE);
                  loadImageEgg3IV.setVisibility(ImageView.INVISIBLE);
                  loadImageEgg4IV.setVisibility(ImageView.INVISIBLE);
              }else if(msg.what==1){
                  loadImage1IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage2IV.setVisibility(LinearLayout.VISIBLE);
                  loadImage3IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage4IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImageEgg1IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg2IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg3IV.setVisibility(ImageView.INVISIBLE);
                  loadImageEgg4IV.setVisibility(ImageView.INVISIBLE);
              }else if(msg.what==2){
                  loadImage1IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage2IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage3IV.setVisibility(LinearLayout.VISIBLE);
                  loadImage4IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImageEgg1IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg2IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg3IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg4IV.setVisibility(ImageView.INVISIBLE);
              }else if(msg.what==3){
                  loadImage1IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage2IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage3IV.setVisibility(LinearLayout.INVISIBLE);
                  loadImage4IV.setVisibility(LinearLayout.VISIBLE);
                  loadImageEgg1IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg2IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg3IV.setVisibility(ImageView.VISIBLE);
                  loadImageEgg4IV.setVisibility(ImageView.VISIBLE);
              }
            }
        };

        new Thread(){
            @Override
            public void run(){
                try{Thread.sleep(200);}catch (InterruptedException e){}
                handler.sendEmptyMessage(0);
                try{Thread.sleep(350);}catch (InterruptedException e){}
                handler.sendEmptyMessage(1);
                try{Thread.sleep(350);}catch (InterruptedException e){}
                handler.sendEmptyMessage(2);
                try{Thread.sleep(350);}catch (InterruptedException e){}
                handler.sendEmptyMessage(3);
                try{Thread.sleep(100);}catch (InterruptedException e){}
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                try{Thread.sleep(1400);}catch (InterruptedException e){}

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);

            }
        }.start();

    }

} // 로딩 액티비티 클래스
