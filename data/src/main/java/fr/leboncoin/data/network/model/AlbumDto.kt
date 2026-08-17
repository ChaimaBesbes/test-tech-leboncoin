package fr.leboncoin.data.network.model

import fr.leboncoin.domain.model.Album
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)

fun AlbumDto.toDomain() = Album(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl
)
