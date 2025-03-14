package com.flydata.data.airport

import android.util.Xml
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 * XML parser for dokumenter med flyplass-flyvninger fra Avinor.
 */
class AirportFlightsXmlParser {
    private val ns: String? = null

    /**
     * Hovedfunksjonen for å parse XML-svaret om flyvninger fra en flyplass fra Avinor-API-et.
     *
     * @throws XmlPullParserException ved feil under parsing av XML-dokumentet.
     * @throws IOException ved feil under innlesing av data.
     * @param inputStream input-streamen som skal parses.
     * @return Liste med [AirportFlight].
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): MutableList<AirportFlight> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): MutableList<AirportFlight> {
        val parties = mutableListOf<AirportFlight>()

        parser.require(XmlPullParser.START_TAG, ns, "flights")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "flight") {
                parties.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return parties
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): AirportFlight {
        parser.require(XmlPullParser.START_TAG, ns, "flight")
        var airline: String? = null
        var flightId: String? = null
        var scheduleTime: String? = null
        var arrDep: String? = null
        var airport: String? = null
        var flightStatus: FlightStatus? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "airline" -> airline = readString(parser, "airline")
                "flight_id" -> flightId = readString(parser, "flight_id")
                "schedule_time" -> scheduleTime = readString(parser, "schedule_time")
                "arr_dep" -> arrDep = readString(parser, "arr_dep")
                "airport" -> airport = readString(parser, "airport")
                "status" -> flightStatus = readTime(parser)

                else -> skip(parser)
            }
        }
        var statusText = ""
        var statusTime = ""
        if (flightStatus != null) {
            statusText = flightStatus.status ?: ""
            statusTime = flightStatus.time ?: ""
            statusTime = convertTime(statusTime)
        }
        scheduleTime = convertTime(scheduleTime ?: "")

        return AirportFlight(
            airline ?: "", flightId ?: "", scheduleTime,
            arrDep ?: "", airport ?: "", statusText,
            statusTime
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readString(parser: XmlPullParser, key: String): String {
        parser.require(XmlPullParser.START_TAG, ns, key)
        val newString = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, key)
        return newString
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTime(parser: XmlPullParser): FlightStatus {
        parser.require(XmlPullParser.START_TAG, ns, "status")
        val code: String? = parser.getAttributeValue(null, "code")
        val time: String? = parser.getAttributeValue(null, "time")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, ns, "status")
        return FlightStatus(code, time)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    /**
     * Konverterer tid fra ISO8601-format til "HH:mm"-format.
     *
     * Fungerer kun fra UTC-tid.
     * Krever minimum API-nivå 26.
     *
     * @param time stringen som skal konverteres.
     * @return konvertert tid. Returnerer tom String hvis input var tom String.
     */
    fun convertTime(time: String): String {
        if (time != "") {
            return SimpleDateFormat("HH:mm", Locale("no", "NO"))
                .format(Date(Instant.parse(time).toEpochMilli()))
        }
        return ""
    }
}
