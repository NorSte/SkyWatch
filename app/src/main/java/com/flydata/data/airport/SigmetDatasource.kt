package com.flydata.data.airport

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SigmetDatasource {

    private val client = HttpClient {}
    private val url = "https://api.met.no/weatherapi/sigmets/2.0/?type=airmets"

    suspend fun getSigmet(): String {
        val response: String = client.get(url).body()

        return response
    }
}
