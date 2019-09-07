package io.novelis.filtragecv.service;

import io.novelis.filtragecv.service.dto.SkillDTO;
import io.novelis.filtragecv.service.mapper.SkillMapper;
import io.novelis.filtragecv.service.textprocessing.DocProcessor;
import io.novelis.filtragecv.service.textprocessing.PdfProcessor;
import io.novelis.filtragecv.service.textprocessing.TextProcessor;
import io.novelis.filtragecv.config.ApplicationProperties;
import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.repository.*;
import io.novelis.filtragecv.service.dto.CandidateDTO;
import io.novelis.filtragecv.service.mapper.CandidateMapper;
import io.novelis.filtragecv.web.rest.errors.BadRequestException;
import io.novelis.filtragecv.web.rest.errors.NotFoundException;
import io.novelis.filtragecv.web.rest.errors.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Candidate}.
 */
@Service
@Transactional
public class CandidateService {

    private final Logger log = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;

    private final SkillRepository skillRepository;

    private final SchoolRepository schoolRepository;

    private final CityRepository cityRepository;

    private final StopWordRepository stopWordRepository;

    private final SkillSynonymRepository skillSynonymRepository;

    private final SchoolSynonymRepository schoolSynonymRepository;

    private final CandidateMapper candidateMapper;

    private FileStorage fileStorage;

    private final SkillMapper skillMapper;

    public CandidateService(CandidateRepository candidateRepository,
                            CandidateMapper candidateMapper,
                            FileStorage fileStorage,
                            SkillRepository skillRepository,
                            SchoolRepository schoolRepository,
                            CityRepository cityRepository,
                            StopWordRepository stopWordRepository,
                            SkillSynonymRepository skillSynonymRepository,
                            SchoolSynonymRepository schoolSynonymRepository,
                            SkillMapper skillMapper) {
        this.candidateRepository = candidateRepository;
        this.candidateMapper = candidateMapper;
        this.skillRepository = skillRepository;
        this.schoolRepository = schoolRepository;
        this.cityRepository = cityRepository;
        this.stopWordRepository = stopWordRepository;
        this.skillSynonymRepository = skillSynonymRepository;
        this.schoolSynonymRepository = schoolSynonymRepository;
        this.skillMapper = skillMapper;
        this.fileStorage = fileStorage;
    }

    /**
     * checks if priority is valid
     *
     * @param p
     * @return
     */
    private boolean inPriorityRange(int p) {
        return p >= 3 && p <= 7;
    }

    /**
     * gets list of candidate skills
     *
     * @param id of candidate
     * @return list of candidate skills
     * @throws EntityNotFoundException if candidate does not exist
     */
    public List<SkillDTO> getSkills(Long id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (!candidate.isPresent()) {
            throw new EntityNotFoundException("candidate with id: " + id + " not found");
        }
        return candidate.get()
            .getSkills()
            .stream()
            .map(skillMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * checks if parameters are valid to order the candidates
     *
     * @param keywords
     * @param priorities
     */
    private void validateParams(List<String> keywords, List<Integer> priorities) {
        if (keywords.size() != priorities.size()) {
            throw new BadRequestException("keywords size does not equal priorites size");
        }
        for (int i = 0; i < keywords.size(); i++) {
            if (!keywords.get(i).matches(".*[a-zA-Z]+.*")) {
                throw new BadRequestException("keyword: " + keywords.get(i) + " does not contain a letter");
            } else if (!inPriorityRange(priorities.get(i))) {
                throw new BadRequestException("priority: " + priorities.get(i) + " not in the [3, 7] range");
            }
        }
    }

    /**
     * gets candidate file
     *
     * @param fileName
     * @return
     */
    public Resource getCandidateFile(String fileName) {
        return fileStorage.loadFileAsResource(fileName);
    }

    /**
     * @param keywords
     * @param priorities
     * @return list of not rejected candidates ordred based on the score they have
     */
    public List<CandidateDTO> getCandidates(List<String> keywords, List<Integer> priorities) {
        if (keywords.size() == 0) {
            return findAll();
        }
        validateParams(keywords, priorities);
        List<Candidate> candidates = candidateRepository.getCandidateByRejected(false);
        for (Candidate candidate :
            candidates) {
            candidate.calcScore(keywords, priorities);
            if (candidate.getScore() == 0) {
                candidates.remove(candidate);
            }
        }
        candidates.sort(Comparator.comparingInt(Candidate::getScore).reversed());
        return candidates.stream().map(candidateMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Save a candidate.
     *
     * @param candidateDTO the entity to save.
     * @return the persisted entity.
     */
    public CandidateDTO save(CandidateDTO candidateDTO) {
        log.debug("Request to save Candidate : {}", candidateDTO);
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        candidate = candidateRepository.save(candidate);
        return candidateMapper.toDto(candidate);
    }

    /**
     * save a candidate
     *
     * @param cv
     * @param funcId
     * @return candidateDTO
     * @throws FileStorageException if file extension not (pdf, doc, docx)
     */
    public CandidateDTO save(MultipartFile cv, Integer funcId) {
        String fileName = fileStorage.storeFile(cv);
        Candidate candidate = new Candidate();
        candidate.setFuncId(funcId);
        candidate.setFileName(fileName);
        addWordsToCandidate(fileStorage.getWords(), candidate);
        candidate = candidateRepository.save(candidate);
        return candidateMapper.toDto(candidate);
    }

    public CandidateDTO update(Long id, MultipartFile cv, Integer funcId, Boolean rejected) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (!candidate.isPresent()) {
            throw new EntityNotFoundException("cant find");
        }
        if (cv != null) {
            String fileName = fileStorage.storeFile(cv);
            candidate.get().setFileName(fileName);
            fileStorage.deleteFile(candidate.get().getFileName());
        }
        if (funcId != null) {
            candidate.get().setFuncId(funcId);
        }
        if (rejected != null) {
            candidate.get().setRejected(rejected);
        }
        Candidate c = candidate.get();
        c = candidateRepository.save(c);
        return candidateMapper.toDto(c);
    }

    /**
     * get words category and add them to the candidate with their occurrence
     *
     * @param words
     * @param candidate
     */
    private void addWordsToCandidate(HashMap<String, Integer> words, Candidate candidate) {
        Iterator it = words.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = (Map.Entry) it.next();
            Object obj = getWordCategory(pair.getKey());
            if (obj instanceof Skill) {
                candidate.addSkill((Skill) obj, pair.getValue());
            } else if (obj instanceof City) {
                candidate.addCity((City) obj);
            } else if (obj instanceof School) {
                candidate.addSchool((School) obj);
            } else if (obj instanceof SkillSynonym) {
                CandidateSkill candidateSkill = candidate.findSkill(((SkillSynonym) obj).getSkill());
                if (candidateSkill == null) {
                    candidate.addSkill(((SkillSynonym) obj).getSkill(), pair.getValue());
                } else {
                    candidateSkill.setCount(candidateSkill.getCount() + pair.getValue());
                }
            } else if (obj instanceof SchoolSynonym) {
                if (!candidate.findSchool(((SchoolSynonym) obj).getSchool())) {
                    candidate.addSchool(((SchoolSynonym) obj).getSchool());
                }
            }
        }
    }

    /**
     * @param word
     * @return null if its a stop word or not valid word
     */
    private Object getWordCategory(String word) {
        Optional<Skill> skill = skillRepository.findByName(word);
        if (skill.isPresent()) {
            return skill.get();
        }
        Optional<City> city = cityRepository.findByName(word);
        if (city.isPresent()) {
            return city.get();
        }
        Optional<School> school = schoolRepository.findByName(word);
        if (school.isPresent()) {
            return school.get();
        }
        Optional<SkillSynonym> skillSynonym = skillSynonymRepository.findByName(word);
        if (skillSynonym.isPresent()) {
            return skillSynonym.get();
        }
        Optional<SchoolSynonym> schoolSynonym = schoolSynonymRepository.findByName(word);
        if (schoolSynonym.isPresent()) {
            return schoolSynonym.get();
        }
        Optional<StopWord> stopWord = stopWordRepository.findByName(word);
        if (stopWord.isPresent()) {
            return null;
        }
        if (!fileStorage.validWord(word)) {
            return null;
        }
        Skill s = new Skill();
        s.setName(word);
        Category c = new Category();
        c.setId(1L);
        s.setCategory(c);
        return s;
    }

    /**
     * Get all the candidates.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CandidateDTO> findAll() {
        log.debug("Request to get all Candidates");
        return candidateRepository.findAllWithEagerRelationships().stream()
            .map(candidateMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the candidates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CandidateDTO> findAllWithEagerRelationships(Pageable pageable) {
        return candidateRepository.findAllWithEagerRelationships(pageable).map(candidateMapper::toDto);
    }


    /**
     * Get one candidate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CandidateDTO> findOne(Long id) {
        log.debug("Request to get Candidate : {}", id);
        return candidateRepository.findOneWithEagerRelationships(id)
            .map(candidateMapper::toDto);
    }

    /**
     * Delete the candidate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Candidate : {}", id);
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (!candidate.isPresent()) {
            throw new EntityNotFoundException("candidate with id " + id + " not found");
        }
        candidateRepository.deleteById(id);
        fileStorage.deleteFile(candidate.get().getFileName());
    }


}
