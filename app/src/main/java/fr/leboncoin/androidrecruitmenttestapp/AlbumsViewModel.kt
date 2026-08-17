package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.leboncoin.data.utils.NetworkMonitor
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.model.Result
import fr.leboncoin.domain.usecase.GetAllAlbumsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _albums = MutableStateFlow<Result<List<Album>>>(Result.Loading)
    val albums: StateFlow<Result<List<Album>>> = _albums

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _offlineEvent = MutableSharedFlow<Unit>()
    val offlineEvent: SharedFlow<Unit> = _offlineEvent

    private val _onlineEvent = MutableSharedFlow<Unit>()
    val onlineEvent: SharedFlow<Unit> = _onlineEvent

    private var wasOffline = false

    private suspend fun handleNetworkState() {
        if (!networkMonitor.isOnline()) {
            wasOffline = true
            _offlineEvent.emit(Unit)
        } else if (wasOffline) {
            wasOffline = false
            _onlineEvent.emit(Unit)
        }
    }

    fun loadAlbums() {
        if (_albums.value is Result.Success) return
        
        viewModelScope.launch {
            handleNetworkState()
            
            _albums.value = Result.Loading
            _albums.value = getAllAlbumsUseCase()
        }
    }

    fun refreshAlbums() {
        viewModelScope.launch {
            _isRefreshing.value = true
            
            handleNetworkState()
            
            _albums.value = getAllAlbumsUseCase()
            _isRefreshing.value = false
        }
    }

    class Factory(
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlbumsViewModel(getAllAlbumsUseCase, networkMonitor) as T
        }
    }
}
