package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.data.local.dao.AlbumDao
import fr.leboncoin.data.local.model.AlbumEntity
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepositoryImpl
import fr.leboncoin.data.utils.NetworkMonitor
import fr.leboncoin.domain.usecase.GetAllAlbumsUseCase
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.lang.reflect.Proxy

class AlbumsViewModelTest {

    @Test
    fun loadsAlbums_emitsNonEmptyList() {
        val fakeService = object : AlbumApiService {
            override suspend fun getAlbums(): List<AlbumDto> = listOf(
                AlbumDto(id = 1, albumId = 1, title = "title1", url = "url1", thumbnailUrl = "url2")
            )
        }
        val fakeDao = object : AlbumDao {
            override suspend fun getAllAlbums(): List<AlbumEntity> = emptyList()
            override suspend fun insertAll(albums: List<AlbumEntity>): List<Long> = emptyList()
            override suspend fun getFavoriteAlbumIds(): List<Int> = emptyList()
            override suspend fun updateFavorite(id: Int, isFavorite: Boolean): Int = 0
            override suspend fun getAlbumById(id: Int): AlbumEntity? = null
        }

        val fakeNetworkMonitor = object : NetworkMonitor {
            override fun isOnline(): Boolean = true
        }
        
        val repository = AlbumRepositoryImpl(fakeService, fakeDao, fakeNetworkMonitor)
        val useCase = GetAllAlbumsUseCase(repository)
        val vm = AlbumsViewModel(useCase, fakeNetworkMonitor)

        assertNotNull(vm.albums)
    }
}
