package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.SkillDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Skill} and its DTO {@link SkillDTO}.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface SkillMapper extends EntityMapper<SkillDTO, Skill> {

    @Mapping(source = "category.id", target = "categoryId")
    SkillDTO toDto(Skill skill);

    @Mapping(target = "candidateSkills", ignore = true)
    @Mapping(target = "removeCandidateSkill", ignore = true)
    @Mapping(source = "categoryId", target = "category")
    @Mapping(target = "skillSynonyms", ignore = true)
    @Mapping(target = "removeSkillSynonym", ignore = true)
    Skill toEntity(SkillDTO skillDTO);

    default Skill fromId(Long id) {
        if (id == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }
}
