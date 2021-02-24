package com.adrianbadarau.klist.front_end.web.rest

import com.adrianbadarau.klist.front_end.domain.Item
import com.adrianbadarau.klist.front_end.repository.ItemRepository
import com.adrianbadarau.klist.front_end.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "item"
/**
 * REST controller for managing [com.adrianbadarau.klist.front_end.domain.Item].
 */
@RestController
@RequestMapping("/api")
class ItemResource(
    private val itemRepository: ItemRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /items` : Create a new item.
     *
     * @param item the item to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new item, or with status `400 (Bad Request)` if the item has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/items")
    fun createItem(@Valid @RequestBody item: Item): ResponseEntity<Item> {
        log.debug("REST request to save Item : $item")
        if (item.id != null) {
            throw BadRequestAlertException(
                "A new item cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = itemRepository.save(item)
        return ResponseEntity.created(URI("/api/items/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
            .body(result)
    }

    /**
     * `PUT  /items` : Updates an existing item.
     *
     * @param item the item to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated item,
     * or with status `400 (Bad Request)` if the item is not valid,
     * or with status `500 (Internal Server Error)` if the item couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/items")
    fun updateItem(@Valid @RequestBody item: Item): ResponseEntity<Item> {
        log.debug("REST request to update Item : $item")
        if (item.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = itemRepository.save(item)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     item.id
                )
            )
            .body(result)
    }
    /**
     * `GET  /items` : get all the items.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of items in body.
     */
    @GetMapping("/items")
    fun getAllItems(): MutableList<Item> {
        log.debug("REST request to get all Items")
                return itemRepository.findAll()
    }

    /**
     * `GET  /items/:id` : get the "id" item.
     *
     * @param id the id of the item to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the item, or with status `404 (Not Found)`.
     */
    @GetMapping("/items/{id}")
    fun getItem(@PathVariable id: String): ResponseEntity<Item> {
        log.debug("REST request to get Item : $id")
        val item = itemRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(item)
    }
    /**
     *  `DELETE  /items/:id` : delete the "id" item.
     *
     * @param id the id of the item to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/items/{id}")
    fun deleteItem(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete Item : $id")

        itemRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
    }
}
