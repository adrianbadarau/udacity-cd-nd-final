package com.adrianbadarau.klist.items.repository

import com.adrianbadarau.klist.items.domain.List
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for the [List] entity.
 */
@Suppress("unused")
@Repository
interface ListRepository : MongoRepository<List, String>
