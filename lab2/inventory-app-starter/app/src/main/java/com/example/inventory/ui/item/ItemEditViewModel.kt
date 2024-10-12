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

package com.example.inventory.ui.item

import android.telephony.PhoneNumberUtils
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [ItemsRepository]'s data source.
 */
class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemStream(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
        }
    }

    /**
     * Update the item in the [ItemsRepository]'s data source
     */
    suspend fun updateItem(): Boolean {
        val (isValid, errorDetails) = validateInput()
        itemUiState = ItemUiState(itemDetails = itemUiState.itemDetails, errorDetails = errorDetails)
        if (isValid) {
            itemsRepository.updateItem(itemUiState.itemDetails.toItem())
        }
        return isValid
    }

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        val (_, errorDetails) = validateInput(itemDetails)
        itemUiState = ItemUiState(itemDetails = itemDetails, errorDetails = errorDetails)
    }

    private fun validateInput(itemDetails: ItemDetails = itemUiState.itemDetails): Pair<Boolean, ErrorDetails> {
        val errorDetails = ErrorDetails()
        var hasErrors = false

        if (itemDetails.name.isBlank()) {
            errorDetails.name = "Item Name cannot be empty"
            hasErrors = true
        }

        if (itemDetails.price.isBlank()) {
            errorDetails.price = "Item Price cannot be empty"
            hasErrors = true
        } else if (!"-?[0-9]+(\\.[0-9]+)?".toRegex().matches(itemDetails.price)) {
            errorDetails.price = "Item Price should be numerical"
            hasErrors = true
        }

        if (itemDetails.quantity.isBlank()) {
            errorDetails.quantity = "Item Quantity cannot be empty"
            hasErrors = true
        } else if (itemDetails.quantity.toIntOrNull() == null) {
            errorDetails.quantity = "Item Quantity should be integer value"
            hasErrors = true
        }

        if (itemDetails.agentEmail.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(itemDetails.agentEmail).matches()) {
            errorDetails.agentEmail = "Agent Email should have proper format"
            hasErrors = true
        }

        if (itemDetails.agentPhoneNumber.isNotBlank() && !PhoneNumberUtils.isGlobalPhoneNumber(itemDetails.agentPhoneNumber)) {
            errorDetails.agentPhoneNumber = "Agent Phone should have proper format"
            hasErrors = true
        }

        return Pair(!hasErrors, errorDetails)
    }
}
