package fr.leboncoin.domain.usecase

import fr.leboncoin.domain.repository.AlbumRepository

class ToggleFavoriteUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(id: Int, isFavorite: Boolean) {
        repository.updateFavorite(id, isFavorite)
    }
}
