package prs.fmtareco.adventure.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByDifficulty(String difficulty);

    List<Book> findDistinctByCategories_NameIn(Set<String> names);

    static Specification<Book> byFilters(
                Optional<String> title,
                Optional<String> author,
                Optional<String> category,
                Optional<String> difficulty) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            author.filter(a -> !a.isBlank())
                    .map(String::toLowerCase)
                    .ifPresent(a ->
                            predicates.add(builder.like(builder.lower(root.get("author")), "%" + a + "%")));

            title.filter(t -> !t.isBlank())
                    .map(String::toLowerCase)
                    .ifPresent(t ->
                            predicates.add(builder.like(builder.lower(root.get("title")), "%" + t + "%")));

            difficulty.filter(d -> !d.isBlank())
                    .map(String::toUpperCase)
                    .flatMap(d -> {
                        try {
                            return Optional.of(Book.Difficulty.from(d));
                        } catch (InvalidEnumValueException ex) {
                            return Optional.empty();
                        }
                    })
                    .ifPresent(d ->
                            predicates.add(builder.equal(root.get("difficulty"), d))
                    );

            category.filter(c -> !c.isBlank())
                    .ifPresent(c -> {
                        Join<Book, Category> categoryJoin = root.join("categories", JoinType.INNER);
                        predicates.add(
                                builder.equal(
                                        builder.lower(categoryJoin.get("name")),
                                        c.toLowerCase()
                                )
                        );
                        query.distinct(true); // prevent duplicates
                    });

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
