package com.example.imagetagseditor.main.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date: TextView = view.findViewById(R.id.tag_key)
    val stepsNumber: TextView = view.findViewById(R.id.tag_value)
}