package com.example.adminapp.data

import com.example.adminapp.model.Student

class Datasource {
    fun loadStudents(): List<Student> {
        return listOf<Student>(
            Student("19je0011", "John", "Doe"),
            Student("19je0012", "John", "Doe"),
            Student("19je0013", "John", "Doe"),
            Student("19je0014", "John", "Doe"),
            Student("19je0015", "John", "Doe"),
            Student("19je0016", "John", "Doe"),
            Student("19je0017", "John", "Doe"),
            Student("19je0018", "John", "Doe"),
            Student("19je0019", "John", "Doe"),
            Student("19je0020", "John", "Doe"),
        )
    }
}