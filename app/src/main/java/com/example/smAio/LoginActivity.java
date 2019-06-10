package com.example.smAio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 로그인을 하기위한 activity입니다.
 * Volley를 이용하여 Server와 연동시켰습니다.
 * SheardPreference를 이용하여 자동로그인 기능을 구현했습니다. (SessionManager Acticity 참고)
 */

public class LoginActivity extends AppCompatActivity {

    //layout의 items 변수선언
    private EditText id,password;
    private ImageButton login;
    private TextView link_signup;
    private ProgressBar loading;
    private CheckBox auto;

    //SessionManager 사용을 위한 선언
    SessionManager sessionManager;

    //자동로그인을 위한 SharedPreferences
    SharedPreferences AutoPref;
    SharedPreferences.Editor edit;

    //Server에 저장된 login.php 주소
    private final static String URL_LOGIN ="http://eileenyoo1.cafe24.com/login.php/";

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SessionManager 객체생성
        sessionManager=new SessionManager(this);

        //layout items id 가져오기
        auto=(CheckBox)findViewById(R.id.AutoLoginCheck);
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        loading=(ProgressBar)findViewById(R.id.progress_loading);
        login = (ImageButton)findViewById(R.id.login);
        link_signup=(TextView)findViewById(R.id.signupButton);

        //SharedPreferences 객체생성
        AutoPref = getSharedPreferences("auto",MODE_PRIVATE);

        //AutoPref(SharedPreferences) 사용을 위한 editor 객체 생성
        edit = AutoPref.edit();

        //MyFragment에서 logout버튼 클릭시 세션초기화를 알려주는 Boolean변수
        Boolean boolc=getIntent().getBooleanExtra("boolcheck",true);


        if(boolc==false){ //MyFragment에서 받은 값이 false일 때 (세션초기화 성공)
            //자동로그인 CheckBox 체크 false
            auto.setChecked(false);
            //AutoPref에 저장된 CheckBox의 체크여부 초기화
            edit.clear();
            //commit을 통해 AutoPref에 변경사항 저장
            edit.commit();
        }
        else{ //MyFragement에서 logout버튼을 클릭하지 않아 true(defaltValue값)이 왔을 때
            if(AutoPref.getBoolean("checkbox",false)==true){ //AutoPref에 저장 된 값이 true(체크됨)일 때
                //checkbox의 체크를 계속 유지
                auto.setChecked(true);
                //아이디칸에 AutoPref에 저장된 id값 저장
                id.setText(AutoPref.getString("id","error"));
                //비밀번호칸에 AutoPref에 저장된 password값 저장
                password.setText(AutoPref.getString("password","error"));
                //Login함수를 실행( AutoPref에 저장된 id, AutoPref에 저장된 password)
                Login(AutoPref.getString("id",null),AutoPref.getString("password",null)); //로그인 실행
            }
        }

    //login이벤트를 자판의 엔터로 하기위한 코드
    password.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
            if (keycode==KeyEvent.KEYCODE_ENTER){ //만약 keycode값이 KEYCOD_ENTER 일 때
                //login의 click 이벤트 불러옴
                login.callOnClick();
                return true;
            }
            return false;
        }
    });
        //login 버튼 Click Event
        login.setOnClickListener(new LoginClickListener());
        //signup TextView Click Event
        link_signup.setOnClickListener(new SignUpClickListener());
    }

    //로그인을 위한 Server연동 함수 (Volley이용)
    private void Login(final String cid, final String cpassword){
        //loading ProgressBar VISIBLE로 변경
        loading.setVisibility(View.VISIBLE);

        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_LOGIN,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");
                            //JSONObject에 저장된 Array파일 객체 생성
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //jsonArray에 들어있는 데이터 확인 및 저장을 위한 for문
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    //jsonArray에 "name"이라는 키값으로 저장된 데이터 가져오기
                                    String name=object.getString("name").trim();
                                    //Toast 메시지로 성공 메시지와 이름 값을 띄운다
                                    Toast.makeText(LoginActivity.this,
                                            "Success Login. \nYour NAME : "
                                                    +name,Toast.LENGTH_SHORT)
                                            .show();
                                    //loading ProgressBar GONE으로 변경
                                    loading.setVisibility(View.GONE);
                                    //SessionManager에 유저 이름과 아이디 저장
                                    sessionManager.createSession(name,cid);

                                    //자동로그인 CheckBox가 체크 됬을 때
                                    if(auto.isChecked()){
                                        //아이디, 비밀번호, 체크 여부를 AutoPref에 저장
                                        edit.putString("id",cid);
                                        edit.putString("password",cpassword);
                                        edit.putBoolean("checkbox",true);
                                        edit.commit();
                                    }
                                    //자동로그인 CheckBox가 체크가 안 됬을 때
                                    else{
                                        //아이디와 비밀번호값을 ""로 초기화하고 체크박스 여부도 false로
                                        edit.putString("id","");
                                        edit.putString("password","");
                                        edit.putBoolean("checkbox",false);
                                        edit.commit();
                                    }

                                    //인텐트를 통해서 FirstActivity로 이동
                                    Intent loginIntent = new Intent(LoginActivity.this, FirstActivity.class);
                                    LoginActivity.this.startActivity(loginIntent);
                                    finish();
                                }
                            }
                            //success값이 1이 아닐 때 (아이디에 맞는 비밀번호가 틀리다는 뜻)
                            else{
                                //password Error Massage 띄우기
                                password.setError("Please check your PASSWORD!!");
                                //loading ProgressBar GONE으로 변경
                                loading.setVisibility(View.GONE);
                            }
                        }
                        //try에서 오류 발생시 (php문에 전달한 id가 서버에 없다는 뜻)
                        catch (JSONException e){
                            //id Error Massage 띄우기
                            id.setError("Please check your ID!!");
                            //loading ProgressBar GONE으로 변경
                            loading.setVisibility(View.GONE);
                            //login Button VISIBLE로 변경
                            login.setVisibility(View.VISIBLE);

                            e.printStackTrace();
                        }
                    }
                },
                //서버 접속 오류
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading ProgressBar GONE으로 변경
                        loading.setVisibility(View.GONE);
                        //login Button VISIBLE로 변경
                        login.setVisibility(View.VISIBLE);
                    }

                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //입력된 id
                params.put("id",cid);
                //입력된 password
                params.put("password",cpassword);
                //php문으로 return
                return params;
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Login Button Click Event
    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //변수에 id, password EditText에 입력된 값 저장
            String mId=id.getText().toString().trim();
            String mPass = password.getText().toString().trim();

            //EditText에 id, password가 모두 입력됬을 때
            if(!mId.isEmpty() || !mPass.isEmpty()){
                //Login 함수 실행
                Login(mId,mPass);
            }
            //하나라도 비어있을 시
            else{
                id.setError("Please insert id");
                password.setError("Please insert password");
                loading.setVisibility(View.GONE);
            }
        }
    }

    //SingUp TextView Click Event
    private class SignUpClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //인텐트를 사용하여 엑티비티 전환
            Intent emailIntent = new Intent(LoginActivity.this, EmailActivity.class);
            LoginActivity.this.startActivity(emailIntent);
        }
    }
}
