package com.example.adminapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adminapp.data.AdminUiState
import com.example.adminapp.model.Attendance

@Composable
private fun StudentAttendanceItem(attendance: Attendance, modifier: Modifier = Modifier) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = (if (attendance.present == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer)
        ),
        headlineContent = { Text(attendance.date) },
        trailingContent = { Text(attendance.present.toString()) }
    )
}

@Preview
@Composable
private fun StudentAttendanceItemPreview() {
    StudentAttendanceItem(Attendance("19je0018", "2023-02-09T18:30:00.000Z", 1))
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentAttendanceList(
    adminUiState: AdminUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshing by remember { mutableStateOf(false) }

    val state = rememberPullRefreshState(refreshing, onRefresh)


    Card(modifier = modifier.padding(8.dp), elevation = CardDefaults.cardElevation()) {
        Box(Modifier.pullRefresh(state)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!refreshing) {
                    items(adminUiState.studentAttendanceList) { attendance ->
                        StudentAttendanceItem(attendance)
                    }
                }
            }
            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }

    }
}

//@Preview
//@Composable
//private fun StudentListPreview() {
//    StudentList(studentList = Datasource().loadStudents())
//}