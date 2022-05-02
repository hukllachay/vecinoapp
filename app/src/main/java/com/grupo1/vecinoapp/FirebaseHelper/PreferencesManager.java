package com.grupo1.vecinoapp.FirebaseHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.grupo1.vecinoapp.Models.UserModel;


public class PreferencesManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveCurrentUser(UserModel user) {
        editor.putString("currentUser", new Gson().toJson(user));
        editor.commit();
    }

    public UserModel getCurrentUser() {
        return new Gson().fromJson(sharedPreferences.getString("currentUser", ""), UserModel.class);

    }
    //save the user click for
    public void setLocationCity(String level) {
        editor.putString("location", level).commit();
        editor.apply();
    } //save the user click for

    public void setChange(String level) {
        editor.putString("change", level).commit();
        editor.apply();
    }

    public String getChange() {
        return sharedPreferences.getString("change", "no");
    }

    public String getLatPref() {
        return sharedPreferences.getString("lat", "");
    }

    public String getLocationCity() {
        return sharedPreferences.getString("location", "");
    }

    public String getLangPref() {
        return sharedPreferences.getString("lng", "");
    }

    public void setLocationPref(String lat, String lng) {
        editor.putString("lat", lat).commit();
        editor.putString("lng", lng).commit();
        editor.apply();
    }


}
