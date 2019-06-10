package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewWriteActivity extends AppCompatActivity {
    /**
     * QR인식 후 리뷰 작성하는 Activity입니다.
     * 별점, 음성인식 등의 기능이 있습니다.
     */

    //가게의 id값을 가져오기위한 서버에 등록된 php문 주소
    private static String URL_getPlaceID ="http://eileenyoo1.cafe24.com/get_placeID.php/";
    private static String URL_getPlaceNAME ="http://eileenyoo1.cafe24.com/get_placeName.php/";
    //DB에서 가져온 가게의 id값을 저장하는 변수
    int place_idx;
    String place_n;

    private static final int REQUEST_CODE = 1234;
    ImageButton Start;
    EditText txtReview;
    TextView txtScore, nameText;
    Button button;

    //QrScanActivity에서 가져온 값들
    String user_Id,place_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        setTitle("Review Write");

        //QrScanActivity에서 값 가져오는 Intent
        Intent i = getIntent();
        user_Id=i.getStringExtra("id");
        place_url=i.getStringExtra("url");

        //layout items ID 가져오기
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setColorFilter(R.color.colorblue);
        Start = (ImageButton)findViewById(R.id.imageButton);
        txtReview=(EditText)findViewById(R.id.review_message);
        txtScore=(TextView)findViewById(R.id.textView3);
        button=(Button)findViewById(R.id.button_reviewsend);
        nameText=(TextView)findViewById(R.id.textView2);

        place_info(place_url);

        //reviewsend button Click Event
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txtReview에 저장된 리뷰내용 변수에 저장
                String txtReview_textCheck=txtReview.getText().toString();

                //txtReview에 값이 있을 때
                if(!txtReview_textCheck.isEmpty()) {
                    //match_url 함수를 통해 DB에서 가게 id값 가져오기
                    match_url(place_url);
                    //Intent에 아이디 값을 실어 액티비티 전환
                    Intent intent = new Intent(ReviewWriteActivity.this, endWriteReview.class);
                    intent.putExtra("id",user_Id);
                    startActivity(intent);
                    finish();
                }
                //txtReview에 값이 없을 때
                else{
                    txtReview.setError("Please insert your Review");
                }
            }
        });

        //별점 레이팅바
        final TextView tv = (TextView) findViewById(R.id.textView3);
        RatingBar rb = (RatingBar)findViewById(R.id.ratingBar);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //Ratingbar(별)을 드래그하거나 클릭해서 값이 변하면 텍스트뷰 tv에 별점이 몇인지 표시해준다.
                tv.setText(" " + rating);
            }
        });

        //음성인식 버튼 클릭 이벤트
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인터넷 연결 되어있을 때
                if(isConnected()){
                    //음성인식창을 띄움
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //결과값을 텍스트로 나타낸다.
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
                }}
        });
    }

    //음성인식 인터넷 연결 확인 코드
    public  boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //음성인식 결과를 뽑아내는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CODE:{
                if(resultCode == RESULT_OK && data != null){
                    // 유사값을 ArrayList에 저장
                    ArrayList<String> result =data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // 인식된 5개의 후보중 가장 유사한 단어부터 시작되는 0번째 문자열을 불러서 텍스트를 작성한다.
                    txtReview.setText(result.get(0));
                }
                break;
            }
        }
    }

    void review(){
//네트워크 관련 작업은 백그라운드 스레드에서 처리합니다.
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String review_content=txtReview.getText().toString();
                    String score = txtScore.getText().toString();
                    String page =
                            Common.SERVER_URL+"/review_insert.php?"+"&userid="+user_Id
                                    +"&place_idx="+place_idx
                                    +"&review_content="+review_content
                                    +"&score="+score;
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(ReviewWriteActivity.this,QrScanActivity.class);
        startActivity(intent);
        finish();
    }

    //QR 주소를 통해 DB에 저장된 해당 가게의 id를 가져오는 코드
    private void match_url(final String url){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_getPlaceID,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");
                            //JSONObject에 저장된 Array파일 객체 생성
                            JSONArray jsonArray = jsonObject.getJSONArray("place_id");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //jsonArray에 들어있는 데이터 확인 및 저장을 위한 for문
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    //jsonArray에 "p_id"이라는 키값으로 저장된 데이터 가져오기
                                    place_idx=object.getInt("p_id");
//                                    place_n=object.getString("p_name");
                                    //review()함수 실행
                                    review();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //QR의 url
                params.put("url",url);
                //php문으로 return
                return params;
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    //QR 주소를 통해 DB에 저장된 해당 가게의 Name를 가져오는 코드
    private void place_info(final String url){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_getPlaceNAME,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");
                            //JSONObject에 저장된 Array파일 객체 생성
                            JSONArray jsonArray = jsonObject.getJSONArray("place_info");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //jsonArray에 들어있는 데이터 확인 및 저장을 위한 for문
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    //jsonArray에 "p_id"이라는 키값으로 저장된 데이터 가져오기
                                    place_n=object.getString("p_name");
                                    //        nameText에 가게이름 띄우기
                                    nameText.setText(place_n);
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.e("place_n","");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("place_n","");
                    }

                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //QR의 url
                params.put("url",url);
                //php문으로 return
                return params;
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
