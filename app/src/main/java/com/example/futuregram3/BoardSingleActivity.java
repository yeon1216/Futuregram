package com.example.futuregram3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class BoardSingleActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView memberProfileImageIV; // 게시글 멤버 프로필 이미지 이미지뷰
    TextView memberIdTV; // 게시글 멤버 아이디 텍스트뷰
    ImageView editIconIV; // 게시글 편집 아이콘 이미지뷰
    ImageView boardImageIV; // 게시글 이미지 이미지뷰
    ImageView emptyHartIconIV; // 빈 하트 아이콘 이미지뷰
    ImageView redHartIconIV; // 빨강 하트 아이콘 이미지뷰
    ImageView replyIconIV; // 댓글 아이콘 이미지뷰;
    TextView likeCountTV; // 좋아요 갯수 텍스트뷰
    TextView boardContentTV; // 게시글 내용 텍스트뷰
    TextView replyCountTV; // 댓글 갯수 텍스트뷰
    TextView boardTimeTV; // 게시글 시간 텍스트뷰
    TextView registerLocationTV; // 등록 장속 텍스트뷰
    TextView registerLocationTV2; // 등록 장속 텍스트뷰
    LinearLayout mapLL; // 지도를 가지고있는 리니어레이아웃
    TextView showMapTV; // 지도보기 텍스트뷰
    TextView hideMapTV; // 지도 숨기기 텍스트뷰
    LinearLayout locationLL;

    ImageView myProfileImageIV; // 로그인 멤버 프로필 사진 이미지뷰
    TextView myIdTV; // 로그인 멤버 아이디 텍스트뷰

    MyAppData myAppData;
    MyAppService myAppService;
    Member loginMember;
    Board tempBoard;

    GoogleMap mMap;
    String registerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_single);

        mapLL = findViewById(R.id.mapLL);
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    } // onCreate() 메소드

    @Override
    protected void onResume() {
        super.onResume();

        myAppService = new MyAppService();
        myAppData = myAppService.readAllData(this);
        loginMember = myAppService.findMemberByMemberNo(myAppData, myAppData.loginMemberNo);

        myProfileImageIV = findViewById(R.id.myProfileImageIV); // 로그인 멤버 프로필 사진 이미지뷰
        if(loginMember.profileImage.substring(0,1).equals("c")){
            myProfileImageIV.setImageURI(Uri.parse(loginMember.profileImage));
        }else{
            myProfileImageIV.setImageResource(Integer.parseInt(loginMember.profileImage));
        }
        myIdTV = findViewById(R.id.myIdTV); // 로그인 멤버 아이디 텍스트뷰
        myIdTV.setText(loginMember.nickName);
        // 내 아이디 텍스트뷰 클릭시 이벤트
        myIdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        tempBoard = (Board) intent.getSerializableExtra("tempBoard");

        for(Board board:myAppData.boards){
            if(tempBoard.boardNo==board.boardNo){
                tempBoard = board;
            }
        }

        memberProfileImageIV = findViewById(R.id.memberProfileImageIV); // 게시글 멤버 프로필 이미지 이미지뷰
        memberIdTV = findViewById(R.id.memberIdTV); // 게시글 멤버 아이디 텍스트뷰
        editIconIV = findViewById(R.id.editIconIV); // 게시글 편집 아이콘 이미지뷰
        boardImageIV = findViewById(R.id.boardImageIV); // 게시글 이미지 이미지뷰
        emptyHartIconIV = findViewById(R.id.emptyHartIconIV); // 빈 하트 아이콘 이미지뷰
        redHartIconIV = findViewById(R.id.redHartIconIV); // 빨강 하트 아이콘 이미지뷰
        replyIconIV = findViewById(R.id.replyIconIV); // 댓글 아이콘 이미지뷰;
        likeCountTV = findViewById(R.id.likeCountTV); // 좋아요 갯수 텍스트뷰
        boardContentTV = findViewById(R.id.boardContentTV); // 게시글 내용 텍스트뷰
        replyCountTV = findViewById(R.id.replyCountTV); // 댓글 갯수 텍스트뷰
        boardTimeTV = findViewById(R.id.boardTimeTV); // 게시글 시간 텍스트뷰
        registerLocationTV = findViewById(R.id.registerLocationTV);
        registerLocationTV2 = findViewById(R.id.registerLocationTV2);
        showMapTV = findViewById(R.id.showMapTV);
        hideMapTV = findViewById(R.id.hideMapTV);
        locationLL = findViewById(R.id.locationLL);

        mapLL.setVisibility(View.GONE);

        Member tempBoardMember = myAppService.findMemberByMemberNo(myAppData, tempBoard.writeMemberNo);


        // 여기서 내용과 장소 분리하자 시작

        String[] strArr = tempBoard.boardContent.split("¡");

        // 여기서 내용과 장소 분리하자 끝

        String boardContent = strArr[0]+"";

        if(strArr.length>1){
            Log.w("map3","strArr[1] : "+strArr[1]);
            if(strArr[1].trim().length()>0){
                registerLocation = strArr[1];
                String []splitStr = registerLocation.split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2)+""; // 주소
                Log.w("map3","address : "+address);
                if(address.length()!=0){
//                    registerLocationTV.setVisibility(View.VISIBLE);
//                    showMapTV.setVisibility(View.VISIBLE);
                    locationLL.setVisibility(View.VISIBLE);
                    if(address.length()>25){
                        registerLocationTV.setText("장소 : "+address.substring(0,25)+"...");
                    }else{
                        registerLocationTV.setText("장소 : "+address);
                    }
                    registerLocationTV2.setText("장소 : "+address);
                }else{
//                    registerLocationTV.setVisibility(View.GONE);
//                    showMapTV.setVisibility(View.GONE);
                    locationLL.setVisibility(View.GONE);
                }
            }
        }

        showMapTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapLL.setVisibility(View.VISIBLE);
                locationLL.setVisibility(View.GONE);
//                showMapTV.setVisibility(View.GONE);
//                registerLocationTV.setVisibility(View.GONE);
            }
        });

        hideMapTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapLL.setVisibility(View.GONE);
                locationLL.setVisibility(View.VISIBLE);
//                showMapTV.setVisibility(View.VISIBLE);
//                registerLocationTV.setVisibility(View.VISIBLE);
            }
        });



        int likeCount = tempBoard.likeMembers.size();
        int replyCount = 0;
        for (Reply tempReply : myAppData.replies) {
            if (tempReply.replyBoardNo == tempBoard.boardNo) {
                replyCount++;
            }
        }

        if (tempBoardMember.profileImage.substring(0, 1).equals("c")) {
            memberProfileImageIV.setImageURI(Uri.parse(tempBoardMember.profileImage));
        } else {
            memberProfileImageIV.setImageResource(Integer.parseInt(tempBoardMember.profileImage));
        }
        memberIdTV.setText(tempBoardMember.nickName);

//        if (loginMember.memberNo == tempBoardMember.memberNo) {
//            editIconIV.setVisibility(EditText.VISIBLE);
//        } else {
//            editIconIV.setVisibility(EditText.GONE);
//        }

        if (tempBoard.boardImage.substring(0, 1).equals("c")) {
            boardImageIV.setImageURI(Uri.parse(tempBoard.boardImage));
        } else {
            boardImageIV.setImageResource(Integer.parseInt(tempBoard.boardImage));
        }

        likeCountTV.setText("좋아요 " + likeCount + "명");

        // 멤버가 이 게시글을 좋아요 했는지 체크하는 코드
        boolean isLike = false;
        final ArrayList<Integer> tempBoardLikeMemberList = tempBoard.likeMembers;
        for (int i = 0; i < tempBoardLikeMemberList.size(); i++) {
            if (loginMember.memberNo == tempBoardLikeMemberList.get(i)) {
                isLike = true;
            }
        }

        if (isLike) {
            emptyHartIconIV.setVisibility(ImageView.GONE);
            redHartIconIV.setVisibility(ImageView.VISIBLE);
        } else {
            redHartIconIV.setVisibility(ImageView.GONE);
            emptyHartIconIV.setVisibility(ImageView.VISIBLE);
        }

        if (boardContent.trim().length() == 0) {
            boardContentTV.setVisibility(TextView.GONE);
        } else {
            boardContentTV.setVisibility(TextView.VISIBLE);
            boardContentTV.setText(boardContent);
        }

        if (replyCount == 0) {
            replyCountTV.setVisibility(TextView.GONE);
        } else {
            replyCountTV.setVisibility(TextView.VISIBLE);
            replyCountTV.setText(replyCount + "개 댓글 모두보기...");
        }

        boardTimeTV.setText(tempBoard.writeTime);


        // 멤버 아이디 클릭시 이벤트
        memberIdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member selectMember = myAppService.findMemberByMemberNo(myAppData, tempBoard.writeMemberNo);
                if (selectMember.memberNo != loginMember.memberNo) {
                    Intent intent = new Intent(getApplicationContext(), MemberInfoActivity.class);
                    intent.putExtra("selectMember", selectMember);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

        // 편집 아이콘 클릭시 이벤트, 다음번에 해보자
//        editIconIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myAppService.boardUpdateRemoveInSingleBoardDialogShow(myAppData, tempBoard.boardNo, BoardSingleActivity.this, BoardSingleActivity.this);
//                // 게시글 삭제시 해당 알람도 다 제거해주어야한다.
//            }
//        });


        likeCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAppService.writeAllData(myAppData,BoardSingleActivity.this);
                Intent intent = new Intent(getApplicationContext(),MemberListActivity.class);
                intent.putExtra("tempBoardNo",tempBoard.boardNo);
                intent.putExtra("screenSelect",0); // 0: 좋아요 목록, 1: 팔로워 목록, 2: 팔로잉 목록
                startActivity(intent);
            }
        });

        replyIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),ReplyActivity.class);
                    intent.putExtra("tempBoardNo",tempBoard.boardNo);
                    startActivity(intent);
            }
        });

        emptyHartIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    emptyHartIconIV.setVisibility(ImageView.GONE);
                    redHartIconIV.setVisibility(ImageView.VISIBLE);

                    tempBoard.likeMembers.add(0,loginMember.memberNo);
                    myAppService.updateBoardData(tempBoard,BoardSingleActivity.this);

                    if(tempBoard.writeMemberNo!=loginMember.memberNo){
                        // 글 작성자가 로그인멤버가 아니면 알람 추가
                        Member tempMember = myAppService.findMemberByMemberNo(myAppData,tempBoard.writeMemberNo);
                        Notification tempNotification = new Notification(tempMember.memberNo,myAppData.notificationCount,loginMember.memberNo,tempBoard.boardNo,1);
                        myAppData.notificationCount++;
                        myAppData.notifications.add(tempNotification);
                    }

                    Intent intent1 = new Intent(getApplicationContext(),BoardSingleActivity.class);
                    intent1.putExtra("tempBoard",tempBoard);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent1);
            }
        });

        redHartIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redHartIconIV.setVisibility(ImageView.GONE);
                emptyHartIconIV.setVisibility(ImageView.VISIBLE);
                for (int i = 0; i < tempBoard.likeMembers.size(); i++) {
                    if(loginMember.memberNo==tempBoard.likeMembers.get(i)){
                        tempBoard.likeMembers.remove(i);
                        break;
                    }
                }
                myAppService.updateBoardData(tempBoard,BoardSingleActivity.this);

                // 좋아요 취소시 알람 객체를 제거해주는 메소드
                for (Notification notification : myAppData.notifications){
                    if(notification.makeNotificationMemberNo==loginMember.memberNo){
                        myAppService.deleteNotificationData(notification,getApplicationContext());
                        myAppData.notifications.remove(notification);
                        break;
                    }
                }

                Intent intent1 = new Intent(getApplicationContext(),BoardSingleActivity.class);
                intent1.putExtra("tempBoard",tempBoard);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
            }
        });

        replyCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReplyActivity.class);
                intent.putExtra("tempBoardNo",tempBoard.boardNo);
                startActivity(intent);
            }
        });



    }// onResume() 메소드

    @Override
    protected void onPause() {
        super.onPause();
        myAppService.writeAllData(myAppData,this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        mMap = googleMap;
        if(registerLocation!=null){
            String []splitStr = registerLocation.split(",");
            String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
            String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
            String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

            // 좌표(위도, 경도) 생성
            LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            // 마커 생성
            MarkerOptions mOptions2 = new MarkerOptions();
            mOptions2.title("등록 장소");
            mOptions2.snippet(address);
            mOptions2.position(point);
            // 마커 추가
            mMap.addMarker(mOptions2);
            // 해당 좌표로 화면 줌
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
        }
    }
} // 게시글 싱글 클래스
