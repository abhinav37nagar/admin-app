package com.example.adminapp.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import kotlinx.serialization.descriptors.StructureKind
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
private fun AttendanceItem(attendance: Attendance, modifier: Modifier = Modifier) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = (if (attendance.present == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer)
        ),
        headlineContent = { Text(attendance.adm_no) },
        supportingContent = { Text(attendance.date) },
        trailingContent = { Text(attendance.present.toString()) }
    )
}

@Preview
@Composable
private fun AttendanceItemPreview() {
    AttendanceItem(Attendance("19je0572", "2023-02-09T18:30:00.000Z", 1))
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AttendanceList(
    adminUiState: AdminUiState,
    onRefresh: () -> Unit,
    onChangeDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshing by remember { mutableStateOf(false) }

    val state = rememberPullRefreshState(refreshing, onRefresh)

    val openDialog = remember { mutableStateOf(false) }


    Card(modifier = modifier.padding(8.dp), elevation = CardDefaults.cardElevation()) {
        Button(onClick = { openDialog.value = true }) {
            Text(
                "Change Date: "
//                        + LocalDateTime.parse(adminUiState.currentDate)
//                    .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
            )
        }
        if (openDialog.value) {

            AlertDialog(onDismissRequest = { openDialog.value = false }) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    LazyColumn() {
                        items(adminUiState.attendanceDateList) { date ->
                            ListItem(headlineContent = {
                                Text(
                                    date.DATE
//                                    LocalDateTime.parse(date.DATE)
//                                        .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
                                )
                            },
                                modifier = Modifier.clickable {
                                    openDialog.value = false
                                    onChangeDate(date.DATE)
                                })
                        }
                    }
                }
            }
        }

        Box(Modifier.pullRefresh(state)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!refreshing) {
                    items(adminUiState.attendanceList) { attendance ->
                        if (attendance.date == adminUiState.currentDate) AttendanceItem(
                            attendance
                        )
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