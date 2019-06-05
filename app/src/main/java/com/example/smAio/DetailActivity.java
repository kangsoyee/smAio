package com.example.smAio;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> Data1 = new ArrayList<HashMap<String, String>>();
    private HashMap<String,String> InputData1 = new HashMap<>();
    ImageView iv;
    boolean change=false;

    int place_idx;
    int avg;

    TextView txtCategory, txtPlaceName, txtStartTime, txtEndTime, txtAddress, txtTel, txtReview, txtMenu, txtPrice;
    PlaceDTO placeInfo;
    ArrayList<ReviewDTO> review_list=new ArrayList<>();
    ArrayList<ReviewDTO> score_avg = new ArrayList<>();
    ListView list;

    TextView placename;
    TextView starttime;
    TextView endtime;

    TextView info_address;
    TextView info_tel;
    TextView info_menu;
    TextView info_price;

    private String mnum;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 1) { //상세
                txtAddress.setText(placeInfo.getAddress());
                txtCategory.setText(placeInfo.getCategory());
                txtEndTime.setText(placeInfo.getEnd_time());
                txtStartTime.setText(placeInfo.getStart_time());
                txtTel.setText(placeInfo.getTel());
                txtMenu.setText(placeInfo.getMenu());
                txtPlaceName.setText(placeInfo.getPlace_name());
                txtPrice.setText(placeInfo.getPrice());
            }else if(msg.what == 2){ //수정,삭제
                finish();
            }else if(msg.what == 3){ //리뷰 목록
                ReviewAdapter adapter = new ReviewAdapter(
                        DetailActivity.this,
                        R.layout.review_row,
                        review_list);       // , 로 두가지를 집어넣어도 되는건가?
                list.setAdapter(adapter);
            }else if(msg.what==4){
                txtReview.setText("");
                review_list();
            }else  if(msg.what==5){
                //별점
                Log.i("test","check");
                final TextView tv = (TextView) findViewById(R.id.textView4);
                RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);

                float rate = avg;

                Log.i("test_rate",String.valueOf(score_avg));

                rb.setRating(rate);

                rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        tv.setText(rating+"");
                    }
                });
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent get_info = getIntent();
        String ad_data = get_info.getStringExtra("address");
        String tel_data = get_info.getStringExtra("tel");
        String menu_data = get_info.getStringExtra("menu");
        String price_data = get_info.getStringExtra("price");
        String name_data = get_info.getStringExtra("placename");
        String start_data = get_info.getStringExtra("starttime");
        String end_data = get_info.getStringExtra("endtime");

        iv = (ImageView) findViewById(R.id.heart_image);

        //drawable에 있는 이미지로 셋팅하기
        iv.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        //하트 버튼 클릭 이벤트
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change) {
                    iv.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    change = false;
                }else {
                    iv.setImageResource(R.drawable.ic_favorite_black_24dp);
                    change = true;
                }
            }
        });

        //tabHost Widget과 연결
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        //TabHost를 설정할 때 가장 먼저 호출해 주어야함
        tabHost.setup();

        setNewTab(tabHost,"정보",R.id.tabSpec1);
        setNewTab(tabHost,"리뷰",R.id.tabSpec2);
        setNewTab(tabHost,"지도",R.id.tabSpec3);

        //초기 Tab 설정
        tabHost.setCurrentTab(0);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });
        info_address = (TextView) findViewById(R.id.info_address);
        info_tel = (TextView) findViewById(R.id.info_tel);
        info_menu = (TextView) findViewById(R.id.info_menu);
        info_price = (TextView) findViewById(R.id.info_price);

        placename = (TextView) findViewById(R.id.place_name);
        starttime = (TextView) findViewById(R.id.start_time);
        endtime = (TextView) findViewById(R.id.end_time);


        info_address.setText(ad_data);
        info_tel.setText(tel_data);
        info_menu.setText(menu_data);
        info_price.setText(price_data);

        placename.setText(name_data);
        starttime.setText(start_data);
        endtime.setText(end_data);

        //전화번호 클릭 이벤트
       info_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mnum = info_tel.getText().toString();
                String tel = "tel:" + mnum;
                switch (v.getId()){
                    case R.id.info_tel:
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                        break;
                }
            }
        });

        txtStartTime=(TextView) findViewById(R.id.start_time);
        txtEndTime=(TextView) findViewById(R.id.end_time);
        txtPlaceName=(TextView) findViewById(R.id.place_name);
        list=(ListView)findViewById(R.id.detail_review_list);

        Intent intent=getIntent();
        place_idx=intent.getIntExtra("idx",0);
        review_list();
        detail();
        avg();

    }

    private void setNewTab(TabHost host, String title, int contentID) {
        TabHost.TabSpec tabSpec = host.newTabSpec(title);
        tabSpec.setIndicator(getTabIndicator(title));
        tabSpec.setContent(contentID);
        host.addTab(tabSpec);
    }

    private View getTabIndicator(String title) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_menu, null);
        TextView tv = view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

    void review_list(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();


        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    review_list = new ArrayList<ReviewDTO>();
                    String page = Common.SERVER_URL+"/review_list2.php?place_idx="+place_idx;
                    Log.e("DetailActivity","여기까지는 이동함");

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
                            //스트림 생성
                            BufferedReader br=
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(),"utf-8"));
                            while(true){
                                String line=br.readLine(); //한 라인을 읽음
                                if(line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line+"\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("test","review_list:"+sb);
// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");     //sendData 어떻게 쓰이는지
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        ReviewDTO dto = new ReviewDTO();
                        dto.setReview_content(row.getString("review_content"));
                        review_list.add(dto);
                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    class ReviewAdapter extends ArrayAdapter<ReviewDTO> {
        public ReviewAdapter(Context context, int textViewResourceId,
                             ArrayList<ReviewDTO> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.review_row, null);
            }
            final ReviewDTO dto = review_list.get(position);
            Log.i("test","review dto:"+dto);
            Log.i("test","review content:"+dto.getReview_content());
            if (dto != null) {
                TextView review_content =(TextView) v.findViewById(R.id.review_content);
                review_content.setText(dto.getReview_content());
            }
            return v;
        }
    }
    void detail(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL+"/place_detail.php?place_idx="+place_idx;

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
                            //스트림 생성
                            BufferedReader br=
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(), StandardCharsets.UTF_8));
                            while(true){
                                String line=br.readLine(); //한 라인을 읽음
                                if(line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line+"\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());

// json.get("변수명")
                    JSONObject row = (JSONObject)jsonObj.get("sendData");
                    placeInfo = new PlaceDTO();
                    placeInfo.setPlace_idx(row.getInt("place_idx"));
                    placeInfo.setEnd_time(row.getString("end_time"));
                    placeInfo.setStart_time(row.getString("start_time"));
                    placeInfo.setPlace_name(row.getString("place_name"));
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    void avg() {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    review_list = new ArrayList<ReviewDTO>();
                    String page = Common.SERVER_URL + "/score_avg.php?place_idx=" + place_idx;
                    Log.i("test_avg()", "여기까지는 이동함");

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
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //스트림 생성
                            BufferedReader br =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(), "utf-8"));
                            while (true) {
                                String line = br.readLine(); //한 라인을 읽음
                                if (line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line + "\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("test", "score_avg:" + sb);
// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");     //sendData 어떻게 쓰이는지
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        ReviewDTO dto = new ReviewDTO();
                        avg = dto.setScore_avg(row.getInt("score_avg"));
                        score_avg.add(dto);
                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}
