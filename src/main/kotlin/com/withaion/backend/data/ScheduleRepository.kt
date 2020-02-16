package com.withaion.backend.data

import com.sun.java.accessibility.util.EventID
import com.withaion.backend.models.Event
import com.withaion.backend.models.Schedule
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ScheduleRepository : ReactiveMongoRepository<Schedule, String> {

    fun findAllByLocation_Id(locationId: String): Flux<Schedule>
    fun deleteAllByEvent(event: Event): Mono<Void>

}
