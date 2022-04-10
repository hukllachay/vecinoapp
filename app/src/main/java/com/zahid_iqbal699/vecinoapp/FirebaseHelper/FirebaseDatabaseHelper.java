package com.zahid_iqbal699.vecinoapp.FirebaseHelper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zahid_iqbal699.vecinoapp.Models.UserModel;

import java.util.Objects;


public class FirebaseDatabaseHelper {
    private UserModel currentUser;
    private PreferencesManager prefs;
    private static final String TAG = "TAG";
    private Context context;
    private DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void attemptSignUp(final UserModel user, final OnLoginSignupAttemptCompleteListener listener) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getuEmail(), user.getuPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    saveUserInfo(user, new OnSaveUserCompleteListener() {
                        @Override
                        public void onSaveUserComplete(boolean isSuccessful) {
                            new PreferencesManager(context).saveCurrentUser(user);
                            listener.onLoginSignupSuccess(user);
                        }
                    });

                } else
                    listener.onLoginSignupFailure(task.getException().getMessage());
            }
        });
    }

    private void saveUserInfo(UserModel user, final OnSaveUserCompleteListener listener) {

        tableUser.child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    listener.onSaveUserComplete(true);
                else
                    listener.onSaveUserComplete(false);
            }
        });

    }

    public interface OnAddProjectCompleteListener {
        void onAddProjectCompleted(String isSuccessful);
    }


    public interface OnSaveUserCompleteListener {
        void onSaveUserComplete(boolean isSuccessful);
    }

    public interface OnLoadUserInfoCompleteListener {
        void onLoadUserInfoComplete(UserModel user);
    }

    public interface OnLoginSignupAttemptCompleteListener {
        void onLoginSignupSuccess(UserModel user);


        void onLoginSignupFailure(String failureMessage);
    }

    public interface OnSingleUserDataCompleteListener {
        void onSingleUserDataCompleted(UserModel isSuccessful);
    }

    public interface OnUploadFileCompleteListener {
        void onUploadFileComplete(String url);
    }

}
