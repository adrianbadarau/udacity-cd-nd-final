package com.adrianbadarau.klist.items.domain

import java.io.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * A List.
 */
@Document(collection = "list")
data class List(
    @Id
    var id: String? = null,
    @Field("name")
    var name: String? = null,

    @DBRef
    @Field("item")
    var items: MutableSet<Item> = mutableSetOf(),

    @DBRef
    @Field("recipe")
    var recipes: MutableSet<Recipe> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addItem(item: Item): List {
        this.items.add(item)
        item.list = this
        return this
    }

    fun removeItem(item: Item): List {
        this.items.remove(item)
        item.list = null
        return this
    }

    fun addRecipe(recipe: Recipe): List {
        this.recipes.add(recipe)
        recipe.list = this
        return this
    }

    fun removeRecipe(recipe: Recipe): List {
        this.recipes.remove(recipe)
        recipe.list = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is List) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "List{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
