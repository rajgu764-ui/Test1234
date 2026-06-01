package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface Screen {
    object Splash : Screen
    object Onboarding : Screen
    object Login : Screen
    object Main : Screen
    data class Details(val movieId: String) : Screen
    data class Player(val movieId: String, val title: String, val videoUrl: String) : Screen
}

sealed interface Tab {
    object Home : Tab
    object Explore : Tab
    object AIRecommender : Tab
    object MyList : Tab
    object Profile : Tab
}

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MovaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MovieDatabase.getDatabase(application)
    private val repository = MovaRepository(db.movieDao())

    // UI Navigation States
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow<Tab>(Tab.Home)
    val currentTab: StateFlow<Tab> = _currentTab.asStateFlow()

    // Auth & Profile State
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Onboarding setup state
    val chosenInterests = MutableStateFlow<Set<String>>(emptySet())

    // Movie Lists from Repository
    val allMovies = repository.getAllMovies()
    
    // Wishlist (My List) Room reactive flow
    val wishlist: StateFlow<List<Movie>> = repository.wishlist
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Dynamic Selected Movie Detail
    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("All")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    val filteredMovies: StateFlow<List<Movie>> = combine(
        searchQuery,
        _selectedGenreFilter
    ) { query, genre ->
        allMovies.filter { movie ->
            val matchesQuery = movie.title.contains(query, ignoreCase = true) || 
                               movie.genre.contains(query, ignoreCase = true) ||
                               movie.overview.contains(query, ignoreCase = true)
            val matchesGenre = genre == "All" || movie.genre.contains(genre, ignoreCase = true)
            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), allMovies)

    // Reviews State
    private val _currentReviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val currentReviews: StateFlow<List<ReviewEntity>> = _currentReviews.asStateFlow()

    // AI Recommender State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(false, "Greetings movie lover! I am Mova, your premium AI Cinematic Companion. Describe what kind of movie or mood you are looking for today!")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    // Navigation trigger methods
    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            if (screen is Screen.Details) {
                _selectedMovie.value = repository.getMovieById(screen.movieId)
                // Load reviews flow in a separate non-blocking job
                viewModelScope.launch {
                    repository.getReviews(screen.movieId).collect { reviews ->
                        _currentReviews.value = reviews
                    }
                }
                // Sync reviews from Supabase in background
                viewModelScope.launch {
                    repository.syncReviewsFromSupabase(screen.movieId)
                }
            }
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            val current = _currentScreen.value
            when (current) {
                is Screen.Details -> _currentScreen.value = Screen.Main
                is Screen.Player -> _currentScreen.value = Screen.Details((current as Screen.Player).movieId)
                else -> _currentScreen.value = Screen.Main
            }
        }
    }

    fun selectTab(tab: Tab) {
        _currentTab.value = tab
    }

    // Wishlist Toggle
    fun toggleWishlist(movie: Movie) {
        viewModelScope.launch {
            repository.toggleWishlist(movie)
        }
    }

    suspend fun isMovieInWishlist(movieId: String): Boolean {
        return repository.getMovieFromWishlist(movieId)
    }

    // Submit user review (Room + Supabase)
    fun submitReview(movieId: String, rating: Int, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            val username = _userProfile.value.userName
            repository.addReview(movieId, username, rating, comment)
            
            // Reload reviews
            repository.getReviews(movieId).take(1).collect { reviews ->
                _currentReviews.value = reviews
            }
        }
    }

    // Send chat prompt to Gemini
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(true, text)
        _chatMessages.update { it + userMsg }
        _aiLoading.value = true

        viewModelScope.launch {
            val aiResponse = repository.askGeminiForRecommendations(text)
            val aiMsg = ChatMessage(false, aiResponse)
            _chatMessages.update { it + aiMsg }
            _aiLoading.value = false
        }
    }

    // Auth Workflows
    fun loginUser(email: String) {
        viewModelScope.launch {
            _userProfile.update { 
                it.copy(
                    email = email,
                    userName = email.substringBefore("@")
                )
            }
            _isLoggedIn.value = true
            _currentScreen.value = Screen.Main
        }
    }

    fun setUpProfile(userName: String, genres: List<String>) {
        _userProfile.update { 
            it.copy(userName = userName, favoriteGenres = genres)
        }
    }

    // Set Premium Membership Account Upgrade
    fun upgradeToPremium(cardNumber: String, cardName: String) {
        _userProfile.update { it.copy(isPremium = true) }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
    }
}
