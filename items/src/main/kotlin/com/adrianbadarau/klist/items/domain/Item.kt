package com.adrianbadarau.klist.items.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * A Item.
 */
@Document(collection = "item")
data class Item(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("name")
    var name: String? = null,

    @Field("qty")
    var qty: Int? = null,

    @Field("unit")
    var unit: String? = null,

    @get: NotNull
    @Field("in_cart")
    var inCart: Boolean? = null,

    @Field("image")
    var image: ByteArray? = null,

    @Field("image_content_type")
    var imageContentType: String? = null,

    @DBRef
    @Field("recipe")
    var recipes: MutableSet<Recipe> = mutableSetOf(),

    @DBRef
    @Field("list")
    @JsonIgnoreProperties(value = ["items"], allowSetters = true)
    var list: List? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addRecipe(recipe: Recipe): Item {
        this.recipes.add(recipe)
        recipe.item = this
        return this
    }

    fun removeRecipe(recipe: Recipe): Item {
        this.recipes.remove(recipe)
        recipe.item = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Item) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Item{" +
        "id=$id" +
        ", name='$name'" +
        ", qty=$qty" +
        ", unit='$unit'" +
        ", inCart='$inCart'" +
        ", image='$image'" +
        ", imageContentType='$imageContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
