package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.CandidateDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidate} and its DTO {@link CandidateDTO}.
 */
@Mapper(componentModel = "spring", uses = {CityMapper.class, SchoolMapper.class, CandidateSkillMapper.class})
public interface CandidateMapper extends EntityMapper<CandidateDTO, Candidate> {


    @Mapping(target = "candidateSkills", ignore = true)
    @Mapping(target = "removeCandidateSkill", ignore = true)
    @Mapping(target = "removeCity", ignore = true)
    @Mapping(target = "removeSchool", ignore = true)
    Candidate toEntity(CandidateDTO candidateDTO);

    default Candidate fromId(Long id) {
        if (id == null) {
            return null;
        }
        Candidate candidate = new Candidate();
        candidate.setId(id);
        return candidate;
    }
}
