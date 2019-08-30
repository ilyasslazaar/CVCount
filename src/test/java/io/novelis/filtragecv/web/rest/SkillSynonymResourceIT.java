package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.FiltragecvApp;
import io.novelis.filtragecv.domain.SkillSynonym;
import io.novelis.filtragecv.domain.Skill;
import io.novelis.filtragecv.repository.SkillSynonymRepository;
import io.novelis.filtragecv.service.SkillSynonymService;
import io.novelis.filtragecv.service.dto.SkillSynonymDTO;
import io.novelis.filtragecv.service.mapper.SkillSynonymMapper;
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
 * Integration tests for the {@Link SkillSynonymResource} REST controller.
 */
@SpringBootTest(classes = FiltragecvApp.class)
public class SkillSynonymResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private SkillSynonymRepository skillSynonymRepository;

    @Autowired
    private SkillSynonymMapper skillSynonymMapper;

    @Autowired
    private SkillSynonymService skillSynonymService;

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

    private MockMvc restSkillSynonymMockMvc;

    private SkillSynonym skillSynonym;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SkillSynonymResource skillSynonymResource = new SkillSynonymResource(skillSynonymService);
        this.restSkillSynonymMockMvc = MockMvcBuilders.standaloneSetup(skillSynonymResource)
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
    public static SkillSynonym createEntity(EntityManager em) {
        SkillSynonym skillSynonym = new SkillSynonym()
            .name(DEFAULT_NAME);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        skillSynonym.setSkill(skill);
        return skillSynonym;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SkillSynonym createUpdatedEntity(EntityManager em) {
        SkillSynonym skillSynonym = new SkillSynonym()
            .name(UPDATED_NAME);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createUpdatedEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        skillSynonym.setSkill(skill);
        return skillSynonym;
    }

    @BeforeEach
    public void initTest() {
        skillSynonym = createEntity(em);
    }

    @Test
    @Transactional
    public void createSkillSynonym() throws Exception {
        int databaseSizeBeforeCreate = skillSynonymRepository.findAll().size();

        // Create the SkillSynonym
        SkillSynonymDTO skillSynonymDTO = skillSynonymMapper.toDto(skillSynonym);
        restSkillSynonymMockMvc.perform(post("/api/skill-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillSynonymDTO)))
            .andExpect(status().isCreated());

        // Validate the SkillSynonym in the database
        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeCreate + 1);
        SkillSynonym testSkillSynonym = skillSynonymList.get(skillSynonymList.size() - 1);
        assertThat(testSkillSynonym.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSkillSynonymWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = skillSynonymRepository.findAll().size();

        // Create the SkillSynonym with an existing ID
        skillSynonym.setId(1L);
        SkillSynonymDTO skillSynonymDTO = skillSynonymMapper.toDto(skillSynonym);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSkillSynonymMockMvc.perform(post("/api/skill-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillSynonymDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SkillSynonym in the database
        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = skillSynonymRepository.findAll().size();
        // set the field null
        skillSynonym.setName(null);

        // Create the SkillSynonym, which fails.
        SkillSynonymDTO skillSynonymDTO = skillSynonymMapper.toDto(skillSynonym);

        restSkillSynonymMockMvc.perform(post("/api/skill-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillSynonymDTO)))
            .andExpect(status().isBadRequest());

        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSkillSynonyms() throws Exception {
        // Initialize the database
        skillSynonymRepository.saveAndFlush(skillSynonym);

        // Get all the skillSynonymList
        restSkillSynonymMockMvc.perform(get("/api/skill-synonyms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skillSynonym.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getSkillSynonym() throws Exception {
        // Initialize the database
        skillSynonymRepository.saveAndFlush(skillSynonym);

        // Get the skillSynonym
        restSkillSynonymMockMvc.perform(get("/api/skill-synonyms/{id}", skillSynonym.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(skillSynonym.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSkillSynonym() throws Exception {
        // Get the skillSynonym
        restSkillSynonymMockMvc.perform(get("/api/skill-synonyms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSkillSynonym() throws Exception {
        // Initialize the database
        skillSynonymRepository.saveAndFlush(skillSynonym);

        int databaseSizeBeforeUpdate = skillSynonymRepository.findAll().size();

        // Update the skillSynonym
        SkillSynonym updatedSkillSynonym = skillSynonymRepository.findById(skillSynonym.getId()).get();
        // Disconnect from session so that the updates on updatedSkillSynonym are not directly saved in db
        em.detach(updatedSkillSynonym);
        updatedSkillSynonym
            .name(UPDATED_NAME);
        SkillSynonymDTO skillSynonymDTO = skillSynonymMapper.toDto(updatedSkillSynonym);

        restSkillSynonymMockMvc.perform(put("/api/skill-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillSynonymDTO)))
            .andExpect(status().isOk());

        // Validate the SkillSynonym in the database
        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeUpdate);
        SkillSynonym testSkillSynonym = skillSynonymList.get(skillSynonymList.size() - 1);
        assertThat(testSkillSynonym.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingSkillSynonym() throws Exception {
        int databaseSizeBeforeUpdate = skillSynonymRepository.findAll().size();

        // Create the SkillSynonym
        SkillSynonymDTO skillSynonymDTO = skillSynonymMapper.toDto(skillSynonym);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSkillSynonymMockMvc.perform(put("/api/skill-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(skillSynonymDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SkillSynonym in the database
        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSkillSynonym() throws Exception {
        // Initialize the database
        skillSynonymRepository.saveAndFlush(skillSynonym);

        int databaseSizeBeforeDelete = skillSynonymRepository.findAll().size();

        // Delete the skillSynonym
        restSkillSynonymMockMvc.perform(delete("/api/skill-synonyms/{id}", skillSynonym.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SkillSynonym> skillSynonymList = skillSynonymRepository.findAll();
        assertThat(skillSynonymList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkillSynonym.class);
        SkillSynonym skillSynonym1 = new SkillSynonym();
        skillSynonym1.setId(1L);
        SkillSynonym skillSynonym2 = new SkillSynonym();
        skillSynonym2.setId(skillSynonym1.getId());
        assertThat(skillSynonym1).isEqualTo(skillSynonym2);
        skillSynonym2.setId(2L);
        assertThat(skillSynonym1).isNotEqualTo(skillSynonym2);
        skillSynonym1.setId(null);
        assertThat(skillSynonym1).isNotEqualTo(skillSynonym2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SkillSynonymDTO.class);
        SkillSynonymDTO skillSynonymDTO1 = new SkillSynonymDTO();
        skillSynonymDTO1.setId(1L);
        SkillSynonymDTO skillSynonymDTO2 = new SkillSynonymDTO();
        assertThat(skillSynonymDTO1).isNotEqualTo(skillSynonymDTO2);
        skillSynonymDTO2.setId(skillSynonymDTO1.getId());
        assertThat(skillSynonymDTO1).isEqualTo(skillSynonymDTO2);
        skillSynonymDTO2.setId(2L);
        assertThat(skillSynonymDTO1).isNotEqualTo(skillSynonymDTO2);
        skillSynonymDTO1.setId(null);
        assertThat(skillSynonymDTO1).isNotEqualTo(skillSynonymDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(skillSynonymMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(skillSynonymMapper.fromId(null)).isNull();
    }
}
