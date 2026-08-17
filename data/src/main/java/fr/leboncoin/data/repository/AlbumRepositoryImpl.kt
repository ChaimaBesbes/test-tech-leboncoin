package fr.leboncoin.data.repository

import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.model.toDomain
import fr.leboncoin.data.local.model.toEntity
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.utils.NetworkMonitor
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepository

class AlbumRepositoryImpl(
    private val albumApiService: AlbumApiService,
    private val albumDao: AlbumDao,
    private val networkMonitor: NetworkMonitor
) : AlbumRepository {

    override suspend fun getAllAlbums(): List<Album> {
        return if (networkMonitor.isOnline()) {
            val remoteAlbums = albumApiService.getAlbums()
            
            val favoriteIds = albumDao.getFavoriteAlbumIds().toSet()
            
            val albumEntities = remoteAlbums.map {
                it.toEntity().copy(isFavorite = favoriteIds.contains(it.id)) 
            }
            albumDao.insertAll(albumEntities)
            
            albumEntities.map { it.toDomain() }
        } else {
            albumDao.getAllAlbums().map { it.toDomain() }
        }
    }

    override suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        albumDao.updateFavorite(id, isFavorite)
    }

    override suspend fun getAlbum(id: Int): Album? {
        return albumDao.getAlbumById(id)?.toDomain()
    }
}
