package io.novelis.filtragecv.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.novelis.filtragecv.Keyword;
import io.swagger.models.auth.In;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.*;

/**
 * A Candidate.
 */
@Entity
@Table(name = "candidate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class Candidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;

    @NotNull
    @Column(name = "rejected", nullable = false)
    private Boolean rejected = false;

    @NotNull
    @Column(name = "func_id", nullable = false, unique = true)
    private Integer funcId;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<CandidateSkill> candidateSkills = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "candidate_city",
        joinColumns = @JoinColumn(name = "candidate_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> cities = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "candidate_school",
        joinColumns = @JoinColumn(name = "candidate_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "school_id", referencedColumnName = "id"))
    private Set<School> schools = new HashSet<>();
    @Transient
    private Integer score;

    @Transient
    private Integer totalSkillsCount;

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    @Transient
    private List<Keyword> keywords;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public void setTotalSkillsCount(Integer totalSkillsCount) {
        this.totalSkillsCount = totalSkillsCount;
    }

    public Integer getTotalSkillsCount() {
        return totalSkillsCount;
    }
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public Candidate fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean isRejected() {
        return rejected;
    }

    public Candidate rejected(Boolean rejected) {
        this.rejected = rejected;
        return this;
    }

    public void setRejected(Boolean rejected) {
        this.rejected = rejected;
    }

    public Integer getFuncId() {
        return funcId;
    }

    public Candidate funcId(Integer funcId) {
        this.funcId = funcId;
        return this;
    }

    public void setFuncId(Integer funcId) {
        this.funcId = funcId;
    }

    public Set<CandidateSkill> getCandidateSkills() {
        this.totalSkillsCount = 0;
        /*for(CandidateSkill candidateSkill:
            this.candidateSkills) {
            if(candidateSkill.getSkill().getCategory().getName().equals("other")) {
                continue;
            }
            this.totalSkillsCount++;
        }*/
        return candidateSkills;
    }

    public Candidate candidateSkills(Set<CandidateSkill> candidateSkills) {
        this.candidateSkills = candidateSkills;
        return this;
    }

    public Candidate addCandidateSkill(CandidateSkill candidateSkill) {
        this.candidateSkills.add(candidateSkill);
        candidateSkill.setCandidate(this);
        return this;
    }

    public Candidate addSkill(Skill skill, Integer count) {
        CandidateSkill candidateSkill = new CandidateSkill(this, skill, count);
        candidateSkills.add(candidateSkill);
        return this;
    }

    public CandidateSkill findSkill(Skill skill) {
        for (CandidateSkill candidateSkill :
            this.candidateSkills) {
            if (candidateSkill.getSkill().getId() == skill.getId()) {
                return candidateSkill;
            }
        }
        return null;
    }

    public Candidate removeCandidateSkill(CandidateSkill candidateSkill) {
        this.candidateSkills.remove(candidateSkill);
        candidateSkill.setCandidate(null);
        return this;
    }

    public Candidate getCandidateBySkill(Skill skill) {
        for (CandidateSkill candidateSkill :
            this.candidateSkills) {
            if (candidateSkill.getSkill().getId() == skill.getId()) {
                return candidateSkill.getCandidate();
            }
        }
        return null;
    }

    public void setCandidateSkills(Set<CandidateSkill> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }
    public List<Skill> getSkills() {
        List<Skill> skills = new ArrayList<>();
        for(CandidateSkill candidateSkill:
        this.candidateSkills) {
            skills.add(candidateSkill.getSkill());
        }
        return skills;
    }
    public Set<City> getCities() {
        return cities;
    }

    public Candidate cities(Set<City> cities) {
        this.cities = cities;
        return this;
    }

    private Integer calcSimilarSkillScore(Skill skill) {

        if(skill.getCategory().getName().equals("other")) {
            return null;
        }
        Integer similarSkillsCount = 0;
        for (CandidateSkill candidateSkill :
            this.candidateSkills) {
            if(skill.getId() == candidateSkill.getSkill().getId()) {
                continue;
            }
            if (candidateSkill.getSkill().getCategory().getId() == skill.getCategory().getId()) {
                similarSkillsCount += candidateSkill.getCount();
            }
        }
        return similarSkillsCount;
    }

    public void calcScore(List<String> keywords, List<Integer> priorities) {
        this.score = 0;
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        outerloop:
        for (int i = 0; i < keywords.size(); i++) {
            for (CandidateSkill candidateSkill :
                this.candidateSkills) {
                if (candidateSkill.getSkill().getName().equals(keywords.get(i))) {
                    this.score += candidateSkill.getCount() * priorities.get(i);
                    Integer similarSkillScore = calcSimilarSkillScore(candidateSkill.getSkill());
                    if(similarSkillScore != null) {
                        this.score += similarSkillScore * 2;
                    }
                    this.keywords.add(new Keyword(candidateSkill.getSkill().getName(),
                        candidateSkill.getSkill().getCategory().getName(),
                        candidateSkill.getCount(),
                        similarSkillScore));
                    break;
                }
            }
            for (City city :
                this.cities) {
                if (city.getName().equals(keywords.get(i))) {
                    this.score += priorities.get(i);
                    this.keywords.add(new Keyword(city.getName(), "city"));
                    continue outerloop;
                }
            }
            for (School school :
                this.schools) {
                if (school.getName().equals(keywords.get(i))) {
                    this.score += priorities.get(i);
                    this.keywords.add(new Keyword(school.getName(),"school"));
                    continue outerloop;
                }
            }
        }
        if(this.score != 0 && this.totalSkillsCount != null) {
            this.score += this.totalSkillsCount;
        }
    }

    public Candidate addCity(City city) {
        this.cities.add(city);
        city.getCandidates().add(this);
        return this;
    }

    public Candidate removeCity(City city) {
        this.cities.remove(city);
        city.getCandidates().remove(this);
        return this;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }

    public Set<School> getSchools() {
        return schools;
    }

    public Candidate schools(Set<School> schools) {
        this.schools = schools;
        return this;
    }

    public Candidate addSchool(School school) {
        this.schools.add(school);
        school.getCandidates().add(this);
        return this;
    }

    public boolean findSchool(School school) {
        for (School schooll :
            this.schools) {
            if (schooll.getId() == school.getId()) {
                return true;
            }
        }
        return false;
    }

    public Candidate removeSchool(School school) {
        this.schools.remove(school);
        school.getCandidates().remove(this);
        return this;
    }

    public void setSchools(Set<School> schools) {
        this.schools = schools;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidate)) {
            return false;
        }
        return id != null && id.equals(((Candidate) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Candidate{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", rejected='" + isRejected() + "'" +
            ", funcId=" + getFuncId() +
            "}";
    }
}
