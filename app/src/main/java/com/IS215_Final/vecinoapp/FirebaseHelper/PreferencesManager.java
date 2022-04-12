package com.IS215_Final.vecinoapp.FirebaseHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.IS215_Final.vecinoapp.Models.UserModel;


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


}
