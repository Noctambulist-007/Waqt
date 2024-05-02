package com.heapiphy101.waqt.data

data class PrayerTimesResponse(
    val city: String, val items: List<PrayerTimesData>
)

data class PrayerTimesData(
    val date_for: String,
    val fajr: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val shurooq: String,
)
