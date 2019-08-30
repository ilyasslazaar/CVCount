package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.service.StopWordService;
import io.novelis.filtragecv.web.rest.errors.BadRequestAlertException;
import io.novelis.filtragecv.service.dto.StopWordDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link io.novelis.filtragecv.domain.StopWord}.
 */
@RestController
@RequestMapping("/api")
public class StopWordResource {

    private final Logger log = LoggerFactory.getLogger(StopWordResource.class);

    private static final String ENTITY_NAME = "filtragecvStopWord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StopWordService stopWordService;

    public StopWordResource(StopWordService stopWordService) {
        this.stopWordService = stopWordService;
    }

    /**
     * {@code POST  /stop-words} : Create a new stopWord.
     *
     * @param stopWordDTO the stopWordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stopWordDTO, or with status {@code 400 (Bad Request)} if the stopWord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stop-words")
    public ResponseEntity<StopWordDTO> createStopWord(@Valid @RequestBody StopWordDTO stopWordDTO) throws URISyntaxException {
        log.debug("REST request to save StopWord : {}", stopWordDTO);
        if (stopWordDTO.getId() != null) {
            throw new BadRequestAlertException("A new stopWord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StopWordDTO result = stopWordService.save(stopWordDTO);
        return ResponseEntity.created(new URI("/api/stop-words/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stop-words} : Updates an existing stopWord.
     *
     * @param stopWordDTO the stopWordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stopWordDTO,
     * or with status {@code 400 (Bad Request)} if the stopWordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stopWordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stop-words")
    public ResponseEntity<StopWordDTO> updateStopWord(@Valid @RequestBody StopWordDTO stopWordDTO) throws URISyntaxException {
        log.debug("REST request to update StopWord : {}", stopWordDTO);
        if (stopWordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        StopWordDTO result = stopWordService.save(stopWordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stopWordDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /stop-words} : get all the stopWords.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stopWords in body.
     */
    @GetMapping("/stop-words")
    public List<StopWordDTO> getAllStopWords() {
        log.debug("REST request to get all StopWords");
        return stopWordService.findAll();
    }

    /**
     * {@code GET  /stop-words/:id} : get the "id" stopWord.
     *
     * @param id the id of the stopWordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stopWordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stop-words/{id}")
    public ResponseEntity<StopWordDTO> getStopWord(@PathVariable Long id) {
        log.debug("REST request to get StopWord : {}", id);
        Optional<StopWordDTO> stopWordDTO = stopWordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stopWordDTO);
    }

    /**
     * {@code DELETE  /stop-words/:id} : delete the "id" stopWord.
     *
     * @param id the id of the stopWordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stop-words/{id}")
    public ResponseEntity<Void> deleteStopWord(@PathVariable Long id) {
        log.debug("REST request to delete StopWord : {}", id);
        stopWordService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
