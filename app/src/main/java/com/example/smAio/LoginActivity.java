package com.example.smAio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    //변수 선언
    TextView txtResult;
    EditText editId, editPwd;
    String result="";
    String userid="";
    String name="";
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtResult=(TextView)findViewById(R.id.txtResult);
        editId=(EditText)findViewById(R.id.editId);
        editPwd=(EditText)findViewById(R.id.editPwd);

        signup=(Button)findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSignUpActivity = new Intent(LoginActivity.this, MailActivity.class);
                startActivity(startSignUpActivity);
            }
        });

    }

    //로그인 버튼 클릭
    public void Click_Signin(View v){

        HashMap map=new HashMap<>();
        map.keySet();

        //백그라운드 스레드로 아이디, 패스워드를 웹서버에 전달
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //웹서버의 주소
                    String page=
                            Common.SERVER_URL+"/login_check.php";
                    URL url = new URL(page);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    String param="userid="+editId.getText().toString()+"&passwd="+editPwd.getText().toString();
                    Log.i("test","page:"+page);
                    Log.i("test","param:"+param);
                    // 연결되면
                    StringBuilder sb=new StringBuilder();
                    if (conn != null) {
                        //타임아웃 시간 설정
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("POST");
                        //캐쉬 사용 여부
                        conn.setUseCaches(false);
                        conn.getOutputStream().write(param.getBytes("utf-8"));
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
//스트링을 json 객체로 변환
                    Log.i("test",sb.toString());
                    final JSONObject jsonObj=new JSONObject(sb.toString());
// json.get("변수명") json변수의 값
                    result=jsonObj.getString("message");

                    try {
                        userid = jsonObj.getString("userid");
                        name = jsonObj.getString("name");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
//백그라운드 스레드에서는 메인화면을 변경할 수 없음
// runOnUiThread ( 메인스레드 영역  )
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(result.trim().equals("success")){
                                Common.userid=userid;
                                Common.name=name;
                                Intent intent=new Intent(LoginActivity.this, FirstActivity.class);
                                startActivity(intent);
                            }else {
                                txtResult.setText(result);
                            }
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        th.start();
        Log.e("MainActivity","login check");
        Intent startFirstActivity = new Intent(this,FirstActivity.class);
        startActivity(startFirstActivity);
    }

}

