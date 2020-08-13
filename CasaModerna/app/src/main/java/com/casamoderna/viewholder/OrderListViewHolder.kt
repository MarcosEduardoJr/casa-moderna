package com.casamoderna.viewholder

import android.content.ContentResolver
import android.provider.Settings.Global.getString
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.casamoderna.R
import com.casamoderna.SearchFragmentDirections
import com.casamoderna.model.Order
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.order_item.view.*

/**
 * Created by marco on 10,August,2020
 */
class OrderListViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    var itemClick: ((String) -> Unit)? = null

    fun bindView(order: Order) {
        itemView.item_local.text = order.local
        itemView.item_estimate.text =
            itemView.resources.getString(R.string.currency_real, order.valueOrder)
        itemView.item_description.text = order.description
        Picasso.get().load(order.imgOnePath).into(itemView.item_image)

        //For Handling RecyclerView Item Click
        itemView?.setOnClickListener {
            val action =
                SearchFragmentDirections.actionOrderDetail(
                    idOrder = order.docId!!
                )
            Navigation.findNavController(itemView).navigate(action)
        }

    }

}