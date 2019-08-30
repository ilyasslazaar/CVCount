package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.service.CandidateService;
import io.novelis.filtragecv.service.dto.SkillDTO;
import io.novelis.filtragecv.web.rest.errors.BadRequestAlertException;
import io.novelis.filtragecv.service.dto.CandidateDTO;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.novelis.filtragecv.web.rest.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link io.novelis.filtragecv.domain.Candidate}.
 */
@RestController
@RequestMapping("/api")
public class CandidateResource {

    private final Logger log = LoggerFactory.getLogger(CandidateResource.class);

    private static final String ENTITY_NAME = "filtragecvCandidate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidateService candidateService;

    public CandidateResource(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /*
        /**
         * {@code POST  /candidates} : Create a new candidate.
         *
         * @param candidateDTO the candidateDTO to create.
         * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidateDTO, or with status {@code 400 (Bad Request)} if the candidate has already an ID.
         * @throws URISyntaxException if the Location URI syntax is incorrect.
         */
   /* @PostMapping("/candidates")
    public ResponseEntity<CandidateDTO> createCandidate(@Valid @RequestBody CandidateDTO candidateDTO) throws URISyntaxException {
        log.debug("REST request to save Candidate : {}", candidateDTO);
        if (candidateDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CandidateDTO result = candidateService.save(candidateDTO);
        return ResponseEntity.created(new URI("/api/candidates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }*/
    @PostMapping("/candidates")
    public ResponseEntity<CandidateDTO> createCandidate(@RequestParam("file") MultipartFile cv, @RequestParam("func_id") int id) throws URISyntaxException {
        CandidateDTO result = candidateService.save(cv, id);
        return ResponseEntity.created(new URI("/api/candidates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /candidates} : Updates an existing candidate.
     *
     * @param candidateDTO the candidateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidateDTO,
     * or with status {@code 400 (Bad Request)} if the candidateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/candidates")
    public ResponseEntity<CandidateDTO> updateCandidate(@Valid @RequestBody CandidateDTO candidateDTO) throws URISyntaxException {
        log.debug("REST request to update Candidate : {}", candidateDTO);
        if (candidateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CandidateDTO result = candidateService.save(candidateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /candidates} : get all the candidates.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidates in body.
     */
  /*  @GetMapping("/candidates")
    public List<CandidateDTO> getAllCandidates(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Candidates");
        return candidateService.findAll();
    }*/

    @GetMapping("/candidates")
    public List<CandidateDTO> getCandidates(@RequestParam(required = false, defaultValue = "false") boolean eagerload,
                                                  @RequestParam(required = false, defaultValue = "") List<String> keywords,
                                                  @RequestParam(required = false, defaultValue = "") List<Integer> priorities) {
        log.debug("REST request to get all Candidates");

        return candidateService.getCandidates(keywords, priorities);
    }

    /**
     * {@code GET  /candidates/:id} : get the "id" candidate.
     *
     * @param id the id of the candidateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateDTO> getCandidate(@PathVariable Long id) {
        log.debug("REST request to get Candidate : {}", id);
        Optional<CandidateDTO> candidateDTO = candidateService.findOne(id);
        if (!candidateDTO.isPresent()) {
            throw new EntityNotFoundException("candidate with id: " + id + " not found");
        }
        return ResponseUtil.wrapOrNotFound(candidateDTO);
    }
    @GetMapping("/candidates/{id}/skills")
    public List<SkillDTO> getCandidateSkills(@PathVariable Long id) {
        log.debug("REST request to get Candidate : {}", id);
        return candidateService.getSkills(id);
    }

    @GetMapping("/candidates/{id}/cv")
    public ResponseEntity<Resource> getCandidateCv(@PathVariable Long id, HttpServletRequest request) {
        log.debug("REST request to get Candidate cv : {}", id);
        Optional<CandidateDTO> candidateDTO = candidateService.findOne(id);
        if (!candidateDTO.isPresent()) {
            throw new EntityNotFoundException("candidate with id: " + id + " not found");
        }
        String fileName = candidateDTO.get().getFileName();
        Resource resource = candidateService.loadFileAsResource(fileName);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    /**
     * {@code DELETE  /candidates/:id} : delete the "id" candidate.
     *
     * @param id the id of the candidateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        log.debug("REST request to delete Candidate : {}", id);
        candidateService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
