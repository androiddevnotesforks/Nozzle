package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

@Composable
internal fun NozzleScaffold(
    vmContainer: VMContainer,
    navActions: NozzleNavActions,
    navController: NavHostController,
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                onNavToProfile = navActions.navigateToProfile,
                onNavToFeed = navActions.navigateToFeed,
                onNavToSearch = navActions.navigateToSearch,
                onNavToMessages = navActions.navigateToMessages,
            )
        },
        floatingActionButton = { CreateNoteButton() },
        content = { padding ->
            NozzleNavGraph(
                modifier = Modifier.padding(padding),
                navController = navController,
                vmContainer = vmContainer,
            )
        }
    )
}

private sealed class BottomBarField(
    val label: String,
    val imageVector: ImageVector,
    val contentDescription: String,
    var navTo: () -> Unit = {},
) {
    object Profile : BottomBarField(
        label = "Profile",
        imageVector = Icons.Rounded.Person,
        contentDescription = "Navigate to your profile",
    )

    object Feed : BottomBarField(
        label = "Feed",
        imageVector = Icons.Rounded.Home,
        contentDescription = "Navigate to the global feed"
    )

    object Search : BottomBarField(
        label = "Search",
        imageVector = Icons.Rounded.Search,
        contentDescription = "Navigate to the search screen"
    )

    object Messages : BottomBarField(
        label = "Messages",
        imageVector = Icons.Rounded.Email,
        contentDescription = "Navigate to private messages"
    )
}

@Composable
private fun BottomBar(
    onNavToProfile: () -> Unit,
    onNavToFeed: () -> Unit,
    onNavToSearch: () -> Unit,
    onNavToMessages: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items =
        listOf(
            BottomBarField.Profile.apply { navTo = onNavToProfile },
            BottomBarField.Feed.apply { navTo = onNavToFeed },
            BottomBarField.Search.apply { navTo = onNavToSearch },
            BottomBarField.Messages.apply { navTo = onNavToMessages })

    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(text = item.label) },
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    item.navTo()
                }
            )
        }
    }
}

@Composable
private fun CreateNoteButton() {
    FloatingActionButton(
        onClick = { },
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Write a note",
        )
    }
}
