package prs.fmtareco.adventure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Section;

import java.util.Optional;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByBookId(Long bookId);

    Page<Section> findByBookId(Long bookId, Pageable pageable);

    Optional<Section> findByBookIdAndSectionNumber(Long bookId, Integer sectionNumber);

}
