package com.kaiwolfram.nozzle.ui.app.views.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.components.ProfilePicture
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun NozzleDrawerScreen(
    pubkeyState: DrawerViewModelState,
    metadataState: Metadata?,
    navActions: NozzleNavActions,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = spacing.screenEdge)
    ) {
        ProfileRow(
            modifier = Modifier.padding(horizontal = spacing.medium),
            picture = metadataState?.picture.orEmpty(),
            pubkey = pubkeyState.pubkey,
            npub = pubkeyState.npub,
            profileName = metadataState?.name.orEmpty(),
            navigateToProfile = navActions.navigateToProfile,
            closeDrawer = closeDrawer
        )
        Spacer(
            modifier = Modifier
                .height(spacing.medium)
                .padding(horizontal = spacing.screenEdge)
        )
        MainRows(
            modifier = Modifier.padding(spacing.screenEdge),
            navigateToFeed = navActions.navigateToFeed,
            navigateToSearch = navActions.navigateToSearch,
            navigateToKeys = navActions.navigateToKeys,
            closeDrawer = closeDrawer
        )
        VersionText()
    }
}

@Composable
private fun ProfileRow(
    picture: String,
    pubkey: String,
    npub: String,
    profileName: String,
    navigateToProfile: (String) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.tiny),
        color = colors.surface,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = {
                navigateToProfile(pubkey)
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
                        .clip(CircleShape),
                    pictureUrl = picture,
                    pubkey = pubkey,
                )
                Spacer(Modifier.width(spacing.large))
                Text(
                    text = profileName.ifEmpty { npub },
                    maxLines = 2,
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
    navigateToSearch: () -> Unit,
    navigateToKeys: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        DrawerRow(
            imageVector = Icons.Rounded.Newspaper,
            label = stringResource(id = R.string.feed),
            action = {
                navigateToFeed()
                closeDrawer()
            }
        )
        DrawerRow(
            imageVector = Icons.Rounded.Search,
            label = stringResource(id = R.string.search),
            action = {
                navigateToSearch()
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
    }
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
