package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByDifficulty(String difficulty);

}
