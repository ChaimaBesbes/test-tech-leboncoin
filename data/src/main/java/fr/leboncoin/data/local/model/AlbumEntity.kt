package fr.leboncoin.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.leboncoin.domain.model.Album
import fr.leboncoin.data.network.model.AlbumDto

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val isFavorite: Boolean = false
)

fun AlbumEntity.toDomain() = Album(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite
)

fun AlbumDto.toEntity() = AlbumEntity(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = false
)
