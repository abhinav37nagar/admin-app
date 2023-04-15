package com.example.adminapp.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.adminapp.R
import com.example.adminapp.ui.AdminViewModel

enum class AdminScreen(@StringRes val title: Int) {
    AdminDashboard(title = R.string.admin_dashboard),
    StudentList(title = R.string.student_list),
    AttendanceList(title = R.string.attendance_list),
    StudentAttendanceList(title = R.string.attendance_list_for),
    StudentRegistration(title = R.string.register_student)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentScreen: AdminScreen,
    modifier: Modifier = Modifier,
    customTitle: String = ""
) {
    TopAppBar(
        title = {
            Text(
                stringResource(currentScreen.title)
                        + (if (currentScreen.title != R.string.attendance_list_for) "" else customTitle)
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun AdminApp(
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AdminScreen.valueOf(
        backStackEntry?.destination?.route ?: AdminScreen.AdminDashboard.name
    )
    val uiState by viewModel.uiState.collectAsState()
    var appTitle by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            AdminAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                customTitle = appTitle,
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminScreen.AdminDashboard.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = AdminScreen.AdminDashboard.name) {
                AdminDashboard(
                    onPageButtonClicked = { navController.navigate(it) }
                )
            }
            composable(route = AdminScreen.StudentList.name) {
                StudentList(
                    adminUiState = uiState,
                    onRefresh = { viewModel.getStudentList() },
                    onSelectStudent = { adm_no ->
                        viewModel.getStudentAttendanceList(adm_no)
                        appTitle = adm_no
                    },
                    onPageButtonClicked = { navController.navigate(it) }

                )
            }
            composable(route = AdminScreen.AttendanceList.name) {
                AttendanceList(
                    adminUiState = uiState,
                    onRefresh = { viewModel.getAttendanceList() },
                    onChangeDate = { date -> viewModel.setDate(date) }
                )
            }
            composable(route = AdminScreen.StudentAttendanceList.name) {
                StudentAttendanceList(
                    adminUiState = uiState,
                    onRefresh = { viewModel.refreshStudentAttendanceList() })
            }
        }
    }
}