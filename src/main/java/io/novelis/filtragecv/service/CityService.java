package io.novelis.filtragecv.service;

import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.domain.City;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.repository.CityRepository;
import io.novelis.filtragecv.repository.SkillRepository;
import io.novelis.filtragecv.service.dto.CityDTO;
import io.novelis.filtragecv.service.mapper.CityMapper;
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
 * Service Implementation for managing {@link City}.
 */
@Service
@Transactional
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    private final SkillRepository skillRepository;

    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper, SkillRepository skillRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.skillRepository = skillRepository;
    }

    /**
     * Save a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    public CityDTO save(CityDTO cityDTO) {
        log.debug("Request to save City : {}", cityDTO);
        City city = cityMapper.toEntity(cityDTO);
        city = cityRepository.save(city);
        Optional<Skill> skill = skillRepository.findByName(city.getName());
        if(skill.isPresent()) {
            addCityToCandidates(skill.get(), city);
            if(skill.get().getCategory().getName().equals("other")) {
                skillRepository.delete(skill.get());
            }
        }
        return cityMapper.toDto(city);
    }
    private void addCityToCandidates(Skill skill, City city) {
        Set<CandidateSkill> candidateSkills = skill.getCandidateSkills();
        for (CandidateSkill candidateSkill : candidateSkills) {
            candidateSkill.getCandidate().addCity(city);
        }
    }
    /**
     * Get all the cities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {
        log.debug("Request to get all Cities");
        return cityRepository.findAll().stream()
            .map(cityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one city by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CityDTO> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findById(id)
            .map(cityMapper::toDto);
    }

    /**
     * Delete the city by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete City : {}", id);
        cityRepository.deleteById(id);
    }
}
