package com.heapiphy101.waqt.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimesService {
    @GET("location.json")
    suspend fun getPrayerTimes(
        @Query("query") location: String, @Query("key") apiKey: String
    ): Response<PrayerTimesResponse>
}
