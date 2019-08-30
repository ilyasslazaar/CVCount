package io.novelis.filtragecv.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.novelis.filtragecv.domain.SkillSynonym} entity.
 */
public class SkillSynonymDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 42)
    private String name;


    private Long skillId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkillSynonymDTO skillSynonymDTO = (SkillSynonymDTO) o;
        if (skillSynonymDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), skillSynonymDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SkillSynonymDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", skill=" + getSkillId() +
            "}";
    }
}
