package com.adrianbadarau.klist.front_end.web.rest

import com.adrianbadarau.klist.front_end.KListApp
import com.adrianbadarau.klist.front_end.domain.Notification
import com.adrianbadarau.klist.front_end.repository.NotificationRepository
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
 * Integration tests for the [NotificationResource] REST controller.
 *
 * @see NotificationResource
 */
@SpringBootTest(classes = [KListApp::class])
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restNotificationMockMvc: MockMvc

    private lateinit var notification: Notification

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val notificationResource = NotificationResource(notificationRepository)
         this.restNotificationMockMvc = MockMvcBuilders.standaloneSetup(notificationResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        notificationRepository.deleteAll()
        notification = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createNotification() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().size

        // Create the Notification
        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isCreated)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.body).isEqualTo(DEFAULT_BODY)
    }

    @Test
    fun createNotificationWithExistingId() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().size

        // Create the Notification with an existing ID
        notification.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    fun checkBodyIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.body = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Throws(Exception::class)
    fun getAllNotifications() {
        // Initialize the database
        notificationRepository.save(notification)

        // Get all the notificationList
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.id)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY))) }

    @Test
    @Throws(Exception::class)
    fun getNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val id = notification.id
        assertNotNull(id)

        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.id))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY)) }

    @Test
    @Throws(Exception::class)
    fun getNonExistingNotification() {
        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    fun updateNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val databaseSizeBeforeUpdate = notificationRepository.findAll().size

        // Update the notification
        val id = notification.id
        assertNotNull(id)
        val updatedNotification = notificationRepository.findById(id).get()
        updatedNotification.body = UPDATED_BODY

        restNotificationMockMvc.perform(
            put("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedNotification))
        ).andExpect(status().isOk)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.body).isEqualTo(UPDATED_BODY)
    }

    @Test
    fun updateNonExistingNotification() {
        val databaseSizeBeforeUpdate = notificationRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc.perform(
            put("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val databaseSizeBeforeDelete = notificationRepository.findAll().size

        // Delete the notification
        restNotificationMockMvc.perform(
            delete("/api/notifications/{id}", notification.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_BODY = "AAAAAAAAAA"
        private const val UPDATED_BODY = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Notification {
            val notification = Notification(
                body = DEFAULT_BODY
            )

            return notification
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Notification {
            val notification = Notification(
                body = UPDATED_BODY
            )

            return notification
        }
    }
}
