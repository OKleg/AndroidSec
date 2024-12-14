package com.example.imagetagseditor.main.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val key: TextView = view.findViewById(R.id.tag_key)
    val value: TextView = view.findViewById(R.id.tag_value)
}