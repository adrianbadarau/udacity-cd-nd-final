package com.adrianbadarau.klist.notifications.domain

import com.adrianbadarau.klist.notifications.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotificationTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Notification::class)
        val notification1 = Notification()
        notification1.id = "id1"
        val notification2 = Notification()
        notification2.id = notification1.id
        assertThat(notification1).isEqualTo(notification2)
        notification2.id = "id2"
        assertThat(notification1).isNotEqualTo(notification2)
        notification1.id = null
        assertThat(notification1).isNotEqualTo(notification2)
    }
}
