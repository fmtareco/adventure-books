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

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    static Specification<Book> byFilters(
                String _title,
                String _author,
                String _category,
                String _difficulty,
                String _condition) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional<String> condition = Optional.ofNullable(_condition);
            condition.filter(c -> !c.isBlank())
                    .map(String::toUpperCase)
                    .flatMap(c -> Book.Condition.fromString(c))
                    .ifPresent(c -> predicates.add(builder.equal(root.get("condition"), c)));

            Optional<String> author = Optional.ofNullable(_author);
            author.filter(a -> !a.isBlank())
                    .map(String::toLowerCase)
                    .ifPresent(a ->
                            predicates.add(builder.like(builder.lower(root.get("author")), "%" + a + "%")));

            Optional<String> title = Optional.ofNullable(_title);
            title.filter(t -> !t.isBlank())
                    .map(String::toLowerCase)
                    .ifPresent(t ->
                            predicates.add(builder.like(builder.lower(root.get("title")), "%" + t + "%")));

            Optional<String> difficulty = Optional.ofNullable(_difficulty);
            difficulty.filter(d -> !d.isBlank())
                    .map(String::toUpperCase)
                    .flatMap(d -> Book.Difficulty.fromString(d))
                    .ifPresent(d -> predicates.add(builder.equal(root.get("difficulty"), d)));

            Optional<String> category = Optional.ofNullable(_category);
            category.filter(c -> !c.isBlank())
                    .ifPresent(c -> {
                        Join<Book, Category> categoryJoin = root.join("categories", JoinType.INNER);
                        predicates.add(
                                builder.equal(builder.lower(categoryJoin.get("name")), c.toLowerCase()
                                )
                        );
                        query.distinct(true); // prevent duplicates
                    });

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);
}
