package com.kaiwolfram.nozzle.ui.app.views.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions

@Composable
fun NozzleDrawerScreen(
    uiState: NozzleDrawerViewModelState,
    navActions: NozzleNavActions,
    onSetPublicKey: (String) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        ProfileRow(
            profilePicture = uiState.picture,
            profileName = uiState.name,
            navigateToProfile = {
                onSetPublicKey(uiState.publicKey)
                navActions.navigateToPersonalProfile()
            },
            closeDrawer = closeDrawer
        )
        Spacer(modifier = Modifier.height(4.dp))
        MainRows(
            navigateToFeed = navActions.navigateToFeed,
            navigateToChat = navActions.navigateToChat,
            navigateToKeys = navActions.navigateToKeys,
            navigateToRelays = navActions.navigateToRelays,
            navigateToSettings = navActions.navigateToSettings,
            closeDrawer = closeDrawer
        )
        VersionText()
    }
}

@Composable
private fun ProfileRow(
    profilePicture: Painter,
    profileName: String,
    navigateToProfile: () -> Unit,
    closeDrawer: () -> Unit,
) {
    DrawerRow(
        icon = profilePicture,
        label = profileName,
        action = {
            navigateToProfile()
            closeDrawer()
        },
        iconTint = Color.Unspecified,
        iconModifier = Modifier
            .fillMaxWidth(0.20f)
            .aspectRatio(1f)
            .clip(CircleShape),
    )
}

@Composable
private fun MainRows(
    navigateToFeed: () -> Unit,
    navigateToChat: () -> Unit,
    navigateToKeys: () -> Unit,
    navigateToRelays: () -> Unit,
    navigateToSettings: () -> Unit,
    closeDrawer: () -> Unit,
) {
    DrawerRow(
        icon = rememberVectorPainter(image = Icons.Rounded.Newspaper),
        label = stringResource(id = R.string.feed),
        action = {
            navigateToFeed()
            closeDrawer()
        }
    )
    DrawerRow(
        icon = rememberVectorPainter(image = Icons.Rounded.Chat),
        label = stringResource(id = R.string.chat),
        action = {
            navigateToChat()
            closeDrawer()
        }
    )
    DrawerRow(
        icon = rememberVectorPainter(image = Icons.Rounded.Key),
        label = stringResource(id = R.string.keys),
        action = {
            navigateToKeys()
            closeDrawer()
        }
    )
    DrawerRow(
        icon = rememberVectorPainter(image = Icons.Rounded.SatelliteAlt),
        label = stringResource(id = R.string.relays),
        action = {
            navigateToRelays()
            closeDrawer()
        }
    )
    DrawerRow(
        icon = rememberVectorPainter(image = Icons.Rounded.Settings),
        label = stringResource(id = R.string.settings),
        action = {
            navigateToSettings()
            closeDrawer()
        }
    )
}

@Composable
private fun VersionText() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = stringResource(id = R.string.nozzle_version),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun DrawerRow(
    icon: Painter,
    label: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconTint: Color = colors.primary
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        color = colors.surface,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    modifier = iconModifier,
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.h6,
                    color = colors.onSurface
                )
            }
        }
    }
}
