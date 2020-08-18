package com.casamoderna.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.casamoderna.R
import com.casamoderna.model.Order
import com.casamoderna.viewholder.OrderListViewHolder

/**
 * Created by marco on 10,August,2020
 */
class OrderListAdapter (orders: MutableList<Order>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfOrders =  orders

    private var itemClick: ((String) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderListViewHolder(  LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        ).apply {
            itemClick = { orderId ->
                this@OrderListAdapter.itemClick?.invoke(orderId)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val orderViewHolder = viewHolder as OrderListViewHolder
        orderViewHolder.bindView(listOfOrders[position])
    }
    override fun getItemCount(): Int = listOfOrders.size
}