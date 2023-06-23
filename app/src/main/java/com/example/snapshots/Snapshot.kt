package com.example.snapshots

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Snapshot(
    @get:Exclude var id : String = "",
    var title: String = "",
    val author: String = "",
    var photoUrl: String = "",
    var likeList: Map<String, Boolean> = mutableMapOf()
)
