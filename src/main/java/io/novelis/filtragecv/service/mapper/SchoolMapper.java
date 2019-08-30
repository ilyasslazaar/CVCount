package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.SchoolDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link School} and its DTO {@link SchoolDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SchoolMapper extends EntityMapper<SchoolDTO, School> {


    @Mapping(target = "schoolSynonyms", ignore = true)
    @Mapping(target = "removeSchoolSynonym", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "removeCandidate", ignore = true)
    School toEntity(SchoolDTO schoolDTO);

    default School fromId(Long id) {
        if (id == null) {
            return null;
        }
        School school = new School();
        school.setId(id);
        return school;
    }
}
