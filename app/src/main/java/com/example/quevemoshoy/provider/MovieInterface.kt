package com.example.quevemoshoy.provider

import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MovieResponse
import com.example.quevemoshoy.model.ProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieInterface {

    @GET("/3/discover/movie")
    suspend fun getMoviesByGenres(
        @Query("api_key") apiKey: String = "2bb0ba9a57e9cdae9dd4f957fd27140a",
        @Query("with_genres") genres: String,
        @Query("language") language: String = "es-ES",
        @Query("sort_by") sortBy: String = "vote_average.desc",
        @Query("vote_count.gte") voteCount: Int = 100

    ): MovieResponse

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("language") language: String = "es-ES"
    ): Movie

    @GET("/3/movie/{movie_id}/watch/providers")
    suspend fun getMovieProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("region") region: String = "ES"
    ): ProvidersResponse



}