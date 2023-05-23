package com.flydata.data.weather

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SigmetDatasource {

    private val client = HttpClient {}
    private val url = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/sigmets/2.0/?type=airmets"

    suspend fun getSigmet(): String {
        return client.get(url) {
            headers {
                append("X-Gravitee-API-Key", "63c8656a-886c-4423-a70c-2937fd41fb5e")
            }
        }.body()
    }
}
