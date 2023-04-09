package com.example.adminapp.ui.screens

import androidx.compose.foundation.clickable
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
import com.example.adminapp.model.Student


@Composable
private fun StudentItem(
    student: Student,
    onSelectStudent: (String) -> Unit,
    onPageButtonClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(student.f_name + " " + student.l_name) },
        supportingContent = { Text(student.adm_no) },
        modifier = Modifier.clickable {
            onSelectStudent(student.adm_no)
            onPageButtonClicked(AdminScreen.StudentAttendanceList.name)
        }
    )
}


@Preview
@Composable
private fun StudentItemPreview() {
//    StudentItem(Student("19je0572", "John", "Doe"))
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentList(
    adminUiState: AdminUiState,
    onRefresh: () -> Unit,
    onSelectStudent: (String) -> Unit,
    onPageButtonClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshing by remember { mutableStateOf(false) }

    val state = rememberPullRefreshState(refreshing, onRefresh)


    Card(modifier = modifier.padding(8.dp), elevation = CardDefaults.cardElevation()) {
        Box(Modifier.pullRefresh(state)) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (!refreshing) {
                    items(adminUiState.studentList) { student ->
                        StudentItem(student, onSelectStudent, onPageButtonClicked)
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