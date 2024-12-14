package com.example.imagetagseditor.edit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R
import com.example.imagetagseditor.main.MainViewModel
import com.example.imagetagseditor.model.StepsInfo
import java.text.ParseException
import java.text.SimpleDateFormat

class ViewAdapter(private var data: MutableList<StepsInfo>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.edit_tag, viewGroup, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val date = data[position].date
        viewHolder.date.setText("${date.year + 1900}/${date.month + 1}/${date.date} ${date.hours}:${date.minutes}")
        viewHolder.date.doOnTextChanged { text, start, before, count ->
            try {
                val currentPosition = viewHolder.adapterPosition
                val newDate = SimpleDateFormat("yyyy/MM/dd kk:mm").parse(text.toString())
                if (newDate != null) {
                    data[currentPosition] = data[currentPosition].copy(date = newDate)
                    if (data[currentPosition].recordId != null) {
                        MainViewModel.getInstance().idsToDelete.add(data[currentPosition].recordId!!)
                        data[currentPosition].recordId = null
                    }
                }
            } catch (e: ParseException) {
                //...
            }
        }
        viewHolder.stepsNumber.setText(data[position].stepsNumber.toString())
        viewHolder.stepsNumber.doOnTextChanged { text, start, before, count ->
            val currentPosition = viewHolder.adapterPosition
            val newStepsNumber = text.toString().toLongOrNull()
            if (newStepsNumber != null && newStepsNumber >= 1 && newStepsNumber <= 1000000) {
                data[currentPosition] = data[currentPosition].copy(stepsNumber = newStepsNumber)
                if (data[currentPosition].recordId != null) {
                    MainViewModel.getInstance().idsToDelete.add(data[currentPosition].recordId!!)
                }
                data[currentPosition].recordId = null
            }
        }
        viewHolder.delete.setOnClickListener {
            val currentPosition = viewHolder.adapterPosition
            if (data[currentPosition].recordId != null) {
                MainViewModel.getInstance().idsToDelete.add(data[currentPosition].recordId!!)
            }
            data.removeAt(currentPosition)
            update()
        }
    }

    override fun getItemCount() = data.count()

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        notifyDataSetChanged()
    }
}