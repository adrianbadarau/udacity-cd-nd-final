package com.adrianbadarau.klist.items.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * A Recipe.
 */
@Document(collection = "recipe")
data class Recipe(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("name")
    var name: String? = null,

    @DBRef
    @Field("list")
    @JsonIgnoreProperties(value = ["recipes"], allowSetters = true)
    var list: List? = null,

    @DBRef
    @Field("item")
    @JsonIgnoreProperties(value = ["recipes"], allowSetters = true)
    var item: Item? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Recipe) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Recipe{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
