package io.novelis.filtragecv.repository;

import io.novelis.filtragecv.domain.StopWord;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the StopWord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StopWordRepository extends JpaRepository<StopWord, Long> {
    Optional<StopWord> findByName(String name);
}
