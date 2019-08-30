package io.novelis.filtragecv.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.novelis.filtragecv.domain.SchoolSynonym} entity.
 */
public class SchoolSynonymDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 42)
    private String name;


    private Long schoolId;

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

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SchoolSynonymDTO schoolSynonymDTO = (SchoolSynonymDTO) o;
        if (schoolSynonymDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), schoolSynonymDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SchoolSynonymDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", school=" + getSchoolId() +
            "}";
    }
}
