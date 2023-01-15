package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

class NozzleNavActions(private val navController: NavHostController) {
    val navigateToProfile: () -> Unit = {
        navController.navigate(NozzleRoute.PROFILE) {
            setNavOptionsWithPop(optionsBuilder = this)
        }
    }

    val navigateToFeed: () -> Unit = {
        navController.navigate(NozzleRoute.FEED) {
            setNavOptionsWithPop(optionsBuilder = this)
        }
    }

    val navigateToSearch: () -> Unit = {
        navController.navigate(NozzleRoute.SEARCH) {
            setNavOptionsWithPop(optionsBuilder = this)
        }
    }

    val navigateToKeys: () -> Unit = {
        navController.navigate(NozzleRoute.KEYS) {
            setNavOptionsWithPop(optionsBuilder = this)
        }
    }

    val navigateToEditProfile: () -> Unit = {
        navController.navigate(NozzleRoute.EDIT_PROFILE) {
            setSimpleNavOptions(optionsBuilder = this)
        }
    }

    val navigateToThread: () -> Unit = {
        navController.navigate(NozzleRoute.THREAD) {
            setSimpleNavOptions(optionsBuilder = this)
        }
    }

    val navigateToReply: () -> Unit = {
        navController.navigate(NozzleRoute.REPLY) {
            setSimpleNavOptions(optionsBuilder = this)
        }
    }

    val navigateToPost: () -> Unit = {
        navController.navigate(NozzleRoute.POST) {
            setSimpleNavOptions(optionsBuilder = this)
        }
    }

    val popStack: () -> Unit = {
        navController.popBackStack()
    }

    private fun setNavOptionsWithPop(optionsBuilder: NavOptionsBuilder) {
        optionsBuilder.apply {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            setSimpleNavOptions(this)
        }
    }

    private fun setSimpleNavOptions(optionsBuilder: NavOptionsBuilder) {
        optionsBuilder.apply {
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
        }
    }
}
