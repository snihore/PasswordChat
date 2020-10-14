package com.passwordchat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreInstance {

    public static CollectionReference getInboxMessageCollectionRef(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) return null;

        return FirebaseFirestore.getInstance()
                .collection(user.getEmail())
                .document(user.getUid())
                .collection("InboxMessage");
    }
}
