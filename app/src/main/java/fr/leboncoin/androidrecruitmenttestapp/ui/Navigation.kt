package fr.leboncoin.androidrecruitmenttestapp.ui

import kotlinx.serialization.Serializable

@Serializable
object AlbumsListRoute

@Serializable
data class AlbumDetailRoute(
    val albumId: Int,
    val title: String,
    val url: String
)
