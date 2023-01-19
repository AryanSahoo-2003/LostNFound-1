package com.example.lostnfound.activities
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyFoundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_found)
        val courseRV = findViewById<RecyclerView>(R.id.idRVCourse)

        // Here, we have created new array list and added data to it
        val courseModelArrayList: ArrayList<Lost> = ArrayList()
        // we are initializing our adapter class and passing our arraylist to it.
        val courseAdapter = MyFoundAdapter(this, courseModelArrayList)

        val db= FirebaseFirestore.getInstance()
        db.collection("founditem")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["user_id"]!=FirebaseAuth.getInstance().currentUser!!.uid) continue
                    val lostitem=Lost(
                        document.data["name"] as String,
                        document.data["phone"] as String,
                        document.data["place"] as String,
                        document.data["description"] as String,
                        document.data["image"] as String,"","",
                        document.data["date_time"] as String,
                        document.data["item_lost"] as String,
                        document.id
                    )
                    courseModelArrayList.add(lostitem)
                }
                courseAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            }
        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        courseRV.layoutManager = linearLayoutManager
        courseRV.adapter = courseAdapter
        courseAdapter.setOnItemClickListener(object : MyFoundAdapter.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                val l=courseModelArrayList[position]
                val alphaName = l.name
//                Lost_Expanded_Description().setLostData(l.name,l.place,l.date_time,l.description,l.phone)
                val intentToLostExpanded = Intent(this@MyFoundActivity,Lost_Expanded_Description::class.java)
                intentToLostExpanded.putExtra("name",alphaName)
                intentToLostExpanded.putExtra("place",l.place)
                intentToLostExpanded.putExtra("date",l.date_time)
                intentToLostExpanded.putExtra("desc",l.description)
                intentToLostExpanded.putExtra("phone",l.phone)
                intentToLostExpanded.putExtra("img",l.image)
                startActivity(intentToLostExpanded)
//                Toast.makeText(this@FoundActivity,"hihiih$position",Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onBackPressed(){
        startActivity(Intent(this,AfterLoginPage::class.java))
    }
}
