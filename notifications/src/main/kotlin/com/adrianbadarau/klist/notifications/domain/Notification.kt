package com.adrianbadarau.klist.notifications.domain

import java.io.Serializable
import javax.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * A Notification.
 */
@Document(collection = "notification")
data class Notification(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("body")
    var body: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Notification{" +
        "id=$id" +
        ", body='$body'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
