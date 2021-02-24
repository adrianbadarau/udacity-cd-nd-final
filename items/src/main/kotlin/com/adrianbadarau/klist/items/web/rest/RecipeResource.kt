package com.adrianbadarau.klist.items.web.rest

import com.adrianbadarau.klist.items.domain.Recipe
import com.adrianbadarau.klist.items.repository.RecipeRepository
import com.adrianbadarau.klist.items.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "itemsRecipe"
/**
 * REST controller for managing [com.adrianbadarau.klist.items.domain.Recipe].
 */
@RestController
@RequestMapping("/api")
class RecipeResource(
    private val recipeRepository: RecipeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /recipes` : Create a new recipe.
     *
     * @param recipe the recipe to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new recipe, or with status `400 (Bad Request)` if the recipe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipes")
    fun createRecipe(@Valid @RequestBody recipe: Recipe): ResponseEntity<Recipe> {
        log.debug("REST request to save Recipe : $recipe")
        if (recipe.id != null) {
            throw BadRequestAlertException(
                "A new recipe cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = recipeRepository.save(recipe)
        return ResponseEntity.created(URI("/api/recipes/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
            .body(result)
    }

    /**
     * `PUT  /recipes` : Updates an existing recipe.
     *
     * @param recipe the recipe to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated recipe,
     * or with status `400 (Bad Request)` if the recipe is not valid,
     * or with status `500 (Internal Server Error)` if the recipe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipes")
    fun updateRecipe(@Valid @RequestBody recipe: Recipe): ResponseEntity<Recipe> {
        log.debug("REST request to update Recipe : $recipe")
        if (recipe.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = recipeRepository.save(recipe)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     recipe.id
                )
            )
            .body(result)
    }
    /**
     * `GET  /recipes` : get all the recipes.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of recipes in body.
     */
    @GetMapping("/recipes")
    fun getAllRecipes(): MutableList<Recipe> {
        log.debug("REST request to get all Recipes")
                return recipeRepository.findAll()
    }

    /**
     * `GET  /recipes/:id` : get the "id" recipe.
     *
     * @param id the id of the recipe to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the recipe, or with status `404 (Not Found)`.
     */
    @GetMapping("/recipes/{id}")
    fun getRecipe(@PathVariable id: String): ResponseEntity<Recipe> {
        log.debug("REST request to get Recipe : $id")
        val recipe = recipeRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(recipe)
    }
    /**
     *  `DELETE  /recipes/:id` : delete the "id" recipe.
     *
     * @param id the id of the recipe to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/recipes/{id}")
    fun deleteRecipe(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete Recipe : $id")

        recipeRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
    }
}
