package io.novelis.filtragecv.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.novelis.filtragecv.domain.StopWord} entity.
 */
public class StopWordDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 42)
    private String name;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StopWordDTO stopWordDTO = (StopWordDTO) o;
        if (stopWordDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stopWordDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StopWordDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
