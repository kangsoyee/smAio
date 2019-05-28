package com.example.smAio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static String URL_LOGIN ="http://eileenyoo.cafe24.com/login.php/";
    private CheckBox auto;
    SessionManager sessionManager;

    //자동로그인 pref

    SharedPreferences AutoPref;
    SharedPreferences.Editor edit;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sessionManager=new SessionManager(this);
        auto=(CheckBox)findViewById(R.id.AutoLoginCheck);
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        loading=(ProgressBar)findViewById(R.id.progress_loading);
        login = (ImageButton)findViewById(R.id.login);
        AutoPref = getSharedPreferences("auto",MODE_PRIVATE);
        edit = AutoPref.edit();

        Log.e(TAG,getIntent().getBooleanExtra("boolcheck",true)+"");
        Boolean boolc=getIntent().getBooleanExtra("boolcheck",true);

        if(boolc==false){
            auto.setChecked(false);
            edit.clear();
            edit.commit();
        }
        else{
            if(AutoPref.getBoolean("checkbox",false)==true){
                auto.setChecked(true);
                id.setText(AutoPref.getString("id","error"));
                password.setText(AutoPref.getString("password","error"));
                Login(AutoPref.getString("id",null),AutoPref.getString("password",null));
            }
        }




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mId=id.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if(!mId.isEmpty() || !mPass.isEmpty()){
                    Login(mId,mPass);


                }else{
                    id.setError("Please insert id");
                    password.setError("Please insert password");
                    loading.setVisibility(View.GONE);
                }
            }

        });


        link_signup=(TextView)findViewById(R.id.signupButton);

        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(signupIntent);
            }
        });
    }

    private void Auto_Login(){

        if(getIntent().getBooleanExtra("boolcheck",true)==false){
            auto.setChecked(false);
        }
        else{
            if(AutoPref.getBoolean("true",false)==true){
                id.setText(AutoPref.getString("id",null));
                password.setText(AutoPref.getString("password",null));
                auto.setChecked(AutoPref.getBoolean("checkbox",false));
                Login(AutoPref.getString("id",null),AutoPref.getString("password",null));
            }
        }
    }

    private void Login(final String cid, final String cpassword){

        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{Log.e(TAG,"try");
                            JSONObject jsonObject=new JSONObject(response);
                            String success = jsonObject.getString("success");
                            Log.e(TAG,success);
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                   String name=object.getString("name").trim();
                                   Toast.makeText(LoginActivity.this,
                                            "Success Login. \nYour NAME : "
                                                    +name,Toast.LENGTH_SHORT)
                                            .show();
                                    loading.setVisibility(View.GONE);
                                    sessionManager.createSession(name,cid);
                                    Intent loginIntent = new Intent(LoginActivity.this, FirstActivity.class);
                                    //만약 자동로그인 체크가 됬으면
                                    if(auto.isChecked()){
                                        edit.putString("id",cid);
                                        edit.putString("password",cpassword);
                                        edit.putBoolean("checkbox",true);
                                        edit.commit();
                                    }
                                    else{ //아니면
                                        edit.putString("id","");
                                        edit.putString("password","");
                                        edit.putBoolean("checkbox",false);
                                        edit.commit();
                                    }
                                    LoginActivity.this.startActivity(loginIntent);
                                    finish();
                                }
                            }
                            else{

                                password.setError("Please check your PASSWORD!!");
                                loading.setVisibility(View.GONE);
                            }
                        }catch (JSONException e){
                            Log.e(TAG,"catch");
                            loading.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                            id.setError("Please check your ID!!");

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
                                Toast.LENGTH_SHORT).show();

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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}
