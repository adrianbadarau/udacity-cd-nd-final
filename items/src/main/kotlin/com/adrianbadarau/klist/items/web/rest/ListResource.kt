package com.adrianbadarau.klist.items.web.rest

import com.adrianbadarau.klist.items.domain.List
import com.adrianbadarau.klist.items.repository.ListRepository
import com.adrianbadarau.klist.items.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "itemsList"
/**
 * REST controller for managing [com.adrianbadarau.klist.items.domain.List].
 */
@RestController
@RequestMapping("/api")
class ListResource(
    private val listRepository: ListRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /lists` : Create a new list.
     *
     * @param list the list to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new list, or with status `400 (Bad Request)` if the list has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lists")
    fun createList(@RequestBody list: List): ResponseEntity<List> {
        log.debug("REST request to save List : $list")
        if (list.id != null) {
            throw BadRequestAlertException(
                "A new list cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = listRepository.save(list)
        return ResponseEntity.created(URI("/api/lists/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
            .body(result)
    }

    /**
     * `PUT  /lists` : Updates an existing list.
     *
     * @param list the list to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated list,
     * or with status `400 (Bad Request)` if the list is not valid,
     * or with status `500 (Internal Server Error)` if the list couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lists")
    fun updateList(@RequestBody list: List): ResponseEntity<List> {
        log.debug("REST request to update List : $list")
        if (list.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = listRepository.save(list)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     list.id
                )
            )
            .body(result)
    }
    /**
     * `GET  /lists` : get all the lists.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of lists in body.
     */
    @GetMapping("/lists")
    fun getAllLists(): MutableList<List> {
        log.debug("REST request to get all Lists")
                return listRepository.findAll()
    }

    /**
     * `GET  /lists/:id` : get the "id" list.
     *
     * @param id the id of the list to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the list, or with status `404 (Not Found)`.
     */
    @GetMapping("/lists/{id}")
    fun getList(@PathVariable id: String): ResponseEntity<List> {
        log.debug("REST request to get List : $id")
        val list = listRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(list)
    }
    /**
     *  `DELETE  /lists/:id` : delete the "id" list.
     *
     * @param id the id of the list to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/lists/{id}")
    fun deleteList(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete List : $id")

        listRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
    }
}
