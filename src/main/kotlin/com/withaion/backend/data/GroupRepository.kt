package com.withaion.backend.data

import com.withaion.backend.models.Group
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.nio.file.attribute.GroupPrincipal

interface GroupRepository : ReactiveMongoRepository<Group, String> {

}
