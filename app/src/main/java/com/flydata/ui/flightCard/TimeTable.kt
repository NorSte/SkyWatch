package com.flydata.ui.flightCard

data class TimeTable(val expected: Long, val actual: Long)
data class TimeTables(val origin: TimeTable, val destination: TimeTable)
