package com.adrianbadarau.klist.items.repository

import com.adrianbadarau.klist.items.domain.Recipe
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for the [Recipe] entity.
 */
@Suppress("unused")
@Repository
interface RecipeRepository : MongoRepository<Recipe, String>
