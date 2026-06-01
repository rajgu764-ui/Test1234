package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM wishlist_movies ORDER BY timestamp DESC")
    fun getWishlistFlow(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM wishlist_movies WHERE id = :movieId")
    suspend fun getMovieFromWishlist(movieId: String): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(movie: MovieEntity)

    @Query("DELETE FROM wishlist_movies WHERE id = :movieId")
    suspend fun removeFromWishlist(movieId: String)

    // Reviews Interface
    @Query("SELECT * FROM movie_reviews WHERE movieId = :movieId ORDER BY timestamp DESC")
    fun getReviewsForMovie(movieId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}
