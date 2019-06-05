package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MOD = 0 ;
    //Session에 저장되는 값들 선언
    private static final String PREF_NAME="LOGIN";
    private static final String LOGIN ="IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";

    public SessionManager(Context context){ //ShardPreferences를 사용하기 위한 editor 선언
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MOD);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name,String id){ //세션만들기 ShardPreferences에 값 저장
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME,name);

        editor.putString(ID,id);
        editor.apply(); //editor.commit()과 같은 역할
    }


    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false); //로그인 되었으면 LOGIN값을 리턴 (defValue가 false니깐 값은 true)
    }

    public void checkLoggin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((FirstActivity)context).finish(); //로그인 되지 않았으면 FirstActivity에서 로그인 화면으로 돌아간다
        }
    }

    public HashMap<String,String>getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(ID,sharedPreferences.getString(ID,null));

        //유저정보 NAME과 ID값이 저장된 것을 return
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        //로그아웃시 모든 세션 지우기
    }
}
