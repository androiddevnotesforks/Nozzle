package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavGraph

@Composable
fun NozzleScaffold(
    vmContainer: VMContainer,
    navController: NavHostController,
    navActions: NozzleNavActions,
) {
    Scaffold(
        floatingActionButton = { CreateNoteButton() },
        content = { padding ->
            NozzleNavGraph(
                modifier = Modifier.padding(padding),
                vmContainer = vmContainer,
                navController = navController,
                navActions = navActions,
            )
        }
    )
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
