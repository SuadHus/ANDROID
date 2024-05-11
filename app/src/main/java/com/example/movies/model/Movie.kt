package com.example.movies.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Entity(tableName = "favorite_movies")
@TypeConverters(Movie.Converters::class)  // Specify that we use Converters class
@Serializable
data class Movie(
    @PrimaryKey
    @SerialName(value = "id")
    var id: Long = 0L,
    @SerialName(value = "title")
    var title: String,
    @SerialName(value = "poster_path")
    var posterPath: String,
    @SerialName(value = "backdrop_path")
    var backdropPath: String,
    @SerialName(value = "release_date")
    var releaseDate: String,
    @SerialName(value = "overview")
    var overview: String,
    @SerialName(value = "genres")
    var genres: List<Genre> = listOf(),
    @SerialName(value = "homepage")
    var homeUrl: String = "",
    @SerialName(value = "imdb_id")
    var imdbId: String = "",

    var isFavorite: Boolean = false,

    var latest: Int = 0
) {
    class Converters {
        @TypeConverter
        fun fromGenreList(genres: List<Genre>): String {
            val gson = Gson()
            return gson.toJson(genres)
        }

        @TypeConverter
        fun toGenreList(genreJson: String): List<Genre> {
            val gson = Gson()
            val type = object : TypeToken<List<Genre>>() {}.type
            return gson.fromJson(genreJson, type)
        }
    }
}