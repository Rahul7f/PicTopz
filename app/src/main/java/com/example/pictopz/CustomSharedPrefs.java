package com.example.pictopz;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPrefs {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public CustomSharedPrefs(Context context){
        sharedPreferences = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public String getPrefString(String key){
        return sharedPreferences.getString(key,"");
    }

    public void setPrefString(String key,String value){
         editor.putString(key,value).commit();
    }

    public void setPrefBool(String key,Boolean bool){
        editor.putBoolean(key,bool).commit();
    }

    public Boolean getPrefBool(String key){
        return sharedPreferences.getBoolean(key,false);
    }
}
