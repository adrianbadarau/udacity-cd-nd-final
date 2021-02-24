package com.adrianbadarau.klist.items.web.rest

import com.adrianbadarau.klist.items.ItemsApp
import com.adrianbadarau.klist.items.domain.Recipe
import com.adrianbadarau.klist.items.repository.RecipeRepository
import com.adrianbadarau.klist.items.web.rest.errors.ExceptionTranslator
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.Validator

/**
 * Integration tests for the [RecipeResource] REST controller.
 *
 * @see RecipeResource
 */
@SpringBootTest(classes = [ItemsApp::class])
@AutoConfigureMockMvc
@WithMockUser
class RecipeResourceIT {

    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restRecipeMockMvc: MockMvc

    private lateinit var recipe: Recipe

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val recipeResource = RecipeResource(recipeRepository)
         this.restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        recipeRepository.deleteAll()
        recipe = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createRecipe() {
        val databaseSizeBeforeCreate = recipeRepository.findAll().size

        // Create the Recipe
        restRecipeMockMvc.perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipe))
        ).andExpect(status().isCreated)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1)
        val testRecipe = recipeList[recipeList.size - 1]
        assertThat(testRecipe.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    fun createRecipeWithExistingId() {
        val databaseSizeBeforeCreate = recipeRepository.findAll().size

        // Create the Recipe with an existing ID
        recipe.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc.perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipe))
        ).andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = recipeRepository.findAll().size
        // set the field null
        recipe.name = null

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipe))
        ).andExpect(status().isBadRequest)

        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRecipes() {
        // Initialize the database
        recipeRepository.save(recipe)

        // Get all the recipeList
        restRecipeMockMvc.perform(get("/api/recipes?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.id)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME))) }

    @Test
    @Throws(Exception::class)
    fun getRecipe() {
        // Initialize the database
        recipeRepository.save(recipe)

        val id = recipe.id
        assertNotNull(id)

        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.id))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME)) }

    @Test
    @Throws(Exception::class)
    fun getNonExistingRecipe() {
        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    fun updateRecipe() {
        // Initialize the database
        recipeRepository.save(recipe)

        val databaseSizeBeforeUpdate = recipeRepository.findAll().size

        // Update the recipe
        val id = recipe.id
        assertNotNull(id)
        val updatedRecipe = recipeRepository.findById(id).get()
        updatedRecipe.name = UPDATED_NAME

        restRecipeMockMvc.perform(
            put("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedRecipe))
        ).andExpect(status().isOk)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
        val testRecipe = recipeList[recipeList.size - 1]
        assertThat(testRecipe.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    fun updateNonExistingRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            put("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipe))
        ).andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun deleteRecipe() {
        // Initialize the database
        recipeRepository.save(recipe)

        val databaseSizeBeforeDelete = recipeRepository.findAll().size

        // Delete the recipe
        restRecipeMockMvc.perform(
            delete("/api/recipes/{id}", recipe.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Recipe {
            val recipe = Recipe(
                name = DEFAULT_NAME
            )

            return recipe
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Recipe {
            val recipe = Recipe(
                name = UPDATED_NAME
            )

            return recipe
        }
    }
}
