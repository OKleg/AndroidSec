package com.example.imagetagseditor.edit

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.BuildConfig
import com.example.imagetagseditor.R
import com.example.imagetagseditor.edit.adapter.ViewAdapter
import com.example.imagetagseditor.main.MainViewModel
import com.example.imagetagseditor.model.HealthData
import com.example.imagetagseditor.model.StepsInfo
import java.time.Instant
import java.util.Date
import java.io.File
import java.util.UUID

class EditFragment : Fragment() {
    private val viewModel = MainViewModel.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewAdapter
    private lateinit var add: Button
    private lateinit var save: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.tempHealthData.stepsRecords.clear()
        viewModel.tempHealthData.stepsRecords.addAll(viewModel.healthData.stepsRecords)
        adapter = ViewAdapter(viewModel.tempHealthData.stepsRecords)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        recyclerView = view.findViewById(R.id.editable_tags_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        add = view.findViewById(R.id.add)
        add.setOnClickListener {
            viewModel.tempHealthData.stepsRecords.add(StepsInfo(
                recordId = null,
                date = Date.from(Instant.now()),
                stepsNumber = 1
            ))
            adapter.update()
        }
        save = view.findViewById(R.id.save)
        save.setOnClickListener {
            viewModel.dumpHealthData()
            viewModel.healthData.stepsRecords.clear()
            viewModel.healthData.stepsRecords.addAll(viewModel.tempHealthData.stepsRecords)
            it.findNavController().navigateUp()
        }
        return view
    }
}