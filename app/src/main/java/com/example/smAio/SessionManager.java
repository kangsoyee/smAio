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

    private static final String PREF_NAME="LOGIN";
    private static final String LOGIN ="IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MOD);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name,String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME,name);

        editor.putString(ID,id);
        editor.apply();
    }

//    public void checkSession(String id){
//        String check_id=sharedPreferences.getString(ID,null);
//        if(check_id!=id){
//            ((LoginActivity)context).editor.putBoolean("auto",false);
//        }
//        else{
//
//            ((LoginActivity)context).editor.putBoolean("auto",true);
//        }
//    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    public void checkLoggin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((FirstActivity)context).finish();
        }
    }

    public HashMap<String,String>getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(ID,sharedPreferences.getString(ID,null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
//        Intent i = new Intent(context,LoginActivity.class);
//        i.putExtra("boolcheck",false);
//        context.startActivity(i);
//        ((FirstActivity)context).finish();

    }
}
