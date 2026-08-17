package fr.leboncoin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.model.AlbumEntity

@Database(entities = [AlbumEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}
