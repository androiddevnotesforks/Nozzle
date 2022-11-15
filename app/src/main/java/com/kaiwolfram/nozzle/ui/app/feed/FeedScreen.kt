package com.kaiwolfram.nozzle.ui.app.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.Post

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
) {
    Feed(posts = uiState.posts)
}

@Composable
private fun Feed(modifier: Modifier = Modifier, posts: List<Post>) {
    Column(modifier = modifier) {
        for (post in posts) {
            NoteCard(post = post)
        }
    }
}

@Composable
private fun NoteCard(post: Post) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Icon(
            imageVector = post.profilePic,
            contentDescription = stringResource(id = R.string.authors_profile_picture),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = post.author, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.body)
        }
    }

}
