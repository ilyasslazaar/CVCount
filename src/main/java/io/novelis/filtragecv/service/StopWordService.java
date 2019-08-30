package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.domain.StopWord;
import io.novelis.filtragecv.repository.SkillRepository;
import io.novelis.filtragecv.repository.StopWordRepository;
import io.novelis.filtragecv.service.dto.StopWordDTO;
import io.novelis.filtragecv.service.mapper.StopWordMapper;
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
 * Service Implementation for managing {@link StopWord}.
 */
@Service
@Transactional
public class StopWordService {

    private final Logger log = LoggerFactory.getLogger(StopWordService.class);

    private final StopWordRepository stopWordRepository;

    private final SkillRepository skillRepository;

    private final StopWordMapper stopWordMapper;

    public StopWordService(StopWordRepository stopWordRepository, StopWordMapper stopWordMapper, SkillRepository skillRepository) {
        this.stopWordRepository = stopWordRepository;
        this.stopWordMapper = stopWordMapper;
        this.skillRepository = skillRepository;
    }

    /**
     * Save a stopWord.
     *
     * @param stopWordDTO the entity to save.
     * @return the persisted entity.
     */
    public StopWordDTO save(StopWordDTO stopWordDTO) {
        log.debug("Request to save StopWord : {}", stopWordDTO);
        StopWord stopWord = stopWordMapper.toEntity(stopWordDTO);
        Optional<Skill> existingSkill = skillRepository.findByName(stopWord.getName());
        if(existingSkill.isPresent()) {
            if(!existingSkill.get().getCategory().getName().equals("other")) {
                throw new BadRequestException("cant add stop word " + stopWord.getName() + " because there is already a skill with a known category with that same name");
            }
        }
        stopWord = stopWordRepository.save(stopWord);
        skillRepository.deleteByName(stopWord.getName());
        return stopWordMapper.toDto(stopWord);
    }
    /**
     * Get all the stopWords.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StopWordDTO> findAll() {
        log.debug("Request to get all StopWords");
        return stopWordRepository.findAll().stream()
            .map(stopWordMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one stopWord by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StopWordDTO> findOne(Long id) {
        log.debug("Request to get StopWord : {}", id);
        return stopWordRepository.findById(id)
            .map(stopWordMapper::toDto);
    }

    /**
     * Delete the stopWord by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StopWord : {}", id);
        stopWordRepository.deleteById(id);
    }
}
