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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.Post

@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    navToFollowing: () -> Unit,
    navToFollowers: () -> Unit,
    navToEditProfile: () -> Unit,
    onGetPicture: (String) -> Painter,
) {
    Column {
        ProfileData(
            profilePicture = uiState.profilePicture,
            name = uiState.name,
            shortenedPubKey = uiState.shortenedPubKey,
            bio = uiState.bio,
            navToEditProfile = navToEditProfile,
        )
        Spacer(modifier = Modifier.height(4.dp))
        FollowerNumbers(
            numOfFollowing = uiState.numOfFollowing,
            numOfFollowers = uiState.numOfFollowers,
            navToFollowing = navToFollowing,
            navToFollowers = navToFollowers
        )
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
        Posts(posts = uiState.posts, onGetPicture = onGetPicture)
    }
}

@Composable
private fun Posts(
    posts: List<Post>,
    onGetPicture: (String) -> Painter
) {
    LazyColumn {
        items(posts) { post ->
            PostCard(post = post, onGetPicture = onGetPicture)
        }
    }
}

@Composable
private fun PostCard(post: Post, onGetPicture: (String) -> Painter) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Icon(
            painter = onGetPicture(post.profilePicUrl),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = post.author, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content)
        }
    }

}

@Composable
private fun ProfileData(
    profilePicture: Painter,
    name: String,
    shortenedPubKey: String,
    bio: String,
    navToEditProfile: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(6.dp))
        ProfilePicture(
            modifier = Modifier.padding(6.dp),
            profilePicture = profilePicture
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            NameAndEdit(
                name = name,
                shortenedPubKey = shortenedPubKey,
                navToEditProfile = navToEditProfile,
            )
            Text(
                text = bio,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FollowerNumbers(
    numOfFollowing: UInt,
    numOfFollowers: UInt,
    navToFollowing: () -> Unit,
    navToFollowers: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp),
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Row(modifier = Modifier.clickable { navToFollowing() }) {
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = numOfFollowing.toString(),
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = stringResource(id = R.string.following)
            )

        }
        Spacer(modifier = Modifier.width(6.dp))
        Row(modifier = Modifier.clickable { navToFollowers() }) {
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = numOfFollowers.toString(),
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = stringResource(id = R.string.followers)
            )

        }
    }
}

@Composable
private fun NameAndEdit(
    name: String,
    shortenedPubKey: String,
    navToEditProfile: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            Text(
                text = shortenedPubKey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray.copy(alpha = 0.8f),
                style = MaterialTheme.typography.body2,
            )
        }
        OutlinedButton(
            onClick = navToEditProfile,
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(id = R.string.nav_to_edit_profile)
            )
            Text(
                text = stringResource(id = R.string.edit),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfilePicture(
    profilePicture: Painter,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = profilePicture,
        contentDescription = stringResource(id = R.string.profile_picture),
        tint = Color.Unspecified,
        modifier = modifier
            .fillMaxWidth(0.20f)
            .aspectRatio(1f)
            .clip(CircleShape)
    )
}
