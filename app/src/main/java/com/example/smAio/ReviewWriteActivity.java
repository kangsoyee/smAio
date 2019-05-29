package com.example.smAio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewWriteActivity extends AppCompatActivity {


    private static String URL_getPlaceID ="http://eileenyoo.cafe24.com/get_placeID.php/";
    int place_idx;
    private static final int REQUEST_CODE = 1234;
    ImageButton Start;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;
    EditText txtReview;
    Button button;
    String user_Id,place_url;
    ArrayList<PlaceDTO>items;
    int place_id;
    final private static String TAG = "가져온 값";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        Intent i = getIntent();
        user_Id=i.getStringExtra("id");
        place_url=i.getStringExtra("url");
        Log.e(TAG,user_Id);
        Log.e(TAG,place_url);

        Start = (ImageButton)findViewById(R.id.imageButton);
        txtReview=(EditText)findViewById(R.id.review_message);
        button=(Button)findViewById(R.id.button_reviewsend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review();
            }
        });

        //음성인식 버튼 클릭 이벤트
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
                }}

        });

        //별점 레이팅바
        final TextView tv = (TextView) findViewById(R.id.textView3);
        RatingBar rb = (RatingBar)findViewById(R.id.ratingBar);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv.setText(" " + rating);
            }
        });
    }

    //음성인식 인터넷 연결 코드
    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //음성인식 다이얼로그 표시
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            match_text_dialog = new Dialog(ReviewWriteActivity.this);
            match_text_dialog.setContentView(R.layout.dialog_matches_frag);
            match_text_dialog.setTitle("Select Matching Text");
            textlist = (ListView)match_text_dialog.findViewById(R.id.list);
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches_text);
            textlist.setAdapter(adapter);
            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    txtReview.setText(matches_text.get(position));
                    match_text_dialog.hide();
                }
            });
            match_text_dialog.show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void review(){
//네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String review_content=txtReview.getText().toString();
                    match_url(place_url);
                    String page =
                            Common.SERVER_URL+"/review_insert.php?"+"&userid="+user_Id
                                    +"&place_idx="+place_idx
                                    +"&review_content="+review_content;
//                    Common.SERVER_URL+"/review_insert.php?userid="+Common.userid
//                            +"&place_idx="+place_idx
//                            +"&review_content="+review_content;

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

    private void match_url(final String url){ //로그인을 위한 함수 edittext에 입력된 아이디와 비밀번호의 값을 가진다
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_getPlaceID, //순서대로 php문에 POST 형식으로 값 보내기, php문 주소
                new Response.Listener<String>() { //php문에서 온 응답에 대한 이벤트
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response); //php문에서 json파일에 응답을 줌 그래서 jsonobject를 통해 응답 확인
                            String success = jsonObject.getString("success"); //php문에서 제이슨 파일에 success라는 키에 1이라는 값을 줌

                            JSONArray jsonArray = jsonObject.getJSONArray("place_id"); //php문에서 array변수에 login 데이터를 담고 jsonArray형식으로 jsonObject에 저장 그래서 그 값을 불러옴

                            if(success.equals("1")){ //만약에 json파일에 success키에 맞는 값이 1면
                                for(int i = 0; i<jsonArray.length();i++){ //jsonArray 크기만큼 for문 돌림
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    place_idx=object.getInt("p_id");
                                    Log.e(TAG,place_idx+"");//jsonArray에 저장된 이름(name)값을 가져온다
                                }
                            }
                            else{
                            }
                        }catch (JSONException e){

                            e.printStackTrace();
                            //이건 아예 아이디가 다르다는 뜻

                        }
                    }
                },
                new Response.ErrorListener() { //여기로 오류 잡힘 서버 접속 오류
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("url",url);
                return params; //hashmap을 통해서 값을 php문에 보내는 구문!
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); //요거는 volley사용을 위한 필수적인 코드 두줄

    }
}
