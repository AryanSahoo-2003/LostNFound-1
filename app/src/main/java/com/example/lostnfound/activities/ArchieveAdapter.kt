package com.example.lostnfound.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.example.lostnfound.models.Users
import com.example.lostnfound.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.archive_iem.view.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.item.view.del_button
import kotlinx.android.synthetic.main.item_my.view.*
import java.io.IOException


class ArchieveAdapter(private val context: Context, courseModelArrayList: ArrayList<Lost>) :
    RecyclerView.Adapter<ArchieveAdapter.Viewholder>() {

    private var courseModelArrayList: ArrayList<Lost>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchieveAdapter.Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.archive_iem, parent, false)
        return Viewholder(view).listen { pos, type, flag ->
            val item = courseModelArrayList[pos]
            if (flag == 0) {
                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("Block")
                //set message for alert dialog
                builder.setMessage("Sure you want to block the user?")

                builder.setPositiveButton("Yes") { dialogInterface, which ->


                    val user = FirebaseFirestore.getInstance().collection("archivefound")
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

            } else {

                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("Remove")
                //set message for alert dialog
                builder.setMessage("Sure you want to delete?")
                builder.setPositiveButton("Yes") { dialogInterface, which ->


                    FirebaseFirestore.getInstance().collection("archivefound")
                        .document(item.item_id).delete()
                        .addOnSuccessListener {
                           Toast.makeText(context,"Deleted Successfully!!",Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context,ArchieveTab::class.java))
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

                fun ffilterList(filterlist: ArrayList<Lost>) {
        // below line is to add our filtered
        // list in our course array list.
        courseModelArrayList = filterlist

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val item: Lost = courseModelArrayList[position]

        holder.name_lost.text=item.name
        holder.keyword_lost.text=item.item_lost
        holder.place_lost.text=item.place
        holder.when_found.text=item.date_time
        holder.target_name.text=item.target_name
        holder.target_roll.text=item.target_roll.uppercase()
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                  if(document.data["roll"]==item.target_roll){
                      holder.target_phone.text=document.data["mobile"].toString()
                  }
                }
            }
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


    class Viewholder(view: View) : RecyclerView.ViewHolder(view) {
        var image_lost: ImageView = view.findViewById(R.id.imgae_lost_item)
        var name_lost: TextView = view.findViewById(R.id.mane_lost_item)
        var keyword_lost: TextView = view.findViewById(R.id.keyword_lost_item)
        var place_lost: TextView = view.findViewById(R.id.place_lost_item)
        var when_found:TextView=view.findViewById(R.id.when_lost_item)
        var target_name:TextView=view.findViewById(R.id.lost_by_name)
        var target_roll:TextView=view.findViewById(R.id.lost_by_roll)
        var target_phone:TextView=view.findViewById(R.id.lost_by_phone)
        var block:Button=view.findViewById(R.id.blockuser)
        var remove:Button=view.findViewById(R.id.removeitem)
    }

    // Constructor
    init {
        this.courseModelArrayList = courseModelArrayList
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.del_button.setOnClickListener {
            event.invoke(getAdapterPosition(), itemViewType)
        }
        return this
    }
    private fun getcurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int,flag:Int) -> Unit): T {
        itemView.blockuser.setOnClickListener {
            event.invoke(getAdapterPosition(), itemViewType,0)
        }
        itemView.removeitem.setOnClickListener {
            event(
                getAdapterPosition(),
                itemViewType,
                1
            )
        }
        return this
    }

}
