package com.example.adminapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    val adm_no: String,
    val date: String,
    val present: Int
)

@Serializable
data class AttendanceList(
    val data: List<Attendance>
)
