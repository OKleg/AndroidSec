package com.example.imagetagseditor.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R
import com.example.imagetagseditor.model.StepsInfo

class ViewAdapter(private var data: MutableList<StepsInfo>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.tag, viewGroup, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val date = data[position].date
        viewHolder.date.text = "${date.year + 1900}/${date.month + 1}/${date.date} ${date.hours}:${date.minutes}"
        viewHolder.stepsNumber.text = data[position].stepsNumber.toString()
    }

    override fun getItemCount() = data.count()

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        notifyDataSetChanged()
    }
}