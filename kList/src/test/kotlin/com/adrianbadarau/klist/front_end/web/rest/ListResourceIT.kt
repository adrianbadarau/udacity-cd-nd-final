package com.adrianbadarau.klist.front_end.web.rest

import com.adrianbadarau.klist.front_end.KListApp
import com.adrianbadarau.klist.front_end.domain.List
import com.adrianbadarau.klist.front_end.repository.ListRepository
import com.adrianbadarau.klist.front_end.web.rest.errors.ExceptionTranslator
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
 * Integration tests for the [ListResource] REST controller.
 *
 * @see ListResource
 */
@SpringBootTest(classes = [KListApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ListResourceIT {

    @Autowired
    private lateinit var listRepository: ListRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restListMockMvc: MockMvc

    private lateinit var list: List

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val listResource = ListResource(listRepository)
         this.restListMockMvc = MockMvcBuilders.standaloneSetup(listResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        listRepository.deleteAll()
        list = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createList() {
        val databaseSizeBeforeCreate = listRepository.findAll().size

        // Create the List
        restListMockMvc.perform(
            post("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(list))
        ).andExpect(status().isCreated)

        // Validate the List in the database
        val listList = listRepository.findAll()
        assertThat(listList).hasSize(databaseSizeBeforeCreate + 1)
        val testList = listList[listList.size - 1]
        assertThat(testList.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    fun createListWithExistingId() {
        val databaseSizeBeforeCreate = listRepository.findAll().size

        // Create the List with an existing ID
        list.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        restListMockMvc.perform(
            post("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(list))
        ).andExpect(status().isBadRequest)

        // Validate the List in the database
        val listList = listRepository.findAll()
        assertThat(listList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Throws(Exception::class)
    fun getAllLists() {
        // Initialize the database
        listRepository.save(list)

        // Get all the listList
        restListMockMvc.perform(get("/api/lists?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(list.id)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME))) }

    @Test
    @Throws(Exception::class)
    fun getList() {
        // Initialize the database
        listRepository.save(list)

        val id = list.id
        assertNotNull(id)

        // Get the list
        restListMockMvc.perform(get("/api/lists/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(list.id))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME)) }

    @Test
    @Throws(Exception::class)
    fun getNonExistingList() {
        // Get the list
        restListMockMvc.perform(get("/api/lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    fun updateList() {
        // Initialize the database
        listRepository.save(list)

        val databaseSizeBeforeUpdate = listRepository.findAll().size

        // Update the list
        val id = list.id
        assertNotNull(id)
        val updatedList = listRepository.findById(id).get()
        updatedList.name = UPDATED_NAME

        restListMockMvc.perform(
            put("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedList))
        ).andExpect(status().isOk)

        // Validate the List in the database
        val listList = listRepository.findAll()
        assertThat(listList).hasSize(databaseSizeBeforeUpdate)
        val testList = listList[listList.size - 1]
        assertThat(testList.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    fun updateNonExistingList() {
        val databaseSizeBeforeUpdate = listRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restListMockMvc.perform(
            put("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(list))
        ).andExpect(status().isBadRequest)

        // Validate the List in the database
        val listList = listRepository.findAll()
        assertThat(listList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun deleteList() {
        // Initialize the database
        listRepository.save(list)

        val databaseSizeBeforeDelete = listRepository.findAll().size

        // Delete the list
        restListMockMvc.perform(
            delete("/api/lists/{id}", list.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val listList = listRepository.findAll()
        assertThat(listList).hasSize(databaseSizeBeforeDelete - 1)
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
        fun createEntity(): List {
            val list = List(
                name = DEFAULT_NAME
            )

            return list
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): List {
            val list = List(
                name = UPDATED_NAME
            )

            return list
        }
    }
}
