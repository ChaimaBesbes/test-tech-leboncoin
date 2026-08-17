package fr.leboncoin.domain.usecase

import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.repository.AlbumRepository

class GetAlbumUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(id: Int): Album? {
        return repository.getAlbum(id)
    }
}
