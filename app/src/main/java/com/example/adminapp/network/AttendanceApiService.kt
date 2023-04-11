package com.example.adminapp.network

import com.example.adminapp.model.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

//private const val BASE_URL = "http://172.16.164.68:5000"
//private const val BASE_URL = "http://172.30.176.1:5000"
//private const val BASE_URL = "  http://192.168.43.111:5000"
private const val BASE_URL = "  http://192.168.137.1:5000"

@OptIn(ExperimentalSerializationApi::class)
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
    .baseUrl(BASE_URL)
    .build()

interface AttendanceApiService {
    @GET("students")
    suspend fun getStudents(): StudentList

    @GET("attendance")
    suspend fun getAttendance(): AttendanceList

    @GET("attendance/student")
    suspend fun getStudentAttendance(@Query("adm_no") adm_no: String): AttendanceList

    @GET("attendance/dates")
    suspend fun getAttendanceDates(): DateList
}

object AttendanceApi {
    val retrofitService: AttendanceApiService by lazy {
        retrofit.create(AttendanceApiService::class.java)
    }
}