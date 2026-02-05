package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;
import prs.fmtareco.adventure.model.Section;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String categoryName);

}
