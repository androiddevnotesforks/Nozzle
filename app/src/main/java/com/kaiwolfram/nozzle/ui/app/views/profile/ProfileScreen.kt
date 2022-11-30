package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.ui.components.ProfilePicture

@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    navToEditProfile: (() -> Unit)?,
    onGetPicture: (String) -> Painter,
    onRefreshProfileView: () -> Unit,
) {
    Column {
        ProfileData(
            publicKey = uiState.publicKey,
            name = uiState.name,
            bio = uiState.bio,
            picture = uiState.picture,
            navToEditProfile = navToEditProfile,
        )
        Spacer(modifier = Modifier.height(4.dp))
        FollowerNumbers(
            numOfFollowing = uiState.numOfFollowing,
            numOfFollowers = uiState.numOfFollowers,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
        Posts(
            posts = uiState.posts,
            isRefreshing = uiState.isRefreshing,
            onGetPicture = onGetPicture,
            onRefreshProfileView = onRefreshProfileView,
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun Posts(
    posts: List<Post>,
    isRefreshing: Boolean,
    onGetPicture: (String) -> Painter,
    onRefreshProfileView: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefreshProfileView,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onGetPicture = onGetPicture
                )
            }
        }
    }
}

@Composable
private fun PostCard(
    post: Post,
    onGetPicture: (String) -> Painter
) {
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            profilePicture = onGetPicture(post.profilePicUrl)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = post.author,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.content,
                maxLines = 12,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

@Composable
private fun ProfileData(
    publicKey: String,
    name: String,
    bio: String,
    picture: Painter,
    navToEditProfile: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            profilePicture = picture
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(verticalArrangement = Arrangement.Center) {
            NameAndEdit(
                name = name,
                pubKey = publicKey,
                navToEditProfile = navToEditProfile,
            )
            if (bio.isNotBlank()) {
                Text(
                    text = bio,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FollowerNumbers(
    numOfFollowing: UInt,
    numOfFollowers: UInt,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Row {
            Text(
                text = numOfFollowing.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.following)
            )

        }
        Spacer(modifier = Modifier.width(8.dp))
        Row {
            Text(
                text = numOfFollowers.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.followers)
            )

        }
    }
}

@Composable
private fun NameAndEdit(
    name: String,
    pubKey: String,
    navToEditProfile: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .padding(end = 4.dp)
                .weight(weight = 3.3f)
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            Text(
                text = "${pubKey.substring(0, 15)}...",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.LightGray,
                style = MaterialTheme.typography.body2,
            )
        }
        if (navToEditProfile != null) {
            OutlinedButton(
                modifier = Modifier.weight(weight = 1f, fill = false),
                onClick = navToEditProfile,
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface)
            ) {
                Text(
                    text = stringResource(id = R.string.edit),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NoPostsHint() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.1f),
            contentDescription = null,
            imageVector = Icons.Rounded.Search,
            tint = Color.LightGray
        )
        Text(
            text = stringResource(id = R.string.no_posts_found),
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
    }
}
