package com.adrianbadarau.klist.front_end.domain

import com.adrianbadarau.klist.front_end.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecipeTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Recipe::class)
        val recipe1 = Recipe()
        recipe1.id = "id1"
        val recipe2 = Recipe()
        recipe2.id = recipe1.id
        assertThat(recipe1).isEqualTo(recipe2)
        recipe2.id = "id2"
        assertThat(recipe1).isNotEqualTo(recipe2)
        recipe1.id = null
        assertThat(recipe1).isNotEqualTo(recipe2)
    }
}
