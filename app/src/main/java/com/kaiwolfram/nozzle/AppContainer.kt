package com.kaiwolfram.nozzle

import android.content.Context
import coil.ImageLoader
import coil.imageLoader
import com.kaiwolfram.nozzle.data.INostrRepository
import com.kaiwolfram.nozzle.data.NostrRepositoryMock

class AppContainer(context: Context) {
    val imageLoader: ImageLoader by lazy {
        context.imageLoader
    }
    val nostrRepository: INostrRepository by lazy {
        NostrRepositoryMock()
    }
}
