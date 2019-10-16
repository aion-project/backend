package com.withaion.backend.data

import com.withaion.backend.models.Resource
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ResourceRepository : ReactiveMongoRepository<Resource, String> {
}
