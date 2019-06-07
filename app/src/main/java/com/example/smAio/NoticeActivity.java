package com.example.smAio;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    ListView noticeListView;
    NoticeListAdapter adapter;
    List<Notice> noticeList;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
            noticeListView.setAdapter(adapter);
        }
    };

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        notice();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticeList = new ArrayList<Notice>();

        notice();
    }

    void notice(){
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder();

        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL+"/NoticeList.php";
                    Log.i("NoticeActivity","php정상작동");

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
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    int count =0;
                    String noticeContent, noticeName, noticeDate;
                    while(count< jsonArray.length())
                    {
                        JSONObject object = jsonArray.getJSONObject(count);
                        noticeContent = object.getString("noticeContent");
                        noticeName = object.getString("noticeName");
                        noticeDate = object.getString("noticeDate");
                        Notice notice = new Notice(noticeContent, noticeName, noticeDate);
                        noticeList.add(notice);
                        count++;
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
}
