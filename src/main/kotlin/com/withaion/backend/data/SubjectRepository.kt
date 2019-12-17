package com.withaion.backend.data

import com.withaion.backend.models.Subject
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface SubjectRepository : ReactiveMongoRepository<Subject, String> {

}