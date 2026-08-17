package fr.leboncoin.androidrecruitmenttestapp.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.components.scaffold.Scaffold
import fr.leboncoin.androidrecruitmenttestapp.AlbumsViewModel
import fr.leboncoin.domain.model.Album
import fr.leboncoin.domain.model.Result

@SuppressLint("MaterialComposableUsageDetector")
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onItemSelected : (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val albumsResult by viewModel.albums.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.offlineEvent.collect {
            Toast.makeText(context, "You are offline, fetching local data...", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onlineEvent.collect {
            Toast.makeText(context, "Connection is back!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { viewModel.loadAlbums() }

    Scaffold(modifier = modifier) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val result = albumsResult) {
                is Result.Loading -> {
                    @Suppress("SparkMaterial3Component")
                    CircularProgressIndicator()
                }
                is Result.Error -> {
                    Text(text = "An error occurred: ${result.exception.message ?: "Unknown error"}")
                }
                is Result.Success -> {
                    AlbumsList(
                        albums = result.data,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refreshAlbums() },
                        onItemSelected = onItemSelected,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsList(
    albums: List<Album>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onItemSelected: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = albums,
                key = { album -> album.id }
            ) { album ->
                AlbumItem(
                    album = album,
                    onItemSelected = onItemSelected,
                )
            }
        }
    }
}
