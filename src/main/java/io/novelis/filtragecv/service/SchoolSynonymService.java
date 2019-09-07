package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.SchoolSynonym;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.repository.SchoolSynonymRepository;
import io.novelis.filtragecv.repository.SkillRepository;
import io.novelis.filtragecv.service.dto.SchoolSynonymDTO;
import io.novelis.filtragecv.service.mapper.SchoolSynonymMapper;
import io.novelis.filtragecv.web.rest.errors.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link SchoolSynonym}.
 */
@Service
@Transactional
public class SchoolSynonymService {

    private final Logger log = LoggerFactory.getLogger(SchoolSynonymService.class);

    private final SchoolSynonymRepository schoolSynonymRepository;

    private final SkillRepository skillRepository;

    private final SchoolSynonymMapper schoolSynonymMapper;

    public SchoolSynonymService(SchoolSynonymRepository schoolSynonymRepository,
                                SchoolSynonymMapper schoolSynonymMapper,
                                SkillRepository skillRepository) {
        this.schoolSynonymRepository = schoolSynonymRepository;
        this.schoolSynonymMapper = schoolSynonymMapper;
        this.skillRepository = skillRepository;
    }

    /**
     * Save a schoolSynonym and deletes it if it exists in the skill table.
     *
     * @param schoolSynonymDTO the entity to save.
     * @throws BadRequestException if the name of the schoolSynonym equals the name of a skill with a known category
     * @return the persisted entity.
     */
    public SchoolSynonymDTO save(SchoolSynonymDTO schoolSynonymDTO) {
        log.debug("Request to save SchoolSynonym : {}", schoolSynonymDTO);
        SchoolSynonym schoolSynonym = schoolSynonymMapper.toEntity(schoolSynonymDTO);
        Optional<Skill> skill = skillRepository.findByName(schoolSynonym.getName());
        if(skill.isPresent()) {
            if(!skill.get().getCategory().getName().equals("other")) {
                throw new BadRequestException("cant add school synonym " + schoolSynonym.getName() + " because there is already a skill with a known category with that same name");
            }
        }
        skillRepository.delete(skill.get());
        schoolSynonym = schoolSynonymRepository.save(schoolSynonym);
        return schoolSynonymMapper.toDto(schoolSynonym);
    }

    /**
     * Get all the schoolSynonyms.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SchoolSynonymDTO> findAll() {
        log.debug("Request to get all SchoolSynonyms");
        return schoolSynonymRepository.findAll().stream()
            .map(schoolSynonymMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one schoolSynonym by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SchoolSynonymDTO> findOne(Long id) {
        log.debug("Request to get SchoolSynonym : {}", id);
        return schoolSynonymRepository.findById(id)
            .map(schoolSynonymMapper::toDto);
    }

    /**
     * Delete the schoolSynonym by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SchoolSynonym : {}", id);
        schoolSynonymRepository.deleteById(id);
    }
}
