package com.mselrod.billtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mselrod.billtracker.data.database.AppDatabase
import com.mselrod.billtracker.data.repository.BillRepository
import com.mselrod.billtracker.ui.navigation.BottomNavItem
import com.mselrod.billtracker.ui.screens.BillsScreen
import com.mselrod.billtracker.ui.screens.MainScreen
import com.mselrod.billtracker.ui.screens.PayDaysScreen
import com.mselrod.billtracker.ui.screens.SettingsScreen
import com.mselrod.billtracker.ui.theme.BillTrackerTheme
import com.mselrod.billtracker.ui.theme.TerracottaOrange
import com.mselrod.billtracker.viewmodel.BillViewModel
import com.mselrod.billtracker.viewmodel.BillViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: BillViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = BillRepository(
            billDao = database.billDao(),
            payDayDao = database.payDayDao()
        )
        BillViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BillTrackerTheme {
                BillTrackerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BillTrackerApp(viewModel: BillViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in BottomNavItem.items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(BottomNavItem.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TerracottaOrange,
                                selectedTextColor = TerracottaOrange,
                                indicatorColor = TerracottaOrange.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                MainScreen(
                    viewModel = viewModel
                )
            }

            composable(BottomNavItem.Bills.route) {
                BillsScreen(
                    viewModel = viewModel
                )
            }

            composable(BottomNavItem.PayDays.route) {
                PayDaysScreen(
                    viewModel = viewModel
                )
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}