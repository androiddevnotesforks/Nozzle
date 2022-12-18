package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
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
    drawerState: DrawerState,
) {
    Scaffold(
        floatingActionButton = { CreateNoteButton() },
        content = { padding ->
            NozzleNavGraph(
                modifier = Modifier.padding(padding),
                vmContainer = vmContainer,
                navController = navController,
                navActions = navActions,
                drawerState = drawerState,
            )
        }
    )
}

@Composable
private fun CreateNoteButton() {
//    FloatingActionButton(
//        onClick = { },
//
//        ) {
//        Icon(
//            imageVector = Icons.Rounded.Add,
//            contentDescription = stringResource(id = R.string.write_a_post),
//        )
//    }
}
