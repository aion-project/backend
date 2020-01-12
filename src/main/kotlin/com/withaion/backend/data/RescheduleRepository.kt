package com.withaion.backend.data

import com.withaion.backend.models.Event
import com.withaion.backend.models.Location
import com.withaion.backend.models.Reschedule
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface RescheduleRepository : ReactiveMongoRepository<Reschedule, String> {

}
