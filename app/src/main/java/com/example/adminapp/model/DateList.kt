package com.example.adminapp.model

import kotlinx.serialization.Serializable

@Serializable
data class DateItem(
    val DATE: String
)

@Serializable
data class DateList(
    val data: List<DateItem>
)
