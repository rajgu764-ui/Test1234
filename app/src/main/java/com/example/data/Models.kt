package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "wishlist_movies")
@JsonClass(generateAdapter = true)
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
    val genre: String,
    val duration: String,
    val director: String,
    val cast: String,
    val trailerUrl: String,
    val category: String, // e.g. "Trending", "New Releases", "Anime", "Action"
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMovie() = Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        overview = overview,
        releaseDate = releaseDate,
        rating = rating,
        genre = genre,
        duration = duration,
        director = director,
        cast = cast,
        trailerUrl = trailerUrl,
        category = category
    )
}

@JsonClass(generateAdapter = true)
data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
    val genre: String,
    val duration: String,
    val director: String,
    val cast: String,
    val trailerUrl: String,
    val category: String = "Popular"
) {
    fun toEntity() = MovieEntity(
        id = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        overview = overview,
        releaseDate = releaseDate,
        rating = rating,
        genre = genre,
        duration = duration,
        director = director,
        cast = cast,
        trailerUrl = trailerUrl,
        category = category
    )
}

@Entity(tableName = "movie_reviews")
@JsonClass(generateAdapter = true)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val movieId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val userName: String = "MovieEnthusiast",
    val email: String = "mova.fan@aistudio.com",
    val isPremium: Boolean = false,
    val favoriteGenres: List<String> = emptyList(),
    val avatarUrl: String = ""
)
