package fr.leboncoin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.leboncoin.data.local.model.AlbumEntity

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums")
    @JvmSuppressWildcards
    suspend fun getAllAlbums(): List<AlbumEntity>

    @Query("SELECT id FROM albums WHERE isFavorite = 1")
    @JvmSuppressWildcards
    suspend fun getFavoriteAlbumIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAll(albums: List<AlbumEntity>): List<Long>

    @Query("UPDATE albums SET isFavorite = :isFavorite WHERE id = :id")
    @JvmSuppressWildcards
    suspend fun updateFavorite(id: Int, isFavorite: Boolean): Int
    
    @Query("SELECT * FROM albums WHERE id = :id")
    @JvmSuppressWildcards
    suspend fun getAlbumById(id: Int): AlbumEntity?
}
