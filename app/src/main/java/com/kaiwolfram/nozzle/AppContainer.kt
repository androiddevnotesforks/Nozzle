package com.kaiwolfram.nozzle

import android.content.Context
import coil.ImageLoader
import coil.imageLoader

class AppContainer(context: Context) {
    val imageLoader: ImageLoader = context.imageLoader
}
