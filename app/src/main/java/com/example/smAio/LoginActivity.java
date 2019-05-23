package com.example.smAio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        loading=(ProgressBar)findViewById(R.id.progress_loading);
        login = (ImageButton)findViewById(R.id.login);
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

                                   String email=object.getString("name").trim();

                                   Toast.makeText(LoginActivity.this,
                                            "Success Login. \nYour NAME : "
                                                    +email,Toast.LENGTH_SHORT)
                                            .show();
                                    loading.setVisibility(View.GONE);
                                    Intent loginIntent = new Intent(LoginActivity.this, FirstActivity.class);
                                    LoginActivity.this.startActivity(loginIntent);
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
                new Response.ErrorListener() {
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
            protected Map<String,String> getParams() throws AuthFailureError{
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
