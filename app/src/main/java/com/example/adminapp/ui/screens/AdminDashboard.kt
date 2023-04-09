package com.example.adminapp.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adminapp.R

@Composable
fun AdminDashboard(
    onPageButtonClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.admin_dashboard),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        SelectPageButton(
            labelResourceId = R.string.student_list,
            onClick = { onPageButtonClicked(AdminScreen.StudentList.name) }
        )
        SelectPageButton(
            labelResourceId = R.string.attendance_list,
            onClick = { onPageButtonClicked(AdminScreen.AttendanceList.name) }
        )
    }
}

@Composable
private fun SelectPageButton(
    @StringRes labelResourceId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(min = 250.dp)
    ) {
        Text(stringResource(labelResourceId))
    }
}

//@Preview
//@Composable
//private fun AdminDashboardPreview() {
//    AdminDashboard()
//}