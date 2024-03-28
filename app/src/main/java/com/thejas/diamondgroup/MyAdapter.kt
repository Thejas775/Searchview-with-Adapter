package com.thejas.diamondgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val numlist: ArrayList<Vehicles>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.itemlist,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return numlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = numlist[position]
        holder.numberl.text = currentItem.number
    }

    // New method to update the list with filtered data
    fun updateList(newList: ArrayList<Vehicles>) {
        numlist.clear() // Clear existing data
        numlist.addAll(newList) // Add the filtered list
        notifyDataSetChanged() // Notify the adapter about the data change
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberl: TextView = itemView.findViewById(R.id.vehnum)
    }
}
