package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.SchoolSynonymDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SchoolSynonym} and its DTO {@link SchoolSynonymDTO}.
 */
@Mapper(componentModel = "spring", uses = {SchoolMapper.class})
public interface SchoolSynonymMapper extends EntityMapper<SchoolSynonymDTO, SchoolSynonym> {

    @Mapping(source = "school.id", target = "schoolId")
    SchoolSynonymDTO toDto(SchoolSynonym schoolSynonym);

    @Mapping(source = "schoolId", target = "school")
    SchoolSynonym toEntity(SchoolSynonymDTO schoolSynonymDTO);

    default SchoolSynonym fromId(Long id) {
        if (id == null) {
            return null;
        }
        SchoolSynonym schoolSynonym = new SchoolSynonym();
        schoolSynonym.setId(id);
        return schoolSynonym;
    }
}
