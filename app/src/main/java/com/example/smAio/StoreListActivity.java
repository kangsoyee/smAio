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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity {
    ListView list;
    Button btnAdd,btnSearch;
    Spinner spnCategory;
    EditText editPlaceName;
    String[] arrPlace;

    ArrayList<PlaceDTO> items;
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
        this.setContentView(R.layout.activity_store_list);

        list=(ListView)findViewById(R.id.list);
        spnCategory=(Spinner)findViewById(R.id.spnCategory);
        editPlaceName=(EditText)findViewById(R.id.editPlaceName);

        btnSearch=(Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category=arrPlace[spnCategory.getSelectedItemPosition()];
                String placeName=editPlaceName.getText().toString();
                search(category, placeName);
            }
        });

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoreListActivity.this, PlaceAdd.class);
                startActivity(intent);
            }
        });

        arrPlace=(String[])getResources().getStringArray(R.array.category);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                arrPlace);
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        list();
    }

    void list(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL+"/place_list.php";
                    Log.e("StoreListActivity","여기까지야");

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
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData"); // 이 부분 이해 안됨
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

    void search(final String category, final String place_name){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL+"/place_search.php?category="+category+"&place_name="+place_name;
                    Log.e("Mainactivity","여기까진 됨");

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
    class PlaceAdapter extends ArrayAdapter<PlaceDTO> {                 // 여기 class 이해 안됨
        //ArrayList<BookDTO> item;
        public PlaceAdapter(Context context, int textViewResourceId,
                            ArrayList<PlaceDTO> objects) {
            super(context, textViewResourceId, objects);
//this.item= objects;
        }

        @Override
        public View getView(int position, View convertView,                 // getView에 대한 이해 부족
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.place_row, null);
            }
            final PlaceDTO dto = items.get(position);
            if (dto != null) {
                TextView place_idx = (TextView) v.findViewById(R.id.place_idx);
                TextView place_name =(TextView) v.findViewById(R.id.place_name);
                TextView start_time =(TextView) v.findViewById(R.id.start_time);
                TextView end_time =(TextView) v.findViewById(R.id.end_time);
                TextView category =(TextView) v.findViewById(R.id.category);
                TextView address =(TextView) v.findViewById(R.id.address);
                TextView tel =(TextView) v.findViewById(R.id.tel);
                TextView menu = (TextView) v.findViewById(R.id.menu);
                TextView price = (TextView) v.findViewById(R.id.price);

                //place_idx.setText(dto.getPlace_idx()+"");  // 여기 주석처리 안하면 로그인 자체도 안됨
                place_name.setText(dto.getPlace_name());
                start_time.setText(dto.getStart_time());
                end_time.setText(dto.getEnd_time());
                category.setText(dto.getCategory());
                address.setText(dto.getAddress());
                tel.setText(dto.getTel());
                menu.setText(dto.getMenu());
                price.setText(dto.getPrice());
            }
            //클릭하면 코드를 넘겨서 받아옴
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(StoreListActivity.this, DetailActivity.class);
                    intent.putExtra("idx", dto.getPlace_idx()); //putExtra 는 값을 전달하는 역할을 한다. 받는곳은 getExtra 가 된다.
                    startActivity(intent);
                }
            });
            return v;
        }
    }
}