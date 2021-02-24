package com.adrianbadarau.klist.front_end.repository

import com.adrianbadarau.klist.front_end.domain.PersistentAuditEvent
import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Spring Data MongoDB for the [PersistentAuditEvent] entity.
 */
interface PersistenceAuditEventRepository : MongoRepository<PersistentAuditEvent, String> {

    fun findByPrincipal(principal: String): List<PersistentAuditEvent>

    fun findByPrincipalAndAuditEventDateAfterAndAuditEventType(
        principal: String,
        after: Instant,
        type: String
    ): List<PersistentAuditEvent>

    fun findAllByAuditEventDateBetween(
        fromDate: Instant,
        toDate: Instant,
        pageable: Pageable
    ): Page<PersistentAuditEvent>

    fun findByAuditEventDateBefore(before: Instant): List<PersistentAuditEvent>
}
