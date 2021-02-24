package com.adrianbadarau.klist.notifications.repository

import com.adrianbadarau.klist.notifications.domain.Notification
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

 /**
 * Spring Data MongoDB reactive repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
interface NotificationRepository : ReactiveMongoRepository<Notification, String>
