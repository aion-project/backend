package com.withaion.backend.data

import com.withaion.backend.models.Location
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface LocationRepository : ReactiveMongoRepository<Location, String> {

}