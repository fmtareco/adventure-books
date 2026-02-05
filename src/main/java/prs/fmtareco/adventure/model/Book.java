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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "book_valid", nullable = false)
    private boolean bookValid;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

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

    public void addSection(Section section) {
        getSections().add(section);
        section.setBook(this);
    }


    //---------------------------------------------------------------------------------------//
    //                              book validation rules                                    //
    //---------------------------------------------------------------------------------------//

    public boolean hasOneOnlyBeginSection() {
         return getSections().stream()
                 .filter(s -> s.getType() == Section.Type.BEGIN).count() ==1;
    }
    public boolean hasAtLeastOneEndSection() {
        return getSections().stream()
                .anyMatch(s -> s.getType() == Section.Type.END);
    }
    public boolean hasInvalidGoToSection() {
        return getSections().stream()
            .flatMap(s -> s.getOptions().stream())
            .anyMatch(o -> getSections().stream().noneMatch(sec -> sec.getSectionNumber().equals(o.getGotoSectionNumber())));
    }
    public boolean hasNonFinalSectionWithoutOptions() {
        return getSections().stream()
                .filter(s -> s.getType() != Section.Type.END)
                .anyMatch(s -> s.getOptions().isEmpty());
    }

    public boolean isValid() {
        if (!hasOneOnlyBeginSection() ||
            !hasAtLeastOneEndSection() ||
            hasInvalidGoToSection() ||
            hasNonFinalSectionWithoutOptions())
            return false;
        return true;
    }
}
