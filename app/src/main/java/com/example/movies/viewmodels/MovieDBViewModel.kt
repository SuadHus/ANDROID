package com.example.movies.viewmodels

import Review
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MovieDBApplication
import com.example.movies.database.MoviesRepository
import com.example.movies.database.SavedMovieRepository
import com.example.movies.model.Movie
import com.example.movies.model.MovieVideo
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>) : MovieListUiState
    object Error : MovieListUiState
    object Loading : MovieListUiState
}

sealed interface SelectedMovieUiState {
    data class Success(val movie: Movie, val reviews: List<Review>, val videos: List<MovieVideo>, val isFavorite: Boolean = false) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState
}

class MovieDBViewModel(private val moviesRepository: MoviesRepository, private val savedMoviesRepository: SavedMovieRepository): ViewModel() {
    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set
    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(moviesRepository.getPopularMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(moviesRepository.getTopRankedMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun getMovieDetails(selectedMovie: Movie) {
        viewModelScope.launch {
            val movieId = selectedMovie.id.toString()

            if (movieId != null) {
                val movie: Movie = moviesRepository.getMovieDetails(movieId)
                val reviews: List<Review> = moviesRepository.getMovieReviews(movieId).results
                val videos: List<MovieVideo> = moviesRepository.getMovieVideos(movieId).results
                setSelectedMovie(movie = movie, reviews = reviews, videos = videos)
            } else {
                SelectedMovieUiState.Error
            }
        }
    }

    fun getSavedMovies() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getsSavedMovies())
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun saveFavoriteMovie(selectedState: SelectedMovieUiState.Success) {
        viewModelScope.launch {
            savedMoviesRepository.insertMovie(selectedState.movie)
            selectedMovieUiState = SelectedMovieUiState.Success(selectedState.movie, selectedState.reviews, selectedState.videos, true)
        }
    }

    fun deleteMovie(selectedState: SelectedMovieUiState.Success) {
        viewModelScope.launch {
            savedMoviesRepository.deleteMovie(selectedState.movie)
            selectedMovieUiState = SelectedMovieUiState.Success(selectedState.movie, selectedState.reviews, selectedState.videos, false)
        }
    }

    fun setSelectedMovie(movie: Movie, reviews: List<Review> = listOf(), videos: List<MovieVideo> = listOf()) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                SelectedMovieUiState.Success(movie = movie, reviews = reviews, videos = videos, savedMoviesRepository.getMovie(movie.id)?.isFavorite ?: false)
            } catch (e: IOException) {
                SelectedMovieUiState.Error
            } catch (e: HttpException) {
                SelectedMovieUiState.Error
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                val savedMovieRepository = application.container.savedMovieRepository
                MovieDBViewModel(moviesRepository = moviesRepository, savedMoviesRepository = savedMovieRepository)
            }
        }
    }

}