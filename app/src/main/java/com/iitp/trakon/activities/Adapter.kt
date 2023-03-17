package com.iitp.trakon.activities
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iitp.trakon.R
import com.iitp.trakon.models.Lost
import com.iitp.trakon.models.Users
import com.iitp.trakon.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item.view.*
import java.io.IOException

class CourseAdapter(private val context: Context, courseModelArrayList: ArrayList<Lost>) :
    RecyclerView.Adapter<CourseAdapter.Viewholder>() {
    private lateinit var mlistener : OnItemClickListener

    interface OnItemClickListener{
        fun OnItemClick(position:Int)
    }

    fun setFilteredList(courseModelArrayListChanged: ArrayList<Lost>){
        courseModelArrayList = courseModelArrayListChanged
        notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        mlistener = listener
    }
    private var courseModelArrayList: ArrayList<Lost>

    private val mFireStore= FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseAdapter.Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return Viewholder(view,mlistener).listen { pos, type,flag ->
            val item = courseModelArrayList[pos]
            if(flag==0){

                var sendto="bjhddh"
                var message=""


                mFireStore.collection(Constants.USERS)
                    .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                        val loggedInUser = document.toObject(Users::class.java)!!
                        message ="Hello "+item.name+",\n"+loggedInUser.name+" has found the lost item.\nDescription:"+item.description+ "\nEmail:"+loggedInUser.email+"\nPhone Number:"+loggedInUser.mobile+"\nWhatsapp:"+loggedInUser.whatsapp;
                    }.addOnCompleteListener{
                        val subject= "Found Lost Item"
                        val msg=message
                        sendto=item.user_email

                        val send = Intent(Intent.ACTION_SENDTO)
                        val uriText = "mailto:" + Uri.encode(item.user_email) +
                                "?subject=" + Uri.encode(subject) +
                                "&body=" + Uri.encode(msg)
                        val uri = Uri.parse(uriText)
                        send.putExtra(Intent.EXTRA_EMAIL,"no-reply@iitp.ac.in")
                        Log.d("aryan",item.user_email)
                        send.data = uri
                        context.startActivity(Intent.createChooser(send, "Choose an Email client :"))
                    }

            }
            else if(flag==1){
                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("Remove")
                //set message for alert dialog
                builder.setMessage("Sure you want to delete?")
                builder.setPositiveButton("Yes") { dialogInterface, which ->


                    FirebaseFirestore.getInstance().collection("lostitem")
                        .document(item.item_id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context,"Deleted Successfully!!", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context,Tabs::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                }
                builder.setNeutralButton("Cancel") { dialogInterface, which ->

                }
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
//                alertDialog.setView(dialogLayout)
//               builder.show()
                alertDialog.show()
            }
            else{
                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("Block")
                //set message for alert dialog
                builder.setMessage("Sure you want to block the user?")

                builder.setPositiveButton("Yes") { dialogInterface, which ->


                    val user = FirebaseFirestore.getInstance().collection("lostitem")
                        .document(item.item_id).get()
                        .addOnSuccessListener {
                            var user_id = it.data?.get("user_id")
                            FirebaseFirestore.getInstance().collection("users")
                                .document(user_id as String).update("valid", false).addOnSuccessListener {
                                    Toast.makeText(context,"User Blocked Successfully!!",Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                }
                builder.setNeutralButton("Cancel") { dialogInterface, which ->

                }
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
//                alertDialog.setView(dialogLayout)
//               builder.show()
                alertDialog.show()

            }

        }

    }

    override fun onBindViewHolder(holder: CourseAdapter.Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val item: Lost = courseModelArrayList[position]


        holder.name_lost.text=item.name
        holder.keyword_lost.text=item.item_lost
        holder.place_lost.text=item.place
        holder.when_lost.text=item.date_time
        holder.found_it_button.text="Found it!"

        FirebaseFirestore.getInstance().collection("users")
            .document(getcurrentUserID()).get().addOnSuccessListener {
                if(it.data?.get("admin") as Boolean==true){
                    holder.block.visibility=View.VISIBLE
                    holder.remove.visibility=View.VISIBLE
                }
            }

        try {
            Glide     //using Glide to display image from url
                .with(context)
                .load(item.image[0])
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.image_lost);
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        // this method is used for showing number of card items in recycler view.
        return courseModelArrayList.size
    }



    class Viewholder(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        var image_lost: ImageView = view.findViewById(R.id.imgae_lost_item)
        var name_lost: TextView = view.findViewById(R.id.mane_lost_item)
        var keyword_lost: TextView = view.findViewById(R.id.keyword_lost_item)
        var place_lost: TextView = view.findViewById(R.id.place_lost_item)
        var when_lost: TextView = view.findViewById(R.id.when_lost_item)
//        var desc_lost: TextView = view.findViewById(R.id.desc_lost_item)
        var found_it_button: Button =view.findViewById(R.id.del_button)
        var block:Button=view.findViewById(R.id.block_button)
        var remove:Button=view.findViewById(R.id.remove_button)
        init{
            view.setOnClickListener {
                listener.OnItemClick(adapterPosition)
            }
        }
    }


    // Constructor
    init {
        this.courseModelArrayList = courseModelArrayList
    }
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int, flag: Int) -> Unit): T {
        itemView.del_button.setOnClickListener {
            event.invoke(getAdapterPosition(), itemViewType, 0)
        }
        itemView.remove_button.setOnClickListener {
            event(
                getAdapterPosition(),
                itemViewType,
                1
            )
        }
        itemView.block_button.setOnClickListener {
            event(
                getAdapterPosition(),
                itemViewType,
                2
            )
        }
        return this
    }
    private fun getcurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}
