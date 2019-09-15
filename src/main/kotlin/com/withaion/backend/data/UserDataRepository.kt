package com.withaion.backend.data

import com.withaion.backend.models.UserData
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDataRepository : ReactiveMongoRepository<UserData, String>