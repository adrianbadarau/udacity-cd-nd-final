package com.adrianbadarau.klist.items.web.rest

import com.adrianbadarau.klist.items.ItemsApp
import com.adrianbadarau.klist.items.domain.Item
import com.adrianbadarau.klist.items.repository.ItemRepository
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
import org.springframework.util.Base64Utils
import org.springframework.validation.Validator

/**
 * Integration tests for the [ItemResource] REST controller.
 *
 * @see ItemResource
 */
@SpringBootTest(classes = [ItemsApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ItemResourceIT {

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restItemMockMvc: MockMvc

    private lateinit var item: Item

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val itemResource = ItemResource(itemRepository)
         this.restItemMockMvc = MockMvcBuilders.standaloneSetup(itemResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        itemRepository.deleteAll()
        item = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createItem() {
        val databaseSizeBeforeCreate = itemRepository.findAll().size

        // Create the Item
        restItemMockMvc.perform(
            post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(item))
        ).andExpect(status().isCreated)

        // Validate the Item in the database
        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeCreate + 1)
        val testItem = itemList[itemList.size - 1]
        assertThat(testItem.name).isEqualTo(DEFAULT_NAME)
        assertThat(testItem.qty).isEqualTo(DEFAULT_QTY)
        assertThat(testItem.unit).isEqualTo(DEFAULT_UNIT)
        assertThat(testItem.inCart).isEqualTo(DEFAULT_IN_CART)
        assertThat(testItem.image).isEqualTo(DEFAULT_IMAGE)
        assertThat(testItem.imageContentType).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE)
    }

    @Test
    fun createItemWithExistingId() {
        val databaseSizeBeforeCreate = itemRepository.findAll().size

        // Create the Item with an existing ID
        item.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemMockMvc.perform(
            post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(item))
        ).andExpect(status().isBadRequest)

        // Validate the Item in the database
        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = itemRepository.findAll().size
        // set the field null
        item.name = null

        // Create the Item, which fails.

        restItemMockMvc.perform(
            post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(item))
        ).andExpect(status().isBadRequest)

        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkInCartIsRequired() {
        val databaseSizeBeforeTest = itemRepository.findAll().size
        // set the field null
        item.inCart = null

        // Create the Item, which fails.

        restItemMockMvc.perform(
            post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(item))
        ).andExpect(status().isBadRequest)

        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Throws(Exception::class)
    fun getAllItems() {
        // Initialize the database
        itemRepository.save(item)

        // Get all the itemList
        restItemMockMvc.perform(get("/api/items?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.id)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].inCart").value(hasItem(DEFAULT_IN_CART)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))) }

    @Test
    @Throws(Exception::class)
    fun getItem() {
        // Initialize the database
        itemRepository.save(item)

        val id = item.id
        assertNotNull(id)

        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(item.id))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.inCart").value(DEFAULT_IN_CART))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE))) }

    @Test
    @Throws(Exception::class)
    fun getNonExistingItem() {
        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    fun updateItem() {
        // Initialize the database
        itemRepository.save(item)

        val databaseSizeBeforeUpdate = itemRepository.findAll().size

        // Update the item
        val id = item.id
        assertNotNull(id)
        val updatedItem = itemRepository.findById(id).get()
        updatedItem.name = UPDATED_NAME
        updatedItem.qty = UPDATED_QTY
        updatedItem.unit = UPDATED_UNIT
        updatedItem.inCart = UPDATED_IN_CART
        updatedItem.image = UPDATED_IMAGE
        updatedItem.imageContentType = UPDATED_IMAGE_CONTENT_TYPE

        restItemMockMvc.perform(
            put("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedItem))
        ).andExpect(status().isOk)

        // Validate the Item in the database
        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate)
        val testItem = itemList[itemList.size - 1]
        assertThat(testItem.name).isEqualTo(UPDATED_NAME)
        assertThat(testItem.qty).isEqualTo(UPDATED_QTY)
        assertThat(testItem.unit).isEqualTo(UPDATED_UNIT)
        assertThat(testItem.inCart).isEqualTo(UPDATED_IN_CART)
        assertThat(testItem.image).isEqualTo(UPDATED_IMAGE)
        assertThat(testItem.imageContentType).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE)
    }

    @Test
    fun updateNonExistingItem() {
        val databaseSizeBeforeUpdate = itemRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemMockMvc.perform(
            put("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(item))
        ).andExpect(status().isBadRequest)

        // Validate the Item in the database
        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun deleteItem() {
        // Initialize the database
        itemRepository.save(item)

        val databaseSizeBeforeDelete = itemRepository.findAll().size

        // Delete the item
        restItemMockMvc.perform(
            delete("/api/items/{id}", item.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val itemList = itemRepository.findAll()
        assertThat(itemList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_QTY: Int = 1
        private const val UPDATED_QTY: Int = 2

        private const val DEFAULT_UNIT = "AAAAAAAAAA"
        private const val UPDATED_UNIT = "BBBBBBBBBB"

        private const val DEFAULT_IN_CART: Boolean = false
        private const val UPDATED_IN_CART: Boolean = true

        private val DEFAULT_IMAGE: ByteArray = createByteArray(1, "0")
        private val UPDATED_IMAGE: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_IMAGE_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_IMAGE_CONTENT_TYPE: String = "image/png"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Item {
            val item = Item(
                name = DEFAULT_NAME,
                qty = DEFAULT_QTY,
                unit = DEFAULT_UNIT,
                inCart = DEFAULT_IN_CART,
                image = DEFAULT_IMAGE,
                imageContentType = DEFAULT_IMAGE_CONTENT_TYPE
            )

            return item
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Item {
            val item = Item(
                name = UPDATED_NAME,
                qty = UPDATED_QTY,
                unit = UPDATED_UNIT,
                inCart = UPDATED_IN_CART,
                image = UPDATED_IMAGE,
                imageContentType = UPDATED_IMAGE_CONTENT_TYPE
            )

            return item
        }
    }
}
