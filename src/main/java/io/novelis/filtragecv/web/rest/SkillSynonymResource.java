package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.service.SkillSynonymService;
import io.novelis.filtragecv.web.rest.errors.BadRequestAlertException;
import io.novelis.filtragecv.service.dto.SkillSynonymDTO;

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
 * REST controller for managing {@link io.novelis.filtragecv.domain.SkillSynonym}.
 */
@RestController
@RequestMapping("/api")
public class SkillSynonymResource {

    private final Logger log = LoggerFactory.getLogger(SkillSynonymResource.class);

    private static final String ENTITY_NAME = "filtragecvSkillSynonym";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SkillSynonymService skillSynonymService;

    public SkillSynonymResource(SkillSynonymService skillSynonymService) {
        this.skillSynonymService = skillSynonymService;
    }

    /**
     * {@code POST  /skill-synonyms} : Create a new skillSynonym.
     *
     * @param skillSynonymDTO the skillSynonymDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new skillSynonymDTO, or with status {@code 400 (Bad Request)} if the skillSynonym has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/skill-synonyms")
    public ResponseEntity<SkillSynonymDTO> createSkillSynonym(@Valid @RequestBody SkillSynonymDTO skillSynonymDTO) throws URISyntaxException {
        log.debug("REST request to save SkillSynonym : {}", skillSynonymDTO);
        if (skillSynonymDTO.getId() != null) {
            throw new BadRequestAlertException("A new skillSynonym cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SkillSynonymDTO result = skillSynonymService.save(skillSynonymDTO);
        return ResponseEntity.created(new URI("/api/skill-synonyms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /skill-synonyms} : Updates an existing skillSynonym.
     *
     * @param skillSynonymDTO the skillSynonymDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated skillSynonymDTO,
     * or with status {@code 400 (Bad Request)} if the skillSynonymDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the skillSynonymDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/skill-synonyms")
    public ResponseEntity<SkillSynonymDTO> updateSkillSynonym(@Valid @RequestBody SkillSynonymDTO skillSynonymDTO) throws URISyntaxException {
        log.debug("REST request to update SkillSynonym : {}", skillSynonymDTO);
        if (skillSynonymDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SkillSynonymDTO result = skillSynonymService.save(skillSynonymDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, skillSynonymDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /skill-synonyms} : get all the skillSynonyms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of skillSynonyms in body.
     */
    @GetMapping("/skill-synonyms")
    public List<SkillSynonymDTO> getAllSkillSynonyms() {
        log.debug("REST request to get all SkillSynonyms");
        return skillSynonymService.findAll();
    }

    /**
     * {@code GET  /skill-synonyms/:id} : get the "id" skillSynonym.
     *
     * @param id the id of the skillSynonymDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the skillSynonymDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/skill-synonyms/{id}")
    public ResponseEntity<SkillSynonymDTO> getSkillSynonym(@PathVariable Long id) {
        log.debug("REST request to get SkillSynonym : {}", id);
        Optional<SkillSynonymDTO> skillSynonymDTO = skillSynonymService.findOne(id);
        return ResponseUtil.wrapOrNotFound(skillSynonymDTO);
    }

    /**
     * {@code DELETE  /skill-synonyms/:id} : delete the "id" skillSynonym.
     *
     * @param id the id of the skillSynonymDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/skill-synonyms/{id}")
    public ResponseEntity<Void> deleteSkillSynonym(@PathVariable Long id) {
        log.debug("REST request to delete SkillSynonym : {}", id);
        skillSynonymService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
