package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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


    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
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
        sections.add(section);
        section.setBook(this);
    }

    public boolean hasOneOnlyBeginSection() {
        return (sections.stream().filter(s -> s.getType() == Section.Type.BEGIN).count()==1);
    }
    public boolean hasAtLeastOneEndSection() {
        return sections.stream().anyMatch(s -> s.getType() == Section.Type.END);
    }
    public boolean hasInvalidGoToSection() {
        return sections.stream()
            .flatMap(s -> s.getOptions().stream())
            .anyMatch(o -> sections.stream().noneMatch(sec -> sec.getSectionNumber().equals(o.getGotoSectionNumber())));
    }
    public boolean hasNonFinalSectionWithoutOptions() {
        return sections.stream()
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
