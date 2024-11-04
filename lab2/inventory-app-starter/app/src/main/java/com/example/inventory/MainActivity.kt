/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import com.example.inventory.ui.theme.InventoryTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File


class MainActivity : ComponentActivity() {
    companion object {
        const val CREATE_FILE = 1
        var dataToSave = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    InventoryApp()
                }
            }
        }

        lifecycleScope.launch {
            SharedData.dataToShare.collect {
                if (it.text.isNotBlank()) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, it.text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
        }
        lifecycleScope.launch {
            SharedData.dataToSave.collect {
                if (it.text.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/json"
                        putExtra(Intent.EXTRA_TITLE, "item.json")
                        dataToSave = it.text
                    }
                    startActivityForResult(intent, CREATE_FILE)
                }
            }
        }
        SharedData.preferences = Preferences(this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            val intent = resultData ?: return
            val uri = intent.data ?: return
            val outputStream = contentResolver.openOutputStream(uri) ?: return
            val file = kotlin.io.path.createTempFile().toFile()
            if (file.exists()) {
                file.delete()
            }
            val encryptedFile: EncryptedFile = EncryptedFile.Builder(
                this,
                file,
                SharedData.preferences.masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val encryptedOutputStream = encryptedFile.openFileOutput()
            encryptedOutputStream.write(dataToSave.toByteArray())
            encryptedOutputStream.flush()
            encryptedOutputStream.close()
            val inputStream = file.inputStream()
            outputStream.write(inputStream.readBytes())
            inputStream.close()
            outputStream.close()
        }
    }
}
