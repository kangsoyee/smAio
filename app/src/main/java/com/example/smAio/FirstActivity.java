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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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
                case R.id.focus:
                    callFragment(3);
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

        sessionManager=new SessionManager(this);
        sessionManager.checkLoggin();

        HashMap<String,String> user = sessionManager.getUserDetail();
        user_name =user.get(sessionManager.NAME);
        user_id = user.get(sessionManager.ID);

        Log.e(TAG,user_id);
        Log.e(TAG,user_name);

        Bundle info_bundle = new Bundle();
        info_bundle.putString("id", user_id);
        info_bundle.putString("name",user_name);

        MyFragment myFragment = new MyFragment();
        myFragment.setArguments(info_bundle);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        callFragment(1);

        //액션바 설정하기//
        //액션바 타이틀 변경하기
        getSupportActionBar().setTitle("SM.Aio");
        //홈버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //액션바 숨기기
        //hideActionBar();

        Intent get_intent=getIntent();
        String logout=get_intent.getStringExtra("Logout");
        if(logout=="1"){
            sessionManager.logout();
        }

        //카메라, 위치 권한 허용 팝업
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    @Override
    public void onBackPressed() {
        if ( pressedTime == 0 ) {
            Toast.makeText(FirstActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > EXIT_INTERVAL_TIME ) {
                Toast.makeText(FirstActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = 0 ;
            }
            else {
                super.onBackPressed();
//                finish(); // app 종료 시키기
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

            case 3:
                // QR 액티비티 호출
//                Intent startQRActivity = new Intent(FirstActivity.this, QRActivity.class);
//                startActivity(startQRActivity);
                break;

            case 4:
                // Heart 프래그먼트 호출
                HeartFragment fragment4 = new HeartFragment();
                transaction.replace(R.id.fragment_container, fragment4);
                transaction.commit();
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

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(this, "검색 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //액션바 숨기기
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
    }

    //레스토랑 버튼 클릭 이벤트
    public void Clicked_res(View view) {
        Intent startRestaurantActivity = new Intent(FirstActivity.this, StoreListActivity.class);
        startActivity(startRestaurantActivity);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        StoreListFragment startStoreListFrag = new StoreListFragment();
//        transaction.replace(R.id.fragment_container, startStoreListFrag);
//        transaction.addToBackStack(null); //이전 프래그먼트 백 스택에 추가
//        transaction.commit();
    }

    //카페 버튼 클릭 이벤트
    public void Clicked_cafe(View view) {
        Intent startRestaurantActivity = new Intent(FirstActivity.this, StoreListActivity.class);
        startActivity(startRestaurantActivity);
    }

    //노래방 버튼 클릭 이벤트
    public void Clicked_karaoke(View view) {
        Intent startRestaurantActivity = new Intent(FirstActivity.this, StoreListActivity.class);
        startActivity(startRestaurantActivity);
    }

    //피시방 버튼 클릭 이벤트
    public void Clicked_internet(View view) {
        Intent startRestaurantActivity = new Intent(FirstActivity.this, StoreListActivity.class);
        startActivity(startRestaurantActivity);
    }

    //QR 버튼 클릭 이벤트
    public void Click_qr(View view){
        Intent startQRActivity = new Intent(FirstActivity.this, QRActivity.class);
        startActivity(startQRActivity);
    }

    public void sessionout(){
        sessionManager.logout();
        Intent i = new Intent(FirstActivity.this,LoginActivity.class);
        i.putExtra("boolcheck",false);
        startActivity(i);
        finish();
    }

    //카메라 권한 허용 함수
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
