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
import com.example.imagetagseditor.model.ImageData
import java.io.File
import java.util.UUID

class EditFragment : Fragment() {
    companion object {
        const val REQUEST_CODE = 3
    }

    private val viewModel = MainViewModel.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewAdapter
    private lateinit var save: Button
    private var localTags = emptyList<Pair<String, String>>().toMutableList()
    private lateinit var saveCallback: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.imageData.tags.forEach {
            if (ImageData.editableTagNames.contains(it.first)) {
                localTags.add(it)
            }
        }
        adapter = ViewAdapter(localTags)
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
        save = view.findViewById(R.id.save)
        saveCallback = {
            saveExifTags(viewModel.imageUri, localTags)
            view.findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
        save.setOnClickListener {
            if (!Environment.isExternalStorageManager()) {
                requestPermission()
            } else {
                saveCallback()
            }
        }
        return view
    }

    private fun saveExifTags(uri: Uri, tags: List<Pair<String, String>>) {
        val mainInputStream = context?.contentResolver?.openInputStream(uri) ?: return
        val file = File(context?.cacheDir?.absolutePath + "/" + UUID.randomUUID().toString())
        if (file.exists()) {
            file.delete()
        }
        val tempOutputStream = file.outputStream()
        tempOutputStream.write(mainInputStream.readBytes())

        val metadata = ExifInterface(file)
        tags.forEach {
            metadata.setAttribute(it.first, it.second)
        }
        metadata.saveAttributes()

        val mainOutputStream = context?.contentResolver?.openOutputStream(uri) ?: return
        val tempInputStream = file.inputStream()
        mainOutputStream.write(tempInputStream.readBytes())
    }

    private fun requestPermission() {
        val accessIntent = Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        )
        startActivityForResult(accessIntent, REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Environment.isExternalStorageManager()) {
            saveCallback()
        }
    }
}