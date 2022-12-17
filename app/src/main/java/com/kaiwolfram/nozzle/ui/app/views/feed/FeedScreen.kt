package com.kaiwolfram.nozzle.ui.app.views.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.components.NoPostsHint
import com.kaiwolfram.nozzle.ui.components.PostCardList
import com.kaiwolfram.nozzle.ui.components.TopBar

@Composable
fun FeedScreen(
    uiState: FeedViewModelState,
    onRefreshFeedView: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.feed))
        PostCardList(
            posts = uiState.posts,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefreshFeedView
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}
