package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.*;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @Builder.Default
    private List<Section> sections = new ArrayList<>();

    /**
     * adds a section to the book guaraneeing also that the section
     * itself will be associated with the book
     * @param section : the section to be associated
     */
    public void addSection(Section section) {
        getSections().add(section);
        section.setBook(this);
    }

    /**
     * fetches the initial section of the book
     * @return begin section (optional)
     */
    public Optional<Section> getInitialSection() {
        return getSections().stream()
                .filter(s -> s.getType() == Section.Type.BEGIN)
                .findFirst();
    }

    public Optional<Section> getSectionNumber(int sectionNumber) {
        return getSections().stream()
                .filter(s -> s.getSectionNumber() == sectionNumber)
                .findFirst();
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
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    /**
     * associates the book w/ a category
     *
     * @param category category to add
     */
    public void addCategory(Category category) {
        if (category == null) {
            return;
        }
        getCategories().add(category);
    }
    /**
     * dissociates the book from a category
     *
     * @param category category to remove
     */
    public void removeCategory(Category category) {
        if (category == null) {
            return;
        }
        getCategories().remove(category);
    }

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

    public boolean isValid() {
        Condition cond = setBookCondition();
        return cond ==  Condition.OK;
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
     */
    public Condition setBookCondition() {
        condition = checkCondition();
        return condition;
    }


    /**
     * checks if Book has none, or more than one beginning
     * @return true, if it is OK
     */
    public boolean hasOneOnlyBeginSection() {
         return getSections().stream()
                 .filter(s -> s.getType() == Section.Type.BEGIN).count() ==1;
    }

    /**
     * checks if Book has no ending (but can have multiple)
     * @return true, if it is OK
     */
    public boolean hasAtLeastOneEndSection() {
        return getSections().stream()
                .anyMatch(s -> s.getType() == Section.Type.END);
    }

    /**
     * checks if Book has invalid next section id
     * @return true, if it is NOT OK
     */
    public boolean hasInvalidGoToSection() {
        return getSections().stream()
            .flatMap(s -> s.getOptions().stream())
            .anyMatch(o -> getSections().stream().noneMatch(sec -> sec.getSectionNumber().equals(o.getGotoSectionNumber())));
    }

    /**
     * checks if Book has a non-ending section with no options
     * @return true, if it is NOT OK
     */
    public boolean hasNonFinalSectionWithoutOptions() {
        return getSections().stream()
                .filter(s -> s.getType() != Section.Type.END)
                .anyMatch(s -> s.getOptions().isEmpty());
    }

    public static Book create(String title, String author, Difficulty difficulty) {
        return Book.builder()
                .title(title)
                .author(author)
                .difficulty(difficulty)
                .build();
    }

}
