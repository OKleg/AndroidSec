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
    var idsToUpdate = emptySet<String>().toMutableSet()
    var idsToDelete = emptySet<String>().toMutableSet()
    private val timeRange: Long = 7 * 86400
    fun loadHealthData(afterCallback: () -> Unit) = viewModelScope.launch {
        readSteps()
        afterCallback()
    }
    fun dumpHealthData() = viewModelScope.launch {
        deleteSteps()
        updateSteps()
        insertSteps()
        tempHealthData.stepsRecords.clear()
        idsToUpdate.clear()
        idsToDelete.clear()
    }
    private suspend fun readSteps() {
        try {
            val endInstant = Instant.now()
            val startInstant = endInstant.minusSeconds(timeRange)
            val response =
                healthConnectClient!!.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)
                    )
                )
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
            healthConnectClient!!.insertRecords(tempHealthData.stepsRecords.filter { it.recordId == null }.map {
                StepsRecord(
                    count = it.stepsNumber,
                    startTime = it.date.toInstant(),
                    endTime = it.date.toInstant().plusSeconds(1),
                    startZoneOffset = ZoneOffset.UTC,
                    endZoneOffset = ZoneOffset.UTC
                )
            })
        } catch (e: Exception) {
            //...
        }
    }
    private suspend fun updateSteps() {
        try {
            healthConnectClient!!.updateRecords(tempHealthData.stepsRecords.filter { idsToUpdate.contains(it.recordId) }.map {
                StepsRecord(
                    count = it.stepsNumber,
                    startTime = it.date.toInstant(),
                    endTime = it.date.toInstant().plusSeconds(1),
                    startZoneOffset = ZoneOffset.UTC,
                    endZoneOffset = ZoneOffset.UTC
                )
            })
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
}