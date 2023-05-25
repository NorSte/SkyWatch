package com.flydata.data.weather

import android.util.Xml
import java.io.IOException
import java.io.InputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 * XML parser for dokumenter med METAR-værmeldinger fra MET.
 */
class MetarXmlParser {
    private val ns: String? = null

    /**
     * Hovedfunksjonen for å parse XML-svaret om METAR-værmeldinger.
     *
     * @throws XmlPullParserException ved feil under parsing av XML-dokumentet.
     * @throws IOException ved feil under innlesing av data.
     * @param inputStream input-streamen som skal parses.
     * @return Liste med [Metar]-værmeldinger.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream?): List<Metar> {
        inputStream.use { stream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Metar> {
        val entries = mutableListOf<Metar>()

        parser.require(XmlPullParser.START_TAG, ns, "metno:aviationProducts")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
            if (parser.name == "metno:meteorologicalAerodromeReport") {
                entries.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Metar {
        parser.require(XmlPullParser.START_TAG, ns, "metno:meteorologicalAerodromeReport")
        var title: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "metno:metarText" -> title = readTitle(parser)
                else -> skip(parser)
            }
        }
        return Metar(title)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "metno:metarText")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "metno:metarText")
        return title
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
}

/**
 * Dataklasse for METAR-værmeldinger.
 *
 * @property metarText METAR-melding.
 */
data class Metar(val metarText: String?)
