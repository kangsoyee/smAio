package com.example.smAio;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceAdd extends AppCompatActivity {


    EditText txtIdx, txtCategory, txtPlaceName, txtStartTime, txtEndTime, txtAddress, txtTel, txtMenu, txtPrice;
    PlaceDTO placeInfo;
    Button btnSave;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_add);

        txtTel=(EditText)findViewById(R.id.tel);
        txtAddress=(EditText)findViewById(R.id.address);
        txtStartTime=(EditText)findViewById(R.id.start_time);
        txtEndTime=(EditText)findViewById(R.id.end_time);
        txtCategory=(EditText)findViewById(R.id.category);
        txtPlaceName=(EditText)findViewById(R.id.place_name);
        txtMenu = (EditText) findViewById(R.id.menu);
        txtPrice = (EditText) findViewById(R.id.price);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });
    }

    void insert(){
//네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String start_time=txtStartTime.getText().toString();
                    String end_time=txtEndTime.getText().toString();
                    String place_name=txtPlaceName.getText().toString();
                    String address=txtAddress.getText().toString();
                    String tel=txtTel.getText().toString();
                    String category=txtCategory.getText().toString();
                    String menu = txtMenu.getText().toString();
                    String price = txtPrice.getText().toString();

                    String page =
                            Common.SERVER_URL+"/place_insert.php?start_time="+start_time+"&end_time="+end_time
                                    +"&place_name="+place_name
                                    +"&address="+address
                                    +"&tel="+tel
                                    +"&category="+category
                                    +"&menu="+menu
                                    +"&price=+"+price;

                    URL url = new URL(page);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 연결되었으면.
                    if (conn != null) {
                        //타임아웃 시간 설정
                        conn.setConnectTimeout(10000);
                        //캐쉬 사용 여부
                        conn.setUseCaches(false);
                        //url에 접속 성공하면
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        }
                        conn.disconnect();
                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}
