package com.grupo1.vecinoapp.FirebaseHelper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grupo1.vecinoapp.Models.UserModel;

import java.util.Objects;


public class FirebaseDatabaseHelpers {
    private UserModel currentUser;
    private PreferencesManager prefs;
    private static final String TAG = "TAG";
    private Context context;
    private DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private StorageReference folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image/");

    public FirebaseDatabaseHelpers(Context context) {
        this.context = context;
    }

    public void attemptSignUp(Uri mainImageUri, final UserModel user, final OnLoginSignupAttemptCompleteListener listener) {
        Log.e(TAG, "attemptSignUp: " );
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getuEmail(), user.getuPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    uploadFile(mainImageUri, folderProfilePics.child(user.getUid() + ".jpg"), new OnUploadFileCompleteListener() {
                        @Override
                        public void onUploadFileComplete(String url) {
                            user.setuImage(url);

                            saveUserInfo(user, new OnSaveUserCompleteListener() {
                                @Override
                                public void onSaveUserComplete(boolean isSuccessful) {
                                    new PreferencesManager(context).saveCurrentUser(user);
                                    listener.onLoginSignupSuccess(user);
                                }
                            });
                        }
                    });

                    /*
                    saveUserInfo(user, new OnSaveUserCompleteListener() {
                        @Override
                        public void onSaveUserComplete(boolean isSuccessful) {
                            new PreferencesManager(context).saveCurrentUser(user);
                            listener.onLoginSignupSuccess(user);
                        }
                    });
*/

                } else {
                    Log.e(TAG, "onComplete: Error" );
                    listener.onLoginSignupFailure(task.getException().getMessage());
                }
            }
        });
    }

    public void updateProfile(Uri mainImageUri, final UserModel userModel, final OnLoginSignupAttemptCompleteListener listener) {
        if (mainImageUri == null) {
            saveUserInfo(userModel, new OnSaveUserCompleteListener() {
                @Override
                public void onSaveUserComplete(boolean isSuccessful) {
                    new PreferencesManager(context).saveCurrentUser(userModel);
                    listener.onLoginSignupSuccess(userModel);
                }
            });
        } else {
            //upload file and then update data
            uploadFile(mainImageUri, folderProfilePics.child(userModel.getUid() + ".jpg"), new OnUploadFileCompleteListener() {
                @Override
                public void onUploadFileComplete(String url) {
                    userModel.setuImage(url);
                    saveUserInfo(userModel, new OnSaveUserCompleteListener() {
                        @Override
                        public void onSaveUserComplete(boolean isSuccessful) {
                            new PreferencesManager(context).saveCurrentUser(userModel);
                            listener.onLoginSignupSuccess(userModel);
                        }
                    });
                }
            });

        }
    }

    private void uploadFile(Uri fileUri, final StorageReference path, final OnUploadFileCompleteListener listener) {
        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "onUploadFileComplete: Now uploadiong file");
                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listener.onUploadFileComplete(uri.toString());
                        }
                    });
                }else{
                    Log.e(TAG, "onComplete: upload file"+task.getException() );
                }
            }
        });
    }

    private void saveUserInfo(UserModel user, final OnSaveUserCompleteListener listener) {
        Log.e(TAG, "saveUserInfo: uid"+user.getUid() );
        tableUser.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
