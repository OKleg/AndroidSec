package com.example.imagetagseditor.edit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewAdapter(private var tags: MutableList<Pair<String, String>>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.edit_tag, viewGroup, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tag.hint = tags[position].first
        viewHolder.tag.setText(tags[position].second)
        viewHolder.tag.doOnTextChanged { text, start, before, count ->
            tags[position] = Pair(tags[position].first, text.toString())
        }
    }

    override fun getItemCount() = tags.count()

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        notifyDataSetChanged()
    }
}