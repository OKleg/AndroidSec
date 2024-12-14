package com.example.imagetagseditor.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewAdapter(private var tags: List<Pair<String, String>>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.tag, viewGroup, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.key.text = tags[position].first
        viewHolder.value.text = tags[position].second
    }

    override fun getItemCount() = tags.count()

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        notifyDataSetChanged()
    }
}