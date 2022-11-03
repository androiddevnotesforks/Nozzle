package com.kaiwolfram.nozzle.ui.app

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object NozzleRoutes {
    const val PROFILE = "profile"
    const val FEED = "feed"
    const val SEARCH = "search"
    const val MESSAGES = "messages"
}

class NozzleNavActions(navController: NavHostController) {
    val navigateToProfile: () -> Unit = {
        navController.navigate(NozzleRoutes.PROFILE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    val navigateToFeed: () -> Unit = {
        navController.navigate(NozzleRoutes.FEED) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSearch: () -> Unit = {
        navController.navigate(NozzleRoutes.SEARCH) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToMessages: () -> Unit = {
        navController.navigate(NozzleRoutes.MESSAGES) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
