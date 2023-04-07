package com.flydata.data

import android.util.Xml
import java.io.IOException
import java.io.InputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class AirportXmlParser {

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<AirportFlight> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<AirportFlight> {
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
        var flight_id: String? = null
        var schedule_time: String? = null
        var arr_dep: String? = null
        var airport: String? = null
        var time: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "airline" -> airline = readString(parser, "airline")
                "flight_id" -> flight_id = readString(parser, "flight_id")
                "schedule_time" -> schedule_time = readString(parser, "schedule_time")
                "arr_dep" -> arr_dep = readString(parser, "arr_dep")
                "airport" -> airport = readString(parser, "airport")
                "status" -> time = readTime(parser)

                else -> skip(parser)
            }
        }
        schedule_time = convertTime(schedule_time)
        time = convertTime(time)
        return AirportFlight(airline, flight_id, schedule_time, arr_dep, airport, time)
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
    private fun readTime(parser: XmlPullParser): String {
        var time = ""
        parser.require(XmlPullParser.START_TAG, ns, "status")
        time = parser.getAttributeValue(null, "time")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, ns, "status")
        return time
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
    // fjerner dato og sekunder fra UTC-tid, og legger til 2 timer (tilsvarer sommertid i Norge)
    private fun convertTime(time: String?): String? {
        if (time != null) {
            var localTime = time.substring(11, 16)
            var hrs: Int = localTime.substring(0, 2).toInt()
            hrs = (hrs + 2) % 24
            return hrs.toString() + ":" + localTime.substring(3, 5)
        }
        return null
    }
}
