package io.novelis.filtragecv.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A School.
 */
@Entity
@Table(name = "school")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 42)
    @Column(name = "name", length = 42, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "school")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SchoolSynonym> schoolSynonyms = new HashSet<>();

    @ManyToMany(mappedBy = "schools")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Candidate> candidates = new HashSet<>();

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

    public School name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SchoolSynonym> getSchoolSynonyms() {
        return schoolSynonyms;
    }

    public School schoolSynonyms(Set<SchoolSynonym> schoolSynonyms) {
        this.schoolSynonyms = schoolSynonyms;
        return this;
    }

    public School addSchoolSynonym(SchoolSynonym schoolSynonym) {
        this.schoolSynonyms.add(schoolSynonym);
        schoolSynonym.setSchool(this);
        return this;
    }

    public School removeSchoolSynonym(SchoolSynonym schoolSynonym) {
        this.schoolSynonyms.remove(schoolSynonym);
        schoolSynonym.setSchool(null);
        return this;
    }

    public void setSchoolSynonyms(Set<SchoolSynonym> schoolSynonyms) {
        this.schoolSynonyms = schoolSynonyms;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public School candidates(Set<Candidate> candidates) {
        this.candidates = candidates;
        return this;
    }

    public School addCandidate(Candidate candidate) {
        this.candidates.add(candidate);
        candidate.getSchools().add(this);
        return this;
    }

    public School removeCandidate(Candidate candidate) {
        this.candidates.remove(candidate);
        candidate.getSchools().remove(this);
        return this;
    }

    public void setCandidates(Set<Candidate> candidates) {
        this.candidates = candidates;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof School)) {
            return false;
        }
        return id != null && id.equals(((School) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "School{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
