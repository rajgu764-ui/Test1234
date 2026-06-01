package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.Movie
import com.example.viewmodel.MovaViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.Tab
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// --- SPLASH SCREEN ---
@Composable
fun SplashScreen(viewModel: MovaViewModel) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        viewModel.navigateTo(Screen.Onboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MovaAbyssBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background aurora
        Box(
            modifier = Modifier
                .size(350.dp)
                .alpha(0.12f)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(MovaPrimaryRed, Color.Transparent),
                            center = center,
                            radius = size.width / 2
                        )
                    )
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Stylized Mova Logo
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Movie,
                    contentDescription = "Movie Loop Icon",
                    tint = MovaPrimaryRed,
                    modifier = Modifier
                        .size(52.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "MOVA",
                    color = MovaPrimaryRed,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 4.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CINEMATIC AI RECON & STREAMING",
                color = MovaLightGrey,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

// --- ONBOARDING INTERESTS SELECTION ---
@Composable
fun OnboardingScreen(viewModel: MovaViewModel) {
    val interests = listOf("Action", "Sci-Fi", "Anime", "Romance", "Comedy", "Drama", "Thriller", "Horror", "Documentary")
    val chosen by viewModel.chosenInterests.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MovaAbyssBlack)
            .padding(24.dp)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Welcome to Mova",
                color = MovaTextWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose your favorite cinematic categories. Mova will generate tailored recommendations for you.",
                color = MovaLightGrey,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Flex-Grid Flow layout using a simple Row with item alignment representation
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(interests) { genre ->
                    val isSelected = chosen.contains(genre)
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MovaPrimaryRed else MovaSurfaceDark
                        ),
                        modifier = Modifier
                            .clickable {
                                if (isSelected) {
                                    viewModel.chosenInterests.value = chosen - genre
                                } else {
                                    viewModel.chosenInterests.value = chosen + genre
                                }
                            }
                            .shadow(elevation = if (isSelected) 8.dp else 0.dp, shape = RoundedCornerShape(24.dp))
                            .testTag("interest_chip_$genre"),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = genre,
                                color = if (isSelected) MovaTextWhite else MovaLightGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.setUpProfile("CinemaFan", chosen.toList())
                    viewModel.navigateTo(Screen.Login)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .navigationBarsPadding()
                    .testTag("onboarding_next"),
                colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed)
            ) {
                Text(
                    text = "Continue",
                    color = MovaTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- SIGN IN SCREEN ---
@Composable
fun LoginScreen(viewModel: MovaViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MovaAbyssBlack)
            .padding(24.dp)
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0x33E21221), CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Movie,
                    contentDescription = "Mova TV Logo Icon",
                    tint = MovaPrimaryRed,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Premium Access",
                color = MovaTextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Sign in to unlock personalized cinematic content",
                color = MovaLightGrey,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 32.dp)
            )

            // Form inputs
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = MovaLightGrey) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MovaPrimaryRed,
                    unfocusedBorderColor = MovaSurfaceDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = MovaLightGrey) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MovaPrimaryRed,
                    unfocusedBorderColor = MovaSurfaceDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val fallbackEmail = if (email.isBlank()) "mova.user@aistudio.com" else email
                    viewModel.loginUser(fallbackEmail)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed)
            ) {
                Text(
                    text = "Sign In & Explore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { viewModel.loginUser("guest.cinephile@aistudio.com") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Browse as Guest", color = MovaPrimaryRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- CONTAINER FOR MAIN NAV (TABS CONTAINER) ---
@Composable
fun MainScreen(viewModel: MovaViewModel) {
    val activeTab by viewModel.currentTab.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MovaSurfaceDark,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == Tab.Home,
                    onClick = { viewModel.selectTab(Tab.Home) },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home Tab") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovaPrimaryRed,
                        unselectedIconColor = MovaLightGrey,
                        selectedTextColor = MovaPrimaryRed,
                        unselectedTextColor = MovaLightGrey,
                        indicatorColor = Color(0x1AE21221)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == Tab.Explore,
                    onClick = { viewModel.selectTab(Tab.Explore) },
                    icon = { Icon(Icons.Rounded.Search, contentDescription = "Explore Tab") },
                    label = { Text("Explore", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovaPrimaryRed,
                        unselectedIconColor = MovaLightGrey,
                        selectedTextColor = MovaPrimaryRed,
                        unselectedTextColor = MovaLightGrey,
                        indicatorColor = Color(0x1AE21221)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == Tab.AIRecommender,
                    onClick = { viewModel.selectTab(Tab.AIRecommender) },
                    icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Recommender") },
                    label = { Text("Mova AI", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovaSparkleAqua,
                        unselectedIconColor = MovaLightGrey,
                        selectedTextColor = MovaSparkleAqua,
                        unselectedTextColor = MovaLightGrey,
                        indicatorColor = Color(0x1A00F0FF)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == Tab.MyList,
                    onClick = { viewModel.selectTab(Tab.MyList) },
                    icon = { Icon(Icons.Rounded.Favorite, contentDescription = "My List Wishlist") },
                    label = { Text("My List", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovaPrimaryRed,
                        unselectedIconColor = MovaLightGrey,
                        selectedTextColor = MovaPrimaryRed,
                        unselectedTextColor = MovaLightGrey,
                        indicatorColor = Color(0x1AE21221)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == Tab.Profile,
                    onClick = { viewModel.selectTab(Tab.Profile) },
                    icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile Settings") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovaPrimaryRed,
                        unselectedIconColor = MovaLightGrey,
                        selectedTextColor = MovaPrimaryRed,
                        unselectedTextColor = MovaLightGrey,
                        indicatorColor = Color(0x1AE21221)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MovaAbyssBlack)
                .padding(innerPadding)
        ) {
            when (activeTab) {
                Tab.Home -> HomeScreen(viewModel)
                Tab.Explore -> ExploreScreen(viewModel)
                Tab.AIRecommender -> AIRecommenderScreen(viewModel)
                Tab.MyList -> MyListScreen(viewModel)
                Tab.Profile -> ProfileScreen(viewModel)
            }
        }
    }
}

// --- HOME SCREEN TAB ---
@Composable
fun HomeScreen(viewModel: MovaViewModel) {
    val wishlist by viewModel.wishlist.collectAsStateWithLifecycle()
    val movies = viewModel.allMovies
    val trending = movies.filter { it.category == "Trending" }
    val anime = movies.filter { it.category == "Anime" }
    val newReleases = movies.filter { it.category == "New Releases" }
    val popular = movies.filter { it.id !in listOf("m1", "m2", "m3") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_tab")
    ) {
        // Spotlight Featured / Billboard
        item {
            BillboardSpotlight(trending.firstOrNull(), wishlist, viewModel)
        }

        // Section: Trending
        item {
            MovieRowSection("Trending Now", trending, viewModel)
        }

        // Section: Top 10 Overlays
        item {
            TopTenSection(movies.take(10), viewModel)
        }

        // Section: New Releases
        item {
            MovieRowSection("New Releases", newReleases, viewModel)
        }

        // Section: Anime Corner
        item {
            MovieRowSection("Anime & Fantasy Corner", anime, viewModel)
        }

        // Section: Curated Popular Selection
        item {
            MovieRowSection("Cinematic Hits", popular, viewModel)
        }
    }
}

@Composable
fun BillboardSpotlight(movie: Movie?, wishlist: List<Movie>, viewModel: MovaViewModel) {
    if (movie == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .clickable { viewModel.navigateTo(Screen.Details(movie.id)) }
            .testTag("billboard_spotlight")
    ) {
        // Poster image
        AsyncImage(
            model = movie.backdropUrl,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Backdrop Ambient Gradient Shaders
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MovaAbyssBlack.copy(alpha = 0.5f),
                            MovaAbyssBlack
                        )
                    )
                )
        )

        // Spotlight details and buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = movie.title.uppercase(),
                color = MovaTextWhite,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(movie.genre.split(", ").first(), color = MovaLightGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(4.dp).background(MovaLightGrey, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.Star, contentDescription = "Star", tint = MovaStarGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(movie.rating.toString(), color = MovaStarGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(4.dp).background(MovaLightGrey, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(movie.duration, color = MovaLightGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.Player(movie.id, movie.title, movie.trailerUrl)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Trailer", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.toggleWishlist(movie) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    val isInWishlist = wishlist.any { it.id == movie.id }
                    Icon(
                        imageVector = if (isInWishlist) Icons.Rounded.Check else Icons.Rounded.Add,
                        contentDescription = "Wishlist Toggle"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isInWishlist) "My List" else "Add List", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MovieRowSection(title: String, movies: List<Movie>, viewModel: MovaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = MovaTextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(Screen.Details(movie.id)) }
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Simple top gradient header tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color(0xE60D0E15), CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, contentDescription = "Score", tint = MovaStarGold, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(movie.rating.toString(), color = MovaStarGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopTenSection(movies: List<Movie>, viewModel: MovaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Mova Top 10 Popularity",
            color = MovaTextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(movies) { idx, movie ->
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(180.dp)
                        .clickable { viewModel.navigateTo(Screen.Details(movie.id)) }
                ) {
                    // Numeric layer
                    Text(
                        text = (idx + 1).toString(),
                        color = Color.White.copy(alpha = 0.15f),
                        fontSize = 150.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-10).dp, y = (14).dp)
                    )

                    // Card Poster overlay
                    Box(
                        modifier = Modifier
                            .width(115.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .align(Alignment.CenterEnd)
                    ) {
                        AsyncImage(
                            model = movie.posterUrl,
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

// --- EXPLORE / SEARCH SCREEN ---
@Composable
fun ExploreScreen(viewModel: MovaViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenreFilter.collectAsStateWithLifecycle()
    val filteredMovies by viewModel.filteredMovies.collectAsStateWithLifecycle()

    val genreFilters = listOf("All", "Sci-Fi", "Anime", "Action", "Romance", "Comedy", "Thriller", "Drama")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("explore_screen_tab")
    ) {
        // Searching field custom
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Finder", tint = MovaLightGrey) },
            placeholder = { Text("Search title, actors, genre...", color = MovaLightGrey) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MovaPrimaryRed,
                unfocusedBorderColor = MovaSurfaceDark,
                focusedContainerColor = MovaSurfaceDark,
                unfocusedContainerColor = MovaSurfaceDark,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        )

        // Filters list
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(genreFilters) { genre ->
                val isSelected = selectedGenre == genre
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) MovaPrimaryRed else MovaSurfaceDark,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.updateGenreFilter(genre) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(genre, color = if (isSelected) MovaTextWhite else MovaLightGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Results
        if (filteredMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Search, contentDescription = "Frown Search", tint = MovaLightGrey, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Matching Movies Found", color = MovaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Try typing keywords like Nolan, Space, or Adventure", color = MovaLightGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredMovies) { movie ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(Screen.Details(movie.id)) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.72f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = movie.posterUrl,
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = movie.title,
                            color = MovaTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// --- MOVA AI ASSISTANT CHAT SCREEN (GEMINI) ---
@Composable
fun AIRecommenderScreen(viewModel: MovaViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    var userPrompt by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val spaceSparkles = Brush.linearGradient(
        colors = listOf(MovaPrimaryRed, MovaSparkleAqua)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("ai_screen_tab")
    ) {
        // AI Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(spaceSparkles, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = "Spark", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Mova AI Recommendation Engine", color = MovaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Powered by Gemini 3.5 Flash Model", color = MovaSparkleAqua, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Divider(color = MovaSurfaceDark, thickness = 1.dp)

        // Chat Bubble list
        val state = rememberLazyListState()
        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                state.animateScrollToItem(chatMessages.size - 1)
            }
        }

        LazyColumn(
            state = state,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(chatMessages) { message ->
                if (message.isUser) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp, 1.dp, 18.dp, 18.dp),
                            colors = CardDefaults.cardColors(containerColor = MovaPrimaryRed),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Card(
                            shape = RoundedCornerShape(1.dp, 18.dp, 18.dp, 18.dp),
                            colors = CardDefaults.cardColors(containerColor = MovaSurfaceDark),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = message.text,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            if (aiLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MovaSparkleAqua, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mova is creating personalized recommendations...", color = MovaLightGrey, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Message typing input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userPrompt,
                onValueChange = { userPrompt = it },
                placeholder = { Text("E.g. I want an intense space movie with good music", color = MovaLightGrey, fontSize = 13.sp) },
                maxLines = 3,
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (userPrompt.isNotBlank()) {
                        viewModel.sendChatMessage(userPrompt)
                        userPrompt = ""
                    }
                    focusManager.clearFocus()
                }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MovaSparkleAqua,
                    unfocusedBorderColor = MovaSurfaceDark,
                    focusedContainerColor = MovaSurfaceDark,
                    unfocusedContainerColor = MovaSurfaceDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (userPrompt.isNotBlank()) {
                        viewModel.sendChatMessage(userPrompt)
                        userPrompt = ""
                    }
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .background(spaceSparkles, CircleShape)
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(Icons.Rounded.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- MY LIST (WISHLIST) SCREEN ---
@Composable
fun MyListScreen(viewModel: MovaViewModel) {
    val wishlist by viewModel.wishlist.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("mylist_screen_tab")
    ) {
        Text(
            text = "My Wishlist",
            color = MovaTextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (wishlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Heart", tint = MovaLightGrey, modifier = Modifier.size(72.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Wishlist is Empty", color = MovaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Explore trending titles and add them to your collection!", color = MovaLightGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(wishlist) { movie ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(Screen.Details(movie.id)) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.72f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = movie.posterUrl,
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = movie.title,
                            color = MovaTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// --- PROFILE SCREEN TAB ---
@Composable
fun ProfileScreen(viewModel: MovaViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    var isUpgrading by remember { mutableStateOf(false) }
    var cardNo by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    val premiumGlow = Brush.linearGradient(
        colors = listOf(MovaPrimaryRed, MovaStarGold)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen_tab")
    ) {
        // Simple User Card Detail
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF222434), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(userProfile.userName.take(2).uppercase(), color = MovaPrimaryRed, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(userProfile.userName, color = MovaTextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(userProfile.email, color = MovaLightGrey, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subscription Tier Panel representer
        if (userProfile.isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(premiumGlow, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = "Star", tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mova Premium Pro Active", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text("Unlocked offline caching, VIP movie reviews, & unlimited Gemini AI insights.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MovaSurfaceDark, RoundedCornerShape(16.dp))
                    .border(1.dp, MovaPrimaryRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = "Upgrade Star", tint = MovaStarGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Upgrade to Premium Core", color = MovaTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Gain access to cloud integrations, exclusive commentary, ultra HD feeds, and VIP ratings sync.",
                        color = MovaLightGrey,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp),
                        lineHeight = 18.sp
                    )

                    if (!isUpgrading) {
                        Button(
                            onClick = { isUpgrading = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth().testTag("upgrade_button")
                        ) {
                            Text("Join For $9.99/mo", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Upgrade Payment form simulation
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = cardNo,
                                onValueChange = { cardNo = it },
                                label = { Text("16-Digit Card Number", color = MovaLightGrey, fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("card_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MovaPrimaryRed,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Cardholder Name", color = MovaLightGrey, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MovaPrimaryRed,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White
                                )
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if (cardNo.length >= 12) {
                                            viewModel.upgradeToPremium(cardNo, cardName)
                                            isUpgrading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MovaStarGold),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.weight(1f).testTag("payment_submit")
                                ) {
                                    Text("Pay Secure", color = MovaAbyssBlack, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { isUpgrading = false }) {
                                    Text("Cancel", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options List Items mock settings
        val sections = listOf("Account Settings", "Clear Local Playlist", "Stream Video Quality", "Security & Lock (Biometrics)", "Help Center", "Privacy Policy")
        for (sec in sections) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(vertical = 16.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(sec, color = Color.White, fontSize = 15.sp)
                Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow Go", tint = MovaLightGrey, modifier = Modifier.size(14.dp))
            }
            Divider(color = MovaSurfaceDark, thickness = 1.dp)
        }
    }
}

// --- MOVIE DETAILS SCREEN ---
@Composable
fun MovieDetailsScreen(viewModel: MovaViewModel) {
    val movie by viewModel.selectedMovie.collectAsStateWithLifecycle()
    val reviews by viewModel.currentReviews.collectAsStateWithLifecycle()
    val wishlist by viewModel.wishlist.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var userComment by remember { mutableStateOf("") }
    var ratingChosen by remember { mutableIntStateOf(5) }

    if (movie == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MovaAbyssBlack)
            .testTag("movie_details_screen")
    ) {
        val currMovie = movie!!

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Poster image + Back buttons overlay
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = currMovie.backdropUrl,
                        contentDescription = currMovie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Bottom gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MovaAbyssBlack.copy(alpha = 0.5f),
                                        MovaAbyssBlack
                                    )
                                )
                            )
                    )

                    // Back Button & Wishlist floating icon buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier
                                .background(Color(0x9906070C), CircleShape)
                                .size(40.dp)
                                .testTag("details_back")
                        ) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back icon", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.toggleWishlist(currMovie) },
                            modifier = Modifier
                                .background(Color(0x9906070C), CircleShape)
                                .size(40.dp)
                                .testTag("toggle_wishlist")
                        ) {
                            val isInWishlist = wishlist.any { it.id == currMovie.id }
                            Icon(
                                imageVector = if (isInWishlist) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "Heart watchlist",
                                tint = if (isInWishlist) MovaPrimaryRed else Color.White
                            )
                        }
                    }
                }
            }

            // Info Content
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = currMovie.title,
                        color = MovaTextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Star, contentDescription = "Star", tint = MovaStarGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(currMovie.rating.toString(), color = MovaStarGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(currMovie.releaseDate, color = MovaLightGrey, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(currMovie.duration, color = MovaLightGrey, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Play simulation button
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Player(currMovie.id, currMovie.title, currMovie.trailerUrl)) },
                        colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("play_movie_action")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play Icon Action")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stream Movie & Trailer", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Overview", color = MovaTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currMovie.overview,
                        color = MovaLightGrey,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Production details
                    Text("Production Details", color = MovaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Director: ${currMovie.director}", color = MovaLightGrey, fontSize = 13.sp)
                    Text("Starring: ${currMovie.cast}", color = MovaLightGrey, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(28.dp))

                    Divider(color = MovaSurfaceDark, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // WRITE A REVIEW BLOCK
                    Text("Write a Cinema Review", color = MovaTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Your rating is stored locally and syncs to Supabase", color = MovaLightGrey, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Star Select row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            val activeVal = star <= ratingChosen
                            IconButton(
                                onClick = { ratingChosen = star },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("star_rate_$star")
                            ) {
                                Icon(
                                    imageVector = if (activeVal) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                    contentDescription = "Rating $star",
                                    tint = if (activeVal) MovaStarGold else MovaLightGrey,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userComment,
                        onValueChange = { userComment = it },
                        placeholder = { Text("Write movie critiques or comments...", color = MovaLightGrey) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_comment_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MovaPrimaryRed,
                            unfocusedBorderColor = MovaSurfaceDark,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.submitReview(currMovie.id, ratingChosen, userComment)
                            userComment = ""
                            ratingChosen = 5
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MovaPrimaryRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("submit_review_button")
                    ) {
                        Text("Submit Review", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // USER REVIEWS LIST FLOW
                    Text("Critiques & Audience Reviews (${reviews.size})", color = MovaTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reviews.isEmpty()) {
                        Text("Be the first to leave a critique on this block!", color = MovaLightGrey, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (rev in reviews) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MovaSurfaceDark),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(rev.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Row {
                                                for (i in 1..rev.rating) {
                                                    Icon(Icons.Rounded.Star, contentDescription = "S", tint = MovaStarGold, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(rev.comment, color = MovaLightGrey, fontSize = 13.sp, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// --- VIDEO PLAYBACK CANVAS SIMULATOR ---
@Composable
fun VideoPlaybackScreen(viewModel: MovaViewModel, movieId: String, title: String) {
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateFloatAsState(if (isPlaying) 1f else 0.4f, animationSpec = tween(durationMillis = 3000))

    val localProgressValue by animateFloatAsState(if (isPlaying) 1f else 0.4f)
    var currentScrubSeconds by remember { mutableIntStateOf(105) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            currentScrubSeconds += 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_playback_screen")
    ) {
        // Simulated cosmic movie video scrolling canvas using Custom DrawBehind
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPlaying = !isPlaying }
        ) {
            val spaceBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE21221).copy(alpha = 0.15f),
                    Color(0xFF00F0FF).copy(alpha = 0.08f),
                    Color.Black
                ),
                center = center,
                radius = size.width / if (isPlaying) 1.2f else 1.8f
            )
            drawRect(spaceBrush)
        }

        // Ambient moving text
        Text(
            text = "STREAMING: $title\n[Simulating UHD Playback]",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Overlay Navigation Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.background(Color(0x6613141E), CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Close stream", tint = Color.White)
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0x99E21221), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("LIVE UHD", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Bottom Player seekbar controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color(0x9913141E), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isPlaying = !isPlaying }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Mock Progress bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = (currentScrubSeconds % 300) / 300f)
                                .background(MovaPrimaryRed, RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val mins = currentScrubSeconds / 60
                    val secs = currentScrubSeconds % 60
                    Text(
                        String.format("%02d:%02d", mins, secs),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
