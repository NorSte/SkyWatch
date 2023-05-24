package com.flydata.data.flight

/**
 * Dataklasse som inneholder en liste over fly-vektorer.
 * Kommer fra API-uthenting fra Avinor.
 *
 * @property aircraft liste over fly-vektorer.
 */
data class FlightList(
    val aircraft: List<List<String>> = emptyList()
)

/**
 * Dataklasse som inneholder detaljert informasjon om en flyvning.
 * Brukes til framvisning av flyvninger.
 *
 * @property identification [Identification]-objekt med kode og navn.
 * @property aircraft [Aircraft]-objekt med flytype og bilde-URL-er.
 * @property airline [Airline]-objekt med navn på flyselskap.
 * @property airport [Airport]-objekt med informasjon om avgang- og ankomstflyplass.
 * @property time [Times]-objekt med planlagt og forventet avgang- og ankomsttid.
 * @property distance avstand fra brukeren til flyet (settes ikke automatisk av API-et).
 */
data class FlightDetails(
    val identification: Identification? = Identification(),
    val aircraft: Aircraft? = Aircraft(),
    val airline: Airline? = Airline(),
    val airport: Airport? = Airport(),
    val time: Times? = Times(),
    var distance: Float? = -1.0f
)

/**
 * Dataklasse som inneholder identifikasjon for en flyvvning.
 * Brukes som del av [FlightDetails]-objekt.
 *
 * @property id iata-kode.
 * @property callsign flight-nummeret.
 */
data class Identification(
    val id: String? = "N/A",
    val callsign: String? = "N/A"
)

/**
 * Dataklasse som inneholder ekstrainformasjon om selve flyet.
 * Brukes som del av [FlightDetails]-objekt.
 *
 * @property model inneholder modellkoden til flyet.
 * @property images inneholder en liste av bilde-URL-er av et fly.
 */
data class Aircraft(
    val model: Model? = Model(),
    val images: Images? = Images()
)

/**
 * Dataklasse som inneholder modellkoden til et fly.
 * Brukes som del av [Aircraft]-objekt.
 *
 * @property code modellkoden.
 */
data class Model(
    val code: String? = "N/A"
)

/**
 * Dataklasse som inneholder en liste av bilde-URL-er av et fly.
 * Brukes som del av [Aircraft]-objekt.
 *
 * @property medium liste over bilde-URL-er av et fly (medium størrelse).
 */
data class Images(
    val medium: List<Medium>? = emptyList()
)

/**
 * Dataklasse som inneholder en bilde-URL av et fly.
 * Brukes som del av [Images]-objekt.
 *
 * @property src bilde-URL.
 */
data class Medium(
    val src: String? = "N/A"
)

/**
 * Dataklasse som inneholder navnet til et flyselskap.
 * Brukes som del av [FlightDetails]-objekt.
 *
 * @property short flyselskapnavn.
 */
data class Airline(
    val short: String? = "N/A"
)

/**
 * Dataklasse som inneholder informasjon om avgangs- og ankomstflyplassen til en flyvning
 * Brukes som del av [FlightDetails]-objekt.
 *
 * @property origin identifikasjonen til avgangsflyplass.
 * @property destination identifikasjonen til ankomstflyplass.
 */
data class Airport(
    val origin: AirportIdentification? = AirportIdentification(),
    val destination: AirportIdentification? = AirportIdentification()
)

/**
 * Dataklasse som inneholder identifikasjonen til en flyplass.
 * Brukes som del av [Airport]-objekt.
 *
 * @property name navnet på flyplassen.
 * @property code koden til flyplassen.
 */
data class AirportIdentification(
    val name: String? = "N/A",
    val code: Code? = Code()
)

/**
 * Dataklasse som inneholder iata- og icao24-koden til en luftfarts-entitet.
 * Brukes som del av [AirportIdentification]-objekt.
 *
 * @property iata IATA-koden.
 * @property icao ICAO24-koden.
 */
data class Code(
    val iata: String = "N/A",
    val icao: String? = "N/A"
)

/**
 * Dataklasse som ineholder forventet og estimert tid for avgang og ankomst.
 * Brukes som del av [FlightDetails]-objekt.
 *
 * @property scheduled planlagt tid.
 * @property estimated forventet tid.
 */
data class Times(
    val scheduled: Time? = Time(),
    val estimated: Time? = Time()
)

/**
 * Dataklasse som inneholder tid for avgang og ankomst.
 * Brukes som del av [Times]-objekt.
 *
 * @property departure avgangstid.
 * @property arrival ankomsttid.
 */
data class Time(
    val departure: Long? = 0,
    val arrival: Long? = 0
)
