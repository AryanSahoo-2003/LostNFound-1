package com.example.lostnfound.firebase

import android.app.Activity
import android.util.Log
import com.example.lostnfound.DataProvider
import com.example.lostnfound.activities.*
import com.example.lostnfound.models.Lost
import com.example.lostnfound.models.Users
import com.example.lostnfound.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class firestoreclass {
    private val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(activity: SignupPage,userInfo: Users){
          mFireStore.collection(Constants.USERS)
              .document(getcurrentUserID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                  activity.userRegisteredSuccess()
              }.addOnFailureListener{
                  Log.e(activity.javaClass.simpleName,"Error")
              }
    }
    fun signInUser(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getcurrentUserID()).get().addOnSuccessListener {document->
               val loggedInUser=document.toObject(Users::class.java)!!
               when(activity){
                   is MainActivity->{
                       activity.signInSuccess(loggedInUser)
                   }
                   is AfterLoginPage->{
                       activity.setDetails(loggedInUser)
                   }
               }
            }.addOnFailureListener{
//                Log.e(activity.javaClass.simpleName,"Error")
            }
    }
    fun registerLostItem(activity: FillLostItem,userInfo: Lost){
        mFireStore.collection("lostitem")
            .add(userInfo).addOnSuccessListener {
                activity.LostitemRegistered()
            }.addOnFailureListener{
                Log.e(activity.javaClass.simpleName,"Error")
            }
    }
    fun registerFoundItem(activity: FillFoundItem, userInfo: Lost){
        mFireStore.collection("founditem")
            .add(userInfo).addOnSuccessListener {
                activity.FounditemRegistered()
            }.addOnFailureListener{
                Log.e(activity.javaClass.simpleName,"Error")
            }
    }
    private fun getcurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}