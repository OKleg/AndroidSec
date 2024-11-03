package com.example.inventory.ui.settings

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.inventory.Preferences.Companion.DEFAULT_ITEMS_QUANTITY
import com.example.inventory.Preferences.Companion.HIDE_IMPORTANT_DATA
import com.example.inventory.Preferences.Companion.PROHIBIT_SENDING_DATA
import com.example.inventory.Preferences.Companion.USE_DEFAULT_ITEMS_QUANTITY
import com.example.inventory.SharedData


class SettingsViewModel : ViewModel() {

    var settingsUiState by mutableStateOf(SettingsUiState(
        hideImportantData = SharedData.preferences.sharedPreferences.getBoolean(HIDE_IMPORTANT_DATA, false),
        prohibitSendingData = SharedData.preferences.sharedPreferences.getBoolean(PROHIBIT_SENDING_DATA, false),
        useDefaultItemsQuantity = SharedData.preferences.sharedPreferences.getBoolean(USE_DEFAULT_ITEMS_QUANTITY, false),
        defaultItemsQuantity = SharedData.preferences.sharedPreferences.getString(DEFAULT_ITEMS_QUANTITY, "1")!!
    ))
        private set


    fun update(newValue: SettingsUiState) {
        settingsUiState = newValue
        dump()
    }

    private fun dump() {
        val editor = SharedData.preferences.sharedPreferences.edit()
        editor.putBoolean(HIDE_IMPORTANT_DATA, settingsUiState.hideImportantData)
        editor.putBoolean(PROHIBIT_SENDING_DATA, settingsUiState.prohibitSendingData)
        editor.putBoolean(USE_DEFAULT_ITEMS_QUANTITY, settingsUiState.useDefaultItemsQuantity)
        editor.putString(DEFAULT_ITEMS_QUANTITY, settingsUiState.defaultItemsQuantity)
        editor.apply()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class SettingsUiState(
    val hideImportantData: Boolean = false,
    val prohibitSendingData: Boolean = false,
    val useDefaultItemsQuantity: Boolean = false,
    val defaultItemsQuantity: String = "1"
)