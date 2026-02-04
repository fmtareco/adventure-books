package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Section;

import java.util.Optional;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByBookId(Long bookId);

    Optional<Section> findByBookIdAndSectionNumber(Long bookId, Integer sectionNumber);

}
