package prs.fmtareco.adventure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import prs.fmtareco.adventure.exceptions.InvalidEnumValueException;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(
    name = "sections",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "section_number"})
    }
)
@Getter
@Setter
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "section_number", nullable = false)
    private Integer sectionNumber;

    @Lob
    @Column(nullable = false)
    private String text;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type implements Serializable {
        BEGIN,
        NODE,
        END;
        public static Type from(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Section Type", type, Type.values().toString());
            }
        }
    }
}
