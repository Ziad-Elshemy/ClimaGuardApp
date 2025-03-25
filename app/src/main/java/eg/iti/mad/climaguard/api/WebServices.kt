package com.example.newsapp.api


import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServices {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): CurrentResponse

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): ForecastResponse

}