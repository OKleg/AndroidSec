package com.example.imagetagseditor.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.imagetagseditor.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val REQUEST_CODE = 2
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var imageView: ImageView
    private lateinit var loadButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        imageView = view.findViewById(R.id.image_view)
        loadButton = view.findViewById(R.id.load)
        loadButton.setOnClickListener { openImage() }
        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            loadImage(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openImage() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_CODE)
    }

    private fun loadImage(intent: Intent?) {
        if (intent == null)
            return
        val pickedImage = intent.data ?: return
        imageView.setImageURI(pickedImage)
    }
}