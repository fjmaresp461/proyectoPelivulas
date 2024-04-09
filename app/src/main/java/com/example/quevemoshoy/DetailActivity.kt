package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityDetailBinding
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.model.Providers
import com.example.quevemoshoy.model.SimpleMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var movieTitle: String? = null
    private val dbManager =DatabaseManager()
    private var movieManager = MoviesManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        fetchAndDisplayMovieDetails(movieId)

        setListeners(movieId)
    }

    private fun setListeners(movieId: Int) {
        binding.ibTmdb.setOnClickListener {
            val url = "https://www.themoviedb.org/movie/$movieId?language=es"
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }
        binding.swFav.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addMovieToFavorites(movieId, movieTitle!!)

            } else {
                removeMoviefromFavorites(movieId)
            }
        }
    }

    private fun fetchAndDisplayMovieDetails(movieId: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val movie = withContext(Dispatchers.IO) { movieManager.fetchMovieById(movieId) }
            if (movie != null) {
                displayMovieDetails(movie)
                movieTitle = movie.title
                val isFavorite = dbManager.readAll().any { it.id == movieId }
                binding.swFav.isChecked = isFavorite
 val providers = withContext(Dispatchers.IO) { movieManager.fetchMovieProviders(movieId) }
                displayProviders(providers)
            } else {
                Toast.makeText(
                    this@DetailActivity,
                    R.string.movie_not_found,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun displayProviders(providers: List<Providers>?) {
        binding.providersLayout.removeAllViews()
        if (providers.isNullOrEmpty()) {
            Toast.makeText(this, R.string.movie_dont_available, Toast.LENGTH_SHORT).show()
        } else {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Plataformas disponibles: ")
            providers.forEach { provider ->
                stringBuilder.append(provider.providerName).append(", ")
            }
            // Elimina la última coma y espacio
            stringBuilder.setLength(stringBuilder.length - 2)
            Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun displayMovieDetails(movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.tvSummary.text = movie.overview
        val genreNames = movie.genres?.joinToString { it.name } ?: "N/A"
        binding.tvGenres.text = genreNames
        binding.tvRuntime.text = "${movie.runtime} minutos."
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.movieImg)
    }

    private fun addMovieToFavorites(movieId: Int, movieTitle: String) {
        val dbManager = DatabaseManager()
        val movie = SimpleMovie( movieId,movieTitle)
        dbManager.create(movie)
        Toast.makeText(this, R.string.favorite, Toast.LENGTH_SHORT).show()
    }

    private fun removeMoviefromFavorites(movieId: Int) {
        val dbManager = DatabaseManager()
        dbManager.delete(movieId)
        Toast.makeText(this, R.string.delete_from_favorite, Toast.LENGTH_SHORT).show()
    }
}