package com.example.lostnfound.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.lostnfound.R
import kotlinx.android.synthetic.main.activity_lost_expanded_description.*
import java.io.IOException

class Lost_Expanded_Description : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_expanded_description)
        LostExpanded_Name.text=intent.getStringExtra("name").toString()
                  LostExpanded_Place.text=intent.getStringExtra("place").toString();
       LostExpanded_When.text=intent.getStringExtra("date").toString();
       LostExpanded_Number.text=intent.getStringExtra("phone").toString()
       LostExpanded_Description.text=intent.getStringExtra("desc").toString()
        var imgSrc =intent.getStringExtra("img").toString()
        try {
           Glide     //using Glide to display image from url
               .with(this)
               .load(imgSrc)
               .centerCrop()
               .placeholder(R.drawable.ic_baseline_person_24)
               .into(LostExpanded_Image);
       }catch (e: IOException){
           e.printStackTrace()
       }
    }

//   fun setLostData(name:String,place:String,date:String,desc:String,phone:String){
//          LostExpanded_Name.text=name;
//          LostExpanded_Place.text=place;
//       LostExpanded_When.text=date;
//       LostExpanded_Number.text=phone
//       LostExpanded_Description.text=desc
////
//    }
}