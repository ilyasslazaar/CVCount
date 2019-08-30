package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.domain.SkillSynonym;
import io.novelis.filtragecv.repository.CandidateSkillRepository;
import io.novelis.filtragecv.repository.SkillRepository;
import io.novelis.filtragecv.repository.SkillSynonymRepository;
import io.novelis.filtragecv.service.dto.SkillSynonymDTO;
import io.novelis.filtragecv.service.mapper.SkillSynonymMapper;
import io.novelis.filtragecv.web.rest.errors.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link SkillSynonym}.
 */
@Service
@Transactional
public class SkillSynonymService {

    private final Logger log = LoggerFactory.getLogger(SkillSynonymService.class);

    private final SkillSynonymRepository skillSynonymRepository;

    private final SkillRepository skillRepository;

    private final CandidateSkillRepository candidateSkillRepository;

    private final SkillSynonymMapper skillSynonymMapper;

    public SkillSynonymService(SkillSynonymRepository skillSynonymRepository,
                               SkillSynonymMapper skillSynonymMapper,
                               SkillRepository skillRepository,
                               CandidateSkillRepository candidateSkillRepository) {
        this.skillSynonymRepository = skillSynonymRepository;
        this.skillSynonymMapper = skillSynonymMapper;
        this.skillRepository = skillRepository;
        this.candidateSkillRepository = candidateSkillRepository;
    }

    /**
     * Save a skillSynonym.
     *
     * @param skillSynonymDTO the entity to save.
     * @return the persisted entity.
     */
    public SkillSynonymDTO save(SkillSynonymDTO skillSynonymDTO) {
        log.debug("Request to save SkillSynonym : {}", skillSynonymDTO);
        SkillSynonym skillSynonym = skillSynonymMapper.toEntity(skillSynonymDTO);
        Optional<Skill> existingSkill = skillRepository.findByName(skillSynonym.getName());
        if (existingSkill.isPresent()) {
            if (!existingSkill.get().getCategory().getName().equals("other")) {
                throw new BadRequestException("cant add skill synonym " + skillSynonym.getName() + " because there is already a skill with a known category with that same name");
            }
            addSkillSynonymToCandidates(existingSkill.get(), skillSynonym.getSkill());
            skillRepository.deleteByName(existingSkill.get().getName());
        }
        skillSynonym = skillSynonymRepository.save(skillSynonym);
        return skillSynonymMapper.toDto(skillSynonym);
    }

    private void addSkillSynonymToCandidates(Skill skillSynonym, Skill skill) {
        Set<CandidateSkill> candidateSkills = skillSynonym.getCandidateSkills();
        for (CandidateSkill candidateSkill :
            candidateSkills) {
            Optional<CandidateSkill> c = candidateSkillRepository.findCandidateSkillBySkillAndCandidate(skill, candidateSkill.getCandidate());
            if(c.isPresent()) {
                c.get().setCount(c.get().getCount() + candidateSkill.getCount());
            } else {
                candidateSkill.getCandidate().addSkill(skill, 1);
            }
        }
    }

    /**
     * Get all the skillSynonyms.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SkillSynonymDTO> findAll() {
        log.debug("Request to get all SkillSynonyms");
        return skillSynonymRepository.findAll().stream()
            .map(skillSynonymMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one skillSynonym by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SkillSynonymDTO> findOne(Long id) {
        log.debug("Request to get SkillSynonym : {}", id);
        return skillSynonymRepository.findById(id)
            .map(skillSynonymMapper::toDto);
    }

    /**
     * Delete the skillSynonym by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SkillSynonym : {}", id);
        skillSynonymRepository.deleteById(id);
        skillRepository.deleteByName("3d");
    }
}
