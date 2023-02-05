package com.example.lostnfound.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.lostnfound.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_lost_expanded_description.*
import java.io.IOException

class Lost_Expanded_Description : AppCompatActivity() {
    lateinit var courseRV: RecyclerView
    lateinit var courseAdapter: ImageSliderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_expanded_description)

        var test : ArrayList<String> = ArrayList()
        var lsize = intent.getIntExtra("lsize",0)
        courseRV = findViewById<RecyclerView>(R.id.idRVCourse)
        for(i in 0..lsize-1){
            intent.getStringExtra("test"+i.toString())?.let { test.add(it) }
        }
        courseRV.setHasFixedSize(true)
        courseRV.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        val snapHelper:SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(courseRV)
        courseAdapter = ImageSliderAdapter(test)

        courseRV.adapter = courseAdapter



        LostExpanded_Name.setText(intent.getStringExtra("name").toString())
                  LostExpanded_Place.setText(intent.getStringExtra("place").toString())
       LostExpanded_When.setText(intent.getStringExtra("date").toString());
       scrollDesc.setText(intent.getStringExtra("desc").toString())
        var alpha = intent.getStringExtra("phone").toString()
        LostExpanded_Number.setOnClickListener{
            var intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:$alpha");
            startActivity(intent)
        }
    }

}