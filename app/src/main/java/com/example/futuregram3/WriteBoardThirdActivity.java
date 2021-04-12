package com.example.futuregram3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WriteBoardThirdActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    Button okBtn; // 확인 버튼
    EditText searchET; // 검색 창
    ImageView currentLocationIconIV; // 현재위치 아이콘
    TextView searchLocationTV; // 검색장소 텍스트뷰

    Uri registerImageUri; // 등록한 이미지
    String boardContent; // 작성중이던 글

    private GoogleMap mMap;
    private Geocoder geocoder;

    InputMethodManager inputMethodManager;

    String searchLocation; // 검색 위치

    GpsTracker gpsTracker; // 현재 위치를 가져오기위한것
    String currentLatitude; // 현재 위도
    String currentLongitude; // 현재 경도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board_third);


        Intent intent = getIntent();
        registerImageUri = intent.getParcelableExtra("registerImageUri");
        boardContent = intent.getStringExtra("boardContent");

        searchET = findViewById(R.id.searchET);
        searchLocationTV = findViewById(R.id.searchLocationTV);

        currentLocationIconIV = findViewById(R.id.currentLocationIconIV); // 현재 위치 찾기 아이콘 이미지뷰


        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WriteBoardSecondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("registerImageUri",registerImageUri);
                intent.putExtra("boardContent",boardContent);
                if(searchLocation==null){
                    Toast.makeText(getApplicationContext(),"장소를 선택하지 않으셨습니다",Toast.LENGTH_SHORT).show();
                }else{
                    intent.putExtra("searchLocation",searchLocation);
                }
                startActivity(intent);
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    } // onCreate() 메소드



    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        geocoder = new Geocoder(this);

        // 현재 위치 받아오기
        gpsTracker = new GpsTracker(this);
        currentLatitude = String.valueOf(gpsTracker.getLatitude());
        currentLongitude = String.valueOf(gpsTracker.getLongitude());

        LatLng currentPoint = new LatLng(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));
        mMap.addMarker(new MarkerOptions().position(currentPoint).title("현재 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPoint));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint,15));

        List<Address> addressList = null;
        try{
            addressList = geocoder.getFromLocation(Double.parseDouble(currentLatitude),Double.parseDouble(currentLongitude),10);
            searchLocation = addressList.get(0).toString();
            String []splitStr = addressList.get(0).toString().split(",");
            String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
            searchLocationTV.setText("현재 위치 : "+address);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"해당 주소가 없습니다",Toast.LENGTH_SHORT).show();
        }



        // 현재 위치 찾기 아이콘 이미지뷰 클릭시 이벤트
        currentLocationIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                currentLatitude = String.valueOf(gpsTracker.getLatitude());
                currentLongitude = String.valueOf(gpsTracker.getLongitude());

                List<Address> addressList = null;
                try{
                    addressList = geocoder.getFromLocation(Double.parseDouble(currentLatitude),Double.parseDouble(currentLongitude),10);

                    Log.w("map","addressList.get(0).toString() : "+addressList.get(0).toString());

                    searchLocation = addressList.get(0).toString();
                    Log.w("map","searchLocation : "+searchLocation);

                    // 콤마를 기준으로 split
                    String []splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                    Log.w("map","address : "+address);

                    LatLng currentPoint = new LatLng(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));
                    mMap.addMarker(new MarkerOptions().position(currentPoint).title("현재 위치").snippet(address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPoint));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint,15));

                    searchLocationTV.setText("현재 위치 : "+address);


                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"해당 주소가 없습니다",Toast.LENGTH_SHORT).show();
                }


            }
        });

        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                // 마커 타이틀
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도

                List<Address> addressList = null;
                try{
                    addressList = geocoder.getFromLocation(latitude,longitude,10);

                    Log.w("map","addressList.get(0).toString() : "+addressList.get(0).toString());

                    searchLocation = addressList.get(0).toString();
                    Log.w("map","searchLocation : "+searchLocation);

                    // 콤마를 기준으로 split
                    String []splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                    Log.w("map","address : "+address);

                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.title("클릭 위치");
                    mOptions.snippet(address);
                    mOptions.position(new LatLng(latitude, longitude));
                    googleMap.addMarker(mOptions); // 마커(핀) 추가

                    searchLocationTV.setText("클릭 위치 : "+address);

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"해당 주소가 없습니다",Toast.LENGTH_SHORT).show();
                }


            }
        });
        ////////////////////

        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    mMap.clear();

                    inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchET.getWindowToken(),0);
                    String str=searchET.getText().toString();
                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str, // 주소
                                10); // 최대 검색 결과 개수


                        Log.w("map","addressList.get(0).toString() : "+addressList.get(0).toString());

                        searchLocation = addressList.get(0).toString();
                        Log.w("map","searchLocation : "+searchLocation);

                        // 콤마를 기준으로 split
                        String []splitStr = addressList.get(0).toString().split(",");
                        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                        Log.w("map","address : "+address);

                        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                        Log.w("map","latitude : "+latitude);
                        Log.w("map","longitude : "+longitude);

                        // 좌표(위도, 경도) 생성
                        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        // 마커 생성
                        MarkerOptions mOptions2 = new MarkerOptions();
                        mOptions2.title("검색결과");
                        mOptions2.snippet(address);
                        mOptions2.position(point);
                        // 마커 추가
                        mMap.addMarker(mOptions2);
                        // 해당 좌표로 화면 줌
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));


                        searchLocationTV.setText("검색 위치 : "+address);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(),"검색 실패 (ioException)",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"올바른 주소를 입력해주세요",Toast.LENGTH_SHORT).show();
                    }


                    return true;
                }
                return false;
            }
        });





    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d("map", "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
//        setCurrentLocation(location, markerTitle, markerSnippet);
//        mCurrentLocatiion = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
} // 글작성 세번째 액티비티 클래스
