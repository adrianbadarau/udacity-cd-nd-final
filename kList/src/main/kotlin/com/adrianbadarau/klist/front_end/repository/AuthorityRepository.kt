package com.adrianbadarau.klist.front_end.repository

import com.adrianbadarau.klist.front_end.domain.Authority
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Spring Data MongoDB repository for the [Authority] entity.
 */

interface AuthorityRepository : MongoRepository<Authority, String>
