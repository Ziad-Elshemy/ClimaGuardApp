package com.example.newsapp.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiManager {
    companion object{
        private var retrofit: Retrofit?=null
        private val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        private fun getInstance():Retrofit{
            if (retrofit==null){
                //Create Retrofit
                val logging = HttpLoggingInterceptor(
                    object :HttpLoggingInterceptor.Logger{
                        override fun log(message: String) {
                            Log.e("api",message)
                        }
                    }
                )
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        fun getApis():WebServices{
            return getInstance().create(WebServices::class.java)
        }

    }
}