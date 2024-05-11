package com.example.movies.database

import com.example.movies.model.Movie
import com.example.movies.model.MovieListResponse
import com.example.movies.model.MovieVideoListResponse
import com.example.movies.model.ReviewListResponse
import com.example.movies.network.MovieDBApiService

interface MoviesRepository {
    suspend fun getPopularMovies(): MovieListResponse
    suspend fun getTopRankedMovies(): MovieListResponse
    suspend fun getMovieDetails(movie_id: String): Movie

    suspend fun getMovieReviews(movie_id: String): ReviewListResponse

    suspend fun  getMovieVideos(movie_id: String): MovieVideoListResponse
}

class NetworkMoviesRepository(private val apiService: MovieDBApiService): MoviesRepository{
    override suspend fun getPopularMovies(): MovieListResponse {
        return apiService.getPopularMovies()
    }




    override suspend fun getTopRankedMovies(): MovieListResponse {
        return apiService.getTopRatedMovies()
    }
    override suspend fun getMovieDetails(movie_id: String): Movie{
        return apiService.getMovieDetails(movie_id)
    }


    override suspend fun getMovieReviews(movie_id: String): ReviewListResponse {
        return apiService.getMovieReviews(movie_id)
    }

    override suspend fun getMovieVideos(movie_id: String): MovieVideoListResponse {
        return apiService.getMovieVideos(movie_id)
    }
}


interface SavedMovieRepository{
    suspend fun getsSavedMovies(): List<Movie>

    suspend fun insertMovie(movie: Movie)

    suspend fun getMovie(id: Long): Movie
    suspend fun deleteMovie(movie: Movie)

    suspend fun deleteOldMovies()


}

class FavouriteMoviesRepository(private val movieDao: MovieDao): SavedMovieRepository{
    override suspend fun getsSavedMovies(): List<Movie> {
        return movieDao.getFavouriteMovies()
    }

    override suspend fun insertMovie(movie: Movie) {
        movie.isFavorite= true
        //Check if it already exists
        val existingMovie = movieDao.getMovie(movie.id)
        if(existingMovie == null){
            movieDao.insertFavouriteMovie(movie)
        }else{
            movieDao.favoriteMovie(movie.id)
        }
    }

    override suspend fun getMovie(id: Long): Movie {
        return movieDao.getMovie(id)
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDao.unFavoriteMovie(movie.id)
        movieDao.deleteFavoriteMovie(movie.id)
    }

    override suspend fun deleteOldMovies() {
        movieDao.updateLatest() //maybe haram
        movieDao.deleteOldMovies()
    }



}