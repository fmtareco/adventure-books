package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    public enum Difficulty implements Serializable {
        Easy,
        Medium,
        Hard;
        public static Difficulty from(String difficulty) {
            try {
                return Difficulty.valueOf(difficulty);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Difficulty", difficulty, Difficulty.values().toString());
            }
        }
    }
}
