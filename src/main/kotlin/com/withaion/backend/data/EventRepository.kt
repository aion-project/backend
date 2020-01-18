package com.withaion.backend.data

import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface EventRepository : ReactiveMongoRepository<Event, String> {

}
