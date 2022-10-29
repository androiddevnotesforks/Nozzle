package com.kaiwolfram.nozzle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NozzleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    FeedScreen(posts = listOf(postMock))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NozzleTheme {
        FeedScreen(posts = listOf(postMock, postMock, postMock))
    }
}

data class Post(
    val author: String,
    val profilePic: ImageVector,
    val published: LocalDateTime,
    val body: String
)

@RequiresApi(Build.VERSION_CODES.O)
val postMock = Post(
    author = "Kai Wolfram",
    profilePic = Icons.Rounded.Call,
    published = LocalDateTime.now(),
    body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit."
)

@Composable
fun FeedScreen(posts: List<Post>) {
    Scaffold(
        bottomBar = { Footer() },
        floatingActionButton = { CreatePostButton() }
    ) {
        Feed(posts = posts)
    }
}

@Composable
fun CreatePostButton() {
    FloatingActionButton(
        onClick = { },
        backgroundColor = Color.LightGray
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Write a post",
            tint = Color.Red,
        )
    }
}

@Composable
fun Feed(posts: List<Post>) {
    Column {
        for (post in posts) {
            PostCard(post = post)
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Icon(
            imageVector = post.profilePic,
            contentDescription = "Author's user icon",
            tint = Color.Red,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = post.author)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.body)
        }
    }

}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Navigate to home",
                tint = Color.Red
            )
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Navigate to search",
                tint = Color.Red
            )
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Navigate to notifications",
                tint = Color.Red
            )
            Icon(
                imageVector = Icons.Rounded.Email,
                contentDescription = "Navigate to private messages",
                tint = Color.Red
            )
        }
    }
}
