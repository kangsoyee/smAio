package com.example.smAio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;

public class FirstActivity extends AppCompatActivity {
    // 2초 간격
    private static final long EXIT_INTERVAL_TIME = 2000;
    // 누른 시간
    private long pressedTime = 0;

    final private static String TAG = "from activity data";
    private String user_id,user_name;
    private TextView mTextMessage;
    SessionManager sessionManager;

    //바텀 네비게이션바 클릭 이벤트
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    callFragment(1);
                    return true;
                case R.id.location:
                    callFragment(2);
                    return true;
                case R.id.favorite:
                    callFragment(4);
                    return true;
                case R.id.person:
                    callFragment(5);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        sessionManager=new SessionManager(this); //세션의 SharedPreFerences사용을 위해 SessionManager 생성
        sessionManager.checkLoggin(); //세션으로 로그인 여부 확인

        HashMap<String,String> user = sessionManager.getUserDetail(); //세션을 통해 값을 받아온다.
        user_name =user.get(sessionManager.NAME); //받아온 값 변수에 저장
        user_id = user.get(sessionManager.ID);

        Log.e(TAG,user_id); //확인로그
        Log.e(TAG,user_name);


        Bundle info_bundle = new Bundle(); //MyFrament로 보내기위해 번들로 이름과 아이디 값 묶어줌
        info_bundle.putString("id", user_id);
        info_bundle.putString("name",user_name);

        MyFragment myFragment = new MyFragment();//MyFragment객체 생성
        myFragment.setArguments(info_bundle);//생성된 객체에 setArguments를 통해 번들 보내기

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        callFragment(1); //맨 처음에 HomeFragment 호출

        Intent get_intent=getIntent();
        String logout=get_intent.getStringExtra("Logout");
        if(logout=="1"){
            sessionManager.logout();
        }

        //권한 종류 선택 후 배열에 저장(카메라, 위치 권한 선택)
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        //선택한 권한 허용 팝업 띄우기
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        if ( pressedTime == 0 ) { //처음 눌렀을 때
            Toast.makeText(FirstActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis(); //누른시간을 잰다
        }
        else {//처음 눌르고 시간이 지났다면
            int seconds = (int) (System.currentTimeMillis() - pressedTime); //현재 시스템 시간에서 처음 눌렀을때 pressedTime값 뺌

            if ( seconds > EXIT_INTERVAL_TIME ) { //그 값이 위에 선언된 2초보다 크면
                Toast.makeText(FirstActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = 0 ; //다시 반복
            }
            else {
                super.onBackPressed();
               finish(); // app 종료 시키기
            }
        }
    }

    //프래그먼트 화면 변경 함수
    private void callFragment(int fragment_no){
        // 프래그먼트 사용을 위해
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment_no){
            case 1:
                // Home 프래그먼트 호출
                HomeFragment fragment1 = new HomeFragment();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 2:
                // Map 프래그먼트 호출
                MapFragment fragment2 = new MapFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment2, "main").commit();
                break;

            case 4:
                // Heart 프래그먼트 호출
                HeartFragment fragment4 = new HeartFragment();
                transaction.replace(R.id.fragment_container, fragment4);
                transaction.commit();
                Bundle Heart_bundle = new Bundle();
                Heart_bundle.putString("id", user_id);
                fragment4.setArguments(Heart_bundle);
                break;

            case 5:
                // My 프래그먼트 호출
                MyFragment fragment5 = new MyFragment();
                transaction.replace(R.id.fragment_container, fragment5);
                transaction.commit();
                Log.e(TAG,user_id);
                Log.e(TAG,user_name);

                Bundle info_bundle = new Bundle();
                info_bundle.putString("id", user_id);
                info_bundle.putString("name",user_name);
                fragment5.setArguments(info_bundle);
                break;
        }
    }

    //레스토랑 버튼 클릭 이벤트
    public void Clicked_res(View view) {
        //레스토랑 버튼 클릭하면 StoreListActivity로 액티비티 전환
        Intent startRestaurantActivity = new Intent(FirstActivity.this, StoreListActivity.class);
        startRestaurantActivity.putExtra("userid",user_id);
        startActivity(startRestaurantActivity);
    }

    //카페 버튼 클릭 이벤트
    public void Clicked_cafe(View view) {
        //카페 버튼 클릭하면 StoreListActivity2로 액티비티 전환
        Intent startCafeActivity = new Intent(FirstActivity.this, StoreListActivity2.class);
        startCafeActivity.putExtra("userid",user_id);
        startActivity(startCafeActivity);
    }

    //노래방 버튼 클릭 이벤트
    public void Clicked_karaoke(View view) {
        //노래방 버튼 클릭하면 StoreListActivity3로 액티비티 전환
        Intent startKaraokeActivity = new Intent(FirstActivity.this, StoreListActivity3.class);
        startKaraokeActivity.putExtra("userid",user_id);
        startActivity(startKaraokeActivity);
    }

    //피시방 버튼 클릭 이벤트
    public void Clicked_internet(View view) {
        //피시방 버튼 클릭하면 StoreListActivity4로 액티비티 전환
        Intent startPCActivity = new Intent(FirstActivity.this, StoreListActivity4.class);
        startPCActivity.putExtra("userid",user_id);
        startActivity(startPCActivity);
    }

    //QR 버튼 클릭 이벤트
    public void Click_qr(View view){
        Intent startQRActivity = new Intent(FirstActivity.this, QrScanActivity.class);
        startQRActivity.putExtra("id",user_id);
        startActivity(startQRActivity);
        finish();
        Log.e("test", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
    }

    //MyFragment에서 Logout버튼을 눌렀을때 이벤트를 발생시키기위해 생성 (참조를 통해)
    public void sessionout(){
        sessionManager.logout(); //세션 값지우기
        Intent i = new Intent(FirstActivity.this,LoginActivity.class);
        i.putExtra("boolcheck",false); //인텐트로 LoginActivity로 이동하고 이때 false라는 값을 보냄
        startActivity(i);
        finish();
    }

    //권한 허용 함수
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
