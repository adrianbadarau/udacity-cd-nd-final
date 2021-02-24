package com.adrianbadarau.klist.notifications.web.rest

import com.adrianbadarau.klist.notifications.NotificationsApp
import com.adrianbadarau.klist.notifications.domain.Notification
import com.adrianbadarau.klist.notifications.repository.NotificationRepository
import java.time.Duration
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Integration tests for the [NotificationResource] REST controller.
 *
 * @see NotificationResource
 */
@SpringBootTest(classes = [NotificationsApp::class])
@AutoConfigureWebTestClient
@WithMockUser
class NotificationResourceIT {

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var notification: Notification

    @BeforeEach
    fun initTest() {
        notificationRepository.deleteAll().block()
        notification = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createNotification() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size

        // Create the Notification
        webTestClient.post().uri("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus().isCreated

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll().collectList().block()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.body).isEqualTo(DEFAULT_BODY)
    }

    @Test
    fun createNotificationWithExistingId() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size

        // Create the Notification with an existing ID
        notification.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll().collectList().block()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    fun checkBodyIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().collectList().block().size
        // set the field null
        notification.body = null

        // Create the Notification, which fails.

        webTestClient.post().uri("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus().isBadRequest

        val notificationList = notificationRepository.findAll().collectList().block()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun getAllNotificationsAsStream() {
        // Initialize the database
        notificationRepository.save(notification).block()

        val notificationList = webTestClient.get().uri("/api/notifications")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_STREAM_JSON)
            .returnResult(Notification::class.java)
            .responseBody
            .filter(notification::equals)
            .collectList()
            .block(Duration.ofSeconds(5))

        assertThat(notificationList).isNotNull
        assertThat(notificationList).hasSize(1)
        val testNotification = notificationList[0]
        assertThat(testNotification.body).isEqualTo(DEFAULT_BODY)
    }
    @Test

    fun getAllNotifications() {
        // Initialize the database
        notificationRepository.save(notification).block()

        webTestClient.get().uri("/api/notifications?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(notification.id))
            .jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)) }

    @Test

    fun getNotification() {
        // Initialize the database
        notificationRepository.save(notification).block()

        val id = notification.id
        assertNotNull(id)

        // Get the notification
        webTestClient.get().uri("/api/notifications/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(notification.id))
            .jsonPath("$.body").value(`is`(DEFAULT_BODY)) }

    @Test

    fun getNonExistingNotification() {
        // Get the notification
        webTestClient.get().uri("/api/notifications/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun updateNotification() {
        // Initialize the database
        notificationRepository.save(notification).block()

        val databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size

        // Update the notification
        val id = notification.id
        assertNotNull(id)
        val updatedNotification = notificationRepository.findById(id).block()
        updatedNotification.body = UPDATED_BODY

        webTestClient.put().uri("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(updatedNotification))
            .exchange()
            .expectStatus().isOk

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll().collectList().block()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.body).isEqualTo(UPDATED_BODY)
    }

    @Test
    fun updateNonExistingNotification() {
        val databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(notification))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll().collectList().block()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deleteNotification() {
        // Initialize the database
        notificationRepository.save(notification).block()

        val databaseSizeBeforeDelete = notificationRepository.findAll().collectList().block().size

        webTestClient.delete().uri("/api/notifications/{id}", notification.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val notificationList = notificationRepository.findAll().collectList().block()
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
