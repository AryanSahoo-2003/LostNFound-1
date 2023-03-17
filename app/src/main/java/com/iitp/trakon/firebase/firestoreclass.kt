package com.iitp.trakon.firebase

import android.app.Activity
import android.util.Log
import com.iitp.trakon.activities.*
import com.iitp.trakon.models.Lost
import com.iitp.trakon.models.Users
import com.iitp.trakon.utils.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.iitp.trakon.models.Query_model

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
                   is Tabs->{
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
    fun registerQuery(activity:qurery,userInfo: Query_model){
        mFireStore.collection("Query")
            .add(userInfo).addOnSuccessListener {
                Log.d("aryan",it.id)
                activity.QueryRegistered()
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