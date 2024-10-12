package com.example.inventory

import kotlinx.coroutines.flow.MutableStateFlow

data class ShareData(val text: String = "")

object SharedData {
    val dataToShare: MutableStateFlow<ShareData> = MutableStateFlow(ShareData())
}