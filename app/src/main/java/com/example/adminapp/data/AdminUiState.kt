package com.example.adminapp.data

import com.example.adminapp.model.Attendance
import com.example.adminapp.model.DateItem
import com.example.adminapp.model.Student

data class AdminUiState(
    val currentDate: String = "",
    val studentList: List<Student> = listOf(),
    val attendanceList: List<Attendance> = listOf(),
    val studentAttendanceList: List<Attendance> = listOf(),
    val attendanceDateList: List<DateItem> = listOf(),
    val currentStudent: String = ""
)
