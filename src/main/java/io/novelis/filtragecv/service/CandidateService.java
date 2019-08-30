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

    private TextProcessor textProcessor;

    private final Path fileStorageLocation;

    private final SkillMapper skillMapper;

    public CandidateService(CandidateRepository candidateRepository,
                            CandidateMapper candidateMapper,
                            ApplicationProperties applicationProperties,
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
        this.fileStorageLocation = Paths.get(applicationProperties.getUploadDir())
            .toAbsolutePath().normalize();
        this.skillMapper = skillMapper;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private boolean inPriorityRange(int p) {
        return p >= 3 && p <= 7;
    }

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

    private void validateParams(List<String> keywords, List<Integer> priorities) {
        if (keywords.size() != priorities.size()) {
            throw new BadRequestException("keywords size does not equal priorites size");
        }
        for (int i = 0; i < keywords.size(); i++) {
            if (!keywords.get(i).matches(".*[a-zA-Z]+.*")) {
                throw new BadRequestException("keyword: "+ keywords.get(i) + " does not contain a letter");
            } else if (!inPriorityRange(priorities.get(i))){
                throw new BadRequestException("priority: " + priorities.get(i) + " not in the [3, 7] range");
            }
        }
    }

    public List<CandidateDTO> getCandidates(List<String> keywords, List<Integer> priorities) {
        if (keywords.size() == 0) {
            return findAll();
        }
        validateParams(keywords, priorities);
        List<Candidate> candidates = candidateRepository.findAllWithEagerRelationships();
        for (Candidate candidate :
            candidates) {
            candidate.calcScore(keywords, priorities);
            if(candidate.getScore() == 0) {
                candidates.remove(candidate);
            }
        }
        candidates.sort(Comparator.comparingInt(Candidate::getScore).reversed());
        return candidates.stream().map(candidateMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    private String getFileExtension(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }
        String fileName = file.getOriginalFilename();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    private String storeFile(MultipartFile file, String fileExtension) {
        String fileName = "" + new Date().getTime() + "." + fileExtension;
        try {
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + fileName, ex);
        }
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

    public CandidateDTO save(MultipartFile cv, int funcId) {
        String fileExtension = getFileExtension(cv);
        if (!fileExtension.equals("pdf") && !fileExtension.equals("docx") && !fileExtension.equals("doc")) {
            throw new FileStorageException("unrecognized file type :" + fileExtension);
        }
        String fileName = storeFile(cv, fileExtension);

        String filePath = this.fileStorageLocation.resolve(fileName).normalize().toString();
        if (fileExtension.equals("pdf")) {
            textProcessor = new PdfProcessor(filePath);
        } else {
            textProcessor = new DocProcessor(filePath);
        }
        Candidate candidate = new Candidate();
        candidate.setFuncId(funcId);
        candidate.setFileName(fileName);
        addWordsToCandidate(textProcessor.getWords(), candidate);
        candidate = candidateRepository.save(candidate);
        return candidateMapper.toDto(candidate);
    }

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
        if (!textProcessor.validWord(word)) {
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
        deleteFile(candidate.get().getFileName());
    }

    private void deleteFile(String fileName) {
        try {
            Files.delete(this.fileStorageLocation.resolve(fileName).normalize());
        } catch (IOException e) {
            throw new NotFoundException("file not found to delete it");
        }
    }
}
