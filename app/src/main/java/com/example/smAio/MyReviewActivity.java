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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * MyFragment에서 내 리뷰 눌렀을때 액티비티
 */
public class MyReviewActivity extends AppCompatActivity {

    ListView list;
    ArrayList<MyReviewDTO> items;
    String id_text;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyReviewActivity.MyreviewAdapter adapter = new MyReviewActivity.MyreviewAdapter(
                    MyReviewActivity.this,
                    R.layout.myreview_row,
                    items);
            list.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        list = (ListView) findViewById(R.id.MyReviewList);

        Intent get_myreview = getIntent();
        id_text = get_myreview.getStringExtra("id");
    }

    @Override
    public void onResume() {
        super.onResume();
        list();
    }

    void list(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<MyReviewDTO>();
                    String page = Common.SERVER_URL+"/my_review.php?userid="+id_text;
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
                        MyReviewDTO dto = new MyReviewDTO();
                        dto.setPlace_name(row.getString("place_name"));/**확인필요**/
                        dto.setmyId(row.getString("userid"));/**확인필요**/
                        dto.setmyreview_content(row.getString("review_content"));/**확인필요**/
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

    class MyreviewAdapter extends ArrayAdapter<MyReviewDTO> {                 // 여기 class 이해 안됨
        //ArrayList<BookDTO> item;
        public MyreviewAdapter(Context context, int textViewResourceId,
                               ArrayList<MyReviewDTO> objects) {
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
                v = li.inflate(R.layout.myreview_row, null);
            }

            try {
                final MyReviewDTO dto = items.get(position);
                if (dto != null) {
                    TextView place_name = (TextView) v.findViewById(R.id.place_name);
                    place_name.setText(dto.getPlace_name());
                    TextView MyId = (TextView) v.findViewById(R.id.myId);
                    MyId.setText(dto.getmyId());
                    TextView myreview_content = (TextView) v.findViewById(R.id.myreview_content);
                    myreview_content.setText(dto.getmyreview_content());
                }
            }catch (Exception e){
                Log.e("Network Exception", e.getMessage());
                return null;
            }
            return v;
        }
    }

}