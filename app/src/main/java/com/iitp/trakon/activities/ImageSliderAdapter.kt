package com.iitp.trakon.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iitp.trakon.R


class ImageSliderAdapter(val image: ArrayList<String>):RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>(){
    class ImageSliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgSliderView : ImageView = itemView.findViewById(R.id.eachImgSlide)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_image_slider,parent,false)
        return ImageSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        val alpha = image[position]
        Glide.with(holder.itemView.context)
            .load(alpha)
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(holder.imgSliderView)
    }

    override fun getItemCount(): Int {
        return image.size
    }


}