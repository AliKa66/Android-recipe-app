package com.bektas.kitchendiary.util;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import id.zelory.compressor.Compressor;

import android.net.Uri;
import android.util.Log;

import com.bektas.kitchendiary.AddOrEditRecipeActivity;
import com.bektas.kitchendiary.R;
import com.bektas.kitchendiary.model.Recipe;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFireBaseAuth;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static final int RC_SIGN_IN = 123;
    public static String uid;
    private static FirebaseUtil firebaseUtil;

    private FirebaseUtil(){}

    public static void openFbReference(String ref, final Activity callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFireBaseAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn(callerActivity);
                    }
                }
            };
            if (uid == null){
                uid = mFireBaseAuth.getUid();
                Log.d("User UID",String.format("User with UID %s has logged in", uid));
            }
            connectStorage();
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(String.format("%s/%s",ref,uid));
    }

    public static void signIn(Activity callerActivity){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        callerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void logout(Context context){
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout", "User Logged Out");
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.detachListener();
    }

    public static void attachListener(){
        mFireBaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener(){
        mFireBaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void sendToDatabase(Recipe recipe) {

        if (recipe.getId() == null) {
            FirebaseUtil.mDatabaseReference.push().setValue(recipe);
        } else {
            FirebaseUtil.mDatabaseReference.child(recipe.getId()).setValue(recipe);
        }
    }

    private static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child(String.format("%s/%s","recipes_pictures",uid));
    }

}
