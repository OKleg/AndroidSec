package com.example.imagetagseditor.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.imagetagseditor.edit.EditFragment
import com.example.imagetagseditor.model.ImageData
import java.io.File

class MainViewModel : ViewModel() {
    companion object {
        private var instance: MainViewModel? = null
        fun getInstance(): MainViewModel {
            if (instance == null) {
                instance = MainViewModel()
            }
            return instance!!
        }
    }

    var imageData = ImageData(emptyMap())
    var imageUri = Uri.EMPTY
}