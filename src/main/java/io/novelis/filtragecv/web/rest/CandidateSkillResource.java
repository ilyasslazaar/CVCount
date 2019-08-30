package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.service.CandidateSkillService;
import io.novelis.filtragecv.web.rest.errors.BadRequestAlertException;
import io.novelis.filtragecv.service.dto.CandidateSkillDTO;

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
 * REST controller for managing {@link io.novelis.filtragecv.domain.CandidateSkill}.
 */
@RestController
@RequestMapping("/api")
public class CandidateSkillResource {

    private final Logger log = LoggerFactory.getLogger(CandidateSkillResource.class);

    private static final String ENTITY_NAME = "filtragecvCandidateSkill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidateSkillService candidateSkillService;

    public CandidateSkillResource(CandidateSkillService candidateSkillService) {
        this.candidateSkillService = candidateSkillService;
    }

    /**
     * {@code POST  /candidate-skills} : Create a new candidateSkill.
     *
     * @param candidateSkillDTO the candidateSkillDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidateSkillDTO, or with status {@code 400 (Bad Request)} if the candidateSkill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/candidate-skills")
    public ResponseEntity<CandidateSkillDTO> createCandidateSkill(@Valid @RequestBody CandidateSkillDTO candidateSkillDTO) throws URISyntaxException {
        log.debug("REST request to save CandidateSkill : {}", candidateSkillDTO);
        if (candidateSkillDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidateSkill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CandidateSkillDTO result = candidateSkillService.save(candidateSkillDTO);
        return ResponseEntity.created(new URI("/api/candidate-skills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /candidate-skills} : Updates an existing candidateSkill.
     *
     * @param candidateSkillDTO the candidateSkillDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidateSkillDTO,
     * or with status {@code 400 (Bad Request)} if the candidateSkillDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidateSkillDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/candidate-skills")
    public ResponseEntity<CandidateSkillDTO> updateCandidateSkill(@Valid @RequestBody CandidateSkillDTO candidateSkillDTO) throws URISyntaxException {
        log.debug("REST request to update CandidateSkill : {}", candidateSkillDTO);
        if (candidateSkillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CandidateSkillDTO result = candidateSkillService.save(candidateSkillDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidateSkillDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /candidate-skills} : get all the candidateSkills.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidateSkills in body.
     */
    @GetMapping("/candidate-skills")
    public List<CandidateSkillDTO> getAllCandidateSkills() {
        log.debug("REST request to get all CandidateSkills");
        return candidateSkillService.findAll();
    }

    /**
     * {@code GET  /candidate-skills/:id} : get the "id" candidateSkill.
     *
     * @param id the id of the candidateSkillDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidateSkillDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/candidate-skills/{id}")
    public ResponseEntity<CandidateSkillDTO> getCandidateSkill(@PathVariable Long id) {
        log.debug("REST request to get CandidateSkill : {}", id);
        Optional<CandidateSkillDTO> candidateSkillDTO = candidateSkillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidateSkillDTO);
    }

    /**
     * {@code DELETE  /candidate-skills/:id} : delete the "id" candidateSkill.
     *
     * @param id the id of the candidateSkillDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/candidate-skills/{id}")
    public ResponseEntity<Void> deleteCandidateSkill(@PathVariable Long id) {
        log.debug("REST request to delete CandidateSkill : {}", id);
        candidateSkillService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
