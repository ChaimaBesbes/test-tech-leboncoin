package fr.leboncoin.domain.repository

import fr.leboncoin.domain.model.Album

interface AlbumRepository {
    suspend fun getAllAlbums(): List<Album>
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)
    suspend fun getAlbum(id: Int): Album?
}
