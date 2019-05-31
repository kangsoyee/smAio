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

public class StoreListActivity2 extends AppCompatActivity {
    ListView list;
    Button btnSearch;
    Spinner spnCategory;
    EditText editPlaceName;
    String[] arrPlace;

    ArrayList<PlaceDTO2> items;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            StoreListActivity2.PlaceAdapter adapter = new StoreListActivity2.PlaceAdapter(
                    StoreListActivity2.this,
                    R.layout.place_row2,
                    items);
            list.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_store_list);

        list = (ListView) findViewById(R.id.list);
        spnCategory = (Spinner) findViewById(R.id.spnCategory);
        editPlaceName = (EditText) findViewById(R.id.editPlaceName);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = arrPlace[spnCategory.getSelectedItemPosition()];
                String placeName = editPlaceName.getText().toString();
                search(category, placeName);
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
                    items = new ArrayList<PlaceDTO2>();
                    String page = Common.SERVER_URL + "/place_list.php";
                    Log.e("StoreListActivity2", "여기까지야");

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
                        PlaceDTO2 dto2 = new PlaceDTO2();
                        dto2.setPlace_idx(row.getInt("place_idx2"));
                        dto2.setCategory(row.getString("category2"));
                        dto2.setPlace_name(row.getString("place_name2"));
                        dto2.setStart_time(row.getString("start_time2"));
                        dto2.setEnd_time(row.getString("end_time2"));
                        dto2.setAddress(row.getString("address2"));
                        dto2.setTel(row.getString("tel2"));
                        dto2.setMenu(row.getString("menu2"));
                        dto2.setPrice(row.getString("price2"));
                        dto2.setLatitude(row.getString("latitude2"));
                        dto2.setLongitude(row.getString("longitude2"));

                        if (!row.isNull("image2"))
                            dto2.setImage(row.getString("image2"));

                        Log.e("test2", dto2.getImage());

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

    void search(final String category, final String place_name) {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<PlaceDTO2>();
                    String page = Common.SERVER_URL + "/place_search.php?category=" + category + "&place_name=" + place_name;
                    Log.e("Mainactivity", "여기까진 됨");

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
                        PlaceDTO2 dto2 = new PlaceDTO2();
                        dto2.setPlace_idx(row.getInt("place_idx2"));
                        dto2.setAddress(row.getString("address2"));
                        dto2.setCategory(row.getString("category2"));
                        dto2.setEnd_time(row.getString("end_time2"));
                        dto2.setStart_time(row.getString("start_time2"));
                        dto2.setTel(row.getString("tel2"));
                        dto2.setPlace_name(row.getString("place_name2"));
                        dto2.setMenu(row.getString("menu2"));
                        dto2.setPrice(row.getString("price2"));
                        dto2.setLatitude(row.getString("latitude2"));
                        dto2.setLongitude(row.getString("longitude2"));

                        if (!row.isNull("image2"))
                            dto2.setImage(row.getString("image2"));

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

    class PlaceAdapter extends ArrayAdapter<PlaceDTO2> {                 // 여기 class 이해 안됨
        //ArrayList<BookDTO> item;
        public PlaceAdapter(Context context, int textViewResourceId,
                            ArrayList<PlaceDTO2> objects) {
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

                final PlaceDTO2 dto2 = items.get(position);
                if (dto2 != null) {
                    TextView place_name = (TextView) v.findViewById(R.id.place_name);
                    TextView start_time = (TextView) v.findViewById(R.id.start_time);
                    TextView end_time = (TextView) v.findViewById(R.id.end_time);
                    TextView category = (TextView) v.findViewById(R.id.category);
                    TextView address = (TextView) v.findViewById(R.id.address);
                    TextView tel = (TextView) v.findViewById(R.id.tel);
                    TextView menu = (TextView) v.findViewById(R.id.menu);
                    TextView price = (TextView) v.findViewById(R.id.price);
                    ImageView imgPlace = (ImageView) v.findViewById(R.id.imgPlace);

                    //place_idx.setText(dto.getPlace_idx()+"");  // 여기 주석처리 안하면 로그인 자체도 안됨
                    place_name.setText(dto2.getPlace_name());
                    start_time.setText(dto2.getStart_time());
                    end_time.setText(dto2.getEnd_time());
                    category.setText(dto2.getCategory());
                    address.setText(dto2.getAddress());
                    tel.setText(dto2.getTel());
                    menu.setText(dto2.getMenu());
                    price.setText(dto2.getPrice());

                    Glide.with(StoreListActivity2.this).load(dto2.getImage()).into(imgPlace);
                }
                //클릭하면 코드를 넘겨서 받아옴
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView address = (TextView) v.findViewById(R.id.address);
                        TextView tel = (TextView) v.findViewById(R.id.tel);
                        TextView menu = (TextView) v.findViewById(R.id.menu);
                        TextView price = (TextView) v.findViewById(R.id.price);
                        TextView placename = (TextView) v.findViewById(R.id.place_name);
                        TextView starttime = (TextView) v.findViewById(R.id.start_time);
                        TextView endtime = (TextView) v.findViewById(R.id.end_time);

                        Intent intent = new Intent(StoreListActivity2.this, DetailActivity.class);
                        intent.putExtra("idx2", dto2.getPlace_idx()); //putExtra 는 값을 전달하는 역할을 한다. 받는곳은 getExtra 가 된다.

                        intent.putExtra("address2", address.getText().toString());
                        intent.putExtra("tel2", tel.getText().toString());
                        intent.putExtra("menu2", menu.getText().toString());
                        intent.putExtra("price2", price.getText().toString());
                        intent.putExtra("placename2", placename.getText().toString());
                        intent.putExtra("starttime2", starttime.getText().toString());
                        intent.putExtra("endtime2", endtime.getText().toString());
                        intent.putExtra("latitude2", dto2.getLatitude());
                        intent.putExtra("longitude2", dto2.getLongitude());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_Alphabetical:
                Toast.makeText(this, "가나다순으로 정렬", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sort_rating:
                Toast.makeText(this, "평점순으로 정렬", Toast.LENGTH_SHORT).show();
                return true;

        }

        return true;
    }

}