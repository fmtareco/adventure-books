package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {

    /**
     * book numeric key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * exposes the book title
     */
    @Column(nullable = false)
    private String title;

    /**
     * exposes the book author
     */
    @Column(nullable = false)
    private String author;

    /**
     * book sections :
     *      aggregates the book sections
     */
    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    /**
     * adds a section to the book guaraneeing also that the section
     * itself will be associated with the book
     * @param section
     */
    public void addSection(Section section) {
        getSections().add(section);
        section.setBook(this);
    }

    /**
     * book categories :
     *      list of categories in which the book is classified
     */
    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    /**
     * book difficulty :
     *      exposes the book difficulty level
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    public enum Difficulty implements Serializable {
        EASY,
        MEDIUM,
        HARD;
        public static Difficulty from(String difficulty) {
            try {
                return Difficulty.valueOf(difficulty);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Difficulty", difficulty, valuesToString());
            }
        }
        public static String valuesToString() {
            return EnumSet.allOf(Difficulty.class).stream().map(Enum::toString).collect(Collectors.joining(","));
        }
    }

    /**
     * book condition :
     *      exposes the book valid status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condition condition;

    public enum Condition implements Serializable {
        OK,
        INVALID_BEGIN,
        NO_END,
        INVALID_GOTO,
        NO_OPTIONS;
        public static Condition from(String condition) {
            try {
                return Condition.valueOf(condition);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Condition", condition, valuesToString());
            }
        }
        public static String valuesToString() {
            return EnumSet.allOf(Condition.class).stream().map(Enum::toString).collect(Collectors.joining(","));
        }
    }

    /**
     * evaluates the book condition, aplying the valiations rules
     * @return Condition
     */
    public Condition checkCondition() {
        if (!hasOneOnlyBeginSection())
            return Condition.INVALID_BEGIN;
        if (!hasAtLeastOneEndSection())
            return  Condition.NO_END;
        if (hasInvalidGoToSection())
            return Condition.INVALID_GOTO;
        if (hasNonFinalSectionWithoutOptions())
            return Condition.NO_OPTIONS;
        return Condition.OK;
    }

    /**
     * updates the book condition, based on condition evaluation
     * @return Condition
     */
    public void setBookCondition() {
        condition = checkCondition();
    }


    /**
     * checks if Book has none, or more than one beginning
     * @return
     */
    public boolean hasOneOnlyBeginSection() {
         return getSections().stream()
                 .filter(s -> s.getType() == Section.Type.BEGIN).count() ==1;
    }

    /**
     * checks if Book has no ending (but can have multiple)
     * @return
     */
    public boolean hasAtLeastOneEndSection() {
        return getSections().stream()
                .anyMatch(s -> s.getType() == Section.Type.END);
    }

    /**
     * checks if Book has invalid next section id
     * @return
     */
    public boolean hasInvalidGoToSection() {
        return getSections().stream()
            .flatMap(s -> s.getOptions().stream())
            .anyMatch(o -> getSections().stream().noneMatch(sec -> sec.getSectionNumber().equals(o.getGotoSectionNumber())));
    }

    /**
     * checks if Book has a non-ending section with no options
     * @return
     */
    public boolean hasNonFinalSectionWithoutOptions() {
        return getSections().stream()
                .filter(s -> s.getType() != Section.Type.END)
                .anyMatch(s -> s.getOptions().isEmpty());
    }

}
