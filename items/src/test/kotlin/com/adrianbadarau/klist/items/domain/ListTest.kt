package com.adrianbadarau.klist.items.domain

import com.adrianbadarau.klist.items.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(List::class)
        val list1 = List()
        list1.id = "id1"
        val list2 = List()
        list2.id = list1.id
        assertThat(list1).isEqualTo(list2)
        list2.id = "id2"
        assertThat(list1).isNotEqualTo(list2)
        list1.id = null
        assertThat(list1).isNotEqualTo(list2)
    }
}
