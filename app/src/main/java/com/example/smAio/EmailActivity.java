package com.example.smAio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class EmailActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button certification;

    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        certification = findViewById(R.id.btn_certification);
        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextPassword.setOnKeyListener(new View.OnKeyListener() { //login이벤트를 자판의 엔터로 하기위한 코드
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keycode==KeyEvent.KEYCODE_ENTER){ //만약 keycode값이 KEYCOD_ENTER이면
                    certification.callOnClick(); //로그인버튼을 클릭
                    return true;
                }
                return false;
            }
        });
    }

    //작성한값 불러서 이메일,비밀번호 유효성 검사하기
    public void singUp(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }

    // 이메일 유효성 검사 메소드
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email+"@sangmyung.kr").matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사 메소드
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        //firebase에 이메일 생성
        firebaseAuth.createUserWithEmailAndPassword(email+"@sangmyung.kr", password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()&&task.isComplete()) {
                            // 회원가입 성공
                            Toast.makeText(EmailActivity.this, "Certification", Toast.LENGTH_LONG).show();
                            //인텐트를 사용하여 엑티비티 전환
                            Intent signupIntent = new Intent(EmailActivity.this, SignUpActivity.class);
                            EmailActivity.this.startActivity(signupIntent);
                            finish();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(EmailActivity.this, "Certification Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
