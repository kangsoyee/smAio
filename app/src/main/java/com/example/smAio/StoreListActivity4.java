package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StoreListActivity4 extends AppCompatActivity {
    ListView list;
    Button btnSearch;
    Spinner spnCategory;
    EditText editPlaceName;
    String[] arrPlace;
    String userid;

    ArrayList<PlaceDTO> items;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            StoreListActivity4.PlaceAdapter adapter = new StoreListActivity4.PlaceAdapter(
                    StoreListActivity4.this,
                    R.layout.place_row2,
                    items);
            list.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_store_list);

        Intent getId=getIntent();
        userid=getId.getStringExtra("userid");

        list = (ListView) findViewById(R.id.list);
        editPlaceName = (EditText) findViewById(R.id.editPlaceName);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = arrPlace[spnCategory.getSelectedItemPosition()];
                String placeName = editPlaceName.getText().toString();
                search(placeName);
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPlaceName.getWindowToken(), 0);
            }
        });

        arrPlace = (String[]) getResources().getStringArray(R.array.category);
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

    void list() {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL + "/pc_list.php";
                    Log.e("StoreListActivity4", "여기까지야");

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

// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData"); // 이 부분 이해 안됨
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        PlaceDTO dto4 = new PlaceDTO();
                        dto4.setPlace_idx(row.getInt("place_idx"));
                        dto4.setCategory(row.getString("category"));
                        dto4.setPlace_name(row.getString("place_name"));
                        dto4.setStart_time(row.getString("start_time"));
                        dto4.setEnd_time(row.getString("end_time"));
                        dto4.setAddress(row.getString("address"));
                        dto4.setTel(row.getString("tel"));
                        dto4.setLatitude(row.getString("latitude"));
                        dto4.setLongitude(row.getString("longitude"));

                        if (!row.isNull("image"))
                            dto4.setImage(row.getString("image"));

                        Log.e("test4", dto4.getImage());

                        items.add(dto4);
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

    void search(final String place_name) {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
//                    category=" + category + "&
                    items = new ArrayList<PlaceDTO>();
                    String page = Common.SERVER_URL + "/place_search_pc.php?place_name="+place_name;
                    Log.i("test_cafe",place_name);

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

// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        PlaceDTO dto2 = new PlaceDTO();
                        dto2.setPlace_idx(row.getInt("place_idx"));
                        dto2.setAddress(row.getString("address"));
                        dto2.setCategory(row.getString("category"));
                        dto2.setEnd_time(row.getString("end_time"));
                        dto2.setStart_time(row.getString("start_time"));
                        dto2.setTel(row.getString("tel"));
                        dto2.setPlace_name(row.getString("place_name"));
                        //dto2.setLatitude(row.getString("latitude"));
                        //dto2.setLongitude(row.getString("longitude"));

                        if (!row.isNull("image"))
                            dto2.setImage(row.getString("image"));

                        items.add(dto2);

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
                v = li.inflate(R.layout.place_row2, null);
            }

            try {

                final PlaceDTO dto4 = items.get(position);
                if (dto4 != null) {
                    TextView place_name = (TextView) v.findViewById(R.id.place_name);
                    TextView start_time = (TextView) v.findViewById(R.id.start_time);
                    TextView end_time = (TextView) v.findViewById(R.id.end_time);
                    TextView category = (TextView) v.findViewById(R.id.category);
                    TextView address = (TextView) v.findViewById(R.id.address);
                    TextView tel = (TextView) v.findViewById(R.id.tel);
                    ImageView imgPlace = (ImageView) v.findViewById(R.id.imgPlace);
                    TextView latitude = (TextView)v.findViewById(R.id.latitude);
                    TextView longitude = (TextView)v.findViewById(R.id.longitude);

                    //place_idx.setText(dto.getPlace_idx()+"");  // 여기 주석처리 안하면 로그인 자체도 안됨
                    place_name.setText(dto4.getPlace_name());
                    start_time.setText(dto4.getStart_time());
                    end_time.setText(dto4.getEnd_time());
                    //category.setText(dto4.getCategory());
                    address.setText(dto4.getAddress());
                    tel.setText(dto4.getTel());
                    latitude.setText(dto4.getLatitude());
                    longitude.setText(dto4.getLongitude());

                    Glide.with(StoreListActivity4.this).load(dto4.getImage()).into(imgPlace);
                }
                //클릭하면 코드를 넘겨서 받아옴
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView address = (TextView) v.findViewById(R.id.address);
                        TextView tel = (TextView) v.findViewById(R.id.tel);
                        TextView placename = (TextView) v.findViewById(R.id.place_name);
                        TextView starttime = (TextView) v.findViewById(R.id.start_time);
                        TextView endtime = (TextView) v.findViewById(R.id.end_time);
                        TextView latitude = (TextView)v.findViewById(R.id.latitude);
                        TextView longitude = (TextView)v.findViewById(R.id.longitude);

                        Intent intent = new Intent(StoreListActivity4.this, DetailActivity.class);
                        intent.putExtra("idx", dto4.getPlace_idx()); //putExtra 는 값을 전달하는 역할을 한다. 받는곳은 getExtra 가 된다.

                        intent.putExtra("address", address.getText().toString());
                        intent.putExtra("tel", tel.getText().toString());
                        intent.putExtra("placename", placename.getText().toString());
                        intent.putExtra("starttime", starttime.getText().toString());
                        intent.putExtra("endtime", endtime.getText().toString());
                        intent.putExtra("latitude",latitude.getText().toString());
                        intent.putExtra("longitude",longitude.getText().toString());
                        intent.putExtra("userid",userid);

                        dto4.setLat(latitude.getText().toString());
                        dto4.setLng(longitude.getText().toString());

                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                Log.e("Network Exception", e.getMessage());
                return null;
            }
            return v;
        }
    }
}
