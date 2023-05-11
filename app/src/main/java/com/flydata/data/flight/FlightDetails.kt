package com.flydata.data.flight

data class FlightList(
    val aircraft: List<List<String>> = emptyList()
)

data class FlightDetails(
    val identification: Identification? = Identification(),
    val aircraft: Aircraft? = Aircraft(),
    val airline: Airline? = Airline(),
    val airport: Airport? = Airport(),
    val time: Times? = Times(),
    var distance: Float? = 0.0f
)

data class Identification(
    val id: String? = "N/A",
    val callsign: String? = "N/A"
)

data class Aircraft(
    val model: Model? = Model(),
    val images: Images? = Images()
)

data class Model(
    val code: String? = "N/A"
)

data class Images(
    val medium: List<Medium>? = emptyList()
)

data class Medium(
    val src: String? = "N/A"
)

data class Airline(
    val short: String? = "N/A"
)

data class Airport(
    val origin: AirportIdentification? = AirportIdentification(),
    val destination: AirportIdentification? = AirportIdentification()
)

data class AirportIdentification(
    val name: String? = "N/A",
    val code: Code? = Code()
)

data class Code(
    val iata: String = "N/A",
    val icao: String? = "N/A"
)

data class Times(
    val scheduled: Time? = Time(),
    val estimated: Time? = Time()
)

data class Time(
    val departure: Long? = 0,
    val arrival: Long? = 0
)
