package com.flydata.ui.flightCard

/**
 * Dataklasse som inneholder informasjon om en rutetid.
 * Brukes i [TimeTables].
 *
 * @property expected forventet tid.
 * @property actual faktisk tid.
 */
data class TimeTable(val expected: Long, val actual: Long)

/**
 * Dataklasse som inneholder infomrasjon om avgang- og ankomstrutetider.
 * Brukes i [FlightCard]
 *
 * @property origin avgangstider.
 * @property destination ankomsttider.
 */
data class TimeTables(val origin: TimeTable, val destination: TimeTable)
