package com.iitp.trakon.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iitp.trakon.R
import com.iitp.trakon.models.Lost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.iitp.trakon.models.Users

class MyLostActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview=inflater.inflate(R.layout.activity_my_lost, container, false)!!

        val courseRV = rootview.findViewById<RecyclerView>(R.id.idRVCourse)

        // Here, we have created new array list and added data to it
        val courseModelArrayList: ArrayList<Lost> = ArrayList()
        val uid=FirebaseAuth.getInstance().currentUser!!.uid
        var detail: Users = Users()
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener {
            detail = it.toObject(Users::class.java)!!
        }
        // we are initializing our adapter class and passing our arraylist to it.
        val courseAdapter = context?.let { MyLostAdapter(it, courseModelArrayList) }!!

        val db= FirebaseFirestore.getInstance()
        db.collection("lostitem")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["user_id"]!= FirebaseAuth.getInstance().currentUser!!.uid) continue
                    if(document.data["delete"] ==true) continue
                    val lostitem=Lost(
                        document.data["name"] as String,
                        document.data["phone"] as String,
                        document.data["place"] as String,
                        document.data["description"] as String,
                        document.data["image"] as ArrayList<String>,
                        document.data["user_id"] as String,
                        document.data["user_email"] as String,
                        document.data["date_time"] as String,
                        document.data["item_lost"] as String,
                        document.id as String,
                        "",
                        "",
                        false


                    )
                    courseModelArrayList.add(lostitem)
                }
                courseAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
            }
        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        courseRV.layoutManager = linearLayoutManager
        courseRV.adapter = courseAdapter


        rootview.findViewById<Button>(R.id.hllo).setOnClickListener{
            val intentFloat = Intent(context,FillLostItem::class.java)


            intentFloat.putExtra("name",detail.name)
            intentFloat.putExtra("whatsapp",detail.whatsapp)
            intentFloat.putExtra("email",detail.email)
            Log.d("aryan",detail.toString())
            startActivity(intentFloat)

        }

        courseAdapter.setOnItemClickListener(object : MyLostAdapter.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                val l=courseModelArrayList[position]
                val alphaName = l.name
                val lsize = l.image.size
                //                Lost_Expanded_Description().setLostData(l.name,l.place,l.date_time,l.description,l.phone)
                val intentToLostExpanded = Intent(context,Lost_Expanded_Description::class.java)
                intentToLostExpanded.putExtra("lsize",lsize)
                intentToLostExpanded.putExtra("name",alphaName)
                intentToLostExpanded.putExtra("place",l.place)
                intentToLostExpanded.putExtra("date",l.date_time)
                intentToLostExpanded.putExtra("desc",l.description)
                intentToLostExpanded.putExtra("phone",l.phone)
                intentToLostExpanded.putExtra("user_id_p",l.user_id)
                for(i in 0..lsize-1){
                    intentToLostExpanded.putExtra("test"+i.toString(),l.image[i])
                }
                startActivity(intentToLostExpanded)
//                Toast.makeText(this@MyLostActivity,"hihiih$position",Toast.LENGTH_SHORT).show()
            }
        })
        return rootview
    }

}
