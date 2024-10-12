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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import java.text.NumberFormat
import android.util.Patterns

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        val (_, errorDetails) = validateInput(itemDetails)
        itemUiState = ItemUiState(itemDetails = itemDetails, errorDetails = errorDetails)
    }

    /**
     * Inserts an [Item] in the Room database
     */
    suspend fun saveItem(): Boolean {
        val (isValid, errorDetails) = validateInput()
        itemUiState = ItemUiState(itemDetails = itemUiState.itemDetails, errorDetails = errorDetails)
        if (isValid) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
        return isValid
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

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val errorDetails: ErrorDetails = ErrorDetails(),
    //val isEntryValid: Boolean = false,
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val agentName: String = "",
    val agentEmail: String = "",
    val agentPhoneNumber: String = ""
) {
    override fun toString(): String {
        var result = "Name: $name\nPrice: $price\nQuantity: $quantity\n"
        if (agentName.isNotBlank()) {
            result += "Agent Name: $agentName\n"
        }
        if (agentEmail.isNotBlank()) {
            result += "Agent Email: $agentEmail\n"
        }
        if (agentPhoneNumber.isNotBlank()) {
            result += "Agent Phone: $agentPhoneNumber"
        }
        return result
    }
}

data class ErrorDetails(
    var name: String = "",
    var price: String = "",
    var quantity: String = "",
    var agentName: String = "",
    var agentEmail: String = "",
    var agentPhoneNumber: String = ""
)

/**
 * Extension function to convert [ItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
    agentName = agentName,
    agentEmail = agentEmail,
    agentPhoneNumber = agentPhoneNumber
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    //isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    agentName = agentName,
    agentEmail = agentEmail,
    agentPhoneNumber = agentPhoneNumber
)
