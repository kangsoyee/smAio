package com.example.smAio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원가입을 하기위한 Activity
 * 아이디 중복체크 기능과 회원가입 기능
 */
public class SignUpActivity extends AppCompatActivity {
    //회원가입과 중복체크를 위한 php문이 저장된 서버의 주소
    private static String URL_SignUp ="http://eileenyoo1.cafe24.com/UserSignUp.php/";
    private static String URL_Check ="http://eileenyoo1.cafe24.com/Idcheck.php/";

    //layout items 변수
    private EditText id,password,name;
    private Button btn_create;
    private Button btn_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //layout items id값 가져오기
        id=(EditText)findViewById(R.id.idText);
        password=(EditText)findViewById(R.id.passwordText);
        name=(EditText)findViewById(R.id.nameText);
        btn_create=(Button)findViewById(R.id.createButton);
        btn_check=(Button)findViewById(R.id.check_id);

        //onCreate시 btn_create Button 비활성화
        btn_create.setClickable(false);

        //btn_create Button Click Event
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText값 가져와 변수에 저장
                String mId=id.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                String mName = name.getText().toString().trim();

                //이름, 아이디, 비밀번호의 EditText에 값이 모두 입력되었을 때
                if(!mId.isEmpty() || !mPass.isEmpty() || !mName.isEmpty()){
                    //SignUp 함수 실행
                    SignUp();
                }
                //하나라도 입력이 안된 EditText가 있을 때
                else{
                    //에러 메시지 발생
                    id.setError("Please insert ID");
                    password.setError("Please insert PASSWORD");
                    name.setError("Please insert NAME");
                }
            }
        });

        //btn_check Button Click Event
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디 EditText에 입력된 값 변수에 저장
                String mId=id.getText().toString().trim();
                //값이 있을 때
                if(!mId.isEmpty()) {
                    //Check 함수 실행
                    Check(mId);
                }
            }
        });
    }

    //ID Check를 위한 Server연동 함수 (Volley이용)
    private  void Check(final String cid){
        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Check,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")) {
                                //아이디를 사용할 수 있다라는 toast Message 띄우기
                                Toast.makeText(SignUpActivity.this,"You can use this ID",Toast.LENGTH_SHORT).show();

                                //id EditText 비활성화
                                id.setClickable(false);
                                id.setFocusable(false);
                                //id EditText 색 회색으로 변경
                                id.setBackground(getDrawable(R.drawable.edit_line_false));

                                //btn_check Button 비활성화
                                btn_check.setClickable(false);
                                //btn_check Button 색 회색으로 변경
                                btn_check.setBackgroundColor(getResources().getColor(R.color.colorGray));

                                //btn_create Button 활성화
                                btn_create.setClickable(true);
                                //btn_create 색 파랑색으로 변경
                                btn_create.setBackgroundColor(getResources().getColor(R.color.colorButtonOn));
                            }
                            //success라는 키에 들어있는 값이 "0" 일 때
                            else{
                                //id가 사용중이다라는 Error Message 띄우기
                                id.setError("Your ID is already in use.");
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //서버 접속오류시
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
                //php문으로 return
                return params;

            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //회원가입을 위한 Server연동 함수 (Volley이용)
    private void SignUp(){

        //btn_create Button GONE으로 변경
        btn_create.setVisibility(View.GONE);

        //각 EditText에 입력된 값 변수에 저장
        final String id = this.id.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String name = this.name.getText().toString().trim();

        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SignUp,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject = new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //회원가입 완료(DB에 성공적으로 값 저장) Toast Message 출력
                                Toast.makeText(SignUpActivity.this,"Reqister Success!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        //try문에서 오류발생 시
                        catch (JSONException e){
                            e.printStackTrace();
                            //btn_create Button VISIBLE으로 변경
                            btn_create.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    //서버 접속 오류시
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //btn_create Button VISIBLE으로 변경
                        btn_create.setVisibility(View.VISIBLE);
                    }
                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //입력된 id
                params.put("id",id);
                //입력된 name
                params.put("name",name);
                //입력된 password
                params.put("password",password);
                //php문으로 return
                return params;

                //php문에 값을 보냄
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        finish();
    }
}