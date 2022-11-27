package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

class NozzleNavActions(navController: NavHostController) {
    val navigateToProfile: () -> Unit = {
        navController.navigate(NozzleRoute.PROFILE) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    val navigateToFeed: () -> Unit = {
        navController.navigate(NozzleRoute.FEED) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    val navigateToChat: () -> Unit = {
        navController.navigate(NozzleRoute.CHAT) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    val navigateToKeys: () -> Unit = {
        navController.navigate(NozzleRoute.KEYS) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    val navigateToRelays: () -> Unit = {
        navController.navigate(NozzleRoute.RELAYS) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    val navigateToSupport: () -> Unit = {
        navController.navigate(NozzleRoute.SUPPORT) {
            setNavOptions(navController = navController, optionsBuilder = this)
        }
    }

    private fun setNavOptions(navController: NavHostController, optionsBuilder: NavOptionsBuilder) {
        optionsBuilder.apply {
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
}
