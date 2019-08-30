package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.FiltragecvApp;
import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.domain.Candidate;
import io.novelis.filtragecv.repository.CandidateSkillRepository;
import io.novelis.filtragecv.service.CandidateSkillService;
import io.novelis.filtragecv.service.dto.CandidateSkillDTO;
import io.novelis.filtragecv.service.mapper.CandidateSkillMapper;
import io.novelis.filtragecv.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.novelis.filtragecv.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link CandidateSkillResource} REST controller.
 */
@SpringBootTest(classes = FiltragecvApp.class)
public class CandidateSkillResourceIT {

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    @Autowired
    private CandidateSkillRepository candidateSkillRepository;

    @Autowired
    private CandidateSkillMapper candidateSkillMapper;

    @Autowired
    private CandidateSkillService candidateSkillService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCandidateSkillMockMvc;

    private CandidateSkill candidateSkill;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CandidateSkillResource candidateSkillResource = new CandidateSkillResource(candidateSkillService);
        this.restCandidateSkillMockMvc = MockMvcBuilders.standaloneSetup(candidateSkillResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CandidateSkill createEntity(EntityManager em) {
        CandidateSkill candidateSkill = new CandidateSkill()
            .count(DEFAULT_COUNT);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        candidateSkill.setSkill(skill);
        // Add required entity
        Candidate candidate;
        if (TestUtil.findAll(em, Candidate.class).isEmpty()) {
            candidate = CandidateResourceIT.createEntity(em);
            em.persist(candidate);
            em.flush();
        } else {
            candidate = TestUtil.findAll(em, Candidate.class).get(0);
        }
        candidateSkill.setCandidate(candidate);
        return candidateSkill;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CandidateSkill createUpdatedEntity(EntityManager em) {
        CandidateSkill candidateSkill = new CandidateSkill()
            .count(UPDATED_COUNT);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createUpdatedEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        candidateSkill.setSkill(skill);
        // Add required entity
        Candidate candidate;
        if (TestUtil.findAll(em, Candidate.class).isEmpty()) {
            candidate = CandidateResourceIT.createUpdatedEntity(em);
            em.persist(candidate);
            em.flush();
        } else {
            candidate = TestUtil.findAll(em, Candidate.class).get(0);
        }
        candidateSkill.setCandidate(candidate);
        return candidateSkill;
    }

    @BeforeEach
    public void initTest() {
        candidateSkill = createEntity(em);
    }

    @Test
    @Transactional
    public void createCandidateSkill() throws Exception {
        int databaseSizeBeforeCreate = candidateSkillRepository.findAll().size();

        // Create the CandidateSkill
        CandidateSkillDTO candidateSkillDTO = candidateSkillMapper.toDto(candidateSkill);
        restCandidateSkillMockMvc.perform(post("/api/candidate-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(candidateSkillDTO)))
            .andExpect(status().isCreated());

        // Validate the CandidateSkill in the database
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeCreate + 1);
        CandidateSkill testCandidateSkill = candidateSkillList.get(candidateSkillList.size() - 1);
        assertThat(testCandidateSkill.getCount()).isEqualTo(DEFAULT_COUNT);
    }

    @Test
    @Transactional
    public void createCandidateSkillWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = candidateSkillRepository.findAll().size();

        // Create the CandidateSkill with an existing ID
        candidateSkill.setId(1L);
        CandidateSkillDTO candidateSkillDTO = candidateSkillMapper.toDto(candidateSkill);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandidateSkillMockMvc.perform(post("/api/candidate-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(candidateSkillDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CandidateSkill in the database
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkCountIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateSkillRepository.findAll().size();
        // set the field null
        candidateSkill.setCount(null);

        // Create the CandidateSkill, which fails.
        CandidateSkillDTO candidateSkillDTO = candidateSkillMapper.toDto(candidateSkill);

        restCandidateSkillMockMvc.perform(post("/api/candidate-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(candidateSkillDTO)))
            .andExpect(status().isBadRequest());

        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCandidateSkills() throws Exception {
        // Initialize the database
        candidateSkillRepository.saveAndFlush(candidateSkill);

        // Get all the candidateSkillList
        restCandidateSkillMockMvc.perform(get("/api/candidate-skills?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidateSkill.getId().intValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)));
    }
    
    @Test
    @Transactional
    public void getCandidateSkill() throws Exception {
        // Initialize the database
        candidateSkillRepository.saveAndFlush(candidateSkill);

        // Get the candidateSkill
        restCandidateSkillMockMvc.perform(get("/api/candidate-skills/{id}", candidateSkill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(candidateSkill.getId().intValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT));
    }

    @Test
    @Transactional
    public void getNonExistingCandidateSkill() throws Exception {
        // Get the candidateSkill
        restCandidateSkillMockMvc.perform(get("/api/candidate-skills/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCandidateSkill() throws Exception {
        // Initialize the database
        candidateSkillRepository.saveAndFlush(candidateSkill);

        int databaseSizeBeforeUpdate = candidateSkillRepository.findAll().size();

        // Update the candidateSkill
        CandidateSkill updatedCandidateSkill = candidateSkillRepository.findById(candidateSkill.getId()).get();
        // Disconnect from session so that the updates on updatedCandidateSkill are not directly saved in db
        em.detach(updatedCandidateSkill);
        updatedCandidateSkill
            .count(UPDATED_COUNT);
        CandidateSkillDTO candidateSkillDTO = candidateSkillMapper.toDto(updatedCandidateSkill);

        restCandidateSkillMockMvc.perform(put("/api/candidate-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(candidateSkillDTO)))
            .andExpect(status().isOk());

        // Validate the CandidateSkill in the database
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeUpdate);
        CandidateSkill testCandidateSkill = candidateSkillList.get(candidateSkillList.size() - 1);
        assertThat(testCandidateSkill.getCount()).isEqualTo(UPDATED_COUNT);
    }

    @Test
    @Transactional
    public void updateNonExistingCandidateSkill() throws Exception {
        int databaseSizeBeforeUpdate = candidateSkillRepository.findAll().size();

        // Create the CandidateSkill
        CandidateSkillDTO candidateSkillDTO = candidateSkillMapper.toDto(candidateSkill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidateSkillMockMvc.perform(put("/api/candidate-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(candidateSkillDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CandidateSkill in the database
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCandidateSkill() throws Exception {
        // Initialize the database
        candidateSkillRepository.saveAndFlush(candidateSkill);

        int databaseSizeBeforeDelete = candidateSkillRepository.findAll().size();

        // Delete the candidateSkill
        restCandidateSkillMockMvc.perform(delete("/api/candidate-skills/{id}", candidateSkill.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CandidateSkill> candidateSkillList = candidateSkillRepository.findAll();
        assertThat(candidateSkillList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CandidateSkill.class);
        CandidateSkill candidateSkill1 = new CandidateSkill();
        candidateSkill1.setId(1L);
        CandidateSkill candidateSkill2 = new CandidateSkill();
        candidateSkill2.setId(candidateSkill1.getId());
        assertThat(candidateSkill1).isEqualTo(candidateSkill2);
        candidateSkill2.setId(2L);
        assertThat(candidateSkill1).isNotEqualTo(candidateSkill2);
        candidateSkill1.setId(null);
        assertThat(candidateSkill1).isNotEqualTo(candidateSkill2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CandidateSkillDTO.class);
        CandidateSkillDTO candidateSkillDTO1 = new CandidateSkillDTO();
        candidateSkillDTO1.setId(1L);
        CandidateSkillDTO candidateSkillDTO2 = new CandidateSkillDTO();
        assertThat(candidateSkillDTO1).isNotEqualTo(candidateSkillDTO2);
        candidateSkillDTO2.setId(candidateSkillDTO1.getId());
        assertThat(candidateSkillDTO1).isEqualTo(candidateSkillDTO2);
        candidateSkillDTO2.setId(2L);
        assertThat(candidateSkillDTO1).isNotEqualTo(candidateSkillDTO2);
        candidateSkillDTO1.setId(null);
        assertThat(candidateSkillDTO1).isNotEqualTo(candidateSkillDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(candidateSkillMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(candidateSkillMapper.fromId(null)).isNull();
    }
}
