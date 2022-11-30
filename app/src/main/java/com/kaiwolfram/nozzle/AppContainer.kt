package com.kaiwolfram.nozzle

import android.content.Context
import coil.ImageLoader
import coil.imageLoader
import com.kaiwolfram.nozzle.data.INostrRepository
import com.kaiwolfram.nozzle.data.NostrRepositoryMock
import com.kaiwolfram.nozzle.data.PictureRequester
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences

class AppContainer(context: Context) {
    val imageLoader: ImageLoader by lazy {
        context.imageLoader
    }
    val nostrRepository: INostrRepository by lazy {
        NostrRepositoryMock()
    }
    val profilePreferences: ProfilePreferences by lazy {
        ProfilePreferences(context = context)
    }
    val pictureRequester: PictureRequester by lazy {
        PictureRequester(imageLoader = imageLoader, context = context)
    }
}
