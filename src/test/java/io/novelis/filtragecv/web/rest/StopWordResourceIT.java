package io.novelis.filtragecv.web.rest;

import io.novelis.filtragecv.FiltragecvApp;
import io.novelis.filtragecv.domain.StopWord;
import io.novelis.filtragecv.repository.StopWordRepository;
import io.novelis.filtragecv.service.StopWordService;
import io.novelis.filtragecv.service.dto.StopWordDTO;
import io.novelis.filtragecv.service.mapper.StopWordMapper;
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
 * Integration tests for the {@Link StopWordResource} REST controller.
 */
@SpringBootTest(classes = FiltragecvApp.class)
public class StopWordResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private StopWordRepository stopWordRepository;

    @Autowired
    private StopWordMapper stopWordMapper;

    @Autowired
    private StopWordService stopWordService;

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

    private MockMvc restStopWordMockMvc;

    private StopWord stopWord;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StopWordResource stopWordResource = new StopWordResource(stopWordService);
        this.restStopWordMockMvc = MockMvcBuilders.standaloneSetup(stopWordResource)
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
    public static StopWord createEntity(EntityManager em) {
        StopWord stopWord = new StopWord()
            .name(DEFAULT_NAME);
        return stopWord;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StopWord createUpdatedEntity(EntityManager em) {
        StopWord stopWord = new StopWord()
            .name(UPDATED_NAME);
        return stopWord;
    }

    @BeforeEach
    public void initTest() {
        stopWord = createEntity(em);
    }

    @Test
    @Transactional
    public void createStopWord() throws Exception {
        int databaseSizeBeforeCreate = stopWordRepository.findAll().size();

        // Create the StopWord
        StopWordDTO stopWordDTO = stopWordMapper.toDto(stopWord);
        restStopWordMockMvc.perform(post("/api/stop-words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stopWordDTO)))
            .andExpect(status().isCreated());

        // Validate the StopWord in the database
        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeCreate + 1);
        StopWord testStopWord = stopWordList.get(stopWordList.size() - 1);
        assertThat(testStopWord.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createStopWordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = stopWordRepository.findAll().size();

        // Create the StopWord with an existing ID
        stopWord.setId(1L);
        StopWordDTO stopWordDTO = stopWordMapper.toDto(stopWord);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStopWordMockMvc.perform(post("/api/stop-words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stopWordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StopWord in the database
        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stopWordRepository.findAll().size();
        // set the field null
        stopWord.setName(null);

        // Create the StopWord, which fails.
        StopWordDTO stopWordDTO = stopWordMapper.toDto(stopWord);

        restStopWordMockMvc.perform(post("/api/stop-words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stopWordDTO)))
            .andExpect(status().isBadRequest());

        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStopWords() throws Exception {
        // Initialize the database
        stopWordRepository.saveAndFlush(stopWord);

        // Get all the stopWordList
        restStopWordMockMvc.perform(get("/api/stop-words?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stopWord.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getStopWord() throws Exception {
        // Initialize the database
        stopWordRepository.saveAndFlush(stopWord);

        // Get the stopWord
        restStopWordMockMvc.perform(get("/api/stop-words/{id}", stopWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(stopWord.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStopWord() throws Exception {
        // Get the stopWord
        restStopWordMockMvc.perform(get("/api/stop-words/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStopWord() throws Exception {
        // Initialize the database
        stopWordRepository.saveAndFlush(stopWord);

        int databaseSizeBeforeUpdate = stopWordRepository.findAll().size();

        // Update the stopWord
        StopWord updatedStopWord = stopWordRepository.findById(stopWord.getId()).get();
        // Disconnect from session so that the updates on updatedStopWord are not directly saved in db
        em.detach(updatedStopWord);
        updatedStopWord
            .name(UPDATED_NAME);
        StopWordDTO stopWordDTO = stopWordMapper.toDto(updatedStopWord);

        restStopWordMockMvc.perform(put("/api/stop-words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stopWordDTO)))
            .andExpect(status().isOk());

        // Validate the StopWord in the database
        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeUpdate);
        StopWord testStopWord = stopWordList.get(stopWordList.size() - 1);
        assertThat(testStopWord.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingStopWord() throws Exception {
        int databaseSizeBeforeUpdate = stopWordRepository.findAll().size();

        // Create the StopWord
        StopWordDTO stopWordDTO = stopWordMapper.toDto(stopWord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStopWordMockMvc.perform(put("/api/stop-words")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stopWordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StopWord in the database
        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteStopWord() throws Exception {
        // Initialize the database
        stopWordRepository.saveAndFlush(stopWord);

        int databaseSizeBeforeDelete = stopWordRepository.findAll().size();

        // Delete the stopWord
        restStopWordMockMvc.perform(delete("/api/stop-words/{id}", stopWord.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StopWord> stopWordList = stopWordRepository.findAll();
        assertThat(stopWordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StopWord.class);
        StopWord stopWord1 = new StopWord();
        stopWord1.setId(1L);
        StopWord stopWord2 = new StopWord();
        stopWord2.setId(stopWord1.getId());
        assertThat(stopWord1).isEqualTo(stopWord2);
        stopWord2.setId(2L);
        assertThat(stopWord1).isNotEqualTo(stopWord2);
        stopWord1.setId(null);
        assertThat(stopWord1).isNotEqualTo(stopWord2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StopWordDTO.class);
        StopWordDTO stopWordDTO1 = new StopWordDTO();
        stopWordDTO1.setId(1L);
        StopWordDTO stopWordDTO2 = new StopWordDTO();
        assertThat(stopWordDTO1).isNotEqualTo(stopWordDTO2);
        stopWordDTO2.setId(stopWordDTO1.getId());
        assertThat(stopWordDTO1).isEqualTo(stopWordDTO2);
        stopWordDTO2.setId(2L);
        assertThat(stopWordDTO1).isNotEqualTo(stopWordDTO2);
        stopWordDTO1.setId(null);
        assertThat(stopWordDTO1).isNotEqualTo(stopWordDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(stopWordMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(stopWordMapper.fromId(null)).isNull();
    }
}
