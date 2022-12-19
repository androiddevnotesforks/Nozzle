package com.kaiwolfram.nozzle.ui.app.views.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun NozzleDrawerScreen(
    uiState: NozzleDrawerViewModelState,
    navActions: NozzleNavActions,
    onSetPubkey: (String) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.screenEdge)
    ) {
        ProfileRow(
            pictureUrl = uiState.pictureUrl,
            profileName = uiState.name,
            navigateToProfile = {
                onSetPubkey(uiState.pubkey)
                navActions.navigateToProfile()
            },
            closeDrawer = closeDrawer
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        MainRows(
            navigateToFeed = navActions.navigateToFeed,
            navigateToKeys = navActions.navigateToKeys,
            navigateToSettings = navActions.navigateToSettings,
            closeDrawer = closeDrawer
        )
        VersionText()
    }
}

@Composable
private fun ProfileRow(
    pictureUrl: String,
    profileName: String,
    navigateToProfile: () -> Unit,
    closeDrawer: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.tiny),
        color = colors.surface,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = {
                navigateToProfile()
                closeDrawer()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfilePicture(
                    modifier = Modifier
                        .fillMaxWidth(0.20f)
                        .aspectRatio(1f)
                        .clip(CircleShape), pictureUrl = pictureUrl
                )
                Spacer(Modifier.width(spacing.large))
                Text(
                    text = profileName,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.h6,
                    color = colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun MainRows(
    navigateToFeed: () -> Unit,
    navigateToKeys: () -> Unit,
    navigateToSettings: () -> Unit,
    closeDrawer: () -> Unit,
) {
    DrawerRow(
        imageVector = Icons.Rounded.Newspaper,
        label = stringResource(id = R.string.feed),
        action = {
            navigateToFeed()
            closeDrawer()
        }
    )
    DrawerRow(
        imageVector = Icons.Rounded.Key,
        label = stringResource(id = R.string.keys),
        action = {
            navigateToKeys()
            closeDrawer()
        }
    )
    DrawerRow(
        imageVector = Icons.Rounded.Settings,
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
    imageVector: ImageVector,
    label: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconTint: Color = colors.primary
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.tiny),
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
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = iconTint,
                )
                Spacer(Modifier.width(spacing.large))
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
