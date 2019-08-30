package io.novelis.filtragecv.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Skill.
 */
@Entity
@Table(name = "skill")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 42)
    @Column(name = "name", length = 42, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<CandidateSkill> candidateSkills = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("skills")
    private Category category;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SkillSynonym> skillSynonyms = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Skill name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CandidateSkill> getCandidateSkills() {
        return candidateSkills;
    }

    public Skill candidateSkills(Set<CandidateSkill> candidateSkills) {
        this.candidateSkills = candidateSkills;
        return this;
    }

    public Skill addCandidateSkill(CandidateSkill candidateSkill) {
        this.candidateSkills.add(candidateSkill);
        candidateSkill.setSkill(this);
        return this;
    }

    public Skill removeCandidateSkill(CandidateSkill candidateSkill) {
        this.candidateSkills.remove(candidateSkill);
        candidateSkill.setSkill(null);
        return this;
    }

    public void setCandidateSkills(Set<CandidateSkill> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    public Skill getSkillByCandidate(Candidate candidate) {
        for (CandidateSkill candidateSkill :
            this.candidateSkills) {
            if (candidateSkill.getCandidate().getId() == candidate.getId()) {
                return candidateSkill.getSkill();
            }
        }
        return null;
    }

    public Category getCategory() {
        return category;
    }

    public Skill category(Category category) {
        this.category = category;
        return this;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<SkillSynonym> getSkillSynonyms() {
        return skillSynonyms;
    }

    public Skill skillSynonyms(Set<SkillSynonym> skillSynonyms) {
        this.skillSynonyms = skillSynonyms;
        return this;
    }

    public Skill addSkillSynonym(SkillSynonym skillSynonym) {
        this.skillSynonyms.add(skillSynonym);
        skillSynonym.setSkill(this);
        return this;
    }

    public Skill removeSkillSynonym(SkillSynonym skillSynonym) {
        this.skillSynonyms.remove(skillSynonym);
        skillSynonym.setSkill(null);
        return this;
    }

    public void setSkillSynonyms(Set<SkillSynonym> skillSynonyms) {
        this.skillSynonyms = skillSynonyms;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        return id != null && id.equals(((Skill) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Skill{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
