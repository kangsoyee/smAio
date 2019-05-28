package com.example.smAio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity {

public static final String TAG = "sucess";

private EditText id,password,name;
private Button btn_create;
private Button btn_check;
private static String URL_SignUp ="http://eileenyoo.cafe24.com/UserSignUp.php/";
private static String URL_Check ="http://eileenyoo.cafe24.com/Idcheck.php/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //id값 가져오기
        id=(EditText)findViewById(R.id.idText);
        password=(EditText)findViewById(R.id.passwordText);
        name=(EditText)findViewById(R.id.nameText);

        //Sign Up 버튼 눌렀을때
        btn_create=(Button)findViewById(R.id.createButton);
        btn_create.setClickable(false);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText값 가져와 변수에 저장
                String mId=id.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                String mName = name.getText().toString().trim();


                if(!mId.isEmpty() || !mPass.isEmpty() || !mName.isEmpty()){//다 입력 되었다면
                    SignUp();
                }else{//하나라도 입력 X
                    id.setError("Please insert ID"); //에러 메시지 발생
                    password.setError("Please insert PASSWORD");
                    name.setError("Please insert NAME");
                }

            }
        });

        btn_check=(Button)findViewById(R.id.check_id); //아이디 중복체크 버튼 id 가져오기
        btn_check.setOnClickListener(new View.OnClickListener() { //클릭 이벤트
            @Override
            public void onClick(View v) {

                String mId=id.getText().toString().trim();
                if(!mId.isEmpty()) { //id EditText에 값이 있으면
                    Check(mId); //중복검사 함수 실행
                }
            }
        });

    }

    private  void Check(final String cid){ //중복검사 함수
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Check, //php문에 Post형식으로 보내고, 보내는 URL= URL_CHECK
                new Response.Listener<String>() { //php문의 응답에 대한 이벤트
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject=new JSONObject(response); //json파일에 응답이 저장
                            String success = jsonObject.getString("success"); //php문에서 success란 키값에 중복이 없다면 1을 반환하도록 설정
                            if(success.equals("1")) { //반환값이 1이면 (중복이 없으면)
                                Toast.makeText(SignUpActivity.this,"You can use this ID",Toast.LENGTH_SHORT).show(); //이 아이디를 사용할 수 있다라는 toast메시지 띄우기
                                id.setClickable(false);
                                id.setFocusable(false);//중복체크가 되는 즉시 id EditText의 클릭이벤트 비활성화
                                id.setBackground(getDrawable(R.drawable.edit_line_false)); //색도 회색으로 변하게
                                btn_check.setClickable(false); //중복체크 버튼 역시 비활성화
                                btn_check.setBackgroundColor(getResources().getColor(R.color.colorGray));//색도 회색으로
                                btn_create.setClickable(true);//중복체크 완료시 Sign Up (회원가입버튼)은 활성화
                                btn_create.setBackgroundColor(getResources().getColor(R.color.colorButtonOn)); //색도 파랑색으로!

                            }
                            else{ //success키에 1이아닌 0의 값이 들어왔다!(중복된다)
                                id.setError("Your ID is already in use."); //누가 사용중이다 메시지 띄우기
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace(); //php에 연결된 db에 체크할 아이디가 없을 수 있기에 빈공간으로 둠
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"error");  //여기는 아예 코드상의 오류가 뜨는 부분
                        Toast.makeText(SignUpActivity.this,"Id Check Error!" + error.toString(),Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Log.e(TAG,cid);
                params.put("id",cid);
                return params;
                //php문에 값을 보내는 부분
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); //필수코드***********
    }


    private void SignUp(){ //회원가입 함수

        btn_create.setVisibility(View.GONE); //회원가입 버튼 안보이게!
        final String id = this.id.getText().toString().trim(); //각 EditText에 입력된 값 변수에 저장
        final String password = this.password.getText().toString().trim();
        final String name = this.name.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SignUp, //php문에 POST형식으로, URL_SignUp 주소에 저장된 php문에 보냄
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //php문 응답에 대한 코드
                    try{
                        Log.e(TAG,"try");
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                        Log.e(TAG,success);
                        if(success.equals("1")){//그 값이 1이면(성공)
                            Toast.makeText(SignUpActivity.this,"Reqister Success!",Toast.LENGTH_SHORT).show();
                            //회원가입 완료(DB에 성공적으로 값 저장)
                        }


                    }catch (JSONException e){ //오류발생

                        Log.e(TAG,"catch");
                        e.printStackTrace();
                        Toast.makeText(SignUpActivity.this,"Reqister Error!" + e.toString(),Toast.LENGTH_SHORT).show();
                        btn_create.setVisibility(View.VISIBLE);
                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//오류발생

                        Log.e(TAG,"error");

                        Toast.makeText(SignUpActivity.this,"Reqister Error!" + error.toString(),Toast.LENGTH_SHORT).show();
                        btn_create.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("name",name);
                params.put("password",password);
                return params;

                //php문에 값을 보냄
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); //필수코드***********
        finish();
    }
}