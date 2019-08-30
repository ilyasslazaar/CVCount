package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.domain.School;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.repository.CandidateSkillRepository;
import io.novelis.filtragecv.repository.SchoolRepository;
import io.novelis.filtragecv.repository.SkillRepository;
import io.novelis.filtragecv.service.dto.SchoolDTO;
import io.novelis.filtragecv.service.mapper.SchoolMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link School}.
 */
@Service
@Transactional
public class SchoolService {

    private final Logger log = LoggerFactory.getLogger(SchoolService.class);

    private final SchoolRepository schoolRepository;

    private final SkillRepository skillRepository;

    private final CandidateSkillRepository candidateSkillRepository;

    private final SchoolMapper schoolMapper;

    public SchoolService(SchoolRepository schoolRepository,
                         SchoolMapper schoolMapper,
                         SkillRepository skillRepository,
                         CandidateSkillRepository candidateSkillRepository) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.skillRepository = skillRepository;
        this.candidateSkillRepository = candidateSkillRepository;
    }

    /**
     * Save a school.
     *
     * @param schoolDTO the entity to save.
     * @return the persisted entity.
     */
    public SchoolDTO save(SchoolDTO schoolDTO) {
        log.debug("Request to save School : {}", schoolDTO);
        School school = schoolMapper.toEntity(schoolDTO);
        school = schoolRepository.save(school);
        Optional<Skill> skill = skillRepository.findByName(school.getName());
        if(skill.isPresent()) {
            addSchoolToCandidate(skill.get(), school);
            if(skill.get().getCategory().getName().equals("other")) {
                skillRepository.delete(skill.get());
            }
        }
        return schoolMapper.toDto(school);
    }
    private void addSchoolToCandidate(Skill skill, School school) {
        List<CandidateSkill> candidateSkills = candidateSkillRepository.getAllBySkill(skill);
        for(int i = 0; i < candidateSkills.size(); i++) {
            candidateSkills.get(i).getCandidate().addSchool(school);
        }

    }
    /**
     * Get all the schools.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SchoolDTO> findAll() {
        log.debug("Request to get all Schools");
        return schoolRepository.findAll().stream()
            .map(schoolMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one school by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SchoolDTO> findOne(Long id) {
        log.debug("Request to get School : {}", id);
        return schoolRepository.findById(id)
            .map(schoolMapper::toDto);
    }

    /**
     * Delete the school by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete School : {}", id);
        schoolRepository.deleteById(id);
    }
}
