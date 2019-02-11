package com.bektas.kitchendiary.util;

import com.bektas.kitchendiary.model.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {
    public static List<Recipe> recipes;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;

    private static FirebaseUtil firebaseUtil;

    private FirebaseUtil(){}

    public static void openFbReference(String ref){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        recipes = new ArrayList<>();

        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}
