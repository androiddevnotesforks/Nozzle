package com.kaiwolfram.nozzle.data.provider

import com.kaiwolfram.nozzle.model.NameAndPicture

interface IProfileProvider {
    fun getNamesAndPicturesMap(pubkeys: List<String>): Map<String, NameAndPicture>
}
