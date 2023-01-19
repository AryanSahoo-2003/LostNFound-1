package com.example.lostnfound.activities
import android.app.AlertDialog
import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lostnfound.R
import com.example.lostnfound.models.Lost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item.view.del_button
import kotlinx.android.synthetic.main.item_my.view.*
import java.io.IOException

class MyLostAdapter(private val context: Context, courseModelArrayList: ArrayList<Lost>) :
    RecyclerView.Adapter<MyLostAdapter.Viewholder>() {

    private lateinit var mlistener : OnItemClickListener

    interface OnItemClickListener{
        fun OnItemClick(position:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mlistener = listener
    }

    private val courseModelArrayList: ArrayList<Lost>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_my, parent, false)
        return Viewholder(view,mlistener).listen{ pos, type ,flag->
//
            val item=courseModelArrayList[pos]
   if(flag==0){
       val builder = AlertDialog.Builder(context)
       //set title for alert dialog
       builder.setTitle("Delete")
       //set message for alert dialog
       builder.setMessage("Sure you want to delete?")
       builder.setIcon(android.R.drawable.ic_dialog_alert)

       builder.setPositiveButton("Yes") { dialogInterface, which ->
           FirebaseFirestore.getInstance().collection("lostitem").document(item.item_id).delete()
               .addOnSuccessListener {
                   Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                   context.startActivity(Intent(context, MyLostActivity::class.java))
                   MyLostActivity().finish()
               }.addOnFailureListener {
               Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
           }
       }
       builder.setNeutralButton("Cancel"){dialogInterface , which ->

       }
       val alertDialog: AlertDialog = builder.create()
       // Set other dialog properties
       alertDialog.setCancelable(false)
       alertDialog.show()
   }
       else{
       val name=item.name
       val phone=item.phone
       val place=item.place
       val date=item.date_time
       val keyword=item.item_lost
       val desc=item.description
       val image=item.image

       val intent = Intent(context, updateLost::class.java)
       //listener?.onClick(AlbumsData)
       intent.putExtra("name", name)
       intent.putExtra("phone", phone)
       intent.putExtra("place", place)
       intent.putExtra("date", date)
       intent.putExtra("keyword", keyword)
       intent.putExtra("desc", desc)
       intent.putExtra("image", image)
       intent.putExtra("doc_id", item.item_id)
       context.startActivity(intent)
   }
   }

    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val item: Lost = courseModelArrayList[position]

        holder.name_lost.text = item.name
        holder.phone_lost.text = item.item_lost
        holder.place_lost.text = item.place
        holder.desc_lost.text = item.date_time
        try {
            Glide     //using Glide to display image from url
                .with(context)
                .load(item.image)
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


    class Viewholder(view: View,listener: MyLostAdapter.OnItemClickListener) : RecyclerView.ViewHolder(view) {
        var image_lost: ImageView = view.findViewById(R.id.imgae_lost_item)
        var name_lost: TextView = view.findViewById(R.id.mane_lost_item)
        var phone_lost: TextView = view.findViewById(R.id.keyword_lost_item)
        var place_lost: TextView = view.findViewById(R.id.place_lost_item)
        var desc_lost: TextView = view.findViewById(R.id.desc_lost_item)

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
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int,flag:Int) -> Unit): T {
        itemView.del_button.setOnClickListener {
            event.invoke(getAdapterPosition(), itemViewType,0)
        }
        itemView.edit_button.setOnClickListener {
            event(
                getAdapterPosition(),
                itemViewType,
                1
            )
        }
        return this
    }
}
