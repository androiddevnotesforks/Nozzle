package com.kaiwolfram.nozzle.ui.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R

@Composable
fun NozzleDrawer(
    profilePicture: Painter,
    profileName: String,
    navigateToProfile: () -> Unit,
    navigateToFeed: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToMessages: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabRowDefaults.Divider(color = colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = profilePicture,
            label = profileName,
            action = {
                navigateToProfile()
                closeDrawer()
            },
            tint = Color.Unspecified,
            iconModifier = Modifier
                .fillMaxWidth(0.20f)
                .aspectRatio(1f)
                .clip(CircleShape),
        )
        DrawerButton(
            icon = rememberVectorPainter(image = Icons.Filled.Home),
            label = stringResource(id = R.string.feed),
            action = {
                navigateToFeed()
                closeDrawer()
            }
        )
        DrawerButton(
            icon = rememberVectorPainter(image = Icons.Filled.Search),
            label = stringResource(id = R.string.search),
            action = {
                navigateToSearch()
                closeDrawer()
            }
        )
        DrawerButton(
            icon = rememberVectorPainter(image = Icons.Filled.Email),
            label = stringResource(id = R.string.messages),
            action = {
                navigateToMessages()
                closeDrawer()
            }
        )
    }
}

@Composable
private fun DrawerButton(
    icon: Painter,
    label: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    tint: Color = colors.primary
) {
    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
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
                    painter = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = iconModifier,
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.h5,
                    color = tint
                )
            }
        }
    }
}
