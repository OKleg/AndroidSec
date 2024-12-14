package com.example.imagetagseditor.main

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetagseditor.model.HealthData
import com.example.imagetagseditor.model.StepsInfo
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date

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

    var healthConnectClient: HealthConnectClient? = null
    var healthData = HealthData()
    var tempHealthData = HealthData()
    //var idsToUpdate = emptySet<String>().toMutableSet()
    var idsToDelete = emptySet<String>().toMutableSet()
    var timeRange: TimeRangeFilter

    init {
        val nowDate = Date()
        timeRange = TimeRangeFilter.between(
            Date(nowDate.year, nowDate.month, nowDate.date, 0, 0, 0).toInstant(),
            Date(nowDate.year, nowDate.month, nowDate.date, 23, 59, 59).toInstant()
        )
    }

    fun loadHealthData(afterCallback: () -> Unit) = viewModelScope.launch {
        readSteps()
        afterCallback()
    }

    fun dumpHealthData() = viewModelScope.launch {
        deleteSteps()
        //updateSteps()
        insertSteps()
        tempHealthData.stepsRecords.clear()
        //idsToUpdate.clear()
        idsToDelete.clear()
    }

    private suspend fun readSteps() {
        try {
            val response =
                healthConnectClient!!.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = timeRange)
                    )
            healthData.stepsRecords.clear()
            for (stepRecord in response.records) {
                healthData.stepsRecords.add(StepsInfo(
                    recordId = stepRecord.metadata.id,
                    stepsNumber = stepRecord.count,
                    date = Date.from(stepRecord.startTime)
                ))
            }
        } catch (e: Exception) {
            //...
        }
    }

    private suspend fun insertSteps() {
        try {
            val recordsToInsert = tempHealthData.stepsRecords.filter { it.recordId == null }
            val response = healthConnectClient!!.insertRecords(recordsToInsert.map {
                StepsRecord(
                    count = it.stepsNumber,
                    startTime = it.date.toInstant(),
                    endTime = it.date.toInstant().plusSeconds(1),
                    startZoneOffset = ZoneOffset.UTC,
                    endZoneOffset = ZoneOffset.UTC
                )
            })
            for(idx in 0 until response.recordIdsList.size) {
                recordsToInsert[idx].recordId = response.recordIdsList[idx]
            }
        } catch (e: Exception) {
            //...
        }
    }

    private suspend fun deleteSteps() {
        try {
            healthConnectClient!!.deleteRecords(
                StepsRecord::class,
                recordIdsList = idsToDelete.toList(),
                clientRecordIdsList = emptyList()
            )
        } catch (e: Exception) {
            //...
        }
    }

//    private suspend fun updateSteps() {
//        try {
//            healthConnectClient!!.updateRecords(tempHealthData.stepsRecords.filter { idsToUpdate.contains(it.recordId) }.map {
//                StepsRecord(
//                    count = it.stepsNumber,
//                    startTime = it.date.toInstant(),
//                    endTime = it.date.toInstant().plusSeconds(1),
//                    startZoneOffset = ZoneOffset.UTC,
//                    endZoneOffset = ZoneOffset.UTC
//                )
//            })
//        } catch (e: Exception) {
//            //...
//        }
//    }
}