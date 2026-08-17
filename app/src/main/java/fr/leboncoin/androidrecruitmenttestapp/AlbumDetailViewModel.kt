package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.usecase.GetAlbumUseCase
import fr.leboncoin.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val getAlbumUseCase: GetAlbumUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album

    fun loadAlbum(id: Int) {
        viewModelScope.launch {
            _album.value = getAlbumUseCase(id)
        }
    }

    fun toggleFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id, isFavorite)
            // Refresh local state after updating
            _album.value = getAlbumUseCase(id)
        }
    }

    class Factory(
        private val getAlbumUseCase: GetAlbumUseCase,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlbumDetailViewModel(getAlbumUseCase, toggleFavoriteUseCase) as T
        }
    }
}
