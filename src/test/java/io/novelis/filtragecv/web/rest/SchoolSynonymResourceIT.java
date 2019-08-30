package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.FiltragecvApp;
import io.novelis.filtragecv.domain.SchoolSynonym;
import io.novelis.filtragecv.domain.School;
import io.novelis.filtragecv.repository.SchoolSynonymRepository;
import io.novelis.filtragecv.service.SchoolSynonymService;
import io.novelis.filtragecv.service.dto.SchoolSynonymDTO;
import io.novelis.filtragecv.service.mapper.SchoolSynonymMapper;
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
 * Integration tests for the {@Link SchoolSynonymResource} REST controller.
 */
@SpringBootTest(classes = FiltragecvApp.class)
public class SchoolSynonymResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private SchoolSynonymRepository schoolSynonymRepository;

    @Autowired
    private SchoolSynonymMapper schoolSynonymMapper;

    @Autowired
    private SchoolSynonymService schoolSynonymService;

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

    private MockMvc restSchoolSynonymMockMvc;

    private SchoolSynonym schoolSynonym;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SchoolSynonymResource schoolSynonymResource = new SchoolSynonymResource(schoolSynonymService);
        this.restSchoolSynonymMockMvc = MockMvcBuilders.standaloneSetup(schoolSynonymResource)
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
    public static SchoolSynonym createEntity(EntityManager em) {
        SchoolSynonym schoolSynonym = new SchoolSynonym()
            .name(DEFAULT_NAME);
        // Add required entity
        School school;
        if (TestUtil.findAll(em, School.class).isEmpty()) {
            school = SchoolResourceIT.createEntity(em);
            em.persist(school);
            em.flush();
        } else {
            school = TestUtil.findAll(em, School.class).get(0);
        }
        schoolSynonym.setSchool(school);
        return schoolSynonym;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SchoolSynonym createUpdatedEntity(EntityManager em) {
        SchoolSynonym schoolSynonym = new SchoolSynonym()
            .name(UPDATED_NAME);
        // Add required entity
        School school;
        if (TestUtil.findAll(em, School.class).isEmpty()) {
            school = SchoolResourceIT.createUpdatedEntity(em);
            em.persist(school);
            em.flush();
        } else {
            school = TestUtil.findAll(em, School.class).get(0);
        }
        schoolSynonym.setSchool(school);
        return schoolSynonym;
    }

    @BeforeEach
    public void initTest() {
        schoolSynonym = createEntity(em);
    }

    @Test
    @Transactional
    public void createSchoolSynonym() throws Exception {
        int databaseSizeBeforeCreate = schoolSynonymRepository.findAll().size();

        // Create the SchoolSynonym
        SchoolSynonymDTO schoolSynonymDTO = schoolSynonymMapper.toDto(schoolSynonym);
        restSchoolSynonymMockMvc.perform(post("/api/school-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schoolSynonymDTO)))
            .andExpect(status().isCreated());

        // Validate the SchoolSynonym in the database
        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeCreate + 1);
        SchoolSynonym testSchoolSynonym = schoolSynonymList.get(schoolSynonymList.size() - 1);
        assertThat(testSchoolSynonym.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSchoolSynonymWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = schoolSynonymRepository.findAll().size();

        // Create the SchoolSynonym with an existing ID
        schoolSynonym.setId(1L);
        SchoolSynonymDTO schoolSynonymDTO = schoolSynonymMapper.toDto(schoolSynonym);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchoolSynonymMockMvc.perform(post("/api/school-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schoolSynonymDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SchoolSynonym in the database
        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = schoolSynonymRepository.findAll().size();
        // set the field null
        schoolSynonym.setName(null);

        // Create the SchoolSynonym, which fails.
        SchoolSynonymDTO schoolSynonymDTO = schoolSynonymMapper.toDto(schoolSynonym);

        restSchoolSynonymMockMvc.perform(post("/api/school-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schoolSynonymDTO)))
            .andExpect(status().isBadRequest());

        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSchoolSynonyms() throws Exception {
        // Initialize the database
        schoolSynonymRepository.saveAndFlush(schoolSynonym);

        // Get all the schoolSynonymList
        restSchoolSynonymMockMvc.perform(get("/api/school-synonyms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schoolSynonym.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getSchoolSynonym() throws Exception {
        // Initialize the database
        schoolSynonymRepository.saveAndFlush(schoolSynonym);

        // Get the schoolSynonym
        restSchoolSynonymMockMvc.perform(get("/api/school-synonyms/{id}", schoolSynonym.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(schoolSynonym.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSchoolSynonym() throws Exception {
        // Get the schoolSynonym
        restSchoolSynonymMockMvc.perform(get("/api/school-synonyms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchoolSynonym() throws Exception {
        // Initialize the database
        schoolSynonymRepository.saveAndFlush(schoolSynonym);

        int databaseSizeBeforeUpdate = schoolSynonymRepository.findAll().size();

        // Update the schoolSynonym
        SchoolSynonym updatedSchoolSynonym = schoolSynonymRepository.findById(schoolSynonym.getId()).get();
        // Disconnect from session so that the updates on updatedSchoolSynonym are not directly saved in db
        em.detach(updatedSchoolSynonym);
        updatedSchoolSynonym
            .name(UPDATED_NAME);
        SchoolSynonymDTO schoolSynonymDTO = schoolSynonymMapper.toDto(updatedSchoolSynonym);

        restSchoolSynonymMockMvc.perform(put("/api/school-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schoolSynonymDTO)))
            .andExpect(status().isOk());

        // Validate the SchoolSynonym in the database
        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeUpdate);
        SchoolSynonym testSchoolSynonym = schoolSynonymList.get(schoolSynonymList.size() - 1);
        assertThat(testSchoolSynonym.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingSchoolSynonym() throws Exception {
        int databaseSizeBeforeUpdate = schoolSynonymRepository.findAll().size();

        // Create the SchoolSynonym
        SchoolSynonymDTO schoolSynonymDTO = schoolSynonymMapper.toDto(schoolSynonym);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchoolSynonymMockMvc.perform(put("/api/school-synonyms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schoolSynonymDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SchoolSynonym in the database
        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSchoolSynonym() throws Exception {
        // Initialize the database
        schoolSynonymRepository.saveAndFlush(schoolSynonym);

        int databaseSizeBeforeDelete = schoolSynonymRepository.findAll().size();

        // Delete the schoolSynonym
        restSchoolSynonymMockMvc.perform(delete("/api/school-synonyms/{id}", schoolSynonym.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SchoolSynonym> schoolSynonymList = schoolSynonymRepository.findAll();
        assertThat(schoolSynonymList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchoolSynonym.class);
        SchoolSynonym schoolSynonym1 = new SchoolSynonym();
        schoolSynonym1.setId(1L);
        SchoolSynonym schoolSynonym2 = new SchoolSynonym();
        schoolSynonym2.setId(schoolSynonym1.getId());
        assertThat(schoolSynonym1).isEqualTo(schoolSynonym2);
        schoolSynonym2.setId(2L);
        assertThat(schoolSynonym1).isNotEqualTo(schoolSynonym2);
        schoolSynonym1.setId(null);
        assertThat(schoolSynonym1).isNotEqualTo(schoolSynonym2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchoolSynonymDTO.class);
        SchoolSynonymDTO schoolSynonymDTO1 = new SchoolSynonymDTO();
        schoolSynonymDTO1.setId(1L);
        SchoolSynonymDTO schoolSynonymDTO2 = new SchoolSynonymDTO();
        assertThat(schoolSynonymDTO1).isNotEqualTo(schoolSynonymDTO2);
        schoolSynonymDTO2.setId(schoolSynonymDTO1.getId());
        assertThat(schoolSynonymDTO1).isEqualTo(schoolSynonymDTO2);
        schoolSynonymDTO2.setId(2L);
        assertThat(schoolSynonymDTO1).isNotEqualTo(schoolSynonymDTO2);
        schoolSynonymDTO1.setId(null);
        assertThat(schoolSynonymDTO1).isNotEqualTo(schoolSynonymDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(schoolSynonymMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(schoolSynonymMapper.fromId(null)).isNull();
    }
}
