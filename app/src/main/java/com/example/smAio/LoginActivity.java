package com.example.smAio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "sucess";

    private EditText id,password;
    private ImageButton login;
    private TextView link_signup;
    private ProgressBar loading;
    private static String URL_LOGIN ="http://eileenyoo1.cafe24.com/login.php/";
    private CheckBox auto;
    SessionManager sessionManager;

    //자동로그인 pref
    SharedPreferences AutoPref;
    SharedPreferences.Editor edit;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager=new SessionManager(this); //세션관련 SessionManager
        //id 가져오기
        auto=(CheckBox)findViewById(R.id.AutoLoginCheck);
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        loading=(ProgressBar)findViewById(R.id.progress_loading); //로그인 실행시 원모양으로 도는 로딩item
        login = (ImageButton)findViewById(R.id.login);
        AutoPref = getSharedPreferences("auto",MODE_PRIVATE);

        edit = AutoPref.edit(); //sharedpreferences 사용을 위한 에디터 //필수**

        Log.e(TAG,getIntent().getBooleanExtra("boolcheck",true)+""); //로그아웃 버튼이 눌렀을때 MyFragment에서 boolean 값이 전해지는지 확인을 위한 TAG
        Boolean boolc=getIntent().getBooleanExtra("boolcheck",true); //MyFragment에서 받은 boolean값 변수에 저장

        if(boolc==false){ //만약에 MyFragment에서 받은 값이 false이면 (원래 false값이 오는게 맞음)
            auto.setChecked(false); //auto CheckBox의 체크를 false로 만듬. 이러면 자동로그인 안됨
            edit.clear(); //SharedPreferences 값 초기화
            edit.commit(); //초기화 한걸 실행// SharedPreferences의 값 변동이 생기면 무조건 해야한다***
        }
        else{ //MyFragement에서 온 값이 아니다! 즉 로그아웃 버튼을 눌르지 않았다.
            if(AutoPref.getBoolean("checkbox",false)==true){ //그리고 sharedPreferences의 checkBox 체크가 됬는지 알아보는 boolean 값이 true 즉 체크 되었다면
                auto.setChecked(true); //checkbox의 체크를 계속 유지
                id.setText(AutoPref.getString("id","error")); //아이디칸에 아이디 띄우기
                password.setText(AutoPref.getString("password","error")); //비밀번호칸에 비밀번호 띄우기
                Login(AutoPref.getString("id",null),AutoPref.getString("password",null)); //로그인 실행
            }
        }

    password.setOnKeyListener(new View.OnKeyListener() { //login이벤트를 자판의 엔터로 하기위한 코드
        @Override
        public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
            if (keycode==KeyEvent.KEYCODE_ENTER){ //만약 keycode값이 KEYCOD_ENTER이면
                    login.callOnClick(); //로그인버튼을 클릭
                    return true;
            }
            return false;
        }
    });
        login.setOnClickListener(new LoginClickListenr()); //login 버튼이 눌렸을때 로그인 이벤트
        link_signup=(TextView)findViewById(R.id.signupButton); //회원가입 Text id
        link_signup.setOnClickListener(new View.OnClickListener() {//회원가입이 눌렸을 때 클릭 리스너
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(LoginActivity.this, EmailActivity.class);
                LoginActivity.this.startActivity(emailIntent); //인텐트를 사용하여 엑티비티 전환
            }
        });
    }

    private void Login(final String cid, final String cpassword){ //로그인을 위한 함수 edittext에 입력된 아이디와 비밀번호의 값을 가진다

        loading.setVisibility(View.VISIBLE); //원모양의 로딩 아이템 보이게하기!
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_LOGIN, //순서대로 php문에 POST 형식으로 값 보내기, php문 주소
                new Response.Listener<String>() { //php문에서 온 응답에 대한 이벤트
                    @Override
                    public void onResponse(String response) {
                        try{Log.e(TAG,"try");
                            JSONObject jsonObject=new JSONObject(response); //php문에서 json파일에 응답을 줌 그래서 jsonobject를 통해 응답 확인
                            String success = jsonObject.getString("success"); //php문에서 제이슨 파일에 success라는 키에 1이라는 값을 줌
                            Log.e(TAG,success);
                            JSONArray jsonArray = jsonObject.getJSONArray("login"); //php문에서 array변수에 login 데이터를 담고 jsonArray형식으로 jsonObject에 저장 그래서 그 값을 불러옴

                            if(success.equals("1")){ //만약에 json파일에 success키에 맞는 값이 1면
                                for(int i = 0; i<jsonArray.length();i++){ //jsonArray 크기만큼 for문 돌림
                                    JSONObject object=jsonArray.getJSONObject(i);
                                   String name=object.getString("name").trim(); //jsonArray에 저장된 이름(name)값을 가져온다
                                   Toast.makeText(LoginActivity.this,
                                            "Success Login. \nYour NAME : "
                                                    +name,Toast.LENGTH_SHORT) //토스트 메시지로 성공 메시지와 이름 값을 띄운다
                                            .show();
                                    loading.setVisibility(View.GONE); //로그인이 성공했으니 로딩아이템 안보이게!
                                    sessionManager.createSession(name,cid); //이름과 아이디로 구성된 세션 생성
                                    Intent loginIntent = new Intent(LoginActivity.this, FirstActivity.class); //이제 인텐트를 통해서 FirstActivity로 이동
                                    //만약 자동로그인 체크가 됬으면
                                    if(auto.isChecked()){
                                        edit.putString("id",cid);
                                        edit.putString("password",cpassword);
                                        edit.putBoolean("checkbox",true);
                                        edit.commit(); //입력된 아이디와 비밀번호 그리고 체크 여부를 SharedPreferences에 저장
                                    }
                                    else{ //아니면
                                        edit.putString("id","");
                                        edit.putString("password","");
                                        edit.putBoolean("checkbox",false);
                                        edit.commit(); //아이디와 비밀번호값을 ""로 아무것도 안주고 체크박스 여부도 false로
                                    }
                                    LoginActivity.this.startActivity(loginIntent);
                                    finish(); //다음 엑티비티로~
                                }
                            }
                            else{
                                password.setError("Please check your PASSWORD!!");
                                loading.setVisibility(View.GONE); //만약에 success값이 1이아니면 아이디에 맞는 비밀번호가 틀리다는 뜻
                            }
                        }catch (JSONException e){
                            Log.e(TAG,"catch");
                            loading.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                            id.setError("Please check your ID!!"); //이건 아예 아이디가 다르다는 뜻

                        }
                    }
                },
                new Response.ErrorListener() { //여기로 오류 잡힘 서버 접속 오류
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        Log.e(TAG,"error");
                        Toast.makeText(LoginActivity.this,
                                "Error "
                                        +error.toString(),
                                Toast.LENGTH_SHORT).show(); //이건 그냥 코드 에러 났을때 보이는 부분 Volley 어쩌구 뜨는게 여기다

                    }

                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Log.e(TAG,cid);
                Log.e(TAG,cpassword);
                params.put("id",cid);
                params.put("password",cpassword);
                return params; //hashmap을 통해서 값을 php문에 보내는 구문!
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); //요거는 volley사용을 위한 필수적인 코드 두줄

    }

    private class LoginClickListenr implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String mId=id.getText().toString().trim(); //trim()이거는 빈 공백이 있을때 오류 잡기위해 넣었음
            String mPass = password.getText().toString().trim(); // 이하동문

            if(!mId.isEmpty() || !mPass.isEmpty()){ //EditText에 입력된 값이 둘다 비어있지 않을시
                Login(mId,mPass);
            }else{ //하나라도 비어있으면!
                id.setError("Please insert id");
                password.setError("Please insert password");
                loading.setVisibility(View.GONE);
            }
        }
    }
}
