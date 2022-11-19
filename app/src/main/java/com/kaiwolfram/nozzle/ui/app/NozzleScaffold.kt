package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavGraph

@Composable
fun NozzleScaffold(
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
    val imageVector: ImageVector,
    var label: String = "",
    var contentDescription: String = "",
    var navTo: () -> Unit = {},
) {
    object Profile : BottomBarField(imageVector = Icons.Rounded.Person)
    object Feed : BottomBarField(imageVector = Icons.Rounded.Home)
    object Search : BottomBarField(imageVector = Icons.Rounded.Search)
    object Messages : BottomBarField(imageVector = Icons.Rounded.Email)
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
            BottomBarField.Profile.apply {
                navTo = onNavToProfile
                label = stringResource(id = R.string.profile)
                contentDescription = stringResource(id = R.string.nav_to_your_profile)
            },
            BottomBarField.Feed.apply {
                navTo = onNavToFeed
                label = stringResource(id = R.string.feed)
                contentDescription = stringResource(id = R.string.nav_to_feed)
            },
            BottomBarField.Search.apply {
                navTo = onNavToSearch
                label = stringResource(id = R.string.search)
                contentDescription = stringResource(id = R.string.nav_to_search)
            },
            BottomBarField.Messages.apply {
                navTo = onNavToMessages
                label = stringResource(id = R.string.messages)
                contentDescription = stringResource(id = R.string.nav_to_messages)
            })

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
