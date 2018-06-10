package com.gloot.androidassignment.network

import com.gloot.androidassignment.network.models.Player
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


interface PlayerApi {
    @GET("/players")
    fun getPlayers(): Single<List<Player>>

    @GET("player/{id}")
    fun getPlayer(@Path("id") id:String):Single<Player>

    @Headers("Content-Type: application/json")
    @PUT("player/{id}")
    fun putPlayer(@Path("id") id:String, @Body player:Player):Single<Player>

    @Headers("Content-Type: application/json")
    @POST("player")
    fun addPlayer(@Body player:Player):Single<Player>

    @DELETE("player/{id}")
    fun deletePlayer(@Path("id") id:String):Single<Player>

    companion object {
        val instance: PlayerApi by lazy {
            val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()
            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.HOST)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            retrofit.create(PlayerApi::class.java)
        }
    }
}