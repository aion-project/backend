package com.withaion.backend.data

import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface EventRepository : ReactiveMongoRepository<Event, String> {

    fun findAllByLocation_Id(locationId: String): Flux<Event>
//    fun findAllByLocation_IdAndStart(locationId: String): Flux<Event>

}
