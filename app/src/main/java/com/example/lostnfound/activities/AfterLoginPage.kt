package com.example.lostnfound.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.lostnfound.R
import com.example.lostnfound.firebase.firestoreclass
import com.example.lostnfound.models.Users
import com.example.lostnfound.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.activity_after_login_page.*
import java.io.IOException


class AfterLoginPage :BaseActivity() {


    private val mFireStore= FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login_page)

        firestoreclass().signInUser(this)
//        Firebase.messaging.subscribeToTopic("LostNFound")
//            .addOnCompleteListener { task ->
//                var msg = "Subscribed"
//                if (!task.isSuccessful) msg = "Subscribe failed"
////                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
        post_lost_item.setOnClickListener{
            startActivity(Intent(this,FillLostItem::class.java))
        }
        post_found_item.setOnClickListener{
            startActivity(Intent(this,FillFoundItem::class.java))
        }
        check_lost_item.setOnClickListener{
            startActivity(Intent(this,LostActivity::class.java))
        }
        check_found_item.setOnClickListener{
            startActivity(Intent(this,FoundActivity::class.java))
        }
        update_button_butoon.setOnClickListener{
            startActivity(Intent(this,UpdatePassword::class.java))
        }
        my_found_post_button.setOnClickListener{
            startActivity(Intent(this,MyFoundActivity::class.java))
        }
        My_lost_post_button.setOnClickListener{
            startActivity(Intent(this,MyLostActivity::class.java))
        }
        Logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    fun setDetails(user:Users)
    {

                name_display.text=user.name;
                roll_display.text=user.roll;
        try {
            Glide     //using Glide to display image from url
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(Profile_pic_image);
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    private fun getcurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}