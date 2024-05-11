package com.example.movies.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM favorite_movies WHERE id= :id AND latest=1")
    suspend fun getLatestMovie(id: Long): Movie


    @Query("SELECT * FROM favorite_movies WHERE isFavorite=1")
    suspend fun getFavouriteMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavouriteMovie(movie: Movie)

    @Query("UPDATE favorite_movies SET isFavorite=1 WHERE id= :id")
    suspend fun favoriteMovie(id: Long)

    @Query("SELECT * FROM favorite_movies WHERE id= :id")
    suspend fun getMovie(id: Long): Movie

    @Query("UPDATE favorite_movies SET isFavorite=0 WHERE id= :id")
    suspend fun unFavoriteMovie(id: Long)

    @Query("DELETE FROM favorite_movies WHERE id= :id AND latest=0")
    suspend fun deleteFavoriteMovie(id: Long)

    @Query("UPDATE favorite_movies SET latest=0")
    suspend fun updateLatest()

    @Query("DELETE FROM favorite_movies WHERE isFavorite=0")
    suspend fun deleteOldMovies()



}