package com.example.data

import com.example.network.GeminiApiClient
import com.example.network.SupabaseApiClient
import com.example.network.SupabaseReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class MovaRepository(private val movieDao: MovieDao) {

    // Gorgeous curated list of movies with real, stable cinematic Unsplash image assets
    private val curatedMovies = listOf(
        Movie(
            id = "m1",
            title = "Interstellar",
            posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1200&auto=format&fit=crop&q=80",
            overview = "When Earth becomes uninhabitable, a team of explorers undertakes the most important mission in human history: traveling beyond this galaxy to discover whether mankind has a future among the stars.",
            releaseDate = "2014-11-07",
            rating = 8.7,
            genre = "Sci-Fi, Adventure",
            duration = "2h 49m",
            director = "Christopher Nolan",
            cast = "Matthew McConaughey, Anne Hathaway, Jessica Chastain",
            trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E",
            category = "Trending"
        ),
        Movie(
            id = "m2",
            title = "Dune: Part Two",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1547483238-f400e65ccd56?w=1200&auto=format&fit=crop&q=80",
            overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the universe, he endeavors to prevent a terrible future.",
            releaseDate = "2024-03-01",
            rating = 8.9,
            genre = "Sci-Fi, Epic",
            duration = "2h 46m",
            director = "Denis Villeneuve",
            cast = "Timothée Chalamet, Zendaya, Rebecca Ferguson, Austin Butler",
            trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
            category = "Trending"
        ),
        Movie(
            id = "m3",
            title = "The Dark Knight",
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509248961158-e54f6934749c?w=1200&auto=format&fit=crop&q=80",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            releaseDate = "2008-07-18",
            rating = 9.0,
            genre = "Action, Crime, Drama",
            duration = "2h 32m",
            director = "Christopher Nolan",
            cast = "Christian Bale, Heath Ledger, Aaron Eckhart, Maggie Gyllenhaal",
            trailerUrl = "https://www.youtube.com/watch?v=EXeTwQWrcwY",
            category = "Trending"
        ),
        Movie(
            id = "m4",
            title = "Spirited Away",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1200&auto=format&fit=crop&q=80",
            overview = "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.",
            releaseDate = "2001-07-20",
            rating = 8.6,
            genre = "Anime, Fantasy, Adventure",
            duration = "2h 5m",
            director = "Hayao Miyazaki",
            cast = "Rumi Hiiragi, Miyu Irino, Mari Natsuki",
            trailerUrl = "https://www.youtube.com/watch?v=ByXuk9QqQkk",
            category = "Anime"
        ),
        Movie(
            id = "m5",
            title = "Spider-Man: Across the Spider-Verse",
            posterUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1569003339405-ea396a5a8a90?w=1200&auto=format&fit=crop&q=80",
            overview = "Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence. When the heroes clash, Miles must redefine what it means to be a hero.",
            releaseDate = "2023-06-02",
            rating = 8.7,
            genre = "Action, Animation, Adventure",
            duration = "2h 20m",
            director = "Joaquim Dos Santos",
            cast = "Shameik Moore, Hailee Steinfeld, Oscar Isaac",
            trailerUrl = "https://www.youtube.com/watch?v=shW9i6k8cB0",
            category = "New Releases"
        ),
        Movie(
            id = "m6",
            title = "Everything Everywhere All at Once",
            posterUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1461360370896-922624d12aa1?w=1200&auto=format&fit=crop&q=80",
            overview = "A middle-aged Chinese immigrant is swept up into an insane adventure in which she alone can save existence by exploring other universes and connecting with the lives she could have led.",
            releaseDate = "2022-03-25",
            rating = 8.5,
            genre = "Sci-Fi, Comedy, Drama",
            duration = "2h 19m",
            director = "Daniel Kwan, Daniel Scheinert",
            cast = "Michelle Yeoh, Stephanie Hsu, Ke Huy Quan, Jamie Lee Curtis",
            trailerUrl = "https://www.youtube.com/watch?v=wxN1T1UxQ2A",
            category = "New Releases"
        ),
        Movie(
            id = "m7",
            title = "Inception",
            posterUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            overview = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project.",
            releaseDate = "2010-07-16",
            rating = 8.8,
            genre = "Sci-Fi, Action, Thriller",
            duration = "2h 28m",
            director = "Christopher Nolan",
            cast = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page, Ken Watanabe",
            trailerUrl = "https://www.youtube.com/watch?v=YoHD9XEInc0",
            category = "Popular"
        ),
        Movie(
            id = "m8",
            title = "Your Name",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=1200&auto=format&fit=crop&q=80",
            overview = "Two strangers find themselves linked in a bizarre way. When a connection forms, will distance be the only thing to keep them apart? A beautifully told romance that transcends space and time.",
            releaseDate = "2016-08-26",
            rating = 8.4,
            genre = "Anime, Romance, Drama",
            duration = "1h 46m",
            director = "Makoto Shinkai",
            cast = "Ryunosuke Kamiki, Mone Kamishiraishi, Ryo Narita",
            trailerUrl = "https://www.youtube.com/watch?v=hRfHcp2t654",
            category = "Anime"
        ),
        Movie(
            id = "m9",
            title = "Parasite",
            posterUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=1200&auto=format&fit=crop&q=80",
            overview = "Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan in this Academy Award-winning thriller.",
            releaseDate = "2019-10-11",
            rating = 8.6,
            genre = "Thriller, Drama, Comedy",
            duration = "2h 12m",
            director = "Bong Joon-ho",
            cast = "Song Kang-ho, Lee Sun-kyun, Cho Yeo-jeong, Choi Woo-shik",
            trailerUrl = "https://www.youtube.com/watch?v=5xH0HfJHsaY",
            category = "Popular"
        ),
        Movie(
            id = "m10",
            title = "La La Land",
            posterUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=1200&auto=format&fit=crop&q=80",
            overview = "While navigating their careers in Los Angeles, a pianist and an actress fall in love while attempting to reconcile their aspirations for the future.",
            releaseDate = "2016-12-09",
            rating = 8.0,
            genre = "Romance, Musical, Drama",
            duration = "2h 8m",
            director = "Damien Chazelle",
            cast = "Ryan Gosling, Emma Stone, John Legend",
            trailerUrl = "https://www.youtube.com/watch?v=0pdqf4P9MB8",
            category = "Romance"
        ),
        Movie(
            id = "m11",
            title = "Knives Out",
            posterUrl = "https://images.unsplash.com/photo-1585647347483-22b66260dfff?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=1200&auto=format&fit=crop&q=80",
            overview = "A detective investigates the death of a patriarch of an eccentric, combative family, leading to a sprawling mystery full of twists and comedic dialogue.",
            releaseDate = "2019-11-27",
            rating = 7.9,
            genre = "Comedy, Mystery, Drama",
            duration = "2h 10m",
            director = "Rian Johnson",
            cast = "Daniel Craig, Chris Evans, Ana de Armas, Jamie Lee Curtis",
            trailerUrl = "https://www.youtube.com/watch?v=qGqiHJTsRkQ",
            category = "Popular"
        ),
        Movie(
            id = "m12",
            title = "Demon Slayer: Mugen Train",
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            overview = "After a string of mysterious disappearances aboard a train, the Demon Slayer Corps sends its most formidable warriors to eliminate a demonic force lurking inside.",
            releaseDate = "2020-10-16",
            rating = 8.2,
            genre = "Anime, Action, Fantasy",
            duration = "1h 57m",
            director = "Haruo Sotozaki",
            cast = "Natsuki Hanae, Akari Kito, Yoshitsugu Matsuoka",
            trailerUrl = "https://www.youtube.com/watch?v=ATJYac_dORw",
            category = "Anime"
        )
    )

    fun getAllMovies(): List<Movie> = curatedMovies

    fun getMovieById(id: String): Movie? {
        return curatedMovies.find { it.id == id }
    }

    // Wishlist (My List) Room connection
    val wishlist: Flow<List<Movie>> = movieDao.getWishlistFlow().map { entities ->
        entities.map { it.toMovie() }
    }

    suspend fun getMovieFromWishlist(movieId: String): Boolean = withContext(Dispatchers.IO) {
        movieDao.getMovieFromWishlist(movieId) != null
    }

    suspend fun toggleWishlist(movie: Movie) = withContext(Dispatchers.IO) {
        val isFav = movieDao.getMovieFromWishlist(movie.id) != null
        if (isFav) {
            movieDao.removeFromWishlist(movie.id)
        } else {
            movieDao.addToWishlist(movie.toEntity())
        }
    }

    // Reviews (Local Room database)
    fun getReviews(movieId: String): Flow<List<ReviewEntity>> {
        return movieDao.getReviewsForMovie(movieId)
    }

    suspend fun syncReviewsFromSupabase(movieId: String) = withContext(Dispatchers.IO) {
        try {
            val supabaseReviews = SupabaseApiClient.fetchReviews(movieId)
            if (supabaseReviews.isNotEmpty()) {
                val mapped = supabaseReviews.map {
                    ReviewEntity(
                        id = it.id,
                        movieId = it.movie_id,
                        userName = it.user_name,
                        rating = it.rating,
                        comment = it.comment,
                        timestamp = try { it.created_at.toLong() } catch (e: Exception) { System.currentTimeMillis() }
                    )
                }
                // Update local Room database cache
                for (rev in mapped) {
                    movieDao.insertReview(rev)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addReview(movieId: String, userName: String, rating: Int, comment: String) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val review = ReviewEntity(id, movieId, userName, rating, comment, System.currentTimeMillis())
        
        // Always insert to Room first (instant local feedback)
        movieDao.insertReview(review)

        // Try syncing to Supabase in parallel
        val supabaseReview = SupabaseReview(
            id = id,
            movie_id = movieId,
            user_name = userName,
            rating = rating,
            comment = comment,
            created_at = review.timestamp.toString()
        )
        SupabaseApiClient.insertReview(supabaseReview)
    }

    // AI recommendation using Gemini
    suspend fun askGeminiForRecommendations(prompt: String): String = withContext(Dispatchers.IO) {
        GeminiApiClient.generateRecommendation(prompt, curatedMovies)
    }
}
