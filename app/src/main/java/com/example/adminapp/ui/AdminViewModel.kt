package com.example.adminapp.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminapp.data.AdminUiState
import com.example.adminapp.network.AttendanceApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        getStudentList()
        getAttendanceList()
//        getStudentAttendanceList()
        getAttendanceDateList()
    }

    fun getStudentList() {
        Log.d("getStudentList()", "Run")
        viewModelScope.launch {
            try {
                val listResult = AttendanceApi.retrofitService.getStudents()
                _uiState.update { currentState ->
                    currentState.copy(
                        studentList = listResult.data,
                    )
                }
            } catch (e: IOException) {
                Log.e("getStudentList()", e.toString())
            } catch (e: HttpException) {
                Log.e("getStudentList()", e.toString())
            }
        }
    }

    fun refreshStudentAttendanceList() {
        Log.d("getStudentAttendanceList()", "Run")
        viewModelScope.launch {
            try {
                val listResult =
                    AttendanceApi.retrofitService.getStudentAttendance(_uiState.value.currentStudent)
                _uiState.update { currentState ->
                    currentState.copy(
                        studentAttendanceList = listResult.data,
                    )
                }
            } catch (e: IOException) {
                Log.e("getStudentAttendanceList()", e.toString())
            } catch (e: HttpException) {
                Log.e("getStudentAttendanceList()", e.toString())
            }
        }
    }

    fun getStudentAttendanceList(adm_no: String) {
        Log.d("getStudentAttendanceList()", "Run")
        viewModelScope.launch {
            try {
                val listResult =
                    AttendanceApi.retrofitService.getStudentAttendance(adm_no)
                _uiState.update { currentState ->
                    currentState.copy(
                        studentAttendanceList = listResult.data,
                    )
                }
            } catch (e: IOException) {
                Log.e("getStudentAttendanceList()", e.toString())
            } catch (e: HttpException) {
                Log.e("getStudentAttendanceList()", e.toString())
            }
        }
    }

    fun getAttendanceList() {
        Log.d("getAttendanceList()", "Run")
        viewModelScope.launch {
            try {
                val listResult = AttendanceApi.retrofitService.getAttendance()
                _uiState.update { currentState ->
                    currentState.copy(
                        attendanceList = listResult.data,
                    )
                }
            } catch (e: IOException) {
                Log.e("getAttendanceList()", e.toString())
            } catch (e: HttpException) {
                Log.e("getAttendanceList()", e.toString())
            }
        }
    }

    fun getAttendanceDateList() {
        Log.d("getAttendanceDateList()", "Run")
        viewModelScope.launch {
            try {
                val listResult = AttendanceApi.retrofitService.getAttendanceDates()
                _uiState.update { currentState ->
                    currentState.copy(
                        attendanceDateList = listResult.data,
                        currentDate = listResult.data[0].DATE
                    )
                }
            } catch (e: IOException) {
                Log.e("getAttendanceDateList()", e.toString())
            } catch (e: HttpException) {
                Log.e("getAttendanceDateList()", e.toString())
            }
        }
    }

    fun setDate(date: String) {
        _uiState.update { currentState ->
            currentState.copy(
                currentDate = date,
            )
        }
    }

    fun setImage(bmp: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                currentImage = bmp,
            )
        }
    }
}