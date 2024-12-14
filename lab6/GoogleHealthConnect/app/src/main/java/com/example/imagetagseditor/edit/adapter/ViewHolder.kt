package com.example.imagetagseditor.edit.adapter

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date: EditText = view.findViewById(R.id.tag_key)
    val stepsNumber: EditText = view.findViewById(R.id.tag_value)
    val delete: ImageButton = view.findViewById(R.id.delete_button)
}