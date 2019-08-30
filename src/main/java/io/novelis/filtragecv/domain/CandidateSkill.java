package io.novelis.filtragecv.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A CandidateSkill.
 */
@Entity
@Table(name = "candidate_skill")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CandidateSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @NotNull
    @JsonIgnoreProperties("candidateSkills")
    private Skill skill;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("candidateSkills")
    private Candidate candidate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public CandidateSkill() {

    }
    public CandidateSkill(Candidate candidate, Skill skill, Integer count) {
        setSkill(skill);
        setCandidate(candidate);
        setCount(count);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public CandidateSkill count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Skill getSkill() {
        return skill;
    }

    public CandidateSkill skill(Skill skill) {
        this.skill = skill;
        return this;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public CandidateSkill candidate(Candidate candidate) {
        this.candidate = candidate;
        return this;
    }


    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidateSkill)) {
            return false;
        }
        return id != null && id.equals(((CandidateSkill) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CandidateSkill{" +
            "id=" + getId() +
            ", count=" + getCount() +
            "}";
    }
}
