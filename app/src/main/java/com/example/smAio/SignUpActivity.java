package com.example.smAio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText pwdText = (EditText) findViewById(R.id.pwdText);
        final EditText pwdcheckText = (EditText) findViewById(R.id.pwdcheckText);

        Button joinButton = (Button) findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idText.getText().toString().length() == 0){
                    Toast.makeText(SignUpActivity.this , "아이디를 입력하세요" ,
                            Toast.LENGTH_SHORT).show();
                    idText.requestFocus();
                    return;
                }

                if(nameText.getText().toString().length() == 0){
                    Toast.makeText(SignUpActivity.this , "이름을 입력하세요" ,
                            Toast.LENGTH_SHORT).show();
                    nameText.requestFocus();
                    return;
                }

                if(pwdText.getText().toString().length() == 0){
                    Toast.makeText(SignUpActivity.this , "비밀번호를 입력하세요" ,
                            Toast.LENGTH_SHORT).show();
                    pwdText.requestFocus();
                    return;
                }

                if(pwdcheckText.getText().toString().length() == 0){
                    Toast.makeText(SignUpActivity.this , "비밀번호확인을 입력하세요" ,
                            Toast.LENGTH_SHORT).show();
                    pwdcheckText.requestFocus();
                    return;
                }

                if(!pwdText.getText().toString().equals(pwdcheckText.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다",
                            Toast.LENGTH_SHORT).show();
                    pwdText.setText("");
                    pwdcheckText.setText("");
                    pwdText.requestFocus();
                    return;
                }
                if(pwdText.getText().toString().equals(pwdcheckText.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "회원가입 완료",
                            Toast.LENGTH_SHORT).show();
                    Intent startLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(startLoginActivity);
                }
            }
        });
    }
}
