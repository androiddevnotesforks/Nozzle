package com.kaiwolfram.nozzle.ui.app.views.thread

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.ui.components.PostCard
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ThreadScreen(
    uiState: ThreadViewModelState,
    onRefreshThreadView: () -> Unit,
    onOpenThread: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onGoBack: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.thread), onGoBack = onGoBack)
        Column {
            ThreadedPosts(
                previous = uiState.previous,
                current = uiState.current,
                replies = uiState.replies,
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefreshThreadView,
                onOpenThread = onOpenThread,
                onNavigateToProfile = onNavigateToProfile,
            )
        }
    }
}

@Composable
private fun ThreadedPosts(
    previous: List<PostWithMeta>,
    current: PostWithMeta,
    replies: List<PostWithMeta>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onOpenThread: (String) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        val listState = LazyListState(firstVisibleItemIndex = previous.size)
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            items(previous) { post ->
                PostCard(
                    post = post,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread
                )
            }
            item {
                Row(modifier = Modifier.padding(spacing.medium)) {
                    PostCard(
                        modifier = Modifier
                            .border(
                                width = spacing.tiny,
                                color = colors.onBackground,
                                shape = shapes.small
                            ),
                        post = current,
                        onOpenProfile = onNavigateToProfile,
                    )

                }
            }
            items(replies) { post ->
                PostCard(
                    post = post,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread
                )
            }
        }
    }
}
