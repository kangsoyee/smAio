package com.example.smAio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    private TextView mTextMessage;

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
                Intent startQRActivity = new Intent(FirstActivity.this, QRActivity.class);
                startActivity(startQRActivity);
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

}
