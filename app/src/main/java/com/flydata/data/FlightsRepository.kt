package com.flydata.data

import com.flydata.ui.theme.FlightUi

class FlightsRepository {
    private val liveFlightsDataSource = LiveFlightsDatasource()
    private val airportDatasource = AirportDatasource()

    suspend fun fetchNearestFlight(): FlightUi {
        val listOfFlights = liveFlightsDataSource.fetchFlights()
        val airportFlights = airportDatasource.fetchAirportFlights()

        for (flight in listOfFlights) {
            if ((flight.callsign.substring(0, 3) == "SAS") or
                (flight.callsign.substring(0, 3) == "NOZ")
            ) {
                for (apFlight in airportFlights) {
                    var number = ""
                    if (flight.callsign.substring(0, 3) == "SAS") {
                        number = "SK" + flight.callsign.substring(3)
                    }
                    if (flight.callsign.substring(0, 3) == "NOZ") {
                        number = "DY" + flight.callsign.substring(3)
                    }
                    if (number == apFlight.flightId) {
                        var arrAirport: String?
                        var arrSchedule: String? = "Ukjent"
                        var arrActual: String? = "Ukjent"
                        var depSchedule: String? = "Ukjent"
                        var depActual: String? = "Ukjent"
                        var depAirport: String?
                        if (apFlight.arrDep == "A") {
                            arrAirport = "OSL"
                            arrSchedule = apFlight.scheduleTime
                            arrActual = apFlight.actualTime
                            depAirport = apFlight.airport
                        } else {
                            arrAirport = apFlight.airport
                            depAirport = "OSL"
                            depSchedule = apFlight.scheduleTime
                            depActual = apFlight.actualTime
                        }
                        return FlightUi(
                            apFlight.flightId,
                            depAirport,
                            depSchedule,
                            depActual,
                            arrAirport,
                            arrSchedule,
                            arrActual,
                            flight.distance,
                        )
                    }
                }
            }
        }
        // returneres kun dersom ingen flight blir funnet i Avinor-dataene
        return FlightUi(
            listOfFlights[0].callsign,
            null,
            null,
            null,
            null,
            null,
            null,
            listOfFlights[0].distance,
        )
    }
}
