package io.novelis.filtragecv.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.novelis.filtragecv.Keyword;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.*;

/**
 * A DTO for the {@link io.novelis.filtragecv.domain.Candidate} entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateDTO implements Serializable {

    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private Boolean rejected;

    @NotNull
    private Integer funcId;

    private Integer totalSkillsCount;

    private Integer score;

    @JsonIgnore
    private Set<CityDTO> cities = new HashSet<>();

    @JsonIgnore
    private Set<SchoolDTO> schools = new HashSet<>();

    @JsonIgnore
    private Set<CandidateSkillDTO> candidateSkills = new HashSet<>();

    private List<Keyword> keywords;

    private Set<CandidateSkillDTO> getCandidateSkills() {
        return candidateSkills;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCandidateSkills(Set<CandidateSkillDTO> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public Integer getTotalSkillsCount() {
        return totalSkillsCount;
    }

    public void setTotalSkillsCount(Integer totalSkillsCount) {
        this.totalSkillsCount = totalSkillsCount;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean isRejected() {
        return rejected;
    }

    public void setRejected(Boolean rejected) {
        this.rejected = rejected;
    }

    public Integer getFuncId() {
        return funcId;
    }

    public void setFuncId(Integer funcId) {
        this.funcId = funcId;
    }

    public Set<CityDTO> getCities() {
        return cities;
    }

    public void setCities(Set<CityDTO> cities) {
        this.cities = cities;
    }

    public Set<SchoolDTO> getSchools() {
        return schools;
    }

    public void setSchools(Set<SchoolDTO> schools) {
        this.schools = schools;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CandidateDTO candidateDTO = (CandidateDTO) o;
        if (candidateDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), candidateDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CandidateDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", rejected='" + isRejected() + "'" +
            ", funcId=" + getFuncId() +
            "}";
    }
}
