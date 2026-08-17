package fr.leboncoin.domain.usecase

import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.model.Result
import fr.leboncoin.domain.repository.AlbumRepository

class GetAllAlbumsUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(): Result<List<Album>> {
        return try {
            Result.Success(repository.getAllAlbums())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
