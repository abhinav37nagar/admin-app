package com.example.adminapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val adm_no: String,
    val f_name: String,
    val l_name: String,
    val face_data: String
)

@Serializable
data class StudentList(
    val data: List<Student>
)
