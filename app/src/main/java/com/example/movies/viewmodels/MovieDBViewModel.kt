package com.example.movies.viewmodels

import Review
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MovieDBApplication
import com.example.movies.database.MoviesRepository
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
    data class Success(val movie: Movie, val reviews: List<Review>, val videos: List<MovieVideo>) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState


}

class MovieDBViewModel(private val moviesRepository: MoviesRepository): ViewModel() {
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

    fun getMovieDetails(selectedMovie: SelectedMovieUiState) {
        viewModelScope.launch {
            when (selectedMovie) {
                is SelectedMovieUiState.Success -> {
                    val movieId =
                        (selectedMovie as? SelectedMovieUiState.Success)?.movie?.id?.toString()

                    if (movieId != null) {
                        val movie: Movie = moviesRepository.getMovieDetails(movie_id = movieId)
                        val reviews : List<Review> = moviesRepository.getMovieReviews(movie_id = movieId).results
                        val videos : List<MovieVideo> = moviesRepository.getMovieVideos(movie_id = movieId).results
                        setSelectedMovie(movie, reviews, videos)
                    } else {
                        SelectedMovieUiState.Error
                    }
                }
                is SelectedMovieUiState.Error -> { SelectedMovieUiState.Error }
                is SelectedMovieUiState.Loading -> {
                    println("[DEBUGGING] In Loading")
                    SelectedMovieUiState.Loading }
                }
        }
    }

    fun setSelectedMovie(movie: Movie, reviews: List<Review> = listOf(), videos: List<MovieVideo> = listOf()) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                SelectedMovieUiState.Success(movie=movie, reviews = reviews, videos = videos)
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
                MovieDBViewModel(moviesRepository = moviesRepository)
            }
        }
    }

}