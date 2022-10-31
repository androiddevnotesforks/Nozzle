package com.kaiwolfram.nozzle

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Write a post",
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
            contentDescription = "Author's profile picture",
            tint = Color.Red,
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

sealed class FooterIcons(
    val label: String,
    val imageVector: ImageVector,
    val contentDescription: String
) {
    object Profile : FooterIcons(
        label = "Profile",
        imageVector = Icons.Rounded.Person,
        contentDescription = "Navigate to your profile"
    )

    object Feed : FooterIcons(
        label = "Feed",
        imageVector = Icons.Rounded.Home,
        contentDescription = "Navigate to the global feed"
    )

    object Search : FooterIcons(
        label = "Search",
        imageVector = Icons.Rounded.Search,
        contentDescription = "Navigate to the search screen"
    )

    object Messages : FooterIcons(
        label = "Messages",
        imageVector = Icons.Rounded.Email,
        contentDescription = "Navigate to private messages"
    )
}

@Composable
fun Footer() {
    var selectedIndex by remember { mutableStateOf(0) }
    val items =
        listOf(FooterIcons.Profile, FooterIcons.Feed, FooterIcons.Search, FooterIcons.Messages)

    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(text = item.label) },
                selected = selectedIndex == index,
                onClick = { selectedIndex = index }
            )
        }
    }
}
