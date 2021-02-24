package com.adrianbadarau.klist.notifications.web.rest

import com.adrianbadarau.klist.notifications.domain.Notification
import com.adrianbadarau.klist.notifications.repository.NotificationRepository
import com.adrianbadarau.klist.notifications.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.reactive.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private const val ENTITY_NAME = "notificationsNotification"
/**
 * REST controller for managing [com.adrianbadarau.klist.notifications.domain.Notification].
 */
@RestController
@RequestMapping("/api")
class NotificationResource(
    private val notificationRepository: NotificationRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /notifications` : Create a new notification.
     *
     * @param notification the notification to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new notification, or with status `400 (Bad Request)` if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notifications")
    fun createNotification(@Valid @RequestBody notification: Notification): Mono<ResponseEntity<Notification>> {
        log.debug("REST request to save Notification : $notification")
        if (notification.id != null) {
            throw BadRequestAlertException(
                "A new notification cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return notificationRepository.save(notification).map { result ->
                try {
                    ResponseEntity.created(URI("/api/notifications/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT  /notifications` : Updates an existing notification.
     *
     * @param notification the notification to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated notification,
     * or with status `400 (Bad Request)` if the notification is not valid,
     * or with status `500 (Internal Server Error)` if the notification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications")
    fun updateNotification(@Valid @RequestBody notification: Notification): Mono<ResponseEntity<Notification>> {
        log.debug("REST request to update Notification : $notification")
        if (notification.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        return notificationRepository.save(notification).switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map { result -> ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.id))
                            .body(result)
                        }
    }
    /**
     * `GET  /notifications` : get all the notifications.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of notifications in body.
     */
    @GetMapping("/notifications")
    fun getAllNotifications(): Mono<MutableList<Notification>> {
        log.debug("REST request to get all Notifications")
                return notificationRepository.findAll().collectList()
    }

    /**
     * `GET  /notifications` : get all the notifications as a stream.
     * @return the [Flux] of notifications.
     */
    @GetMapping(value = ["/notifications"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getAllNotificationsAsStream(): Flux<Notification> {
        log.debug("REST request to get all Notifications as a stream")
        return notificationRepository.findAll() }

    /**
     * `GET  /notifications/:id` : get the "id" notification.
     *
     * @param id the id of the notification to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the notification, or with status `404 (Not Found)`.
     */
    @GetMapping("/notifications/{id}")
    fun getNotification(@PathVariable id: String): Mono<ResponseEntity<Notification>> {
        log.debug("REST request to get Notification : $id")
        val notification = notificationRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(notification)
    }
    /**
     *  `DELETE  /notifications/:id` : delete the "id" notification.
     *
     * @param id the id of the notification to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/notifications/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteNotification(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Notification : $id")
        return notificationRepository.deleteById(id).map {
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build<Void>()
                }
    }
}
