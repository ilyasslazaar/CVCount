package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.service.SchoolSynonymService;
import io.novelis.filtragecv.web.rest.errors.BadRequestAlertException;
import io.novelis.filtragecv.service.dto.SchoolSynonymDTO;

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
 * REST controller for managing {@link io.novelis.filtragecv.domain.SchoolSynonym}.
 */
@RestController
@RequestMapping("/api")
public class SchoolSynonymResource {

    private final Logger log = LoggerFactory.getLogger(SchoolSynonymResource.class);

    private static final String ENTITY_NAME = "filtragecvSchoolSynonym";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SchoolSynonymService schoolSynonymService;

    public SchoolSynonymResource(SchoolSynonymService schoolSynonymService) {
        this.schoolSynonymService = schoolSynonymService;
    }

    /**
     * {@code POST  /school-synonyms} : Create a new schoolSynonym.
     *
     * @param schoolSynonymDTO the schoolSynonymDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new schoolSynonymDTO, or with status {@code 400 (Bad Request)} if the schoolSynonym has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/school-synonyms")
    public ResponseEntity<SchoolSynonymDTO> createSchoolSynonym(@Valid @RequestBody SchoolSynonymDTO schoolSynonymDTO) throws URISyntaxException {
        log.debug("REST request to save SchoolSynonym : {}", schoolSynonymDTO);
        if (schoolSynonymDTO.getId() != null) {
            throw new BadRequestAlertException("A new schoolSynonym cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SchoolSynonymDTO result = schoolSynonymService.save(schoolSynonymDTO);
        return ResponseEntity.created(new URI("/api/school-synonyms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /school-synonyms} : Updates an existing schoolSynonym.
     *
     * @param schoolSynonymDTO the schoolSynonymDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schoolSynonymDTO,
     * or with status {@code 400 (Bad Request)} if the schoolSynonymDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the schoolSynonymDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/school-synonyms")
    public ResponseEntity<SchoolSynonymDTO> updateSchoolSynonym(@Valid @RequestBody SchoolSynonymDTO schoolSynonymDTO) throws URISyntaxException {
        log.debug("REST request to update SchoolSynonym : {}", schoolSynonymDTO);
        if (schoolSynonymDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SchoolSynonymDTO result = schoolSynonymService.save(schoolSynonymDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, schoolSynonymDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /school-synonyms} : get all the schoolSynonyms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of schoolSynonyms in body.
     */
    @GetMapping("/school-synonyms")
    public List<SchoolSynonymDTO> getAllSchoolSynonyms() {
        log.debug("REST request to get all SchoolSynonyms");
        return schoolSynonymService.findAll();
    }

    /**
     * {@code GET  /school-synonyms/:id} : get the "id" schoolSynonym.
     *
     * @param id the id of the schoolSynonymDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the schoolSynonymDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/school-synonyms/{id}")
    public ResponseEntity<SchoolSynonymDTO> getSchoolSynonym(@PathVariable Long id) {
        log.debug("REST request to get SchoolSynonym : {}", id);
        Optional<SchoolSynonymDTO> schoolSynonymDTO = schoolSynonymService.findOne(id);
        return ResponseUtil.wrapOrNotFound(schoolSynonymDTO);
    }

    /**
     * {@code DELETE  /school-synonyms/:id} : delete the "id" schoolSynonym.
     *
     * @param id the id of the schoolSynonymDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/school-synonyms/{id}")
    public ResponseEntity<Void> deleteSchoolSynonym(@PathVariable Long id) {
        log.debug("REST request to delete SchoolSynonym : {}", id);
        schoolSynonymService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
