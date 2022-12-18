package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.PostCardList
import com.kaiwolfram.nozzle.ui.components.ProfilePictureIcon
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
    onRefreshFeedView: () -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onNavigateToThread: () -> Unit
) {
    Column {
        FeedTopBar(
            profilePicture = uiState.profilePicture,
            onPictureClick = onOpenDrawer,
        )
        PostCardList(
            posts = uiState.posts,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefreshFeedView,
            onOpenProfile = onOpenProfile,
            onNavigateToThread = onNavigateToThread,
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun FeedTopBar(profilePicture: Painter, onPictureClick: () -> Unit) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Spacer(modifier = Modifier.width(spacing.large))
                ProfilePictureIcon(
                    profilePicture = profilePicture,
                    modifier = Modifier
                        .size(sizing.smallProfilePicture)
                        .clickable { onPictureClick() },
                )
            }
            Text(
                text = stringResource(id = R.string.app_name),
                style = typography.h6,
                color = colors.background
            )
            // TODO: Replace with settings icon
            Row {
                Spacer(modifier = Modifier.size(sizing.smallProfilePicture))
                Spacer(modifier = Modifier.width(spacing.large))
            }
        }
    }
}
