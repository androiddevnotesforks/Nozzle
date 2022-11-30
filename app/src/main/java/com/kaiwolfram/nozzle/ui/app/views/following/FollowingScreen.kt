package com.kaiwolfram.nozzle.ui.app.views.following

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.Profile
import com.kaiwolfram.nozzle.ui.components.ProfilePicture

@Composable
fun FollowingScreen(
    uiState: FollowingViewModelState,
) {
    FollowingList(
        profiles = listOf(),
        isRefreshing = false,
    )
}

@Composable
fun FollowingList(
    profiles: List<Profile>,
    isRefreshing: Boolean,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { TODO() },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(profiles) { profile ->
                FollowingCard(
                    profile = profile,
                    onGetPicture = TODO()
                )
            }
        }
    }
}


@Composable
fun FollowingCard(
    profile: Profile,
    onGetPicture: (String) -> Painter,
) {
    val isFollowed = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.clickable { /*TODO: Open profile*/ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            profilePicture = onGetPicture(profile.picture)
        )
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = profile.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            Text(
                text = profile.bio,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        FollowToggleButton(
            pubKey = profile.pubKey,
            isFollowed = isFollowed.value,
            onFollowPubKey = { isFollowed.value = true },
            onUnfollowPubKey = { isFollowed.value = false },
        )
    }
}

@Composable
fun FollowToggleButton(
    pubKey: String,
    isFollowed: Boolean,
    onFollowPubKey: (String) -> Unit,
    onUnfollowPubKey: (String) -> Unit,
) {
    val icon = remember {
        if (isFollowed) Icons.Filled.Remove else Icons.Filled.Add
    }
    val tint = remember {
        if (isFollowed) Color.Red else Color.Green
    }
    Icon(
        modifier = Modifier.clickable {
            if (isFollowed) onUnfollowPubKey(pubKey) else onFollowPubKey(pubKey)
        },
        imageVector = icon,
        contentDescription = stringResource(id = R.string.follow_or_unfollow),
        tint = tint,
    )
}
