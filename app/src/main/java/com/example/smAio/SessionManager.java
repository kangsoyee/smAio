package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * user의 정보를 유지하기위한 session을 관리하는 Activity
 * SharedPreferences 이용
 */
public class SessionManager {

    //세션을 만들기위한 sharedpreferences와 editor
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;


    public Context context;
    int PRIVATE_MOD = 0 ;

    //Session에 저장되는 값들 선언
    private static final String PREF_NAME="LOGIN";
    private static final String LOGIN ="IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";

    //ShardPreferences를 사용하기 위한 editor를 선언하는 함수
    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MOD);
        editor = sharedPreferences.edit();
    }

    //세션만들기 ShardPreferences에 값 저장
    public void createSession(String name,String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME,name);
        editor.putString(ID,id);
        editor.apply();
    }

    //로그인 여부를 체크해 boolean값을 저장하는 함수
    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    //로그인 여부를 체크해 액티비티를 종료하는 함수
    public void checkLoggin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((FirstActivity)context).finish();
        }
    }

    //저장된 유저 정보를 가져오는 함수
    public HashMap<String,String>getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(ID,sharedPreferences.getString(ID,null));
        return user;
    }

    //로그아웃시 세션을 지우는 함수
    public void logout(){
        editor.clear();
        editor.commit();
    }
}
