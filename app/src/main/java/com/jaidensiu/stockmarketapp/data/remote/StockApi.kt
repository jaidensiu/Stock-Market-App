package com.jaidensiu.stockmarketapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET(value = "query?function=LSITING_STATUS")
    suspend fun getListing(
        @Query(value = "apiKey") apiKey: String
    ): ResponseBody

    companion object {
        const val API_KEY = "J755VDSADDSAAJ23"
        const val BASE_URL = "https://alphavantage.co"
    }
}
