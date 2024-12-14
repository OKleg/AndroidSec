package com.example.imagetagseditor.edit.adapter

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tag: EditText = view.findViewById(R.id.editable_tag)
}