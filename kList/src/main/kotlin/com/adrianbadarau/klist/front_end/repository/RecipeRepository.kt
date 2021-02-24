package com.adrianbadarau.klist.front_end.repository

import com.adrianbadarau.klist.front_end.domain.Recipe
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for the [Recipe] entity.
 */
@Suppress("unused")
@Repository
interface RecipeRepository : MongoRepository<Recipe, String>
