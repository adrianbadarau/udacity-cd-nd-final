package com.adrianbadarau.klist.front_end.repository

import com.adrianbadarau.klist.front_end.domain.List
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for the [List] entity.
 */
@Suppress("unused")
@Repository
interface ListRepository : MongoRepository<List, String>
