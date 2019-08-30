package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.StopWordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link StopWord} and its DTO {@link StopWordDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface StopWordMapper extends EntityMapper<StopWordDTO, StopWord> {



    default StopWord fromId(Long id) {
        if (id == null) {
            return null;
        }
        StopWord stopWord = new StopWord();
        stopWord.setId(id);
        return stopWord;
    }
}
