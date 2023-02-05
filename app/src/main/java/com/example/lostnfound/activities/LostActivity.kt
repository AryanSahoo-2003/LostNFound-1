package com.example.lostnfound.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Integer.max
import kotlin.properties.Delegates

class LostActivity :Fragment() {
    lateinit var searchLzView : SearchView
    lateinit var courseRV: RecyclerView
    lateinit var courseModelArrayList : ArrayList<Lost>
    lateinit var SearchList: ArrayList<Lost>
    lateinit var courseAdapter : CourseAdapter
    lateinit var db: FirebaseFirestore
    lateinit var linearLayoutManager:LinearLayoutManager
    lateinit var scrollListener: RecyclerView.OnScrollListener
    var isLastPage by Delegates.notNull<Boolean>()
    var isLoading by Delegates.notNull<Boolean>()
    var currentPage by Delegates.notNull<Int>()
   private var lastuser: DocumentSnapshot? =null

    lateinit var query: Query

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview=inflater.inflate(R.layout.activity_lost, container, false)!!

        searchLzView=rootview.findViewById(R.id.searchView_Lost)


        courseRV = rootview.findViewById<RecyclerView>(R.id.idRVCourse)




//        setRecyclerViewScrollListener()
        courseModelArrayList= ArrayList()
//        // Here, we have created new array list and added data to it
//        val courseModelArrayList: ArrayList<Lost> = ArrayList()
        // we are initializing our adapter class and passing our arraylist to it.
        courseAdapter = context?.let { CourseAdapter(it, courseModelArrayList) }!!
         currentPage=0;

        SearchList= ArrayList()
        db = FirebaseFirestore.getInstance()
        db.collection("lostitem").orderBy("timestamp",Query.Direction.DESCENDING).get().addOnSuccessListener {
            for(document in it){
                if(document.data["delete"]==true) continue;
                val lostitem = Lost(
                    document.data["name"] as String,
                    document.data["phone"] as String,
                    document.data["place"] as String,
                    document.data["description"] as String,
                    document.data["image"] as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */,
                    "",
                    "",
                    document.data["date_time"] as String,
                    document.data["item_lost"] as String
                )
                SearchList.add(lostitem)
            }
        }

        query=db.collection("lostitem")
            .orderBy("timestamp",Query.Direction.DESCENDING)

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //Pagination
        isLastPage=false;
        isLoading=false
         loadMoreItems()
        courseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        loadMoreItems()
                    }
                }
            }
        })

//         in below two lines we are setting layoutmanager and adapter to our recycler view.
        courseRV.layoutManager = linearLayoutManager
        courseRV.adapter = courseAdapter

        rootview.findViewById<Button>(R.id.hllo).setOnClickListener{
            val intentFloat = Intent(context,FillLostItem::class.java)
            startActivity(intentFloat)
        }

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
                val lsize = l.image.size
    //                Lost_Expanded_Description().setLostData(l.name,l.place,l.date_time,l.description,l.phone)
                    val intentToLostExpanded = Intent(context,Lost_Expanded_Description::class.java)
                    intentToLostExpanded.putExtra("lsize",lsize)
                    intentToLostExpanded.putExtra("name",alphaName)
                    intentToLostExpanded.putExtra("place",l.place)
                    intentToLostExpanded.putExtra("date",l.date_time)
                    intentToLostExpanded.putExtra("desc",l.description)
                    intentToLostExpanded.putExtra("phone",l.phone)
                    for(i in 0..lsize-1){
                        intentToLostExpanded.putExtra("test"+i.toString(),l.image[i])
                    }
                    startActivity(intentToLostExpanded)
//                Toast.makeText(this@LostActivity,"hihiih$position",Toast.LENGTH_SHORT).show()
            }
        })

       return rootview
    }

//private fun setRecyclerViewScrollListener() {
//    scrollListener = object : RecyclerView.OnScrollListener() {
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
//            val totalItemCount = recyclerView.layoutManager?.itemCount
//            if (totalItemCount == lastVisibleItemPosition + 1) {
//                loadMoreData();
//                Log.d("MyTAG", "Load new list")
//               courseRV.removeOnScrollListener(scrollListener)
//            }
//            loadData();
//        }
//    }
//    courseRV.addOnScrollListener(scrollListener)
//}

//    private fun loadData() {
//        Toast.makeText(context,currentPage.toString(),Toast.LENGTH_SHORT).show()
//        // example
//        // at first load : currentPage = 0 -> we startAt(0 * 10 = 0)
//        // at second load (first loadmore) : currentPage = 1 -> we startAt(1 * 10 = 10)
//        db.collection("lostitem")
//            .limit(4)
//            .orderBy("timestamp",Query.Direction.DESCENDING)
//            .startAt()
//            .get()
//            .addOnSuccessListener { result ->
//
//                if (result.isEmpty()) {
//                    currentpage--;
//                }
//                for (document in result) {
//                    val lostitem = Lost(
//                        document.data["name"] as String,
//                        document.data["phone"] as String,
//                        document.data["place"] as String,
//                        document.data["description"] as String,
//                        document.data["image"] as String,
//                        "",
//                        "",
//                        document.data["date_time"] as String,
//                        document.data["item_lost"] as String
//                    )
//                    courseModelArrayList.add(lostitem)
//                }
//                courseAdapter.notifyDataSetChanged()
//            }
//    }


//    private fun loadMoreData() {
//        currentpage++
//        loadData()
//    }


    private fun loadMoreItems() {
        isLoading = true
        currentPage += 1
//        Toast.makeText(context,currentPage.toString(),Toast.LENGTH_SHORT).show()

        if(lastuser!=null){
            Log.d("aryan",lastuser.toString())
            query=query.startAfter(lastuser!!)
            Log.d("aryan",query.toString())
        }

            query
            .limit(10)
            .get()
            .addOnSuccessListener {
                    result ->
                isLoading=false

                if(result.size()<10){
                    isLastPage=true
                }

                for (document in result) {
                    lastuser=document
                    Log.d("aryan",lastuser.toString())
                    if(document.data["delete"]==true) continue;
                    val lostitem = Lost(
                        document.data["name"] as String,
                        document.data["phone"] as String,
                        document.data["place"] as String,
                        document.data["description"] as String,
                        document.data["image"] as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */,
                        "",
                        document.data["user_email"] as String,
                        document.data["date_time"] as String,
                        document.data["item_lost"] as String
                    )
//                    Log.d("aryan",lostitem.item_lost.toString())
                    courseModelArrayList.add(lostitem)
                }
//                    lastuser=result.documents[result.size()-1]
//                Log.d("aryan",isLoading.toString())
//                Log.d("aryan",isLastPage.toString())
                courseAdapter.notifyItemRangeInserted(max(10*(currentPage-1)-1,0),result.size())
            }

    }


    private fun filters(text: String) {
        // creating a new array list to filter our data.
        var filteredlist: ArrayList<Lost> = ArrayList()

        // running a for loop to compare elements.
        for (item in SearchList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.item_lost.toLowerCase().startsWith(text.toLowerCase())) {
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
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            courseAdapter.setFilteredList(filteredlist)
        }
    }
}
