package com.example.futuregram3;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class JoinSecondActivity extends AppCompatActivity {

    EditText nickNameInputET; // 닉네임 입력 창
    TextView nickNameLengthConditionTestingTV; // 닉네임 글자수 검사 텍스트뷰
    TextView nickNameConditionTestingTV;
    TextView nickNameDuplicationConditionTestingTV; // 닉네임 중복 검사 텍스트뷰
    EditText passwordInputET; // 비밀번호 입력 창
    TextView passwordConditionTestingTV; // 비밀번호 조건검사 텍스트뷰
    EditText passwordCheckInputET; // 비밀번호 확인 입력 창
    TextView passwordCheckConditionTestingTV; // 비밀번호 확인 조건검사 텍스트뷰
    Button joinBtn; // 회원가입 버튼

    MyAppData myAppData; // 앱 데이터
    MyAppService myAppService; // 앱 서비스
    private Handler handler;

    String inputPhoneNum; // 회원가입 화면에서 입력한 전화번호

    boolean isJoinConditionTestingThreadRun; // 회원가입 조건검사 쓰레드 실행여부

    String nickName; // 닉네임
    String password; // 비밀번호
    String passwordCheck; // 비밀번호 확인

    boolean isJoinOk; // 회원가입 조건 충족 여부 검사

    int memberCount; // 멤버 숫자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_second);
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onCreate() 호출");

        myAppService = new MyAppService(); // 앱 서비스 객체 생성
        Intent intent = getIntent();
        myAppData = myAppService.readAllData(this);
        inputPhoneNum = intent.getStringExtra("inputPhoneNum"); // 인텐트에서 입력한 전화번호 받기
        nickNameInputET = findViewById(R.id.nickNameInputET); // 닉네임 입력 창
        nickNameLengthConditionTestingTV = findViewById(R.id.nickNameLengthConditionTestingTV);
        nickNameConditionTestingTV = findViewById(R.id.nickNameConditionTestingTV); //
        nickNameDuplicationConditionTestingTV = findViewById(R.id.nickNameDuplicationConditionTestingTV);
        nickNameDuplicationConditionTestingTV.setVisibility(TextView.VISIBLE);
        passwordInputET = findViewById(R.id.passwordInputET); // 비밀번호 입력 창
        passwordConditionTestingTV=findViewById(R.id.passwordConditionTestingTV); // 비밀번호 조건검사 텍스트뷰
        passwordCheckInputET = findViewById(R.id.passwordCheckInputET); // 비밀번호 확인 입력 창
        passwordCheckConditionTestingTV=findViewById(R.id.passwordCheckConditionTestingTV); // 비밀번호 확인 조건검사 텍스트뷰
        joinBtn = findViewById(R.id.joinBtn); // 회원가입 버튼
        isJoinOk=false; // 회원가입 조건 충족여부 false로 초기화




        // 회원가입 버튼 클릭 이벤트
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isJoinOk){
                    myAppService.join(myAppData,inputPhoneNum,password,nickName,memberCount,getApplicationContext());
                    nickNameDuplicationConditionTestingTV.setVisibility(TextView.INVISIBLE);
                    isJoinConditionTestingThreadRun=false;
                    Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_SHORT).show();
                    Intent joinSecondToLoginIntent = new Intent(getApplicationContext(),LoginActivity.class);

                    joinSecondToLoginIntent.addFlags(joinSecondToLoginIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(joinSecondToLoginIntent);
                }else{
                    Toast.makeText(getApplicationContext(),"회원가입 조건을 충족하지 않습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // 뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 회원가입이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onStart() 호출");

        DatabaseReference memberCountDatabaseReference = FirebaseDatabase.getInstance().getReference("myAppData").child("memberCount");
        memberCountDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memberCount = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(LoginActivity.TAG+this.getClass().getSimpleName(),"onResume() 호출");

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        nickNameLengthConditionTestingTV.setText("");
                        break;
                    case 8:
                        // 8 : 닉네임이 영문 + 숫자로만 이루어져있는지 검사
                        nickNameConditionTestingTV.setText("");
                        break;
                    case 1:
                        nickNameDuplicationConditionTestingTV.setText("");
                        break;
                    case 2:
                        passwordConditionTestingTV.setText("");
                        break;
                    case 3:
                        passwordCheckConditionTestingTV.setText("");
                        break;
                    case 4:
                        passwordCheckConditionTestingTV.setText("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                        break;
                    case 5:
                        passwordConditionTestingTV.setText("비밀번호를 3 ~ 8자로 입력하세요");
                        passwordCheckConditionTestingTV.setText("");
                        break;
                    case 6:
                        nickNameDuplicationConditionTestingTV.setText("해당 닉네임이 이미 존재합니다.");
                        passwordConditionTestingTV.setText("");
                        passwordCheckConditionTestingTV.setText("");
                        break;
                    case 9:
                        // 9 : 닉네임은 영문(소문자)과 숫자로만 이루어져야합니다.
                        nickNameConditionTestingTV.setText("닉네임은 영문(소문자)과 숫자로만 이루어져야합니다.");
                        nickNameDuplicationConditionTestingTV.setText("");
                        passwordConditionTestingTV.setText("");
                        passwordCheckConditionTestingTV.setText("");
                        break;
                    case 7:
                        nickNameLengthConditionTestingTV.setText("닉네임을 3 ~ 8자로 입력하세요");
                        nickNameConditionTestingTV.setText("");
                        nickNameDuplicationConditionTestingTV.setText("");
                        passwordConditionTestingTV.setText("");
                        passwordCheckConditionTestingTV.setText("");
                        break;
                }

            }
        };

        // 회원가입 실시간 조건검사 쓰레드
        new Thread(){
            @Override
            public void run(){
                isJoinConditionTestingThreadRun=true;  // 회원가입 조건검사 쓰레드 실행여부
                while(isJoinConditionTestingThreadRun){
                    try{Thread.sleep(100);}catch (InterruptedException e){}
                    nickName = nickNameInputET.getText().toString().trim(); // 닉네임
                    password = passwordInputET.getText().toString().trim(); // 비밀번호
                    passwordCheck = passwordCheckInputET.getText().toString().trim(); // 비밀번호 확인
                    if(myAppService.checkNickNameLength(nickName)){
                        // 0 : 닉네임 글자 수 검사
                        handler.sendEmptyMessage(0);
                        if(myAppService.checkNickNameInputOnlyNumberAndAlphabet(nickName)){
                            // 8 : 닉네임이 영문 + 숫자로만 이루어져있는지 검사
                            handler.sendEmptyMessage(8);
                            if(!myAppService.checkDuplicateNickName(myAppData,nickName)){
                                // 1 : 닉네임 중복 검사
                                handler.sendEmptyMessage(1);
                                if(myAppService.checkPassword(password)){
                                    // 2 : 비밀번호 조건검사
                                    handler.sendEmptyMessage(2);
                                    if(myAppService.checkPasswordCheck(password,passwordCheck)){
                                        // 3 : 비밀번호와 비밀번호 확인 일치 검사
                                        handler.sendEmptyMessage(3);
                                        isJoinOk=true;
                                    }else{
                                        isJoinOk=false;
                                        // 4 : 비밀번호와 비밀번호 확인이 일치하지 않습니다.
                                        handler.sendEmptyMessage(4);
                                    }
                                }else{
                                    // 5 : 올바른 비밀번호를 입력해주세요
                                    handler.sendEmptyMessage(5);
                                    isJoinOk=false;
                                }
                            }else{
                                // 6 : 해당 닉네임이 이미 존재합니다.
                                handler.sendEmptyMessage(6);
                                isJoinOk=false;
                            }
                        }else{
                            // 9 : 닉네임은 영문(소문자)과 숫자로만 이루어져야합니다.
                            handler.sendEmptyMessage(9);
                        }
                    }else{
                        // 7 : 닉네임이 올바르지 않습니다. (3 ~ 8자)
                        handler.sendEmptyMessage(7);
                        isJoinOk=false;
                    }
//                    if(myAppService.checkNickNameLength(nickName)){
//                        // 0 : 닉네임 글자 수 검사
//                        handler.sendEmptyMessage(0);
//                        if(!myAppService.checkDuplicateNickName(myAppData,nickName)){
//                            // 1 : 닉네임 중복 검사
//                            handler.sendEmptyMessage(1);
//                            if(myAppService.checkPassword(password)){
//                                // 2 : 비밀번호 조건검사
//                                handler.sendEmptyMessage(2);
//                                if(myAppService.checkPasswordCheck(password,passwordCheck)){
//                                    // 3 : 비밀번호와 비밀번호 확인 일치 검사
//                                    handler.sendEmptyMessage(3);
//                                    isJoinOk=true;
//                                }else{
//                                    isJoinOk=false;
//                                    // 4 : 비밀번호와 비밀번호 확인이 일치하지 않습니다.
//                                    handler.sendEmptyMessage(4);
//                                }
//                            }else{
//                                // 5 : 올바른 비밀번호를 입력해주세요
//                                handler.sendEmptyMessage(5);
//                                isJoinOk=false;
//                            }
//                        }else{
//                            // 6 : 해당 닉네임이 이미 존재합니다.
//                            handler.sendEmptyMessage(6);
//                            isJoinOk=false;
//                        }
//                    }else{
//                        // 7 : 닉네임이 올바르지 않습니다. (10자 이내)
//                        handler.sendEmptyMessage(7);
//                        isJoinOk=false;
//                    }

                } // 큰 while 문
            } // run 메소드
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isJoinConditionTestingThreadRun=false;
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
}
