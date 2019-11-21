package com.withaion.backend.data

import com.withaion.backend.models.Event
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface EventRepository : ReactiveMongoRepository<Event, String> {

}
