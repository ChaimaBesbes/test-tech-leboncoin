package fr.leboncoin.data.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import fr.leboncoin.data.BuildConfig
import fr.leboncoin.data.local.AppDatabase
import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.repository.AlbumRepositoryImpl
import fr.leboncoin.data.utils.NetworkMonitor
import fr.leboncoin.data.utils.NetworkMonitorImpl
import fr.leboncoin.domain.repository.AlbumRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create

class DataDependencies(private val context: Context) {

    val albumsRepository: AlbumRepository by lazy { 
        AlbumRepositoryImpl(apiService, albumDao, networkMonitor) 
    }

    val networkMonitor: NetworkMonitor by lazy { NetworkMonitorImpl(context) }

    private val albumDao: AlbumDao by lazy { database.albumDao() }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "albums-database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val apiService: AlbumApiService by lazy { retrofit.create<AlbumApiService>() }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(AlbumApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        builder.build()
    }

    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
}
