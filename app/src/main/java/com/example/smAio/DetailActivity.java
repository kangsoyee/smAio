package com.example.smAio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> Data1 = new ArrayList<HashMap<String, String>>();
    private HashMap<String,String> InputData1 = new HashMap<>();

    ImageView iv;
    boolean change=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        ListView listView2 = (ListView) findViewById(R.id.ListView2);
        //데이터 초기화
        InputData1.put("name","김수연");
        Data1.add(InputData1);

        //simpleAdapter 생성
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,Data1,android.R.layout.simple_list_item_2,
                new String[]{"name","review"},
                new int[]{android.R.id.text1,android.R.id.text2});

        //InputData1.put("review","너무 맛있어요~");
        //listView2.setAdapter(simpleAdapter);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        StoreListFragment fragment5 = new StoreListFragment();
        transaction.replace(R.id.storelist_container, fragment5);
        transaction.commit();
//        list=(ListView)findViewById(R.id.list);
//        review_list();

        //별점
        final TextView tv = (TextView) findViewById(R.id.textView4);
        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv.setText("" + rating);
            }
        });

        //tabHost Widget과 연결
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        //TabHost를 설정할 때 가장 먼저 호출해 주어야함
        tabHost.setup();

        //Tab Spec생성 (Tab Spec:탭을 구성하는 요소들의 집합)
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("정보");
        tabSpec1.setContent(R.id.tabSpec1);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("리뷰");
        tabSpec2.setContent(R.id.tabSpec2);

        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("tab3");
        tabSpec3.setIndicator("지도");
        tabSpec3.setContent(R.id.tabSpec3);

        //Tab 추가
        tabHost.addTab(tabSpec1);
        tabHost.addTab(tabSpec2);
        tabHost.addTab(tabSpec3);

        //초기 Tab 설정
        tabHost.setCurrentTab(0);
    }
/*
    int place_idx;

    EditText txtCategory, txtPlaceName, txtStartTime, txtEndTime, txtAddress, txtTel, txtReview, txtMenu, txtPrice;
    Button btnUpdate, btnDelete, btnReview;
    ListView list;
    PlaceDTO placeInfo;
    ArrayList<ReviewDTO> review_list=new ArrayList<>();

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
                txtPlaceName.setText(placeInfo.getPlace_name());
                txtMenu.setText(placeInfo.getMenu());
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        txtCategory=(EditText)findViewById(R.id.category);
        txtStartTime=(EditText)findViewById(R.id.start_time);
        txtEndTime=(EditText)findViewById(R.id.end_time);
        txtPlaceName=(EditText)findViewById(R.id.place_name);
        txtAddress=(EditText)findViewById(R.id.address);
        txtTel=(EditText)findViewById(R.id.tel);
        txtMenu = (EditText) findViewById(R.id.menu);
        txtPrice = (EditText) findViewById(R.id.price);
        txtReview=(EditText)findViewById(R.id.review);

        btnUpdate=(Button)findViewById(R.id.btnUpdate);
        btnDelete=(Button)findViewById(R.id.btnDelete);
        btnReview=(Button)findViewById(R.id.btnReview);

        list=(ListView)findViewById(R.id.list);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailActivity.this)
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review();
            }
        });

        Intent intent=getIntent();
        place_idx=intent.getIntExtra("idx",0);
        Toast.makeText(this, "번호:"+place_idx, Toast.LENGTH_SHORT).show();     // 이게 어떻게 쓰이는 건지 모르겠음

        detail();

        review_list();
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
                                                    conn.getInputStream(),"UTF-8"));
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
                    placeInfo.setAddress(row.getString("address"));
                    placeInfo.setCategory(row.getString("category"));
                    placeInfo.setEnd_time(row.getString("end_time"));
                    placeInfo.setStart_time(row.getString("start_time"));
                    placeInfo.setTel(row.getString("tel"));
                    placeInfo.setPlace_name(row.getString("place_name"));
                    placeInfo.setMenu(row.getString("menu"));
                    placeInfo.setPrice(row.getString("price"));
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    void update(){
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
                            Common.SERVER_URL+"/place_update.php?start_time="+start_time+"&end_time="+end_time
                                    +"&place_name="+place_name
                                    +"&address="+address
                                    +"&tel="+tel
                                    +"&category="+category
                                    +"&place_idx="+place_idx
                                    +"&menu="+menu
                                    +"&price"+price;

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
                    handler.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    void delete(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL+"/place_delete.php?place_idx="+place_idx;

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
                    handler.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    void review(){
//네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String review_content=txtReview.getText().toString();
//                    String userid="kim";
                    String page =
                            Common.SERVER_URL+"/review_insert.php?idx="+place_idx+"&userid="+Common.userid
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
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
    void review_list(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();


        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    review_list = new ArrayList<ReviewDTO>();
                    String page = Common.SERVER_URL+"/review_list.php?place_idx="+place_idx;
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
    }*/
}

