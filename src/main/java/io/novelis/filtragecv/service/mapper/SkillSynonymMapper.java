package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.SkillSynonymDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SkillSynonym} and its DTO {@link SkillSynonymDTO}.
 */
@Mapper(componentModel = "spring", uses = {SkillMapper.class})
public interface SkillSynonymMapper extends EntityMapper<SkillSynonymDTO, SkillSynonym> {

    @Mapping(source = "skill.id", target = "skillId")
    SkillSynonymDTO toDto(SkillSynonym skillSynonym);

    @Mapping(source = "skillId", target = "skill")
    SkillSynonym toEntity(SkillSynonymDTO skillSynonymDTO);

    default SkillSynonym fromId(Long id) {
        if (id == null) {
            return null;
        }
        SkillSynonym skillSynonym = new SkillSynonym();
        skillSynonym.setId(id);
        return skillSynonym;
    }
}
