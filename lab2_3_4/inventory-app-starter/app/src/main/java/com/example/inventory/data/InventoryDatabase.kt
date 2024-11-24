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

package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.example.inventory.SharedData


/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var instance: InventoryDatabase? = null
        private val databaseName = "item_database"

        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return instance ?: synchronized(this) {
                val key = SharedData.preferences.masterKey.toString().toCharArray()
                val factory = SafeHelperFactory(key)
                val databaseState = SQLCipherUtils.getDatabaseState(context, databaseName)
                if (databaseState == SQLCipherUtils.State.UNENCRYPTED) {
                    SQLCipherUtils.encrypt(context, databaseName, key)
                }
                Room.databaseBuilder(context, InventoryDatabase::class.java, databaseName)
                    .openHelperFactory(factory)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
