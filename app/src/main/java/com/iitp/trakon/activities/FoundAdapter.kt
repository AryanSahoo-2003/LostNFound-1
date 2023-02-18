package com.iitp.trakon.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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


class FoundAdapter(private val context: Context, courseModelArrayList: ArrayList<Lost>) :
    RecyclerView.Adapter<FoundAdapter.Viewholder>() {
    private lateinit var mlistener : OnItemClickListener

    interface OnItemClickListener{
        fun OnItemClick(position:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mlistener = listener
    }

    private var courseModelArrayList: ArrayList<Lost>

    private val mFireStore=FirebaseFirestore.getInstance()
//    private lateinit var  loggedInUser:Users
    @SuppressLint("IntentReset")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundAdapter.Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return Viewholder(view,mlistener).listen { pos, type ->
            val item = courseModelArrayList[pos]


            var sendto="bjhddh"
            var message=""

             mFireStore.collection(Constants.USERS)
                .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                    val loggedInUser = document.toObject(Users::class.java)!!
                    message ="Dear "+item.name+",\n"+loggedInUser.name+" has claimed the found item.\nDescription:"+item.description+ "\n Email:"+loggedInUser.email+"\nPhone Number:"+loggedInUser.mobile+"\nWhatsapp:"+loggedInUser.whatsapp;
                }.addOnCompleteListener{
                    val subject= "Claim for found item"
                    val msg=message
                    sendto=item.user_email

                    val send = Intent(Intent.ACTION_SENDTO)
                    val uriText = "mailto:" + Uri.encode(item.user_email) +
                            "?subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(msg)
                    val uri = Uri.parse(uriText)

                    send.data = uri
                    context.startActivity(Intent.createChooser(send, "Choose an Email client :"))
                }
        }
    }

    fun filterList(filterlist: ArrayList<Lost>) {
        // below line is to add our filtered
        // list in our course array list.
        courseModelArrayList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FoundAdapter.Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val item: Lost = courseModelArrayList[position]

        holder.name_lost.text=item.name
        holder.keyword_lost.text=item.item_lost
        holder.place_lost.text=item.place
        holder.when_found.text=item.date_time
        holder.found_it_button.text="Claim it!"
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
//        var desc_lost: TextView = view.findViewById(R.id.desc_lost_item)
        var when_found:TextView=view.findViewById(R.id.when_lost_item)
        var found_it_button: Button =view.findViewById(R.id.del_button)
        init {
            view.setOnClickListener {
                listener.OnItemClick(adapterPosition)
            }

        }
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
}
