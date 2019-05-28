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

        id=(EditText)findViewById(R.id.idText);
        password=(EditText)findViewById(R.id.passwordText);
        name=(EditText)findViewById(R.id.nameText);

        btn_create=(Button)findViewById(R.id.createButton);
        btn_create.setClickable(false);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mId=id.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                String mName = name.getText().toString().trim();


                if(!mId.isEmpty() || !mPass.isEmpty() || !mName.isEmpty()){
                    SignUp();
                }else{
                    id.setError("Please insert ID");
                    password.setError("Please insert PASSWORD");
                    name.setError("Please insert NAME");
                }

            }
        });

        btn_check=(Button)findViewById(R.id.check_id);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mId=id.getText().toString().trim();
                if(!mId.isEmpty()) {
                    Check(mId);
                }
            }
        });

    }

    private  void Check(final String cid){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Check,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toast.makeText(SignUpActivity.this,"You can use this ID",Toast.LENGTH_SHORT).show();
                                id.setClickable(false);
                                id.setFocusable(false);
                                id.setBackground(getDrawable(R.drawable.edit_line_false));
                                btn_check.setClickable(false);
                                btn_check.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                btn_create.setClickable(true);
                                btn_create.setBackgroundColor(getResources().getColor(R.color.colorButtonOn));

                            }
                            else{
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
                        Log.e(TAG,"error");
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
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void SignUp(){

        btn_create.setVisibility(View.GONE);
        final String id = this.id.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String name = this.name.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SignUp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    try{
                        Log.e(TAG,"try");
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        Log.e(TAG,success);
                        if(success.equals("1")){
                            Toast.makeText(SignUpActivity.this,"Reqister Success!",Toast.LENGTH_SHORT).show();

                        }


                    }catch (JSONException e){

                        Log.e(TAG,"catch");
                        e.printStackTrace();
                        Toast.makeText(SignUpActivity.this,"Reqister Error!" + e.toString(),Toast.LENGTH_SHORT).show();
                        btn_create.setVisibility(View.VISIBLE);
                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        finish();
    }
}