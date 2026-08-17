package fr.leboncoin.androidrecruitmenttestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.adevinta.spark.SparkTheme
import fr.leboncoin.androidrecruitmenttestapp.di.AppDependenciesProvider
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumDetailRoute
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumDetailScreen
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumsListRoute
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumsScreen
import fr.leboncoin.androidrecruitmenttestapp.utils.AnalyticsHelper
import fr.leboncoin.domain.usecase.GetAlbumUseCase
import fr.leboncoin.domain.usecase.GetAllAlbumsUseCase
import fr.leboncoin.domain.usecase.ToggleFavoriteUseCase

class MainActivity : ComponentActivity() {

    private val viewModel: AlbumsViewModel by lazy {
        val dependencies = (application as AppDependenciesProvider).dependencies
        val getAllAlbumsUseCase = GetAllAlbumsUseCase(dependencies.dataDependencies.albumsRepository)
        val networkMonitor = dependencies.dataDependencies.networkMonitor
        val factory = AlbumsViewModel.Factory(getAllAlbumsUseCase, networkMonitor)
        ViewModelProvider(this, factory)[AlbumsViewModel::class.java]
    }

    private val detailViewModelFactory: AlbumDetailViewModel.Factory by lazy {
        val dependencies = (application as AppDependenciesProvider).dependencies
        val repository = dependencies.dataDependencies.albumsRepository
        val getAlbumUseCase = GetAlbumUseCase(repository)
        val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
        AlbumDetailViewModel.Factory(getAlbumUseCase, toggleFavoriteUseCase)
    }

    private val analyticsHelper: AnalyticsHelper by lazy {
        val dependencies = (application as AppDependenciesProvider).dependencies
        dependencies.analyticsHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        analyticsHelper.initialize(this)

        setContent {
            SparkTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = AlbumsListRoute
                ) {
                    composable<AlbumsListRoute> {
                        AlbumsScreen(
                            viewModel = viewModel,
                            onItemSelected = { album ->
                                analyticsHelper.trackSelection(album.id.toString())
                                navController.navigate(
                                    AlbumDetailRoute(
                                        albumId = album.id,
                                        title = album.title,
                                        url = album.url
                                    )
                                )
                            }
                        )
                    }
                    
                    composable<AlbumDetailRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<AlbumDetailRoute>()
                        analyticsHelper.trackScreenView("Details - ${route.albumId}")
                        
                        val detailViewModel = ViewModelProvider(this@MainActivity, detailViewModelFactory)[route.albumId.toString(), AlbumDetailViewModel::class.java]
                        
                        AlbumDetailScreen(
                            albumId = route.albumId,
                            title = route.title,
                            url = route.url,
                            viewModel = detailViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
