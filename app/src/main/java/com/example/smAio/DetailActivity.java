package com.example.smAio;

import android.content.Context;
import android.graphics.Paint;
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
import android.widget.Toast;

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
/**
 * 각 상점을 클릭했을시 넘어오는 액티비티이다. 이곳에선 찜하기, 상점 정보, 상점 위치
 * 상점의 리뷰 평균을 확인할 수 있다.
 */

public class DetailActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> Data1 = new ArrayList<HashMap<String, String>>();
    private HashMap<String,String> InputData1 = new HashMap<>();
    ImageView iv;

    int place_idx;
    int avg;

    TextView txtCategory, txtPlaceName, txtStartTime, txtEndTime, txtAddress, txtTel, txtReview, txtMenu, txtPrice, txtmenu, txtprice;
    PlaceDTO placeInfo;
    ArrayList<ReviewDTO> review_list=new ArrayList<>(); // 각 상점별 리뷰를 담을 수 있는 array list를 만들어준다.
    ArrayList<ReviewDTO> score_avg = new ArrayList<>(); // 평균값을 담을 수 있는 array list를 만들어준다.
    ListView list;


    //StoreListActivity에서 불러온 정보를 담는 Textview
    TextView placename;
    TextView startendtime;

    TextView info_address;
    TextView info_tel;
    TextView info_menu;
    TextView info_price;
    String thisuserid;

    Boolean check=false;
    private String mnum;


    final private static String URL_sendHeart = "http://eileenyoo1.cafe24.com/sendHeart.php/";
    final private static String URL_deleteHeart = "http://eileenyoo1.cafe24.com/deleteHeart.php/";
    final private static String URL_heartCheck = "http://eileenyoo1.cafe24.com/heartcheck.php/";

    Handler handler = new Handler() { // 네트워크 작업을 사용했으므로 쓰레드를 만들어 이 곳에서 처리한다.
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 1) { // placeInfo 의 값을 받아와서 txt에 넣어준다.
                txtAddress.setText(placeInfo.getAddress());
                txtCategory.setText(placeInfo.getCategory());
                txtEndTime.setText(placeInfo.getEnd_time());
                txtStartTime.setText(placeInfo.getStart_time());
                txtTel.setText(placeInfo.getTel());
                txtMenu.setText(placeInfo.getMenu());
                txtPlaceName.setText(placeInfo.getPlace_name());
                txtPrice.setText(placeInfo.getPrice());
//            }else if(msg.what == 2){ //수정,삭제
//                finish();
            }else if(msg.what == 3){ //리뷰 목록
                ReviewAdapter adapter = new ReviewAdapter(
                        DetailActivity.this,
                        R.layout.review_row,
                        review_list);
                list.setAdapter(adapter);
            }else if(msg.what==4){
                txtReview.setText("");
                review_list();
            }else  if(msg.what==5){
                //별점
                Log.i("test","check");
                final TextView tv = (TextView) findViewById(R.id.textView4); // 평균점수를 나타낼 수 있는 텍스트를 설정한다.
                RatingBar rb = (RatingBar) findViewById(R.id.ratingBar); // 점수를 별 모양으로 출력해준다.

                float rate = avg; // 평균값을 float 형으로 변환시킨다.

                Log.i("test_rate",rate+"");

                rb.setRating(rate);
                tv.setText(rate+"");

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.i("test_DetailActivity","onCreate 시작");

        //getIntent 메서드를 이용해 StoreListActivity에서 보낸 데이터를 받는다.
        Intent get_info = getIntent();

        //intent의 get타입명Extra 메서드를 호출한다.
        // 이때 StoreListActivity에서 putExtra로 지정했던 데이터의 키 값을 지정하면 해당하는 데이터 값이 나오게 된다.
        //만약 지정한 키 값에 맞는 데이터가 없으면 null이 반환된다.
        String ad_data = get_info.getStringExtra("address");
        String tel_data = get_info.getStringExtra("tel");
        String menu_data = get_info.getStringExtra("menu");
        String price_data = get_info.getStringExtra("price");
        final String name_data = get_info.getStringExtra("placename");
        String start_data = get_info.getStringExtra("starttime");
        String end_data = get_info.getStringExtra("endtime");
        place_idx=get_info.getIntExtra("idx",0);
        Log.i("값 테스트",name_data+"");
        Log.i("값 테스트",thisuserid+"");

        info_address = (TextView) findViewById(R.id.info_address);
        info_tel = (TextView) findViewById(R.id.info_tel);
        info_menu = (TextView) findViewById(R.id.info_menu);
        info_price = (TextView) findViewById(R.id.info_price);
        placename = (TextView) findViewById(R.id.place_name);
        startendtime = (TextView) findViewById(R.id.start_end_time);
        txtStartTime=(TextView) findViewById(R.id.start_time);
        txtEndTime=(TextView) findViewById(R.id.end_time);
        txtPlaceName=(TextView) findViewById(R.id.place_name);
        list=(ListView)findViewById(R.id.detail_review_list);
        txtmenu=(TextView)findViewById(R.id.menu_text);
        txtprice=(TextView)findViewById(R.id.price_text);

        //StoreListActivity에서 가져온 값을 텍스트뷰에 보여준다.
        info_address.setText(ad_data);
        info_tel.setText(tel_data);
        info_menu.setText(menu_data);
        info_price.setText(price_data);
        placename.setText(name_data);
        startendtime.setText(start_data+" ~ "+end_data);
        info_tel.setPaintFlags(info_tel.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        if(info_menu.getText() == ""){
            txtmenu.setText("");
            txtprice.setText("");
        }

        //전화번호가 적혀있는 info_tel 텍스트뷰의 클릭 이벤트
        info_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전화번호가 적혀있는 텍스트뷰(info_tel)를 클릭할때만 그 텍스트뷰 값을 받아와 저장한 후 다이얼로 화면 전환하여 번호를 띄워준다.
                mnum = info_tel.getText().toString();
                String tel = "tel:" + mnum;
                switch (v.getId()){
                    case R.id.info_tel:
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                        break;
                }
            }
        });

        heartCheck(thisuserid,name_data);
        iv = (ImageView) findViewById(R.id.heart_image);

        //하트 버튼 클릭 이벤트
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv.isSelected()) {//만약 버튼이 선택되어있다면
                    iv.setSelected(false); //클릭했을때 선택이 안된걸로
                    iv.setImageResource(R.drawable.ic_favorite_border_black_24dp); //이미지도 수정
                    Log.i("testheart","deleteheart");
                    deleteHeart(thisuserid,name_data);
                    Log.i("testheart","dh Finish");
                }else { //만약 버튼이 선택되지 않았다면
                    iv.setImageResource(R.drawable.ic_favorite_black_24dp); //이미지수정
                    iv.setSelected(true); //선택된걸로
                    Log.i("testheart","sendheart");
                    sendHeart(thisuserid,name_data);

                    Log.i("testheart","send Finish");
                }
            }
        });

        //tabHost Widget과 연결
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        //TabHost를 설정할 때 가장 먼저 호출해 주어야함
        //만약 setup() 함수를 호출하지 않으면 TabWidget이 정상적으로 표시되지 않는다.
        tabHost.setup();

        //TabHost에 탭을 지정하였다.
        //(host,title,content ID)
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

        setTitle("");
        review_list();
        avg();
    }

    //TabHost에 탭을 지정해줄때 사용하기위해 만든 함수이다.
    private void setNewTab(TabHost host, String title, int contentID) {
        TabHost.TabSpec tabSpec = host.newTabSpec(title);
        tabSpec.setIndicator(getTabIndicator(title));
        tabSpec.setContent(contentID);
        host.addTab(tabSpec);
    }

    //각각의 탭에 이름(title)을 붙여주기 위해 사용하는 함수이다.
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
                    String page = Common.SERVER_URL+"/review_list.php?place_idx="+place_idx;
                    Log.e("DetailActivity","review_list 확인");

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
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
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

    void avg() {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    review_list = new ArrayList<ReviewDTO>();
                    String page = Common.SERVER_URL + "/score_avg.php?place_idx=" + place_idx;
                    Log.i("DetailActivity", "score_avg 확인");

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
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
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

    void sendHeart(final String userId, final String place_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_sendHeart, //php문에 POST형식으로, URL_SignUp 주소에 저장된 php문에 보냄
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //php문 응답에 대한 코드
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                            if(success.equals("1")){//그 값이 1이면(성공)
                                Toast.makeText(DetailActivity.this,"찜목록에 추가되었습니다.",Toast.LENGTH_SHORT).show();//찜성공 메시지
                            }
                        }catch (JSONException e){ //오류발생
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//오류발생
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("name",place_name);
                return params;

                //php문에 값을 보냄
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    void deleteHeart(final String userId, final String place_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_deleteHeart, //php문에 POST형식으로, URL_SignUp 주소에 저장된 php문에 보냄
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //php문 응답에 대한 코드
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                            if(success.equals("1")){//그 값이 1이면(성공)
                                Toast.makeText(DetailActivity.this,"찜목록에서 삭제되었습니다.",Toast.LENGTH_SHORT).show();//찜성공 메시지
                            }
                        }catch (JSONException e){ //오류발생
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//오류발생
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("name",place_name);
                return params;

                //php문에 값을 보냄
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    void heartCheck(final String userId, final String place_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_heartCheck, //php문에 POST형식으로, URL_SignUp 주소에 저장된 php문에 보냄
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //php문 응답에 대한 코드
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                            if(success.equals("1")){//그 값이 1이면(성공)
                                iv.setSelected(true);
                                iv.setImageResource(R.drawable.ic_favorite_black_24dp); //이미지수정
                            }
                        }catch (JSONException e){ //오류발생
                            e.printStackTrace();
                            iv.setSelected(false); //클릭했을때 선택이 안된걸로
                            iv.setImageResource(R.drawable.ic_favorite_border_black_24dp); //이미지 수정
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//오류발생
                        check=false;
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("name",place_name);
                return params;

                //php문에 값을 보냄
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
