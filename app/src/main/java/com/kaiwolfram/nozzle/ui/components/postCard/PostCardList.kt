package com.kaiwolfram.nozzle.ui.components.postCard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta

@Composable
fun PostCardList(
    posts: List<PostWithMeta>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onPrepareReply: (PostWithMeta) -> Unit,
    onLoadMore: () -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onOpenProfile: ((String) -> Unit)? = null,
) {
    SwipeRefresh(
        modifier = modifier,
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = lazyListState) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onLike = onLike,
                    onRepost = onRepost,
                    onOpenProfile = onOpenProfile,
                    onPrepareReply = onPrepareReply,
                    onNavigateToThread = onNavigateToThread,
                    onNavigateToReply = onNavigateToReply,
                )
            }
            item {
                LaunchedEffect(true) {
                    if (lazyListState.firstVisibleItemIndex > 0 && lazyListState.isScrollInProgress) {
                        onLoadMore()
                    }
                }
            }
        }
    }
}
