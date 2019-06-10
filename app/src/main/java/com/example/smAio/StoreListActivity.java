package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity  {
    ListView list;
    ImageButton btnSearch;
    EditText editPlaceName;
    String[] arrPlace;
    String userid;
    ArrayList<PlaceDTO> items;

    //thread 실행 결과값을 핸들러로 불러온다.
    //place_row에 있는것을 리스트 형태로 adapter에 전달한다.
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PlaceAdapter adapter = new PlaceAdapter(
                    StoreListActivity.this,
                    R.layout.place_row,
                    items);
            list.setAdapter(adapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_store_list1~4까지 모두 같은 내용의 xml이라서 하나로 통일하였다.
        this.setContentView(R.layout.activity_store_list);

        Intent getId=getIntent();
        userid=getId.getStringExtra("userid");

        list=(ListView)findViewById(R.id.list);
        editPlaceName=(EditText)findViewById(R.id.editPlaceName);

        btnSearch=(ImageButton)findViewById(R.id.btnSearch);

        //검색 버튼을 클릭하면 생기는 이벤트이다.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색버튼 눌렀을때 키보드 사라지게 해준다.(밑으로 내려준다)
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPlaceName.getWindowToken(), 0);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //editText에 입력한 상점 이름을 가져와 search()에 넣어 실행한다.
                        String placeName=editPlaceName.getText().toString();
                        search(placeName);
                    }
                }, 100);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        list();
    }

    //상점 리스트를 보여주는데 사용되는 메소드
    void list(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL+"/place_list.php";

                    URL url = new URL(page);
                    // 커넥션 객체 생성
                    //HTTPURLConnection을 통해 해당 URL에 출력되는 결과물을 얻어올 수 있다.
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

                    // json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        PlaceDTO dto = new PlaceDTO();
                        dto.setPlace_idx(row.getInt("place_idx"));
                        dto.setCategory(row.getString("category"));
                        dto.setPlace_name(row.getString("place_name"));
                        dto.setStart_time(row.getString("start_time"));
                        dto.setEnd_time(row.getString("end_time"));
                        dto.setAddress(row.getString("address"));
                        dto.setTel(row.getString("tel"));
                        dto.setMenu(row.getString("menu"));
                        dto.setPrice(row.getString("price"));
                        dto.setLatitude(row.getString("latitude"));
                        dto.setLongitude(row.getString("longitude"));

                        if(!row.isNull("image"))
                            dto.setImage(row.getString("image"));

                        Log.e("test", dto.getImage());

                        items.add(dto);
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

    //검색에 사용되는 메소드
    void search(final String place_name){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL+"/place_search_food.php?place_name="+place_name;

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

                    // json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        PlaceDTO dto = new PlaceDTO();
                        dto.setPlace_idx(row.getInt("place_idx"));
                        dto.setAddress(row.getString("address"));
                        dto.setCategory(row.getString("category"));
                        dto.setEnd_time(row.getString("end_time"));
                        dto.setStart_time(row.getString("start_time"));
                        dto.setTel(row.getString("tel"));
                        dto.setPlace_name(row.getString("place_name"));
                        dto.setMenu(row.getString("menu"));
                        dto.setPrice(row.getString("price"));
                        dto.setLongitude(row.getString("longitude"));
                        dto.setLatitude(row.getString("latitude"));

                        if(!row.isNull("image"))
                            dto.setImage(row.getString("image"));

                        items.add(dto);
                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    list();
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    //ListView에 아이템을 추가,수정,삭제할때 사용된다.
    //데이터를 Adapter에 리스트 형태로 전달한다.
    class PlaceAdapter extends ArrayAdapter<PlaceDTO> {
        //ArrayList<BookDTO> item;
        public PlaceAdapter(Context context, int textViewResourceId,
                            ArrayList<PlaceDTO> objects) {
            super(context, textViewResourceId, objects);
        }
        //화면이 디스플레이 되기 전에 getView() 메소드가 호출된다.
        //getView() 메소드는 화면에 보여져야 할 아이템의 수 만큼 호출된다.
        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                //LayoutInflater는 xml에 정의된 resource들을 view 형태로 반환해준다.
                //배경이 될 layout을 만들어 놓고 view 형태로 반환받아 Activity에서 실행하게 된다.
                LayoutInflater li = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.place_row, null);
            }
            try {

                final PlaceDTO dto = items.get(position);
                if (dto != null) {
                    TextView place_name = (TextView) v.findViewById(R.id.place_name);
                    TextView start_time = (TextView) v.findViewById(R.id.start_time);
                    TextView end_time = (TextView) v.findViewById(R.id.end_time);
                    TextView address = (TextView) v.findViewById(R.id.address);
                    TextView tel = (TextView) v.findViewById(R.id.tel);
                    TextView menu = (TextView) v.findViewById(R.id.menu);
                    TextView price = (TextView) v.findViewById(R.id.price);
                    ImageView imgPlace = (ImageView) v.findViewById(R.id.imgPlace);
                    TextView latitude = (TextView)v.findViewById(R.id.latitude);
                    TextView longitude = (TextView)v.findViewById(R.id.longitude);

                    place_name.setText(dto.getPlace_name());
                    start_time.setText(dto.getStart_time());
                    end_time.setText(dto.getEnd_time());
                    address.setText(dto.getAddress());
                    tel.setText(dto.getTel());
                    menu.setText(dto.getMenu());
                    price.setText(dto.getPrice());
                    latitude.setText(dto.getLatitude());
                    longitude.setText(dto.getLongitude());

                    Glide.with(StoreListActivity.this).load(dto.getImage()).into(imgPlace);
                }

                //클릭하면 코드를 넘겨서 받아옴
                v.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView address = (TextView) v.findViewById(R.id.address);
                        TextView tel = (TextView) v.findViewById(R.id.tel);
                        TextView menu = (TextView) v.findViewById(R.id.menu);
                        TextView price = (TextView) v.findViewById(R.id.price);
                        TextView placename = (TextView) v.findViewById(R.id.place_name);
                        TextView starttime = (TextView) v.findViewById(R.id.start_time);
                        TextView endtime = (TextView) v.findViewById(R.id.end_time);
                        TextView latitude = (TextView)v.findViewById(R.id.latitude);
                        TextView longitude = (TextView)v.findViewById(R.id.longitude);

                        //StoreListActivity와 DetailActivity 간에 정보를 주고받기 위해 인텐트에 데이터를 넣어 보낸다.
                        Intent intent = new Intent(StoreListActivity.this, DetailActivity.class);
                        intent.putExtra("idx", dto.getPlace_idx()); //putExtra 는 값을 전달하는 역할을 한다. 받는곳은 getExtra 가 된다.

                        //intent에 putExtra 메서드를 사용하여 데이터를 넣는다.
                        //첫번째 인자는 나중에 데이터를 꺼내기 위한 키 값이고 두번째 인자는 전달할 데이터이다.
                        intent.putExtra("address",address.getText().toString());
                        intent.putExtra("tel",tel.getText().toString());
                        intent.putExtra("menu",menu.getText().toString());
                        intent.putExtra("price",price.getText().toString());
                        intent.putExtra("placename",placename.getText().toString());
                        intent.putExtra("starttime",starttime.getText().toString());
                        intent.putExtra("endtime",endtime.getText().toString());
                        intent.putExtra("userid",userid);

                        dto.setLat(latitude.getText().toString());
                        dto.setLng(longitude.getText().toString());
                        Log.e("test", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ"+dto.getLatitude());

                        //startActivity 메서드 사용하여 데이터를 DetailActivity로 보낸다.
                        startActivity(intent);
                    }
                });

            }catch (Exception e){
                Log.e("Network Exception", e.getMessage());
                return null;
            }
            return v;
        }
    }
}