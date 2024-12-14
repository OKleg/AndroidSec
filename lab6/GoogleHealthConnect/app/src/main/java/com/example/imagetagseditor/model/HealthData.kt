package com.example.imagetagseditor.model
import java.time.Instant
import java.util.Date
data class StepsInfo(var recordId: String?, var stepsNumber: Long, var date: Date)
class HealthData {
    val stepsRecords = emptyList<StepsInfo>().toMutableList()
}