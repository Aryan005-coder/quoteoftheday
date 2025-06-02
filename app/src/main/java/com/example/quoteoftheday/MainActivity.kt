// MainActivity.kt
package com.example.quoteoftheday

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// Data class for quotes
data class Quote(
    val id: Int,
    val text: String,
    val author: String,
    val category: String = "Inspiration"
)

// Quote repository with sample quotes
object QuoteRepository {
    private val quotes = listOf(
        Quote(1, "The only way to do great work is to love what you do.", "Steve Jobs"),
        Quote(2, "Innovation distinguishes between a leader and a follower.", "Steve Jobs"),
        Quote(3, "Life is what happens to you while you're busy making other plans.", "John Lennon"),
        Quote(4, "The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
        Quote(5, "It is during our darkest moments that we must focus to see the light.", "Aristotle"),
        Quote(6, "Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        Quote(7, "The only impossible journey is the one you never begin.", "Tony Robbins"),
        Quote(8, "In the middle of difficulty lies opportunity.", "Albert Einstein"),
        Quote(9, "Believe you can and you're halfway there.", "Theodore Roosevelt"),
        Quote(10, "Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
        Quote(11, "Whether you think you can or you think you can't, you're right.", "Henry Ford"),
        Quote(12, "The way to get started is to quit talking and begin doing.", "Walt Disney"),
        Quote(13, "Don't be afraid to give up the good to go for the great.", "John D. Rockefeller"),
        Quote(14, "The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"),
        Quote(15, "Your limitation—it's only your imagination.", "Unknown")
    )

    fun getRandomQuote(): Quote {
        return quotes.random()
    }

    fun getTodaysQuote(): Quote {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val index = today.hashCode() % quotes.size
        return quotes[abs(index)]
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuoteOfTheDayTheme {
                QuoteApp()
            }
        }
    }
}

@Composable
fun QuoteOfTheDayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6366F1),
            primaryContainer = Color(0xFFE0E7FF),
            secondary = Color(0xFF8B5CF6),
            secondaryContainer = Color(0xFFF3E8FF),
            surface = Color(0xFFFAFAFA),
            background = Color(0xFFF8FAFC)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var favoriteQuotes by remember { mutableStateOf(listOf<Quote>()) }
    var currentQuote by remember { mutableStateOf(QuoteRepository.getTodaysQuote()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quote of the Day",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = { currentScreen = "favorites" }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = if (currentScreen == "favorites") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { currentScreen = "home" }) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (currentScreen == "home") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (currentScreen) {
            "home" -> HomeScreen(
                modifier = Modifier.padding(paddingValues),
                currentQuote = currentQuote,
                onRefreshQuote = { currentQuote = QuoteRepository.getRandomQuote() },
                onAddToFavorites = { quote ->
                    if (!favoriteQuotes.contains(quote)) {
                        favoriteQuotes = favoriteQuotes + quote
                    }
                },
                isFavorite = favoriteQuotes.contains(currentQuote)
            )
            "favorites" -> FavoritesScreen(
                modifier = Modifier.padding(paddingValues),
                favoriteQuotes = favoriteQuotes,
                onRemoveFromFavorites = { quote ->
                    favoriteQuotes = favoriteQuotes - quote
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentQuote: Quote,
    onRefreshQuote: () -> Unit,
    onAddToFavorites: (Quote) -> Unit,
    isFavorite: Boolean
) {
    val context = LocalContext.current
    val currentDate = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = currentDate,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quote Card
        AnimatedContent(
            targetState = currentQuote,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
            },
            label = "quote_animation"
        ) { quote ->
            QuoteCard(
                quote = quote,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Refresh Button
            FloatingActionButton(
                onClick = onRefreshQuote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "New Quote",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Favorite Button
            FloatingActionButton(
                onClick = { onAddToFavorites(currentQuote) },
                containerColor = if (isFavorite) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.outline,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Add to Favorites",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Share Button
            FloatingActionButton(
                onClick = {
                    val shareText = "${currentQuote.text}\n\n- ${currentQuote.author}"
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        putExtra(Intent.EXTRA_SUBJECT, "Quote of the Day")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share Quote",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Inspirational message
        Text(
            text = "Start your day with inspiration ✨",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFE2E8F0)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Quote icon - Using a simple text instead of FormatQuote icon
                Text(
                    text = "❝",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quote text
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Author
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                // Category chip
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = quote.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    favoriteQuotes: List<Quote>,
    onRemoveFromFavorites: (Quote) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Favorite Quotes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteQuotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorite quotes yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Add quotes to favorites from the home screen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteQuotes) { quote ->
                    FavoriteQuoteItem(
                        quote = quote,
                        onRemoveFromFavorites = onRemoveFromFavorites
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteQuoteItem(
    quote: Quote,
    onRemoveFromFavorites: (Quote) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "\"${quote.text}\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— ${quote.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        val shareText = "${quote.text}\n\n- ${quote.author}"
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                    }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(
                    onClick = { onRemoveFromFavorites(quote) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove from Favorites",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}