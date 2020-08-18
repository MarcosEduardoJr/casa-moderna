package com.casamoderna.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.casamoderna.R
import com.casamoderna.model.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import java.util.*

class SliderAdapter(private val context: Context) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    private var mSliderItems: MutableList<SliderItem> =
        ArrayList()

    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(
        viewHolder: SliderAdapterVH,
        position: Int
    ) {
        val sliderItem = mSliderItems[position]
        Picasso.get().load(sliderItem.imageUrl)
            .into(viewHolder.imageViewBackground)
        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
                .show()
        })
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)

    }

}