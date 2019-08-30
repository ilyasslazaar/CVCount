package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.repository.CandidateSkillRepository;
import io.novelis.filtragecv.service.dto.CandidateSkillDTO;
import io.novelis.filtragecv.service.mapper.CandidateSkillMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link CandidateSkill}.
 */
@Service
@Transactional
public class CandidateSkillService {

    private final Logger log = LoggerFactory.getLogger(CandidateSkillService.class);

    private final CandidateSkillRepository candidateSkillRepository;

    private final CandidateSkillMapper candidateSkillMapper;

    public CandidateSkillService(CandidateSkillRepository candidateSkillRepository, CandidateSkillMapper candidateSkillMapper) {
        this.candidateSkillRepository = candidateSkillRepository;
        this.candidateSkillMapper = candidateSkillMapper;
    }

    /**
     * Save a candidateSkill.
     *
     * @param candidateSkillDTO the entity to save.
     * @return the persisted entity.
     */
    public CandidateSkillDTO save(CandidateSkillDTO candidateSkillDTO) {
        log.debug("Request to save CandidateSkill : {}", candidateSkillDTO);
        CandidateSkill candidateSkill = candidateSkillMapper.toEntity(candidateSkillDTO);
        candidateSkill = candidateSkillRepository.save(candidateSkill);
        return candidateSkillMapper.toDto(candidateSkill);
    }

    /**
     * Get all the candidateSkills.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CandidateSkillDTO> findAll() {
        log.debug("Request to get all CandidateSkills");
        return candidateSkillRepository.findAll().stream()
            .map(candidateSkillMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one candidateSkill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CandidateSkillDTO> findOne(Long id) {
        log.debug("Request to get CandidateSkill : {}", id);
        return candidateSkillRepository.findById(id)
            .map(candidateSkillMapper::toDto);
    }

    /**
     * Delete the candidateSkill by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CandidateSkill : {}", id);
        candidateSkillRepository.deleteById(id);
    }
}
