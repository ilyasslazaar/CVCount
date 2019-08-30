package io.novelis.filtragecv.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.novelis.filtragecv.domain.CandidateSkill} entity.
 */
public class CandidateSkillDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer count;


    private Long skillId;

    private Long candidateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CandidateSkillDTO candidateSkillDTO = (CandidateSkillDTO) o;
        if (candidateSkillDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), candidateSkillDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CandidateSkillDTO{" +
            "id=" + getId() +
            ", count=" + getCount() +
            ", skill=" + getSkillId() +
            ", candidate=" + getCandidateId() +
            "}";
    }
}
