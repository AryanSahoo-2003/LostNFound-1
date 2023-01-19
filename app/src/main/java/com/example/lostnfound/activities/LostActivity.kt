package com.example.lostnfound.activities
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.collections.ArrayList

class LostActivity : AppCompatActivity() {
    lateinit var searchLzView : SearchView
    lateinit var courseRV: RecyclerView
    lateinit var courseModelArrayList : ArrayList<Lost>
    lateinit var courseAdapter : CourseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
//        val mycalender=Calendar.getInstance()
//        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
//            mycalender.set(Calendar.YEAR,i)
//            mycalender.set(Calendar.MONTH,i2)
//            mycalender.set(Calendar.DAY_OF_MONTH,i3)
//            updateLabel(mycalender)
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)

//        findViewById<ImageView>(R.id.Lost_When).setOnClickListener{
//            DatePickerDialog(this,datePicker,mycalender.get(Calendar.YEAR),mycalender.get(Calendar.MONTH),mycalender.get(Calendar.DAY_OF_MONTH)).show()
//        }
        searchLzView=findViewById(R.id.searchView_Lost)


        courseRV = findViewById<RecyclerView>(R.id.idRVCourse)
        courseModelArrayList= ArrayList()
//        // Here, we have created new array list and added data to it
//        val courseModelArrayList: ArrayList<Lost> = ArrayList()
        // we are initializing our adapter class and passing our arraylist to it.
        courseAdapter = CourseAdapter(this, courseModelArrayList)

        val db= FirebaseFirestore.getInstance()
        db.collection("lostitem")
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val lostitem=Lost(
                        document.data["name"] as String,
                        document.data["phone"] as String,
                        document.data["place"] as String,
                        document.data["description"] as String,
                        document.data["image"] as String,
                        "",
                        "",
                        document.data["date_time"] as String,
                        document.data["item_lost"] as String
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
//         in below two lines we are setting layoutmanager and adapter to our recycler view.
        courseRV.layoutManager = linearLayoutManager
        courseRV.adapter = courseAdapter

        searchLzView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filters(msg)
                return false
            }
        })


        courseAdapter.setOnItemClickListener(object : CourseAdapter.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                var l=courseModelArrayList[position]
                var alphaName = l.name
//                Lost_Expanded_Description().setLostData(l.name,l.place,l.date_time,l.description,l.phone)
                val intentToLostExpanded = Intent(this@LostActivity,Lost_Expanded_Description::class.java)
                intentToLostExpanded.putExtra("name",alphaName)
                intentToLostExpanded.putExtra("place",l.place)
                intentToLostExpanded.putExtra("date",l.date_time)
                intentToLostExpanded.putExtra("desc",l.description)
                intentToLostExpanded.putExtra("phone",l.phone)
                intentToLostExpanded.putExtra("img",l.image)
                startActivity(intentToLostExpanded)
                Toast.makeText(this@LostActivity,"hihiih$position",Toast.LENGTH_SHORT).show()
            }
        })

    }
//    private fun filterlist(query : String?){
//        if(query!=null){
//            val filteredList = ArrayList<Lost>()
//            for(i in courseModelArrayList){
//                if(i.item_lost.toString().lowercase(Locale.ROOT).contains(query)){
//                    filteredList.add(i)
//                }
//            }
//            if(filteredList.isEmpty()){
//                Toast.makeText(this@LostActivity,"No Data Found Here",Toast.LENGTH_SHORT).show()
//            }
//            else{
//                courseAdapter.setFilteredList(filteredList)
//            }
//        }
//    }

    private fun filters(text: String) {
        // creating a new array list to filter our data.
        var filteredlist: ArrayList<Lost> = ArrayList()

        // running a for loop to compare elements.
        for (item in courseModelArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.item_lost.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            filteredlist= ArrayList()
            courseAdapter.setFilteredList(filteredlist)

            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            courseAdapter.setFilteredList(filteredlist)
        }
    }
}
